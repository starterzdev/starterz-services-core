package com.starterz.starterzservicescore.handler

import com.starterz.starterzservicescore.domain.AuthRequest
import com.starterz.starterzservicescore.domain.AuthResponse
import com.starterz.starterzservicescore.domain.AuthType
import com.starterz.starterzservicescore.service.JwtService
import com.starterz.starterzservicescore.service.KakaoAuthService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class AuthHandler(
    val kakaoAuthService: KakaoAuthService,
    val jwtService: JwtService,
) {
    fun authenticate(request: ServerRequest): Mono<ServerResponse> {
        return request
            .bodyToMono(AuthRequest::class.java)
            .flatMap {
                when(it.authType) {
                    AuthType.KAKAO -> kakaoAuthService.verify(it.token)
                    else -> Mono.error(Throwable("Unsupported auth type: " + it.authType))
                }
            }
            .flatMap(jwtService::generateAuthJwt)
            .map(::AuthResponse)
            .flatMap(ServerResponse.ok()::bodyValue)
            .onErrorResume { ServerResponse.status(HttpStatus.UNAUTHORIZED).build() }
    }
}