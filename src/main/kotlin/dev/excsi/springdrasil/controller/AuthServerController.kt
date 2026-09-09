package dev.excsi.springdrasil.controller

import dev.excsi.springdrasil.dto.AuthRequest
import dev.excsi.springdrasil.dto.AuthResponse
import dev.excsi.springdrasil.dto.RefreshRequest
import dev.excsi.springdrasil.dto.RefreshResponse
import dev.excsi.springdrasil.dto.ValidateRequest
import dev.excsi.springdrasil.service.YggdrasilAuthService
import dev.excsi.springdrasil.service.SessionTokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
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

    @PostMapping("validate")
    fun validate(@RequestBody validateRequest: ValidateRequest): ResponseEntity<Void> {
        sessionTokenService.validateToken(validateRequest)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PostMapping("invalidate")
    fun invalidate() {

    }
}