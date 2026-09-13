package dev.excsi.springdrasil.service

import com.github.benmanes.caffeine.cache.Cache
import dev.excsi.springdrasil.dto.ClientJoinRequest
import dev.excsi.springdrasil.dto.JoinSessionData
import dev.excsi.springdrasil.dto.ProfileDto
import dev.excsi.springdrasil.exception.YggdrasilException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MinecraftSessionService(
    val joinSessionCache: Cache<String, JoinSessionData>,
    val sessionTokenService: SessionTokenService,
    val profileService: ProfileService,
) {

    fun join(clientJoinRequest: ClientJoinRequest, servletRequest: HttpServletRequest) {
        sessionTokenService.validateTokenAgainstProfileId(clientJoinRequest.accessToken, clientJoinRequest.selectedProfile)

        //ideally this should be behind a proxy with proper X-Forwarded-For header
        val ipAddress = servletRequest.remoteAddr
        val joinSessionData = JoinSessionData(
            ipAddress = ipAddress,
            accessToken = clientJoinRequest.accessToken,
            serverId = clientJoinRequest.serverId,
        )

        joinSessionCache.put(clientJoinRequest.serverId, joinSessionData)
    }

    @Transactional
    fun hasJoined(username: String, serverId: String, ipAddress: String?): ProfileDto? {
        val joinData = joinSessionCache.getIfPresent(serverId) ?: return null

        ipAddress?.let {
            if (joinData.ipAddress != it) return null
        }

        return try {
            val profile = sessionTokenService.validateTokenAgainstUsername(joinData.accessToken, username)
            profileService.toProfileDtoWithProperties(profile)
        } catch (exception: YggdrasilException) {
            null
        }
    }

    @Transactional
    fun profile(uuid: String, unsigned: Boolean): ProfileDto? {
        return try {
            val profile = profileService.getProfileByUUID(uuid)
            profileService.toProfileDtoWithProperties(profile, !unsigned)
        } catch (exception: Exception) {
            null
        }
    }
}
