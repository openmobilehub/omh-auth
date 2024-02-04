/*
 * Copyright 2023 Open Mobile Hub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.omh.android.auth.mslive.data.login

import com.omh.android.auth.mobileweb.domain.models.ApiResult
import com.omh.android.auth.mslive.data.login.models.AuthTokenResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Reference: https://learn.microsoft.com/en-us/entra/identity-platform/v2-protocols-oidc
 */
@Suppress("LongParameterList")
internal interface MsLiveAuthRest {

    @POST("oauth2/v2.0/token")
    @FormUrlEncoded
    suspend fun getToken(
        @Field("client_id") clientId: String,
        @Field("code") code: String,
        @Field("scope") scopes: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("code_verifier") codeVerifier: String,
        @Field("grant_type") grantType: String = "authorization_code",
    ): ApiResult<AuthTokenResponse>

    @POST("oauth2/v2.0/token")
    @FormUrlEncoded
    suspend fun refreshToken(
        @Field("client_id") clientId: String,
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token",
    ): ApiResult<AuthTokenResponse>

    @GET("oauth2/v2.0/logout")
    suspend fun logout(
        @Query("post_logout_redirect_uri") redirectUri: String,
        @Header("Authorization") authorization: String
    ): ApiResult<Unit>
}
