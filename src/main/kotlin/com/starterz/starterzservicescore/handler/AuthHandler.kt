package com.starterz.starterzservicescore.handler

import com.starterz.starterzservicescore.handler.domain.AuthRequest
import com.starterz.starterzservicescore.handler.domain.AuthResponse
import com.starterz.starterzservicescore.handler.domain.AuthType
import com.starterz.starterzservicescore.handler.domain.IntegrationRequest
import com.starterz.starterzservicescore.service.JwtService
import com.starterz.starterzservicescore.service.KakaoAuthService
import com.starterz.starterzservicescore.service.UserService
import com.starterz.starterzservicescore.service.domain.KakaoAccessTokenResponse
import com.starterz.starterzservicescore.service.domain.OAuthProperties
import com.starterz.starterzservicescore.service.exception.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class AuthHandler(
    val kakaoAuthService: KakaoAuthService,
    val jwtService: JwtService,
    val userService: UserService,
) {
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
        val integrationRequestMono: Mono<IntegrationRequest> = request.bodyToMono(IntegrationRequest::class.java).cache()
        val kakaoAccessTokenResponseMono: Mono<KakaoAccessTokenResponse> = integrationRequestMono
            .flatMap {
                when(it.authType) {
                    AuthType.KAKAO -> kakaoAuthService.verifyWithServer(it.oAuthToken)
                    else -> Mono.error(Throwable("Unsupported auth type: " + it.authType))
                }
            }
            .cache()
        return kakaoAccessTokenResponseMono
            .zipWith(integrationRequestMono.map(IntegrationRequest::email))
            .flatMap { userService.updateKakaoConnectionId(it.t2, it.t1.id) }
            .zipWith(kakaoAccessTokenResponseMono)
            .map {
                val duration = Duration.ofSeconds(it.t2.expiresIn.toLong() / 2)
                OAuthProperties(it.t1.id!!, AuthType.KAKAO, it.t1.kakaoConnectionId!!, duration)
            }
            .flatMap(jwtService::generateAuthJwt)
            .map(::AuthResponse)
            .flatMap(ServerResponse.ok()::bodyValue)
    }
}