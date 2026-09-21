package dev.excsi.springdrasil.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "springdrasil.authlib")
data class AuthlibConfigurationProperties(

    val sessionTokenTimeout: Duration,

    val maxTokensPerUserInRotation: Int,

    val maxProfilesPerRequest: Int,

    val baseDomainUrl: String,

    val yggdrasilSignaturePrivateKey: String,

    val yggdrasilSignaturePublicKey: String
)