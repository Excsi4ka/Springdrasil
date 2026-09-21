package dev.excsi.springdrasil.configuration

import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import dev.excsi.springdrasil.component.JwtTokenAuthenticationConverter
import dev.excsi.springdrasil.configuration.properties.AuthlibConfigurationProperties
import dev.excsi.springdrasil.configuration.properties.JwtConfigurationProperties
import dev.excsi.springdrasil.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import javax.crypto.spec.SecretKeySpec

@Configuration
@EnableWebSecurity
class SecurityConfiguration(

    val authlibConfigurationProperties: AuthlibConfigurationProperties,

    val jwtConfigurationProperties: JwtConfigurationProperties,

    val jwtAuthenticationConverter: JwtTokenAuthenticationConverter
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests {
                val prefix = authlibConfigurationProperties.prefix.trim('/')
                it.requestMatchers(
                    "$prefix/**",
                    "/admin",
                    "/admin/login",
                    "/admin/dashboard/**",
                    "/api/auth/login",
                    "/api/auth/refresh",
                ).permitAll()

                it.requestMatchers(
                    "/admin/api/**"
                ).hasRole("ADMIN")
            }
            .csrf {
                it.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                it.requireCsrfProtectionMatcher {
                    request -> HttpMethod.POST.matches(request.method) && request.servletPath.equals("/api/auth/refresh")
                }
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .oauth2ResourceServer {
                it.jwt {
                    jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)
                }
            }
            .build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    // AuthenticationManager might not always be injectable, supposedly, so just in case
    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager? {
        return config.getAuthenticationManager()
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withSecretKey(getSecretKeySpec()).build()
    }

    @Bean
    fun jwtEncoder(): JwtEncoder {
        val jwks: JWKSource<SecurityContext> = ImmutableSecret(getSecretKeySpec())
        return NimbusJwtEncoder(jwks)
    }

    @Bean
    fun userDetailsService(userRepository: UserRepository): UserDetailsService {
        return UserDetailsService {
            userRepository.findByEmail(it) ?: throw UsernameNotFoundException("User not found")
        }
    }

    private fun getSecretKeySpec(): SecretKeySpec {
        return SecretKeySpec(jwtConfigurationProperties.secretKey.toByteArray(), "HmacSHA256")
    }
}
