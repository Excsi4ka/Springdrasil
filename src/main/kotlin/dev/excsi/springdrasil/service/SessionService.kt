package dev.excsi.springdrasil.service

import dev.excsi.springdrasil.configuration.ConfigurationValues
import dev.excsi.springdrasil.model.Profile
import dev.excsi.springdrasil.model.SessionToken
import dev.excsi.springdrasil.model.TokenState
import dev.excsi.springdrasil.repository.SessionTokenRepository
import dev.excsi.springdrasil.unhyphenatedString
import jakarta.persistence.EntityManager
import jakarta.persistence.LockModeType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class SessionService(
    val sessionTokenRepository: SessionTokenRepository,
    val configurationValues: ConfigurationValues,
    val entityManager: EntityManager,
) {

    @Transactional
    fun createSessionTokenOnAuthentication(suppliedClientToken : String?, profile: Profile): SessionToken {
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
}