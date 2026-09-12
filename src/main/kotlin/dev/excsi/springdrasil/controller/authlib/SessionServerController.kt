package dev.excsi.springdrasil.controller.authlib

import dev.excsi.springdrasil.dto.ClientJoinRequest
import dev.excsi.springdrasil.dto.ProfileDto
import dev.excsi.springdrasil.service.MinecraftSessionService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    "/sessionserver/session/minecraft",
)
class SessionServerController(
    val minecraftSessionService: MinecraftSessionService,
) {

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(
        "join",
        consumes = ["application/json;charset=UTF-8"]
    )
    fun join(
        @RequestBody clientJoinRequest: ClientJoinRequest,
        httpServletRequest: HttpServletRequest
    ) {
        minecraftSessionService.join(clientJoinRequest, httpServletRequest)
    }

    @GetMapping("hasJoined")
    fun hasJoined(
        @RequestParam(name = "username") username: String,
        @RequestParam(name = "serverId") serverId: String,
        @RequestParam(name = "ip", required = false) ip: String?,
    ): ResponseEntity<ProfileDto> {
        val profile = minecraftSessionService.hasJoined(username, serverId, ip)
            ?: return ResponseEntity(HttpStatus.NO_CONTENT)

        return ResponseEntity.ok(profile)
    }
}