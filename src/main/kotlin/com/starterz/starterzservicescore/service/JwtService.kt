package com.starterz.starterzservicescore.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.starterz.starterzservicescore.handler.domain.AuthType
import com.starterz.starterzservicescore.service.domain.OAuthProperties
import com.starterz.starterzservicescore.service.domain.VerificationPayload
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
final class JwtService(
    @Value("\${service.jwt.keystore.type}") private val keystoreType: String,
    @Value("\${service.jwt.keystore.filename}") private val keystoreFilename: String,
    @Value("\${service.jwt.keystore.password}") private val keystorePassword: String,
    @Value("\${service.jwt.auth.signing-key.id}") private val signingKeyId: String,
    @Value("\${service.jwt.auth.signing-key.password}") private val signingKeyPassword: String,
    @Value("\${service.jwt.auth.issuer}") private val issuer: String,
    @Value("\${service.jwt.auth.audience}") private val audience: String,
    @Value("\${service.jwt.auth.duration}") private val defaultAuthDuration: String,
    @Value("\${service.jwt.verification.duration}") private val defaultVerificationDuration: Duration,
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val keyStore: KeyStore = KeyStore.getInstance(keystoreType)
    private val signingPk: ECPublicKey
    private val signingSk: ECPrivateKey

    companion object {
        const val CLAIM_NAME_AUTH_TYPE = "atype"
        const val CLAIM_NAME_CONNECTION_ID = "cid"
    }

    init {
        val keyStoreStream = ClassPathResource(keystoreFilename).inputStream
        keyStore.load(keyStoreStream, keystorePassword.toCharArray())
        signingPk = keyStore.getCertificate(signingKeyId).publicKey as ECPublicKey
        signingSk = keyStore.getKey(signingKeyId, signingKeyPassword.toCharArray()) as ECPrivateKey
    }

    fun generateAuthJwt(OAuthProperties: OAuthProperties): Mono<String> {
        return Mono.fromCallable {
            val now = Instant.now()
            val duration = OAuthProperties.duration ?: Duration.parse(defaultAuthDuration)
            JWT.create()
                .withKeyId(signingKeyId)
                .withSubject(OAuthProperties.userId.toString())
                .withIssuer(issuer)
                .withAudience(audience)
                .withIssuedAt(Date.from(now))
                .withNotBefore(Date.from(now))
                .withExpiresAt(Date.from(now.plus(duration)))
                .withClaim(CLAIM_NAME_AUTH_TYPE, OAuthProperties.connectionId)
                .withClaim(CLAIM_NAME_CONNECTION_ID, OAuthProperties.authType.name)
                .sign(Algorithm.ECDSA256(signingPk, signingSk))
        }
    }

    fun generateVerificationJwt(verificationPayload: VerificationPayload): Mono<String> {
        return Mono.fromCallable {
            val now = Instant.now()
            JWT.create()
                .withKeyId(signingKeyId)
                .withSubject(verificationPayload.userId.toString())
                .withIssuer(issuer)
                .withAudience(audience)
                .withIssuedAt(Date.from(now))
                .withNotBefore(Date.from(now))
                .withExpiresAt(Date.from(now.plus(defaultVerificationDuration)))
                .withClaim(CLAIM_NAME_AUTH_TYPE, verificationPayload.authType.name)
                .withClaim(CLAIM_NAME_CONNECTION_ID, verificationPayload.connectionId)
                .sign(Algorithm.ECDSA256(signingPk, signingSk))
        }
    }

    fun verifyAuthJwt(token: String): Mono<Long> {
        return Mono.just(JWT.decode(token).keyId)
            .flatMap(::generateAlgorithm)
            .map { algorithm ->
                JWT.require(algorithm)
                    .withIssuer(issuer)
                    .withAudience(audience)
                    .withClaimPresence(CLAIM_NAME_AUTH_TYPE)
                    .withClaimPresence(CLAIM_NAME_CONNECTION_ID)
                    .build()
                    .verify(token)
                    .subject
                    .toLong()
            }
    }

    fun verifyVerificationJwt(token: String): Mono<VerificationPayload> {
        return Mono.just(JWT.decode(token).keyId)
            .flatMap(::generateAlgorithm)
            .map { algorithm ->
                JWT.require(algorithm)
                    .withIssuer(issuer)
                    .withAudience(audience)
                    .withClaimPresence(CLAIM_NAME_AUTH_TYPE)
                    .withClaimPresence(CLAIM_NAME_CONNECTION_ID)
                    .build()
                    .verify(token)
            }
            .map { decodedJwt ->
                val userId = decodedJwt.subject.toLong()
                val connectionId = decodedJwt.getClaim(CLAIM_NAME_CONNECTION_ID).asString()
                val authType = AuthType.valueOf(decodedJwt.getClaim(CLAIM_NAME_AUTH_TYPE).asString())
                VerificationPayload(userId = userId, connectionId = connectionId, authType = authType)
            }
    }

    private fun generateAlgorithm(keyId: String): Mono<Algorithm> {
        val pk = keyStore.getCertificate(keyId).publicKey
        val sk = keyStore.getKey(keyId, signingKeyPassword.toCharArray())
        return Mono.just(Algorithm.ECDSA256(pk as ECPublicKey, sk as ECPrivateKey))
    }
}