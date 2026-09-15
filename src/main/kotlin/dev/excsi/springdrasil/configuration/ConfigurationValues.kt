package dev.excsi.springdrasil.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "springdrasil.application")
data class ConfigurationValues(

    val sessionTokenTimeout: Duration,

    val maxTokensPerUserInRotation: Int,

    val maxProfilesPerRequest: Int,

    val textureBaseUrl: String,

    val yggdrasilSignaturePrivateKey: String,

    val yggdrasilSignaturePublicKey: String
)