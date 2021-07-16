package com.starterz.starterzservicescore.router

import com.starterz.starterzservicescore.handler.UserHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class UserRouter(private val userHandler: UserHandler) {
    @Bean
    fun routes() = router {
        GET("/users", userHandler::findAllUsers)
        "/users".nest {
            GET("/{id}", userHandler::findUserById)
        }
    }
}