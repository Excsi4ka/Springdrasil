package dev.excsi.springdrasil.service

import dev.excsi.springdrasil.configuration.ConfigurationValues
import dev.excsi.springdrasil.dto.ProfileDto
import dev.excsi.springdrasil.dto.Property
import dev.excsi.springdrasil.exception.YggdrasilException
import dev.excsi.springdrasil.model.Profile
import dev.excsi.springdrasil.repository.ProfileRepository
import dev.excsi.springdrasil.unhyphenatedString
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ProfileService(
    val profileRepository: ProfileRepository,
    val textureService: TextureService,
    val configurationValues: ConfigurationValues,
    val yggdrasilSignatureService: YggdrasilSignatureService,
) {

    fun getProfileByUUID(uuid: String): Profile {
        val id = unhyphenatedToUuid(uuid)
        return profileRepository.findById(id).orElseThrow {
            throw YggdrasilException(HttpStatus.NOT_FOUND, "Profile not found")
        }
    }

    fun queryProfiles(usernames: List<String>): List<ProfileDto> {
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

    fun unhyphenatedToUuid(value: String): UUID {
        require(value.matches(Regex("[0-9a-fA-F]{32}"))) {
            "Invalid unhyphenated UUID: $value"
        }

        val hyphenated = value.substring(0, 8) + "-" +
                value.substring(8, 12) + "-" +
                value.substring(12, 16) + "-" +
                value.substring(16, 20) + "-" +
                value.substring(20, 32)

        return UUID.fromString(hyphenated)
    }
}
