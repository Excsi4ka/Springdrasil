package dev.excsi.springdrasil.service

import dev.excsi.springdrasil.configuration.ConfigurationValues
import dev.excsi.springdrasil.dto.RefreshRequest
import dev.excsi.springdrasil.dto.RefreshResponse
import dev.excsi.springdrasil.dto.ValidateRequest
import dev.excsi.springdrasil.exception.YggdrasilException
import dev.excsi.springdrasil.model.Profile
import dev.excsi.springdrasil.model.SessionToken
import dev.excsi.springdrasil.model.TokenState
import dev.excsi.springdrasil.repository.SessionTokenRepository
import dev.excsi.springdrasil.unhyphenatedString
import jakarta.persistence.EntityManager
import jakarta.persistence.LockModeType
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class SessionTokenService(
    val sessionTokenRepository: SessionTokenRepository,
    val configurationValues: ConfigurationValues,
    val serializationService: SerializationService,
    val entityManager: EntityManager,
) {

    @Transactional
    fun issueToken(suppliedClientToken : String?, profile: Profile): SessionToken {
        val maxTokens = configurationValues.maxTokensPerUserInRotation

        entityManager.lock(profile, LockModeType.PESSIMISTIC_WRITE)

        val existingTokens = sessionTokenRepository
            .findAllByBoundProfileIdOrderByIssuedAtAsc(profile.id)
            .toMutableList()

        while (existingTokens.size >= maxTokens) {
            sessionTokenRepository.delete(existingTokens.removeFirst())
        }

        existingTokens.forEach {
            it.state = TokenState.TEMPORARILY_INVALID
        }

        val accessToken = UUID.randomUUID().unhyphenatedString()
        val clientToken = suppliedClientToken ?: UUID.randomUUID().unhyphenatedString()
        val expiresAt = Instant.now().plus(configurationValues.sessionTokenTimeout)

        val sessionToken = SessionToken(
            accessToken = accessToken,
            clientToken = clientToken,
            expiresAt = expiresAt,
            boundProfile = profile
        )

        return sessionTokenRepository.save(sessionToken)
    }

    @Transactional
    fun validateToken(validateRequest: ValidateRequest) {
        validateTokenInternal(validateRequest.accessToken, validateRequest.clientToken)
    }

    @Transactional
    fun refreshToken(refreshRequest: RefreshRequest): RefreshResponse {
        val sessionToken = validateTokenInternal(refreshRequest.accessToken, refreshRequest.clientToken, true)

        if (refreshRequest.selectedProfile != null) {
            throw YggdrasilException(HttpStatus.BAD_REQUEST, "IllegalArgumentException", "Access token already has a profile assigned.")
        }

        val profile = sessionToken.boundProfile
        entityManager.lock(profile, LockModeType.PESSIMISTIC_WRITE)

        val accessToken = UUID.randomUUID().unhyphenatedString()
        val clientToken = sessionToken.clientToken
        val expiresAt = Instant.now().plus(configurationValues.sessionTokenTimeout)

        val newSessionToken = SessionToken(
            accessToken = accessToken,
            clientToken = clientToken,
            expiresAt = expiresAt,
            boundProfile = profile
        )

        sessionTokenRepository.save(newSessionToken)
        sessionTokenRepository.delete(sessionToken)

        val selectedProfile = serializationService.toProfileDto(profile)
        val userInfo = if (refreshRequest.requestUser) {
            profile.user?.let { serializationService.toUserDto(it) }
        } else null

        val refreshResponse = RefreshResponse(
            accessToken = accessToken,
            clientToken = clientToken,
            user = userInfo,
            selectedProfile = selectedProfile,
        )

        return refreshResponse
    }

    private fun validateTokenInternal(accessToken: String, clientToken: String?, allowTemporarilyInvalid: Boolean = false): SessionToken {
        val sessionToken = sessionTokenRepository.findById(accessToken).orElseThrow {
            throw YggdrasilException(HttpStatus.FORBIDDEN, "ForbiddenOperationException", "Invalid token")
        }

        clientToken?.let {
            if (sessionToken.clientToken != it) {
                throw YggdrasilException(HttpStatus.FORBIDDEN, "ForbiddenOperationException", "Invalid token")
            }
        }

        val allowedState = sessionToken.state == TokenState.VALID ||
            (allowTemporarilyInvalid && sessionToken.state == TokenState.TEMPORARILY_INVALID)

        if (!allowedState) {
            throw YggdrasilException(HttpStatus.FORBIDDEN, "ForbiddenOperationException", "Invalid token")
        }

        if (sessionToken.expiresAt.isBefore(Instant.now())) {
            sessionToken.state = TokenState.INVALID
            throw YggdrasilException(HttpStatus.FORBIDDEN, "ForbiddenOperationException", "Invalid token")
        }

        return sessionToken
    }
}