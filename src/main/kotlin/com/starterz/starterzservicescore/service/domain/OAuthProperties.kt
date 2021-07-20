package com.starterz.starterzservicescore.service.domain

import com.starterz.starterzservicescore.handler.domain.AuthType
import java.time.Duration

data class OAuthProperties(
    val userId: Long,
    val authType: AuthType,
    val connectionId: Long,
    val duration: Duration?,
)
