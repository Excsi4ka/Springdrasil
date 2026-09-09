package dev.excsi.springdrasil.dto

import com.fasterxml.jackson.annotation.JsonInclude
import dev.excsi.springdrasil.model.TextureType
import java.time.Instant

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
    val availableProfiles: List<ProfileDto>,
    val selectedProfile: ProfileDto,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val user: UserDto? = null
)

data class UserDto(
    val id: String,
    val properties: List<Property>
)

data class Property(
    val name: String,
    val value: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val signature: String? = null,
)

data class ProfileDto(
    val id: String,
    val name: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val properties: List<Property>? = null,
)

data class TextureDto(
    val timestamp: String = Instant.now().toString(),
    val profileId: String,
    val profileName: String,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val textures: Map<TextureType, TextureData>,
)

data class TextureData(
    val url: String,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val metadata: Map<String, String>? = null,
)

data class ValidateRequest(
    val accessToken: String,
    val clientToken: String? = null,
)

data class RefreshRequest(
    val accessToken: String,
    val clientToken: String? = null,
    val requestUser: Boolean = false,
)

data class RefreshResponse(
    val accessToken: String,
    val clientToken: String,
    val selectedProfile: ProfileDto,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val user: UserDto? = null
)
