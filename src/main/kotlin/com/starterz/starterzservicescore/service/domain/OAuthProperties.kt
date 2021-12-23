package com.starterz.starterzservicescore.service.domain

import com.starterz.starterzservicescore.entity.user.Platform
import com.starterz.starterzservicescore.handler.domain.AuthType
import java.time.Duration

data class OAuthProperties(
    val platformUserId: Long,
    val platform: Platform,
    val duration: Duration?,
)
