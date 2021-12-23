package com.starterz.starterzservicescore.client

import com.starterz.starterzservicescore.client.domain.KakaoAuthResponse
import com.starterz.starterzservicescore.client.domain.KakaoProfileResponse
import com.starterz.starterzservicescore.client.domain.KakaoTokenInfoResponse
import com.starterz.starterzservicescore.config.AuthKakaoProperties
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class KakaoOauthClient(
    private val authKakaoProperties: AuthKakaoProperties,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val authClient: WebClient = WebClient.builder().baseUrl(authKakaoProperties.auth.baseUrl).build()
    private val userClient: WebClient = WebClient.builder().baseUrl(authKakaoProperties.user.baseUrl).build()

    fun authenticate(authCode: String): Mono<KakaoAuthResponse> {
        return authClient
            .post()
            .uri {
                it
                    .path(authKakaoProperties.auth.path.requestToken)
                    .queryParam("grant_type", "authorization_code")
                    .queryParam("client_id", authKakaoProperties.apiKey)
                    .queryParam("redirect_uri", "kakao73d19306c0fb4b4a496c0377eac5768a://oauth")
                    .queryParam("code", authCode)
                    .build()
            }
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .retrieve()
            .onStatus(HttpStatus::isError) {
                Mono.error(RuntimeException("Received error from Kakao Auth Server"))
            }
            .bodyToMono(KakaoAuthResponse::class.java)
            .doOnNext { logger.info("Received auth response from Kakao Auth server = $it") }
    }

    fun getTokenInfo(accessToken: String): Mono<KakaoTokenInfoResponse> {
        return userClient
            .get()
            .uri(authKakaoProperties.user.path.tokenInfo)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .retrieve()
            .onStatus(HttpStatus::isError) {
                Mono.error(RuntimeException("Received get token info error from Kakao User Server = $it"))
            }
            .bodyToMono(KakaoTokenInfoResponse::class.java)
            .doOnNext { logger.info("Received token info response from Kakao User server = $it") }
    }

    fun getProfile(accessToken: String): Mono<KakaoProfileResponse> {
        return userClient
            .get()
            .uri(authKakaoProperties.user.path.profile)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .retrieve()
            .onStatus(HttpStatus::isError) {
                Mono.error(RuntimeException("Received get profile error from Kakao User Server = $it"))
            }
            .bodyToMono(KakaoProfileResponse::class.java)
            .doOnNext { logger.info("Received get profile response from Kakao User server = $it") }
    }
}