package com.omh.android.auth.box.data.login.models

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
class UserProfileResponse (
    @JsonProperty("id")
    val id: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("login")
    val email: String,
    @JsonProperty("avatar_url")
    val picture: String,
    @JsonProperty("notification_email")
    val notificationEmail: String?
)
