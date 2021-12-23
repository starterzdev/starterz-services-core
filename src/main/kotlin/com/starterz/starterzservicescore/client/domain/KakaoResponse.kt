package com.starterz.starterzservicescore.client.domain

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import org.springframework.stereotype.Component
import java.io.IOException
import java.time.LocalDateTime

@JsonInclude
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class KakaoAuthResponse(
    val accessToken: String,
    val tokenType: String,
    val refreshToken: String,
    val expiresIn: Long,
    @JsonDeserialize(using = ScopeDeserializer::class)
    @JsonProperty("scope")
    val scopes: List<String>,
    val refreshTokenExpiresIn: Long,
) {
    @Component
    class ScopeDeserializer: StdDeserializer<List<String?>?>(MutableList::class.java) {
        @Throws(IOException::class)
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): List<String> {
            return p.valueAsString.split(" ")
        }
    }
}

@JsonInclude
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class KakaoTokenInfoResponse(
    val id: String,
    val expiresIn: Long,
    val appId: String,
)

@JsonInclude
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class KakaoProfileResponse(
    val id: Long,
    val hasSignedUp: Boolean,
    val connectedAt: LocalDateTime,
    val synchedAt: LocalDateTime,
    val kakaoAccount: KakaoAccount,
) {
    @JsonInclude
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class KakaoAccount(
        val profileNeedsAgreement: Boolean,
        val profileNicknameNeedsAgreement: Boolean,
        val profileImageNeedsAgreement: Boolean,
        val nameNeedsAgreement: Boolean,
        val name: String,
        val emailNeedsAgreement: Boolean,
        val isEmailValid: Boolean,
        val isEmailVerified: Boolean,
        val email: String,
        val ageRangeNeedsAgreement: Boolean,
        val ageRange: String,
        val birthyearNeedsAgreement: Boolean,
        val birthyear: String,
        val birthdayNeedsAgreement: Boolean,
        val birthday: String,
        val birthdayType: String,
        val genderNeedsAgreement: Boolean,
        val gender: String,
        val phoneNumberNeedsAgreement: Boolean,
        val phoneNumber: String,
        val ciNeedsAgreement: Boolean,
        val ci: String,
        val ciAuthenticatedAt: LocalDateTime,
        val profile: Profile,
    )

    @JsonInclude
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class Profile(
        val nickname: String,
        val thumbnailImageUrl: String,
        val profileImageUrl: String,
        val isDefaultImage: Boolean,
    )
}