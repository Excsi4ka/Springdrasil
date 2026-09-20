package dev.excsi.springdrasil.dto

data class NameAvailableResponse(
    val name: String,
    val available: Boolean
)

data class LoginUserRequest(
    val email: String,
    val password: String
)

data class LoginUserResponse(
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
