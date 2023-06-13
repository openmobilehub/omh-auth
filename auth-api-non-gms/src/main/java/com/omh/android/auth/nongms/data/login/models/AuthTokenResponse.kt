package com.omh.android.auth.nongms.data.login.models

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
internal class AuthTokenResponse(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("token_type")
    val tokenType: String,
    @JsonProperty("expires_in")
    val expiresIn: Long,
    @JsonProperty("refresh_token")
    val refreshToken: String?,
    @JsonProperty("id_token")
    val idToken: String
)
