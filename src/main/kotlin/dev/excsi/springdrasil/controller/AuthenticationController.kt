package dev.excsi.springdrasil.controller

import dev.excsi.springdrasil.configuration.properties.JwtConfigurationProperties
import dev.excsi.springdrasil.configuration.properties.WebConfigurationProperties
import dev.excsi.springdrasil.dto.CsrfTokenResponse
import dev.excsi.springdrasil.dto.JwtRefreshResponse
import dev.excsi.springdrasil.dto.LoginResponse
import dev.excsi.springdrasil.dto.UserDataResponse
import dev.excsi.springdrasil.dto.WebLoginRequest
import dev.excsi.springdrasil.service.web.WebAuthenticationService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/auth")
class AuthenticationController(
    val webAuthService: WebAuthenticationService,
    val jwtConfigurationProperties: JwtConfigurationProperties,
    val webConfigurationProperties: WebConfigurationProperties
) {

    @PostMapping("login")
    fun login(@RequestBody webLoginRequest: WebLoginRequest): ResponseEntity<LoginResponse> {
        val result = webAuthService.login(webLoginRequest)

        val responseBody = LoginResponse(
            email = result.email,
            profileName = result.profileName,
            jwtToken = result.jwtToken
        )

        val refreshCookie = ResponseCookie.from("jwt_refresh_token", result.jwtRefreshToken)
            .httpOnly(true)
            .path("/api/auth/refresh")
            .maxAge(jwtConfigurationProperties.refreshTokenDuration)
            .secure(webConfigurationProperties.httpsEnabled)
            .sameSite("Lax")
            .build()

        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(responseBody)
    }

    @PostMapping("logout")
    fun logout() {

    }

    @PostMapping("refresh")
    fun refresh(
        @CookieValue(name = "jwt_refresh_token", required = false) refreshToken: String?
    ): ResponseEntity<JwtRefreshResponse> {
        val result = webAuthService.refresh(refreshToken)

        val responseBody = JwtRefreshResponse(
            jwtToken = result.jwtToken,
        )

        val refreshCookie = ResponseCookie.from("jwt_refresh_token", result.jwtRefreshToken)
            .httpOnly(true)
            .path("/api/auth/refresh")
            .maxAge(jwtConfigurationProperties.refreshTokenDuration)
            .secure(webConfigurationProperties.httpsEnabled)
            .sameSite("Lax")
            .build()

        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(responseBody)
    }

    @GetMapping("csrf")
    fun csrf(csrfToken: CsrfToken): CsrfTokenResponse {
        return CsrfTokenResponse(csrfToken.token)
    }

    @GetMapping("me")
    fun me(jwtAuthToken: JwtAuthenticationToken): UserDataResponse {
        return webAuthService.me(jwtAuthToken)
    }
}