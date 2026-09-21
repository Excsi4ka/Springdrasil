package dev.excsi.springdrasil.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "springdrasil.jwt")
data class JwtConfigurationProperties(

    val secretKey: String,

    val tokenDuration: Duration,

    val refreshTokenDuration: Duration,
)