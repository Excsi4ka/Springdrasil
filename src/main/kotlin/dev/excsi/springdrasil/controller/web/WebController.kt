package dev.excsi.springdrasil.controller.web

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.web.bind.annotation.RestController

@RestController
@ConditionalOnProperty(
    prefix = "springdrasil.application",
    value = ["web-enabled"],
    havingValue = "true",
    matchIfMissing = true
)
class WebController {

}