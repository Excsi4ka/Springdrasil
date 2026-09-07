package dev.excsi.springdrasil.service

import dev.excsi.springdrasil.dto.AuthRequest
import dev.excsi.springdrasil.dto.AuthResponse
import dev.excsi.springdrasil.exception.YggdrasilException
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthenticationService(
    val accountService: AccountService,
    val sessionService: SessionService,
    val passwordEncoder: PasswordEncoder,
) {

    @Transactional
    fun authenticate(authRequest: AuthRequest) : AuthResponse {
        val account = accountService.findByUsernameAndLock(authRequest.username)
            ?: throw YggdrasilException(HttpStatus.FORBIDDEN, "ForbiddenOperationException", "Invalid credentials. Invalid username or password")

        if (!passwordEncoder.matches(authRequest.password, account.passwordHash)) {
            throw YggdrasilException(HttpStatus.FORBIDDEN, "ForbiddenOperationException", "Invalid credentials. Invalid username or password")
        }

        val sessionToken = sessionService.createSessionTokenOnAuthentication(
            authRequest.clientToken,
            account.profile
        )

        val authResponse = AuthResponse(
            accessToken = sessionToken.accessToken,
            clientToken = sessionToken.clientToken,
        )

        return authResponse
    }
}