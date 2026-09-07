package dev.excsi.springdrasil.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "custom")
data class ConfigurationValues(

    val sessionTokenTimeout: Duration,

    val maxTokensPerUserInRotation: Int
)