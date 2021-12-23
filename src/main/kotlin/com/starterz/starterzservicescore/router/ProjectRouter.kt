package com.starterz.starterzservicescore.router

import com.starterz.starterzservicescore.handler.ProjectHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class ProjectRouter(
    private val projectHandler: ProjectHandler,
) {
    @Bean
    fun projectRoutes(): RouterFunction<ServerResponse> = router {
        GET("projects", projectHandler::getAllProjects)
    }
}