package com.starterz.starterzservicescore.handler.domain

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude
data class AuthRequest(
    @JsonProperty("authType")
    val authType: AuthType,
    @JsonProperty("oAuthToken")
    val oAuthToken: String,
)
