package dev.excsi.springdrasil.service

import dev.excsi.springdrasil.configuration.ConfigurationValues
import dev.excsi.springdrasil.model.Profile
import dev.excsi.springdrasil.model.SessionToken
import dev.excsi.springdrasil.repository.SessionTokenRepository
import dev.excsi.springdrasil.unhyphenatedString
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class SessionService(
    val sessionTokenRepository: SessionTokenRepository,
    val configurationValues: ConfigurationValues,
) {

    fun createSessionTokenOnAuthentication(suppliedClientToken : String?, profile: Profile): SessionToken {
        val count = sessionTokenRepository.countByBoundProfileId(profile.id)

        if (count >= configurationValues.maxTokensPerUserInRotation) {
            val oldestToken = sessionTokenRepository.findFirstByBoundProfileIdOrderByIssuedAtAsc(profile.id)
            oldestToken?.let {
                sessionTokenRepository.delete(it)
            }
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