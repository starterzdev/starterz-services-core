package com.starterz.starterzservicescore.service.domain

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude
data class KakaoAccessTokenResponse(
    @JsonProperty("id") val id: String,
    @JsonProperty("expires_in") val expiresIn: Int,
    @JsonProperty("app_id") val appId: Int,
)
