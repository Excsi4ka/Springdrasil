package dev.excsi.springdrasil.controller

import dev.excsi.springdrasil.dto.AuthRequest
import dev.excsi.springdrasil.dto.AuthResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/authserver")
class AuthServerController {

    @PostMapping("/authenticate", consumes = ["application/json;charset=UTF-8"])
    fun authenticate(@RequestBody authRequest: AuthRequest) : AuthResponse {

    }
}