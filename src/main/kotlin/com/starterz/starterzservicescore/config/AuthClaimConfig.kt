package com.starterz.starterzservicescore.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.time.Duration

@ConstructorBinding
@ConfigurationProperties(prefix = "auth.claim")
data class AuthClaimConfig(
    val issuer: String,
    val audience: String,
    val duration: Duration,
)
