package dev.excsi.springdrasil.service.yggdrasil

import dev.excsi.springdrasil.dto.AuthRequest
import dev.excsi.springdrasil.dto.AuthResponse
import dev.excsi.springdrasil.dto.SignoutRequest
import dev.excsi.springdrasil.dto.UserDto
import dev.excsi.springdrasil.exception.YggdrasilException
import dev.excsi.springdrasil.service.profile.ProfileSerializationService
import dev.excsi.springdrasil.service.user.UserService
import dev.excsi.springdrasil.unhyphenatedString
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class YggdrasilAuthService(
    val userService: UserService,
    val sessionTokenService: SessionTokenService,
    val profileSerializationService: ProfileSerializationService,
    val passwordEncoder: PasswordEncoder,
) {

    @Transactional
    fun authenticate(authRequest: AuthRequest): AuthResponse {
        val requestUsername = authRequest.username
        val user = if (requestUsername.contains('@')) {
            userService.findByEmail(requestUsername)
        } else {
            userService.findByProfileName(requestUsername)
        } ?: throw YggdrasilException(
            HttpStatus.FORBIDDEN,
            "ForbiddenOperationException",
            "Invalid credentials. Invalid username or password"
        )

        if (!passwordEncoder.matches(authRequest.password, user.passwordHash)) {
            throw YggdrasilException(
                HttpStatus.FORBIDDEN,
                "ForbiddenOperationException",
                "Invalid credentials. Invalid username or password"
            )
        }

        val sessionToken = sessionTokenService.issueToken(
            authRequest.clientToken,
            user.profile
        )

        val profile = profileSerializationService.serializeProfile(user.profile)

        val userInfo = if (authRequest.requestUser) UserDto(
            user.id.unhyphenatedString()
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

    @Transactional
    fun signout(signoutRequest: SignoutRequest) {
        val requestUsername = signoutRequest.username
        val user = if (requestUsername.contains('@')) {
            userService.findByEmail(requestUsername)
        } else {
            userService.findByProfileName(requestUsername)
        } ?: throw YggdrasilException(
            HttpStatus.FORBIDDEN,
            "ForbiddenOperationException",
            "Invalid credentials. Invalid username or password"
        )

        if (!passwordEncoder.matches(signoutRequest.password, user.passwordHash)) {
            throw YggdrasilException(
                HttpStatus.FORBIDDEN,
                "ForbiddenOperationException",
                "Invalid credentials. Invalid username or password"
            )
        }

        sessionTokenService.invalidateAllTokensForProfile(user.profile)
    }
}