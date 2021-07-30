package com.starterz.starterzservicescore.router

import com.starterz.starterzservicescore.handler.VerificationHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class VerificationRouter(private val verificationHandler: VerificationHandler) {
    @Bean
    fun verificationRoutes() = router {
        GET("/verification/user/{token}", verificationHandler::verifyUserIntegration)
    }
}