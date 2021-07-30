package com.starterz.starterzservicescore.handler

import com.starterz.starterzservicescore.handler.domain.AuthRequest
import com.starterz.starterzservicescore.handler.domain.AuthResponse
import com.starterz.starterzservicescore.handler.domain.AuthType
import com.starterz.starterzservicescore.handler.domain.IntegrationRequest
import com.starterz.starterzservicescore.service.JwtService
import com.starterz.starterzservicescore.service.KakaoAuthService
import com.starterz.starterzservicescore.service.UserService
import com.starterz.starterzservicescore.service.domain.KakaoAccessTokenResponse
import com.starterz.starterzservicescore.service.domain.VerificationPayload
import com.starterz.starterzservicescore.service.exception.UnsupportedAuthTypeException
import com.starterz.starterzservicescore.service.exception.UserNotFoundException
import com.starterz.starterzservicescore.service.exception.UserUpdateFailedException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class AuthHandler(
    val kakaoAuthService: KakaoAuthService,
    val jwtService: JwtService,
    val userService: UserService,
    @Value("\${server.port}") val serverPort: String,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    fun authenticate(request: ServerRequest): Mono<ServerResponse> {
        return request
            .bodyToMono(AuthRequest::class.java)
            .flatMap {
                when(it.authType) {
                    AuthType.KAKAO -> kakaoAuthService.authenticate(it.oAuthToken)
                    else -> Mono.error(Throwable("Unsupported auth type: " + it.authType))
                }
            }
            .flatMap(jwtService::generateAuthJwt)
            .map(::AuthResponse)
            .flatMap(ServerResponse.ok()::bodyValue)
            .onErrorResume(UserNotFoundException::class.java) { ServerResponse.notFound().build() }
            .onErrorResume { ServerResponse.status(HttpStatus.UNAUTHORIZED).build() }
    }

    fun integrate(request: ServerRequest): Mono<ServerResponse> {
        val integrationRequestMono = request.bodyToMono(IntegrationRequest::class.java).cache()
        return integrationRequestMono
            .flatMap {
                when(it.authType) {
                    AuthType.KAKAO -> kakaoAuthService.verifyWithServer(it.oAuthToken)
                        .map(KakaoAccessTokenResponse::id)
                        .zipWith(integrationRequestMono.map(IntegrationRequest::email).flatMap(userService::getUserByEmail))
                        .map { tuple -> VerificationPayload(userId = tuple.t2.id!!, connectionId = tuple.t1, authType = it.authType) }
                    else -> error(UnsupportedAuthTypeException("AuthType: ${it.authType.name} is not supported"))
                }
            }
            .flatMap(jwtService::generateVerificationJwt)
            .doOnNext { jwt -> logger.info("Verification Link: http://localhost:$serverPort/api/v1/verification/user/$jwt") }
            .then(ServerResponse.ok().build())
            .onErrorResume(UserNotFoundException::class.java) { ServerResponse.notFound().build() }
            .onErrorResume(UserUpdateFailedException::class.java) { ServerResponse.unprocessableEntity().build() }
    }
}