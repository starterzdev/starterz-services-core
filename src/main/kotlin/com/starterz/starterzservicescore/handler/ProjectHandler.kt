package com.starterz.starterzservicescore.handler

import com.starterz.starterzservicescore.handler.domain.IndexProjectResponse
import com.starterz.starterzservicescore.service.ProjectService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class ProjectHandler(
    private val projectService: ProjectService,
) {
    fun getAllProjects(request: ServerRequest): Mono<ServerResponse> {
        return projectService.getAllProjects()
            .map { IndexProjectResponse(id = it.id, name = it.name, description = it.description) }
            .collectList()
            .flatMap { ServerResponse.ok().bodyValue(it) }
    }
}