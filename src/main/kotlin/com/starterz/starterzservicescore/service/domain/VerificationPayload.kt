package com.starterz.starterzservicescore.service.domain

import com.starterz.starterzservicescore.handler.domain.AuthType

data class VerificationPayload(
    val userId: Long,
    val authType: AuthType,
    val connectionId: String,
)