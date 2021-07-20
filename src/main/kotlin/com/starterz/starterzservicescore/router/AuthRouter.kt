package com.starterz.starterzservicescore.router

import com.starterz.starterzservicescore.handler.AuthHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class AuthRouter(private val authHandler: AuthHandler) {
    @Bean
    fun authRoutes() = router {
        POST("/auth", authHandler::authenticate)
        POST("/auth/integrate", authHandler::integrate)
    }
}