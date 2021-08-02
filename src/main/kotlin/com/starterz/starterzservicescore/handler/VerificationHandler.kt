package com.starterz.starterzservicescore.handler

import com.starterz.starterzservicescore.service.JwtService
import com.starterz.starterzservicescore.service.UserService
import com.starterz.starterzservicescore.service.exception.AlreadyIntegratedException
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.net.URI

@Component
class VerificationHandler(
    private val jwtService: JwtService,
    private val userService: UserService,
) {
    fun verifyUserIntegration(request: ServerRequest): Mono<ServerResponse> {
        return Mono.justOrEmpty(request.pathVariable("token"))
            .flatMap(jwtService::verifyVerificationJwt)
            .flatMap { verificationPayload ->
                userService.updateKakaoConnectionId(verificationPayload.userId, verificationPayload.connectionId) }
            .flatMap { user ->
                val models = mapOf("verifiedUser" to user)
                ServerResponse.temporaryRedirect(URI.create("/web/verification/complete")).build()
            }
            .onErrorResume(AlreadyIntegratedException::class.java) { buildExpiredPageResponse() }
            .onErrorResume { buildFailedPageResponse() }
    }
    private fun buildExpiredPageResponse() = ServerResponse.temporaryRedirect(URI.create("/web/verification/expired")).build()
//    private fun buildExpiredPageResponse() = ServerResponse.ok().contentType(MediaType.TEXT_HTML).render("verification/expired")
//    private fun buildFailedPageResponse() = ServerResponse.ok().contentType(MediaType.TEXT_HTML).render("verification/failed")
    private fun buildFailedPageResponse() = ServerResponse.temporaryRedirect(URI.create("/web/verification/failed")).build()
}