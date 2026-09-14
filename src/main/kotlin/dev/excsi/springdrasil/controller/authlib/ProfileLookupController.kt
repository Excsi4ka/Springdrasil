package dev.excsi.springdrasil.controller.authlib

import dev.excsi.springdrasil.dto.ProfileDto
import dev.excsi.springdrasil.service.ProfileService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping($$"${springdrasil.application.authlib-prefix}/api/profiles/minecraft")
class ProfileLookupController(
    val profileService: ProfileService
) {

    @PostMapping("/")
    fun queryProfiles(@RequestBody usernames: List<String>): List<ProfileDto> {
        return profileService.queryProfiles(usernames)
    }
}