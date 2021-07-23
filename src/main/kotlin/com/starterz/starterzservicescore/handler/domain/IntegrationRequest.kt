package com.starterz.starterzservicescore.handler.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class IntegrationRequest(
    @JsonProperty("email")
    val email: String,
    @JsonProperty("authType")
    val authType: AuthType,
    @JsonProperty("oAuthToken")
    val oAuthToken: String,
)