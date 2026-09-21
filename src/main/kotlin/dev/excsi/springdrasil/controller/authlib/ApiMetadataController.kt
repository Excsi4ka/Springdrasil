package dev.excsi.springdrasil.controller.authlib

import dev.excsi.springdrasil.configuration.properties.AuthlibConfigurationProperties
import dev.excsi.springdrasil.dto.ApiMetadataResponse
import dev.excsi.springdrasil.service.SignatureService
import org.springframework.boot.info.BuildProperties
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping($$"${springdrasil.authlib.prefix}")
class ApiMetadataController(
    val authlibConfigurationProperties: AuthlibConfigurationProperties,
    val signatureService: SignatureService,
    val buildProperties: BuildProperties
) {

    @GetMapping("/", "")
    fun metadata(): ApiMetadataResponse {
        return ApiMetadataResponse(
            meta = mapOf(
                "serverName" to "SpringdrasilAuthServer",
                "implementationName" to "Springdrasil",
                "implementationVersion" to "${buildProperties.version}",
                "feature.non_email_login" to false,
                "links" to mapOf(
                    "homepage" to authlibConfigurationProperties.baseDomainUrl,
                )
            ),
            skinDomains = listOf(
                authlibConfigurationProperties.baseDomainUrl
                    .replace("https://", "")
                    .replace("http://", "")
                    .trimEnd('/'),
            ),
            signaturePublickey = signatureService.publicKeyPem,
        )
    }
}
