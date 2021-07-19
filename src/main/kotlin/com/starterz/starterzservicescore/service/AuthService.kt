package com.starterz.starterzservicescore.service

import com.starterz.starterzservicescore.service.domain.AuthJwtClaims
import reactor.core.publisher.Mono

interface AuthService {
    fun verify(token: String): Mono<AuthJwtClaims>
}