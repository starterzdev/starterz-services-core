package com.starterz.starterzservicescore.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "auth.header")
data class AuthHeaderConfig(
    val userId: String,
)