package dev.excsi.springdrasil.dto

data class JoinSessionData(
    val serverId: String,
    val accessToken: String,
    val ipAddress: String,
)

data class ClientJoinRequest(
    val accessToken: String,
    // In this case the selectedProfile is just an unhyphenated uuid
    val selectedProfile: String,
    val serverId: String,
)