package com.starterz.starterzservicescore.service

import com.starterz.starterzservicescore.client.KakaoOauthClient
import com.starterz.starterzservicescore.client.domain.KakaoAuthResponse
import com.starterz.starterzservicescore.client.domain.KakaoProfileResponse
import com.starterz.starterzservicescore.client.domain.KakaoTokenInfoResponse
import com.starterz.starterzservicescore.entity.User
import com.starterz.starterzservicescore.entity.user.Platform
import com.starterz.starterzservicescore.entity.user.UserOauth
import com.starterz.starterzservicescore.repository.UserOauthRepository
import com.starterz.starterzservicescore.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class KakaoAuthService(
    private val userRepository: UserRepository,
    private val userOauthRepository: UserOauthRepository,
    private val kakaoOauthClient: KakaoOauthClient,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val platform: Platform = Platform.KAKAO

    fun authenticate(authCode: String): Mono<User> {
        val accessTokenMono = Mono.just(authCode)
            .flatMap(kakaoOauthClient::authenticate)
            .map(KakaoAuthResponse::accessToken)
            .cache()
        val connectionIdMono = accessTokenMono
            .flatMap(kakaoOauthClient::getTokenInfo)
            .map(KakaoTokenInfoResponse::id)
            .cache()
        return connectionIdMono
            .flatMap { userOauthRepository.findByConnectionIdAndPlatform(it, platform) }
            .switchIfEmpty(Mono.defer {
                Mono.zip(accessTokenMono, connectionIdMono).flatMap { createUserWithOauth(it.t1, it.t2) }
            })
            .map(UserOauth::userId)
            .flatMap(userRepository::findById)
            .doOnNext { logger.info("User = $it") }
    }

    private fun createUserWithOauth(accessToken: String, connectionId: String): Mono<UserOauth> {
        return kakaoOauthClient
            .getProfile(accessToken)
            .flatMap { kakaoProfileResponse ->
                val kakaoAccount: KakaoProfileResponse.KakaoAccount = kakaoProfileResponse.kakaoAccount
                val userMono = Mono.just(
                    User(
                        email = kakaoAccount.email,
                        name = kakaoAccount.name,
                        nickname = kakaoAccount.profile.nickname,
                        roles = listOf(User.Role.COMMON),
                    )
                )
                userMono
                    .mapNotNull(User::id)
                    .map { userId ->
                        UserOauth(
                            userId = userId!!,
                            connectionId = connectionId,
                            platform = platform,
                            connectedAt = LocalDateTime.now(),
                        )
                    }
                    .flatMap(userOauthRepository::save)
            }
    }
}
