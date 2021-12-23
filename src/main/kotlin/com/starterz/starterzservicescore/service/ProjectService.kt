package com.starterz.starterzservicescore.service

import com.starterz.starterzservicescore.entity.Project
import com.starterz.starterzservicescore.repository.ProjectRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ProjectService(
    private val projectRepository: ProjectRepository
) {
    fun getAllProjects(): Flux<Project> {
        return projectRepository.findAll()
    }
}