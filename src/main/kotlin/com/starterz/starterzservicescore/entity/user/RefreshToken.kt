package com.starterz.starterzservicescore.entity.user

import lombok.Builder
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Builder
@Table(RefreshToken.TABLE_NAME)
data class RefreshToken(
    @Id
    val id: Long?,
    val token: String,
    val platform: Platform,
    val connectionId: String,
    val expiresAt: LocalDateTime,
    @CreatedDate
    val createdAt: LocalDateTime,
) {
    companion object {
        const val TABLE_NAME = "refresh_tokens"
    }
}
