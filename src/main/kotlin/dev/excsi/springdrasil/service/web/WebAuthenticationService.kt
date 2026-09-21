package dev.excsi.springdrasil.service.web

import dev.excsi.springdrasil.dto.JwtRefreshResult
import dev.excsi.springdrasil.dto.LoginResult
import dev.excsi.springdrasil.dto.UserDataResponse
import dev.excsi.springdrasil.dto.WebLoginRequest
import dev.excsi.springdrasil.model.User
import dev.excsi.springdrasil.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class WebAuthenticationService(
    val authenticationManager: AuthenticationManager,
    val userRepository: UserRepository,
    val jwtTokenService: JwtTokenService,
    val jwtRefreshTokenService: JwtRefreshTokenService
) {

    @Transactional
    fun login(webLoginRequest: WebLoginRequest): LoginResult {
        try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(webLoginRequest.email, webLoginRequest.password,)
            )

            val user = authentication.principal as User
            val jwtToken = jwtTokenService.issueToken(user)
            val jwtRefreshToken = jwtRefreshTokenService.issueToken(user)

            return LoginResult(
                email = user.email,
                profileName = user.profile.profileUsername,
                jwtToken = jwtToken,
                jwtRefreshToken = jwtRefreshToken.id.toString()
            )
        } catch (exception: AuthenticationException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }
    }

    @Transactional
    fun refresh(refreshToken: String?): JwtRefreshResult {
        if (refreshToken == null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }

        if (refreshToken.isBlank()) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }

        val refreshToken = jwtRefreshTokenService.rotateToken(refreshToken)
        val jwtToken = jwtTokenService.issueToken(refreshToken.user)

        return JwtRefreshResult(
            jwtRefreshToken = refreshToken.id.toString(),
            jwtToken = jwtToken,
        )
    }

    fun me(jwtAuthenticationToken: JwtAuthenticationToken): UserDataResponse {
        val userId = UUID.fromString(jwtAuthenticationToken.name)
        val user = userRepository.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND)
        }

        return UserDataResponse(
            email = user.email,
            profileName = user.profile.profileUsername
        )
    }
}
