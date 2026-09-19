package dev.excsi.springdrasil.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "springdrasil.authlib")
data class AuthlibConfigurationValues(

    val sessionTokenTimeout: Duration,

    val maxTokensPerUserInRotation: Int,

    val maxProfilesPerRequest: Int,

    val baseDomainUrl: String,

    val yggdrasilSignaturePrivateKey: String,

    val yggdrasilSignaturePublicKey: String
)