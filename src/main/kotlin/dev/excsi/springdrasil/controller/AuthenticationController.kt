package dev.excsi.springdrasil.controller

import dev.excsi.springdrasil.dto.LoginUserRequest
import dev.excsi.springdrasil.service.web.WebAuthenticationService
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/auth")
class AuthenticationController(
    val webAuthService: WebAuthenticationService
) {

    @PostMapping("login")
    fun login(@RequestBody loginUserRequest: LoginUserRequest) {

    }

    @PostMapping("logout")
    fun logout() {

    }

    @PostMapping("refresh")
    fun refresh(@CookieValue(name = "jwt_refresh_token", required = true) refreshToken: String) {

    }

    @GetMapping("csrf")
    fun csrf() {

    }

    @GetMapping("me")
    fun me() {

    }
}