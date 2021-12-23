package com.starterz.starterzservicescore.util

import com.starterz.starterzservicescore.config.AuthClaimConfig
import com.starterz.starterzservicescore.config.AuthSigningKeyConfig
import com.starterz.starterzservicescore.entity.User
import io.jsonwebtoken.Jwts
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.security.Key
import java.security.KeyStore
import java.time.Instant
import java.util.*

@Component
class JwtUtil(
    private val authClaimConfig: AuthClaimConfig,
    authSigningKeyConfig: AuthSigningKeyConfig,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val signingSk: Key

    init {
        val keyStore = KeyStore.getInstance(authSigningKeyConfig.type)
        val fs = ClassLoader.getSystemResourceAsStream(authSigningKeyConfig.filename);
        keyStore.load(fs, authSigningKeyConfig.password.toCharArray())
        signingSk = keyStore.getKey(authSigningKeyConfig.alias, authSigningKeyConfig.password.toCharArray())
    }

    fun createAccessToken(user: User): String {
        val now: Instant = Instant.now()
        logger.info("Creating access token for user = $user")
        return Jwts.builder()
            .setIssuer(authClaimConfig.issuer)
            .setAudience(authClaimConfig.audience)
            .setSubject(user.id.toString())
            .setIssuedAt(Date.from(now))
            .setNotBefore(Date.from(now))
            .setExpiration(Date.from(now.plus(authClaimConfig.duration)))
            .claim("roles", user.roles)
            .signWith(signingSk)
            .compact()
    }
}