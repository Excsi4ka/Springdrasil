package dev.excsi.springdrasil.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "springdrasil.web")
data class WebConfigurationProperties(

    val frontendEnabled: Boolean,

    val httpsEnabled: Boolean,
)