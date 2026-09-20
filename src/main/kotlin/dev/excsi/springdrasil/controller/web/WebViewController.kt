package dev.excsi.springdrasil.controller.web

import dev.excsi.springdrasil.component.ConditionalOnWebEnabled
import dev.excsi.springdrasil.configuration.AuthlibConfigurationValues
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@ConditionalOnWebEnabled
class WebViewController(
    val authlibConfigurationValues: AuthlibConfigurationValues,
) {

    @GetMapping("", "/", "login", "/register", "user/**")
    fun html(response: HttpServletResponse): String {
        response.setHeader(
            "X-Authlib-Injector-API-Location",
            "${authlibConfigurationValues.baseDomainUrl.trim('/')}/",
        )

        return "forward:/index.html"
    }
}