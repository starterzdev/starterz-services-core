package com.starterz.starterzservicescore.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Configuration

@ConstructorBinding
@ConfigurationProperties(prefix = "auth.signing-key")
data class AuthSigningKeyConfig(
    val type: String,
    val filename: String,
    val password: String,
    val alias: String,
)