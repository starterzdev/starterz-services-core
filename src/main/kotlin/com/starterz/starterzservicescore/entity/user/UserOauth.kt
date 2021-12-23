package com.starterz.starterzservicescore.entity.user

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(UserOauth.TABLE_NAME)
data class UserOauth(
    @Id
    val id: Long? = null,
    val userId: Long,
    val platform: Platform,
    val connectionId: String,
    val connectedAt: LocalDateTime,
    @CreatedDate
    val createdAt: LocalDateTime? = null,
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null,
    @Version
    val version: Long? = null,
) {
    companion object {
        const val TABLE_NAME = "user_oauths"
    }
}
