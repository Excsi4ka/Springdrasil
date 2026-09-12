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
        sessionTokenService.validateTokenAgainstProfile(clientJoinRequest.accessToken, clientJoinRequest.selectedProfile)

        val ipAddress = extractClientIp(servletRequest)
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

    fun extractClientIp(request: HttpServletRequest): String {
        val forwardedFor = request.getHeader("X-Forwarded-For")
        if (!forwardedFor.isNullOrBlank()) {
            return forwardedFor.split(",").first().trim()
        }

        val realIp = request.getHeader("X-Real-IP")
        if (!realIp.isNullOrBlank()) {
            return realIp
        }

        return request.remoteAddr
    }
}
