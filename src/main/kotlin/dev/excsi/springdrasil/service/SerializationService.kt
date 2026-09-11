package dev.excsi.springdrasil.service

import dev.excsi.springdrasil.configuration.ConfigurationValues
import dev.excsi.springdrasil.dto.ProfileDto
import dev.excsi.springdrasil.dto.UserDto
import dev.excsi.springdrasil.model.Profile
import dev.excsi.springdrasil.model.User
import dev.excsi.springdrasil.unhyphenatedString
import org.springframework.stereotype.Service

@Service
class SerializationService(
    val textureService: TextureService,
    val configurationValues: ConfigurationValues,
) {

    fun toProfileDto(profile: Profile): ProfileDto {
        return ProfileDto(
            id = profile.id.unhyphenatedString(),
            name = profile.profileUsername,
        )
    }

    fun toProfileDtoWithProperties(profile: Profile): ProfileDto {
        val properties = listOfNotNull(
            textureService.toTexturesProperty(profile, configurationValues.textureBaseUrl)
        )

        return ProfileDto(
            id = profile.id.unhyphenatedString(),
            name = profile.profileUsername,
            properties = properties.ifEmpty { null },
        )
    }

    fun toUserDto(user: User): UserDto {
        return UserDto(
            id = user.id.unhyphenatedString(),
            properties = emptyList()
        )
    }
}
