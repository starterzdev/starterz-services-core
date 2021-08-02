package com.starterz.starterzservicescore.handler.domain

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude
data class CheckTokenRequest(
    @JsonProperty("token")
    val token: String,
)