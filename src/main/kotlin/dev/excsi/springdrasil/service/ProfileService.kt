package dev.excsi.springdrasil.service

import dev.excsi.springdrasil.configuration.ConfigurationValues
import dev.excsi.springdrasil.dto.ProfileDto
import dev.excsi.springdrasil.dto.Property
import dev.excsi.springdrasil.exception.YggdrasilException
import dev.excsi.springdrasil.isUnhyphenatedUuidValid
import dev.excsi.springdrasil.model.Profile
import dev.excsi.springdrasil.repository.ProfileRepository
import dev.excsi.springdrasil.toUuid
import dev.excsi.springdrasil.unhyphenatedString
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ProfileService(
    val profileRepository: ProfileRepository,
    val textureService: TextureService,
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
                list.add(toProfileDto(profile))
            }
        }

        return list
    }

    fun toProfileDto(profile: Profile): ProfileDto {
        return ProfileDto(
            id = profile.id.unhyphenatedString(),
            name = profile.profileUsername,
        )
    }

    fun toProfileDtoWithProperties(profile: Profile, sign: Boolean = true): ProfileDto {
        val uploadableTextureProperties = Property(
            name = "uploadableTextures",
            value = "skin,cape",
            signature = if (sign) yggdrasilSignatureService.sign("skin,cape") else null
        )

        val textureProperties = textureService.toTexturesProperty(profile, configurationValues.textureBaseUrl, sign)

        val properties = listOfNotNull(uploadableTextureProperties, textureProperties)

        return ProfileDto(
            id = profile.id.unhyphenatedString(),
            name = profile.profileUsername,
            properties = properties,
        )
    }
}
