package com.starterz.starterzservicescore.handler.domain

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.starterz.starterzservicescore.entity.user.Platform

@JsonInclude
data class KakaoAuthRequest(
    @JsonProperty("auth_code")
    val authCode: String,
)
