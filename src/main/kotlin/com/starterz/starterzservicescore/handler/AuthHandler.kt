package com.starterz.starterzservicescore.handler

import com.starterz.starterzservicescore.handler.domain.AuthResponse
import com.starterz.starterzservicescore.handler.domain.KakaoAuthRequest
import com.starterz.starterzservicescore.service.KakaoAuthService
import com.starterz.starterzservicescore.service.exception.UserNotFoundException
import com.starterz.starterzservicescore.util.JwtUtil
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class AuthHandler(
    val kakaoAuthService: KakaoAuthService,
    val jwtUtil: JwtUtil,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    fun withKakao(request: ServerRequest): Mono<ServerResponse> {
        return request
            .bodyToMono(KakaoAuthRequest::class.java)
            .map(KakaoAuthRequest::authCode)
            .flatMap(kakaoAuthService::authenticate)
            .map(jwtUtil::createAccessToken)
            .map(::AuthResponse)
            .flatMap(ServerResponse.ok()::bodyValue)
            .onErrorResume(UserNotFoundException::class.java) { ServerResponse.notFound().build() }
            .onErrorResume { ServerResponse.status(HttpStatus.UNAUTHORIZED).build() }
    }

//    fun refreshToken(request: ServerRequest): Mono<ServerResponse> {
//        return request
//            .bodyToMono(CheckTokenRequest::class.java)
//            .map(CheckTokenRequest::token)
//            .doOnNext { logger.info("Checking Token: {}...", it) }
//            .flatMap(jwtUtil::verifyAuthJwt)
//            .flatMap { ServerResponse.ok().build() }
//            .onErrorResume { ServerResponse.status(HttpStatus.UNAUTHORIZED).build() }
//    }
}