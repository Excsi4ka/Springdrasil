package dev.excsi.springdrasil.dto

import com.fasterxml.jackson.annotation.JsonInclude
import dev.excsi.springdrasil.model.Role
import dev.excsi.springdrasil.model.Status
import java.util.UUID

data class NameAvailableResponse(
    val name: String,
    val available: Boolean
)

data class WebLoginRequest(
    val email: String,
    val password: String
)

data class LoginResult(
    val email: String,
    val profileName: String,
    val jwtToken: String,
    val jwtRefreshToken: String
)

data class LoginResponse(
    val email: String,
    val profileName: String,
    val jwtToken: String,
)

data class JwtRefreshResult(
    val jwtToken: String,
    val jwtRefreshToken: String
)

data class JwtRefreshResponse(
    val jwtToken: String,
)

data class EmailVerification(
    val email: String,
    val verificationCode: String
)

data class RegisterUserRequest(
    val email: String,
    val password: String,
    val profileName: String
)

data class RegisterUserResponse(
    val email: String,
    val profileName: String,
    val creationDate: String,
)

data class UserData(
    val email: String,
    val profileName: String,
    val userId: UUID,
    val gameProfileUUID: UUID,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val creationDate: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val status: Status? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val role: Role? = null,
)

data class CsrfTokenResponse(
    val csrfToken: String,
)