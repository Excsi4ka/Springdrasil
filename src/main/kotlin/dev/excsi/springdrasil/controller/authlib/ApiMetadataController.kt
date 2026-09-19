package dev.excsi.springdrasil.controller.authlib

import dev.excsi.springdrasil.configuration.AuthlibConfigurationValues
import dev.excsi.springdrasil.dto.ApiMetadataResponse
import dev.excsi.springdrasil.service.YggdrasilSignatureService
import org.springframework.boot.info.BuildProperties
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping($$"${springdrasil.authlib.prefix}")
class ApiMetadataController(
    val authlibConfigurationValues: AuthlibConfigurationValues,
    val yggdrasilSignatureService: YggdrasilSignatureService,
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
                    "homepage" to authlibConfigurationValues.baseDomainUrl,
                )
            ),
            skinDomains = listOf(
                authlibConfigurationValues.baseDomainUrl
                    .replace("https://", "")
                    .replace("http://", "")
                    .trimEnd('/'),
            ),
            signaturePublickey = yggdrasilSignatureService.publicKeyPem,
        )
    }
}
