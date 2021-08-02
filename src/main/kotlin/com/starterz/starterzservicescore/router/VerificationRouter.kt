package com.starterz.starterzservicescore.router

import com.starterz.starterzservicescore.handler.VerificationHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

@Configuration
class VerificationRouter(private val verificationHandler: VerificationHandler) {
    @Bean
    fun verificationRoutes() = router {
        (accept(MediaType.APPLICATION_JSON) and "/api/v1").nest {
            GET("/verification/user/{token}", verificationHandler::verifyUserIntegration)
        }
    }
}