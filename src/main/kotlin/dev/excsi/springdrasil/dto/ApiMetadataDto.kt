package dev.excsi.springdrasil.dto

data class ApiMetadataDto(
    val meta: Map<String, Any> = emptyMap(),
    val skinDomains: List<String> = emptyList(),
    val signaturePublickey: String,
)
