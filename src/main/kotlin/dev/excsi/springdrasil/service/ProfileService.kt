package dev.excsi.springdrasil.service

import dev.excsi.springdrasil.configuration.ConfigurationValues
import dev.excsi.springdrasil.dto.ProfileDto
import dev.excsi.springdrasil.dto.Property
import dev.excsi.springdrasil.dto.TextureData
import dev.excsi.springdrasil.dto.TextureDto
import dev.excsi.springdrasil.exception.YggdrasilException
import dev.excsi.springdrasil.isUnhyphenatedUuidValid
import dev.excsi.springdrasil.model.Profile
import dev.excsi.springdrasil.model.Texture
import dev.excsi.springdrasil.model.TextureType
import dev.excsi.springdrasil.repository.ProfileRepository
import dev.excsi.springdrasil.toUuid
import dev.excsi.springdrasil.unhyphenatedString
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper
import java.util.Base64
import java.util.LinkedHashMap

@Service
class ProfileService(
    val profileRepository: ProfileRepository,
    val objectMapper: ObjectMapper,
    val configurationValues: ConfigurationValues,
    val yggdrasilSignatureService: YggdrasilSignatureService,
) {

    fun getProfileByUUID(uuid: String): Profile {
        if (!uuid.isUnhyphenatedUuidValid()) {
            throw YggdrasilException(HttpStatus.FORBIDDEN, "IllegalArgumentException", "Unhyphenated UUID required.")
        }

        return profileRepository.findById(uuid.toUuid()).orElseThrow {
            throw YggdrasilException(HttpStatus.NOT_FOUND, "Profile not found")
        }
    }

    fun queryProfiles(usernames: List<String>): List<ProfileDto> {
        val count = usernames.size
        if (count < 2 || count > configurationValues.maxProfilesPerRequest) {
            return emptyList()
        }

        val list = mutableListOf<ProfileDto>()
        for (username in usernames) {
            val profile = profileRepository.findByProfileUsername(username)
            profile?.let {
                list.add(serializeProfile(it))
            }
        }

        return list
    }

    fun serializeProfile(profile: Profile): ProfileDto {
        return ProfileDto(
            id = profile.id.unhyphenatedString(),
            name = profile.profileUsername,
        )
    }

    fun serializeProfileWithProperties(profile: Profile, sign: Boolean = true): ProfileDto {
        val textureBaseUrl = configurationValues.baseDomainUrl
        val textureMap = LinkedHashMap<TextureType, TextureData>()

        for (texture in profile.textures) {
            val textureData = toTextureData(profile, texture, textureBaseUrl)
            textureMap[texture.textureType] = textureData
        }

        val textureDto = TextureDto(
            profileId = profile.id.unhyphenatedString(),
            profileName = profile.profileUsername,
            textures = textureMap,
        )

        val value = textureDtoToString(textureDto)

        val textureProperties = Property(
            name = "textures",
            value = value,
            signature = if (sign) yggdrasilSignatureService.sign(value) else null,
        )

        //authlib-injector specific
        val uploadableTextureProperties = Property(
            name = "uploadableTextures",
            value = "skin,cape",
            signature = if (sign) yggdrasilSignatureService.sign("skin,cape") else null
        )

        val properties = listOfNotNull(uploadableTextureProperties, textureProperties)

        return ProfileDto(
            id = profile.id.unhyphenatedString(),
            name = profile.profileUsername,
            properties = properties,
        )
    }

    private fun toTextureData(profile: Profile, texture: Texture, textureBaseUrl: String): TextureData {
        val metadata: Map<String, String>?
        if (texture.textureType == TextureType.SKIN) {
            val skinMetadata = HashMap<String, String>()
            skinMetadata["model"] = profile.skinModel.nameLowercase()
            metadata = skinMetadata
        } else {
            metadata = null
        }

        return TextureData(
            url = textureUrl(textureBaseUrl, texture.textureHash),
            metadata = metadata
        )
    }

    private fun textureUrl(textureBaseUrl: String, textureHash: String): String {
        return "${textureBaseUrl.trimEnd('/')}/textures/$textureHash"
    }

    fun textureDtoToString(textureDto: TextureDto): String {
        val jsonString = objectMapper.writeValueAsString(textureDto)
        return Base64.getEncoder().encodeToString(jsonString.toByteArray(Charsets.UTF_8))
    }
}
