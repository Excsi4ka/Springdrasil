package dev.excsi.springdrasil.controller.web

import dev.excsi.springdrasil.component.ConditionalOnWebEnabled
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@ConditionalOnWebEnabled
class WebController {

    @GetMapping("", "/", "login", "/register", "user/**")
    fun html(): String {
        return "forward:/index.html"
    }

}