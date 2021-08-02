package com.starterz.starterzservicescore.handler

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class WebViewHandler {
    fun renderVerificationOk(request: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok().contentType(MediaType.TEXT_HTML).render("verification/complete")
    }

    fun renderVerificationExpired(request: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok().contentType(MediaType.TEXT_HTML).render("verification/expired")
    }

    fun renderVerificationFailed(request: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok().contentType(MediaType.TEXT_HTML).render("verification/failed")
    }
}