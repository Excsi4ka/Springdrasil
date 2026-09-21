package dev.excsi.springdrasil.controller.web

import dev.excsi.springdrasil.component.ConditionalOnWebEnabled
import dev.excsi.springdrasil.configuration.properties.AuthlibConfigurationProperties
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@ConditionalOnWebEnabled
class WebViewController(
    val authlibConfigurationProperties: AuthlibConfigurationProperties,
) {

    @GetMapping("", "/", "login", "/register", "user/**")
    fun html(response: HttpServletResponse): String {
        response.setHeader(
            "X-Authlib-Injector-API-Location",
            "${authlibConfigurationProperties.baseDomainUrl.trim('/')}/",
        )

        return "forward:/index.html"
    }
}