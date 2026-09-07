package dev.excsi.springdrasil.controller

import dev.excsi.springdrasil.dto.AuthRequest
import dev.excsi.springdrasil.dto.AuthResponse
import dev.excsi.springdrasil.service.AuthenticationService
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
    val authenticationService: AuthenticationService
) {

    @PostMapping("authenticate")
    fun authenticate(@RequestBody authRequest: AuthRequest) : AuthResponse {
        return authenticationService.authenticate(authRequest)
    }
}