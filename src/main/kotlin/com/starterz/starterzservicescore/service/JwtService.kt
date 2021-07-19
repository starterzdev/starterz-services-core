package com.starterz.starterzservicescore.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.starterz.starterzservicescore.service.domain.AuthJwtClaims
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.security.KeyStore
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.time.Duration
import java.time.Instant
import java.util.*

@Service
class JwtService(
    @Value("\${service.jwt.keystore.type}") private val keystoreType: String,
    @Value("\${service.jwt.keystore.filename}") private val keystoreFilename: String,
    @Value("\${service.jwt.keystore.password}") private val keystorePassword: String,
    @Value("\${service.jwt.auth.signing-key.id}") private val signingKeyId: String,
    @Value("\${service.jwt.auth.signing-key.password}") private val signingKeyPassword: String,
    @Value("\${service.jwt.auth.issuer}") private val issuer: String,
    @Value("\${service.jwt.auth.audience}") private val audience: String,
    @Value("\${service.jwt.auth.duration}") private val defaultDuration: String,
) {
    private val keyStore: KeyStore
    private val signingPk: ECPublicKey
    private val signingSk: ECPrivateKey

    init {
        keyStore = KeyStore.getInstance(keystoreType)
        val keyStoreStream = ClassPathResource(keystoreFilename).inputStream
        keyStore.load(keyStoreStream, keystorePassword.toCharArray())
        signingPk = keyStore.getCertificate(signingKeyId).publicKey as ECPublicKey
        signingSk = keyStore.getKey(signingKeyId, signingKeyPassword.toCharArray()) as ECPrivateKey
    }

    fun generateAuthJwt(authJwtClaims: AuthJwtClaims): Mono<String> {
        return Mono.fromCallable {
            val now = Instant.now()
            val duration = authJwtClaims.duration ?: Duration.parse(defaultDuration)
            val token = JWT.create()
                .withKeyId(signingKeyId)
                .withSubject(authJwtClaims.userId.toString())
                .withIssuer(issuer)
                .withAudience(audience)
                .withIssuedAt(Date.from(now))
                .withNotBefore(Date.from(now))
                .withExpiresAt(Date.from(now.plus(duration)))
                .withClaim("cid", authJwtClaims.connectionId)
                .withClaim("atyp", authJwtClaims.authType.name)
                .sign(Algorithm.ECDSA256(signingPk, signingSk))
            token
        }
    }

    fun verifyAuthJwt(token: String): Mono<Long> {
        return Mono.just(JWT.decode(token).keyId)
            .flatMap(::generateAlgorithm)
            .map { algorithm ->
                JWT.require(algorithm)
                    .withIssuer(issuer)
                    .withAudience(audience)
                    .withClaimPresence("cid")
                    .withClaimPresence("atyp")
                    .build()
                    .verify(token)
                    .subject
                    .toLong()
            }
    }

    private fun generateAlgorithm(keyId: String): Mono<Algorithm> {
        val pk = keyStore.getCertificate(keyId).publicKey
        val sk = keyStore.getKey(keyId, signingKeyPassword.toCharArray())
        return Mono.just(Algorithm.ECDSA256(pk as ECPublicKey, sk as ECPrivateKey))
    }
}