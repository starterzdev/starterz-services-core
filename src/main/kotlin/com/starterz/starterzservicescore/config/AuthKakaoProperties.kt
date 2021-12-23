package com.starterz.starterzservicescore.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "auth.kakao")
data class AuthKakaoProperties(
    val apiKey: String,
    val auth: Auth,
    val user: User,
) {

    data class Auth(
        val baseUrl: String,
        val path: Path,
    ) {
        data class Path(
            val requestToken: String,
        )
    }

    data class User(
        val baseUrl: String,
        val path: Path,
    ) {
        data class Path(
            val tokenInfo: String,
            val profile: String,
        )
    }
}