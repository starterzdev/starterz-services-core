package com.starterz.starterzservicescore.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(Project.TABLE_NAME)
data class Project(
    val id: Long,
    val userId: Long,
    val name: String,
    val description: String,
    @CreatedDate
    val createdAt: LocalDateTime,
    @LastModifiedDate
    val updatedAt: LocalDateTime,
    @Version
    val version: Long,
) {
    companion object {
        const val TABLE_NAME = "projects"
    }
}
