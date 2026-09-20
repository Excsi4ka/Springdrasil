package dev.excsi.springdrasil.controller.admin

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminViewController {

    @GetMapping("admin", "admin/login", "admin/dashboard/**")
    fun adminHtml(): String {
        return "forward:/index.html"
    }
}