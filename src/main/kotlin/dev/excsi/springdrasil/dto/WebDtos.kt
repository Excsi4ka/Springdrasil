package dev.excsi.springdrasil.dto

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

data class CsrfTokenResponse(
    val csrfToken: String,
)

data class UserDataResponse(
    val email: String,
    val profileName: String
)