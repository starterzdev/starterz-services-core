package com.starterz.starterzservicescore.service.domain

import com.starterz.starterzservicescore.domain.AuthType
import java.time.Duration

data class AuthJwtClaims(
    val userId: Long,
    val authType: AuthType,
    val connectionId: Long,
    val duration: Duration?,
)
