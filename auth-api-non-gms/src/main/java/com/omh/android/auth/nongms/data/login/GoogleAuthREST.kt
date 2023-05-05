package com.omh.android.auth.nongms.data.login

import com.google.android.gms.tasks.Task
import com.google.common.util.concurrent.ListenableFuture
import com.omh.android.auth.nongms.data.login.models.AuthTokenResponse
import com.omh.android.auth.nongms.domain.models.ApiResult
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

internal interface GoogleAuthREST {

    @POST("/token")
    @FormUrlEncoded
    suspend fun getToken(
        @Field("client_id") clientId: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("code_verifier") codeVerifier: String,
        @Field("grant_type") grantType: String = "authorization_code",
    ): ApiResult<AuthTokenResponse>

    @POST("/token")
    @FormUrlEncoded
    suspend fun refreshToken(
        @Field("client_id") clientId: String,
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token"
    ): ApiResult<AuthTokenResponse>

    @POST("/revoke")
    @FormUrlEncoded
    fun revokeToken(
        @Field("token") token: String
    ): Task<Unit>
}
