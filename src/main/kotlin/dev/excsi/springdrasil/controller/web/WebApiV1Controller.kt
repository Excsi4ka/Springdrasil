package dev.excsi.springdrasil.controller.web

import dev.excsi.springdrasil.component.ConditionalOnWebEnabled
import dev.excsi.springdrasil.dto.NameAvailableResponse
import dev.excsi.springdrasil.service.profile.ProfileService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1")
@ConditionalOnWebEnabled
class WebApiV1Controller(
    val profileService: ProfileService
) {

    @GetMapping("profile/available")
    fun nameAvailable(@RequestParam name: String): NameAvailableResponse {
        return profileService.nameAvailable(name)
    }
}