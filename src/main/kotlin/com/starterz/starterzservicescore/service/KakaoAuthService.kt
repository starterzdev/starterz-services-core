package com.starterz.starterzservicescore.service

import com.starterz.starterzservicescore.domain.AuthType
import com.starterz.starterzservicescore.repository.UserRepository
import com.starterz.starterzservicescore.service.domain.AuthJwtClaims
import com.starterz.starterzservicescore.service.domain.KakaoAccessTokenResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.Duration

@Service
class KakaoAuthService(
    private val userRepository: UserRepository,
): AuthService {
    companion object {
        const val KAKAO_BASE_URL = "https://kapi.kakao.com/"
        const val ACCESS_TOKEN_INFO_ENDPOINT = "v1/user/access_token_info"
    }
    private val logger = LoggerFactory.getLogger(javaClass)
    private val webClient = WebClient.create(KAKAO_BASE_URL)

    override fun verify(token: String): Mono<AuthJwtClaims> {
        val kakaoAccessTokenResponseMono = webClient
            .get()
            .uri(ACCESS_TOKEN_INFO_ENDPOINT)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=utf-8")
            .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
            .retrieve()
            .onStatus(HttpStatus::isError) { Mono.error(Throwable("Failed to verify token")) }
            .bodyToMono(KakaoAccessTokenResponse::class.java)
            .doOnNext { logger.info("Successfully retrieved Kakao data: {}", it)}
            .cache()
        return kakaoAccessTokenResponseMono
            .map(KakaoAccessTokenResponse::id)
            .flatMap(userRepository::findByKakaoConnectionId)
            .switchIfEmpty(Mono.defer { Mono.error(Throwable("User Not Found")) })
            .doOnNext { logger.info("Successfully found user with Kakao connection: {}", it)}
            .zipWith(kakaoAccessTokenResponseMono)
            .map {
                val duration = Duration.ofSeconds(it.t2.expiresIn.toLong() / 2)
                AuthJwtClaims(it.t1.id!!, AuthType.KAKAO, it.t1.kakaoConnectionId, duration)
            }
    }
}