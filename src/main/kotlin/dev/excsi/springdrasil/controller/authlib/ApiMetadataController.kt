package dev.excsi.springdrasil.controller.authlib

import dev.excsi.springdrasil.dto.ApiMetadataDto
import dev.excsi.springdrasil.service.YggdrasilSignatureService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping($$"${springdrasil.application.authlib-prefix}")
class ApiMetadataController(
    private val yggdrasilSignatureService: YggdrasilSignatureService,
) {

    @GetMapping("/", "")
    fun metadata(): ApiMetadataDto {
        return ApiMetadataDto(
            signaturePublickey = yggdrasilSignatureService.publicKeyPem,
        )
    }
}
