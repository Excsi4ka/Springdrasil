package dev.excsi.springdrasil.component

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.stereotype.Component

@Component
class JwtTokenAuthenticationConverter : JwtAuthenticationConverter() {

    init {
        val roleAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
        roleAuthoritiesConverter.setAuthoritiesClaimName("role")
        roleAuthoritiesConverter.setAuthorityPrefix("ROLE_")

        setJwtGrantedAuthoritiesConverter(roleAuthoritiesConverter)
    }
}
