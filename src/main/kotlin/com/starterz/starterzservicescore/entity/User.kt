package com.starterz.starterzservicescore.entity

import lombok.Builder
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Builder
@Table(User.TABLE_NAME)
data class User(
    @Id
    val id: Long? = null,
    val nickname: String,
    val name: String,
    val email: String,
    val roles: List<Role>? = null,
    @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,
    @Version
    val version: Long? = null,
) {
    companion object {
        const val TABLE_NAME = "users"
    }
    enum class Role {
        COMMON, OWNER, ADMIN
    }
}
