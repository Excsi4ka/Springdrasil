package dev.excsi.springdrasil.service

import dev.excsi.springdrasil.dto.AuthRequest
import dev.excsi.springdrasil.dto.AuthResponse
import dev.excsi.springdrasil.dto.ProfileDto
import dev.excsi.springdrasil.dto.UserDto
import dev.excsi.springdrasil.exception.YggdrasilException
import dev.excsi.springdrasil.model.Profile
import dev.excsi.springdrasil.unhyphenatedString
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class YggdrasilAuthService(
    val userService: UserService,
    val sessionTokenService: SessionTokenService,
    val passwordEncoder: PasswordEncoder,
) {

    @Transactional
    fun authenticate(authRequest: AuthRequest): AuthResponse {
        val user = userService.findByUsername(authRequest.username)
            ?: throw YggdrasilException(HttpStatus.FORBIDDEN, "ForbiddenOperationException", "Invalid credentials. Invalid username or password")

        if (!passwordEncoder.matches(authRequest.password, user.passwordHash)) {
            throw YggdrasilException(HttpStatus.FORBIDDEN, "ForbiddenOperationException", "Invalid credentials. Invalid username or password")
        }

        val sessionToken = sessionTokenService.issueToken(
            authRequest.clientToken,
            user.profile
        )

        val profile = serializeProfile(user.profile)

        val userInfo: UserDto? = if (authRequest.requestUser) UserDto(
            id = user.id.unhyphenatedString(),
            properties = emptyList()
        ) else null

        val authResponse = AuthResponse(
            accessToken = sessionToken.accessToken,
            clientToken = sessionToken.clientToken,
            //multiple profiles  per account not supported
            availableProfiles = listOf(profile),
            selectedProfile = profile,
            user = userInfo
        )

        return authResponse
    }

    fun signout() {
        TODO()
    }

    //only works within a transaction
    fun serializeProfile(profile: Profile): ProfileDto {
        return ProfileDto(
            id = profile.id.unhyphenatedString(),
            name = profile.profileUsername,
        )
    }
}