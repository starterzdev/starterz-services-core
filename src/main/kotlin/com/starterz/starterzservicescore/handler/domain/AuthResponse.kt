package com.starterz.starterzservicescore.handler.domain

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude
data class AuthResponse(
    @JsonProperty("token")
    val token: String
)
