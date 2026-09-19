package dev.excsi.springdrasil.controller.web

import dev.excsi.springdrasil.component.ConditionalOnWebEnabled
import org.springframework.web.bind.annotation.RestController

@RestController
@ConditionalOnWebEnabled
class WebAuthController {
}