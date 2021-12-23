package com.starterz.starterzservicescore.repository

import com.starterz.starterzservicescore.entity.Project
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface ProjectRepository: R2dbcRepository<Project, Long>