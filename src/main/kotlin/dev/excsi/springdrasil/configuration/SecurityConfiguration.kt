package dev.excsi.springdrasil.configuration

import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import dev.excsi.springdrasil.component.ConditionalOnWebEnabled
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.web.SecurityFilterChain
import javax.crypto.spec.SecretKeySpec

@Configuration
@EnableWebSecurity
class SecurityConfiguration(

    @Value($$"${springdrasil.authlib.prefix}")
    val authLibPrefix: String,

    @Value($$"${springdrasil.jwt.secret-key}")
    val jwtSecretKey: String

) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests {
                val prefix = authLibPrefix.trim('/')
                it.requestMatchers(
                    "$prefix/**",
                ).permitAll()
            }
            .csrf {
                it.disable()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .oauth2ResourceServer {
                it.jwt(Customizer.withDefaults())
            }
            .build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    // AuthenticationManager might not always be injectable, supposedly, so just in case
    @Bean
    @ConditionalOnWebEnabled
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager? {
        return config.getAuthenticationManager()
    }

    @Bean
    @ConditionalOnWebEnabled
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withSecretKey(getSecretKeySpec()).build()
    }

    @Bean
    @ConditionalOnWebEnabled
    fun jwtEncoder(): JwtEncoder {
        val jwks: JWKSource<SecurityContext> = ImmutableSecret(getSecretKeySpec())
        return NimbusJwtEncoder(jwks)
    }

    private fun getSecretKeySpec(): SecretKeySpec {
        return SecretKeySpec(jwtSecretKey.toByteArray(), "HmacSHA256")
    }
}
