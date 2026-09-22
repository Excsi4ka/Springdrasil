package dev.excsi.springdrasil.service.web

import dev.excsi.springdrasil.dto.JwtRefreshResult
import dev.excsi.springdrasil.dto.LoginResult
import dev.excsi.springdrasil.dto.UserData
import dev.excsi.springdrasil.dto.WebLoginRequest
import dev.excsi.springdrasil.model.Status
import dev.excsi.springdrasil.model.User
import dev.excsi.springdrasil.service.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.DisabledException
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
    val userService: UserService,
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

            if (user.status != Status.ACTIVE) {
                throw DisabledException("User account inactive")
            }

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
    fun refresh(rawRefreshToken: String?): JwtRefreshResult {
        if (rawRefreshToken == null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }

        if (rawRefreshToken.isBlank()) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }

        val newRefreshToken = jwtRefreshTokenService.rotateToken(rawRefreshToken)
        val jwtToken = jwtTokenService.issueToken(newRefreshToken.user)

        return JwtRefreshResult(
            jwtRefreshToken = newRefreshToken.id.toString(),
            jwtToken = jwtToken,
        )
    }

    @Transactional
    fun logout(rawRefreshToken: String?) {
        if (rawRefreshToken == null) {
            return
        }

        if (rawRefreshToken.isBlank()) {
            return
        }

        jwtRefreshTokenService.invalidateToken(rawRefreshToken)
    }

    fun me(jwtAuthenticationToken: JwtAuthenticationToken): UserData {
        val userId = UUID.fromString(jwtAuthenticationToken.name)
        val user = userService.findById(userId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        val profile = user.profile

        return UserData(
            email = user.email,
            userId = user.id,
            profileName = profile.profileUsername,
            gameProfileUUID = profile.id,
            creationDate = user.createdAt.toString(),
            status = user.status,
            role = user.role,
        )
    }
}
