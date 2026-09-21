package dev.excsi.springdrasil.service.profile

import dev.excsi.springdrasil.configuration.properties.AuthlibConfigurationProperties
import dev.excsi.springdrasil.dto.ProfileDto
import dev.excsi.springdrasil.dto.Property
import dev.excsi.springdrasil.dto.TextureData
import dev.excsi.springdrasil.dto.TextureDto
import dev.excsi.springdrasil.model.Profile
import dev.excsi.springdrasil.model.Texture
import dev.excsi.springdrasil.model.TextureType
import dev.excsi.springdrasil.service.SignatureService
import dev.excsi.springdrasil.unhyphenatedString
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper
import java.util.Base64
import java.util.LinkedHashMap

@Service
class ProfileSerializationService(
    val objectMapper: ObjectMapper,
    val authlibConfigurationProperties: AuthlibConfigurationProperties,
    val signatureService: SignatureService,
) {

    fun serializeProfile(profile: Profile): ProfileDto {
        return ProfileDto(
            id = profile.id.unhyphenatedString(),
            name = profile.profileUsername,
        )
    }

    fun serializeProfileWithProperties(profile: Profile, sign: Boolean = true): ProfileDto {
        val textureMap = LinkedHashMap<TextureType, TextureData>()

        for (texture in profile.textures) {
            val textureData = toTextureData(profile, texture)
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
            signature = if (sign) signatureService.sign(value) else null,
        )

        val uploadableTextureProperties = Property(
            name = "uploadableTextures",
            value = "skin,cape",
            signature = if (sign) signatureService.sign("skin,cape") else null
        )

        return ProfileDto(
            id = profile.id.unhyphenatedString(),
            name = profile.profileUsername,
            properties = listOf(uploadableTextureProperties, textureProperties),
        )
    }

    fun textureDtoToString(textureDto: TextureDto): String {
        val jsonString = objectMapper.writeValueAsString(textureDto)
        return Base64.getEncoder().encodeToString(jsonString.toByteArray(Charsets.UTF_8))
    }

    private fun toTextureData(profile: Profile, texture: Texture): TextureData {
        val metadata: Map<String, String>?
        if (texture.textureType == TextureType.SKIN) {
            val skinMetadata = HashMap<String, String>()
            skinMetadata["model"] = profile.skinModel.nameLowercase()
            metadata = skinMetadata
        } else {
            metadata = null
        }

        return TextureData(
            url = textureUrl(texture.textureHash),
            metadata = metadata
        )
    }

    private fun textureUrl(textureHash: String): String {
        return "${authlibConfigurationProperties.baseDomainUrl.trimEnd('/')}/textures/$textureHash"
    }
}