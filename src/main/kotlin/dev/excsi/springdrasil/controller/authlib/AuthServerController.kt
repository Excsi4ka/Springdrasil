package dev.excsi.springdrasil.controller.authlib

import dev.excsi.springdrasil.dto.AuthRequest
import dev.excsi.springdrasil.dto.AuthResponse
import dev.excsi.springdrasil.dto.RefreshRequest
import dev.excsi.springdrasil.dto.RefreshResponse
import dev.excsi.springdrasil.dto.TokenStateRequest
import dev.excsi.springdrasil.service.YggdrasilAuthService
import dev.excsi.springdrasil.service.SessionTokenService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    "/authserver",
    consumes = ["application/json;charset=UTF-8"]
)
class AuthServerController(
    val authenticationService: YggdrasilAuthService,
    val sessionTokenService: SessionTokenService,
) {

    @PostMapping("authenticate")
    fun authenticate(@RequestBody authRequest: AuthRequest): AuthResponse {
        return authenticationService.authenticate(authRequest)
    }

    @PostMapping("refresh")
    fun refresh(@RequestBody refreshRequest: RefreshRequest): RefreshResponse {
        return sessionTokenService.refreshToken(refreshRequest)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("validate")
    fun validate(@RequestBody validateRequest: TokenStateRequest) {
        sessionTokenService.validateToken(validateRequest)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("invalidate")
    fun invalidate(@RequestBody invalidateRequest: TokenStateRequest) {
        sessionTokenService.invalidateToken(invalidateRequest)
    }
}