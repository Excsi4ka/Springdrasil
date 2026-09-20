package dev.excsi.springdrasil.service.web

import dev.excsi.springdrasil.dto.LoginUserRequest
import dev.excsi.springdrasil.dto.LoginUserResponse
import dev.excsi.springdrasil.service.user.UserService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service

@Service
class WebAuthenticationService(
    val userService: UserService,
    val authenticationManager: AuthenticationManager,
    val jwtTokenService: JwtTokenService,
) {

    fun login(loginUserRequest: LoginUserRequest): LoginUserResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginUserRequest.email,
                loginUserRequest.password,
            )
        )

        val user = userService.findByEmail(loginUserRequest.email)
            ?: throw IllegalArgumentException("User not found.")

        return LoginUserResponse(
            jwtToken = jwtTokenService.issue(user),
        )
    }
}
