package dev.excsi.springdrasil.service.yggdrasil

import dev.excsi.springdrasil.exception.YggdrasilException
import dev.excsi.springdrasil.model.Profile
import dev.excsi.springdrasil.model.SessionToken
import dev.excsi.springdrasil.model.TokenState
import dev.excsi.springdrasil.repository.SessionTokenRepository
import dev.excsi.springdrasil.unhyphenatedString
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class SessionValidationService(
    val sessionTokenRepository: SessionTokenRepository,
) {

    @Transactional
    fun validate(accessToken: String, clientToken: String? = null): SessionToken {
        return validateInternal(accessToken, clientToken)
    }

    @Transactional
    fun validateTemporarilyInvalid(accessToken: String, clientToken: String? = null): SessionToken {
        return validateInternal(accessToken, clientToken, true)
    }

    @Transactional
    fun validateTokenAgainstProfileId(accessToken: String, profileId: String): Profile {
        val sessionToken = validateInternal(accessToken)
        val profile = sessionToken.boundProfile

        val normalizedProfileId = profileId.replace("-", "").lowercase()

        if (profile.id.unhyphenatedString() != normalizedProfileId) {
            throw YggdrasilException(HttpStatus.FORBIDDEN, "ForbiddenOperationException", "Invalid token")
        }

        return profile
    }

    @Transactional
    fun validateTokenAgainstUsername(accessToken: String, username: String): Profile {
        val sessionToken = validateInternal(accessToken)
        val profile = sessionToken.boundProfile

        if (!profile.profileUsername.equals(username, ignoreCase = true)) {
            throw YggdrasilException(HttpStatus.FORBIDDEN, "ForbiddenOperationException", "Invalid token")
        }

        return profile
    }

    private fun validateInternal(accessToken: String, clientToken: String? = null, allowTemporarilyInvalid: Boolean = false): SessionToken {
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

    private fun invalidToken(): YggdrasilException {
        return YggdrasilException(HttpStatus.FORBIDDEN, "ForbiddenOperationException", "Invalid token")
    }
}