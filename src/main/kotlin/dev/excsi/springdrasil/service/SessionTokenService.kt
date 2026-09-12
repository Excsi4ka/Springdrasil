package dev.excsi.springdrasil.service

import dev.excsi.springdrasil.configuration.ConfigurationValues
import dev.excsi.springdrasil.dto.RefreshRequest
import dev.excsi.springdrasil.dto.RefreshResponse
import dev.excsi.springdrasil.dto.TokenStateRequest
import dev.excsi.springdrasil.exception.YggdrasilException
import dev.excsi.springdrasil.model.Profile
import dev.excsi.springdrasil.model.SessionToken
import dev.excsi.springdrasil.model.TokenState
import dev.excsi.springdrasil.repository.SessionTokenRepository
import dev.excsi.springdrasil.unhyphenatedString
import jakarta.persistence.EntityManager
import jakarta.persistence.LockModeType
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID
import kotlin.jvm.optionals.getOrElse

@Service
class SessionTokenService(
    val sessionTokenRepository: SessionTokenRepository,
    val configurationValues: ConfigurationValues,
    val profileService: ProfileService,
    val entityManager: EntityManager,
) {

    @Transactional
    fun issueToken(suppliedClientToken : String?, profile: Profile): SessionToken {
        val maxTokens = configurationValues.maxTokensPerUserInRotation

        entityManager.lock(profile, LockModeType.PESSIMISTIC_WRITE)

        val existingTokens = sessionTokenRepository.findAllByBoundProfileIdAndStateNotOrderByIssuedAtAsc(
                profile.id,
                TokenState.INVALID,
                PageRequest.of(0, maxTokens),
            ).toMutableList()

        while (existingTokens.size >= maxTokens) {
            existingTokens.removeFirst().state = TokenState.INVALID
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
    fun validateToken(validateRequest: TokenStateRequest) {
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
        sessionToken.state = TokenState.INVALID

        val selectedProfile = profileService.toProfileDto(profile)
        val userInfo = if (refreshRequest.requestUser) {
            profile.user?.let {
                profileService.toUserDto(it)
            }
        } else null

        val refreshResponse = RefreshResponse(
            accessToken = accessToken,
            clientToken = clientToken,
            user = userInfo,
            selectedProfile = selectedProfile,
        )

        return refreshResponse
    }

    @Transactional
    fun invalidateToken(invalidateRequest: TokenStateRequest) {
        val sessionToken = sessionTokenRepository.findById(invalidateRequest.accessToken).getOrElse {
            return
        }

        sessionToken.state = TokenState.INVALID
    }

    @Transactional
    fun invalidateAllTokensForProfile(profile: Profile) {
        val tokenList = sessionTokenRepository.findAllByBoundProfileIdAndStateNot(profile.id, TokenState.INVALID)

        tokenList.forEach {
            it.state = TokenState.INVALID
        }
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