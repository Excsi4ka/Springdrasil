package dev.excsi.springdrasil.dto

import com.fasterxml.jackson.annotation.JsonInclude

data class YggdrasilErrorResponse(
    val error: String,
    val errorMessage: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val cause: String? = null
)