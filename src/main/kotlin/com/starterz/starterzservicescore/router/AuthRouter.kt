package com.starterz.starterzservicescore.router

import com.starterz.starterzservicescore.handler.AuthHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

@Configuration
class AuthRouter(private val authHandler: AuthHandler) {
    @Bean
    fun authRoutes() = router {
        (accept(MediaType.APPLICATION_JSON) and "/api/v1").nest {
            POST("/auth/kakao", authHandler::withKakao)
//            POST("/auth/refresh", authHandler::refreshToken)
        }
    }
}