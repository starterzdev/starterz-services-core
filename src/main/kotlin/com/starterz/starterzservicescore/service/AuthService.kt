package com.starterz.starterzservicescore.service

import com.starterz.starterzservicescore.service.domain.OAuthProperties
import reactor.core.publisher.Mono

interface AuthService {
    fun authenticate(token: String): Mono<OAuthProperties>
}