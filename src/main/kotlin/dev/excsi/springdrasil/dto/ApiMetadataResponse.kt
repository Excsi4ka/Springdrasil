package dev.excsi.springdrasil.dto

data class ApiMetadataResponse(
    val meta: Map<String, Any> = emptyMap(),
    val skinDomains: List<String> = emptyList(),
    val signaturePublickey: String,
)
