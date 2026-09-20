package dev.excsi.springdrasil.service.profile

import dev.excsi.springdrasil.configuration.AuthlibConfigurationValues
import dev.excsi.springdrasil.dto.NameAvailableResponse
import dev.excsi.springdrasil.dto.ProfileDto
import dev.excsi.springdrasil.exception.YggdrasilException
import dev.excsi.springdrasil.isUnhyphenatedUuidValid
import dev.excsi.springdrasil.model.Profile
import dev.excsi.springdrasil.repository.ProfileRepository
import dev.excsi.springdrasil.toUuid
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ProfileService(
    val profileRepository: ProfileRepository,
    val profileSerializationService: ProfileSerializationService,
    val authlibConfigurationValues: AuthlibConfigurationValues
) {

    fun nameAvailable(profileName: String): NameAvailableResponse {
        val profile = profileRepository.findByProfileUsername(profileName)

        return NameAvailableResponse(
            name = profileName,
            available = profile == null,
        )
    }

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
        if (count < 2 || count > authlibConfigurationValues.maxProfilesPerRequest) {
            return emptyList()
        }

        val list = mutableListOf<ProfileDto>()
        for (username in usernames) {
            val profile = profileRepository.findByProfileUsername(username)
            profile?.let {
                list.add(profileSerializationService.serializeProfile(it))
            }
        }

        return list
    }
}