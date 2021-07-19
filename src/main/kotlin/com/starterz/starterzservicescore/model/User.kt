package com.starterz.starterzservicescore.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("users")
data class User(
    @Id
    val id: Long? = null,
    val firstName: String,
    val lastName: String,
    val email: String,
    val kakaoConnectionId: Long,
    @CreatedDate
    val createdAt: LocalDateTime,
    @LastModifiedDate
    val updatedAt: LocalDateTime,
    val version: Long,
)
