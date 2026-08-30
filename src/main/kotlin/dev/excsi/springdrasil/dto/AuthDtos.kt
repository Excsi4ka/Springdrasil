package dev.excsi.springdrasil.dto

import com.fasterxml.jackson.annotation.JsonInclude

data class AuthRequest(
    val username: String,
    val password: String,
    val clientToken: String? = null,
    val requestUser: Boolean = false,
    val agent: Agent? = Agent()
)

data class Agent(
    val name: String = "Minecraft",
    val version: Int = 1
)

data class AuthResponse(
    val accessToken: String,
    val clientToken: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val user: User? = null

)

data class User(
    val id: String,
    val properties: List<Property>
)

data class Property(
    val name: String,
    val value: String
)
