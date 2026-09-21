package dev.excsi.springdrasil.service.web

import dev.excsi.springdrasil.configuration.properties.JwtConfigurationProperties
import dev.excsi.springdrasil.model.JwtRefreshToken
import dev.excsi.springdrasil.model.User
import dev.excsi.springdrasil.repository.JwtRefreshTokenRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Clock
import java.time.Instant
import java.util.*

@Service
class JwtRefreshTokenService(
    val jwtRefreshTokenRepository: JwtRefreshTokenRepository,
    val jwtConfigurationProperties: JwtConfigurationProperties
) {

    fun issueToken(user: User): JwtRefreshToken {
        val now = Instant.now()
        val jwtRefreshToken = JwtRefreshToken(
            user = user,
            expiresAt = now.plus(jwtConfigurationProperties.refreshTokenDuration)
        )

        return jwtRefreshTokenRepository.save(jwtRefreshToken)
    }

    @Transactional
    fun rotateToken(rawRefreshToken: String): JwtRefreshToken {
        val refreshTokenId = parseRefreshToken(rawRefreshToken)
        val currentToken = jwtRefreshTokenRepository.findById(refreshTokenId).orElseThrow {
            ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }

        if (currentToken.expiresAt.isBefore(Instant.now(Clock.systemUTC()))) {
            jwtRefreshTokenRepository.delete(currentToken)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }

        val user = currentToken.user
        jwtRefreshTokenRepository.delete(currentToken)
        return issueToken(user)
    }

    private fun parseRefreshToken(refreshToken: String): UUID {
        return try {
            UUID.fromString(refreshToken)
        } catch (exception: Exception) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }
    }
}