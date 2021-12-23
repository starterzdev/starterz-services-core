package com.starterz.starterzservicescore.repository

import com.starterz.starterzservicescore.entity.user.Platform
import com.starterz.starterzservicescore.entity.user.UserOauth
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserOauthRepository: R2dbcRepository<UserOauth, Long> {
    fun findByConnectionIdAndPlatform(connectionId: String, platform: Platform): Mono<UserOauth>
    fun findByUserId(userId: Long): Mono<UserOauth>
}