package dev.excsi.springdrasil.service.web

import dev.excsi.springdrasil.model.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class JwtTokenService(

    val jwtEncoder: JwtEncoder,

    @Value($$"${springdrasil.jwt.token-duration}")
    val accessTokenTimeout: Duration,
) {

    fun issue(user: User): String {
        val issuedAt = Instant.now()
        val claims = JwtClaimsSet.builder()
            .subject(user.id.toString())
            .claim("email", user.email)
            .claim("role", user.role.name)
            .issuedAt(issuedAt)
            .expiresAt(issuedAt.plus(accessTokenTimeout))
            .build()

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }
}
