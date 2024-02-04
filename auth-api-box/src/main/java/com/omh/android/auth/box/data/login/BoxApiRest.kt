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

package com.omh.android.auth.box.data.login

import com.omh.android.auth.box.data.login.models.AuthTokenResponse
import com.omh.android.auth.box.data.login.models.UserProfileResponse
import com.omh.android.auth.mobileweb.domain.models.ApiResult
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

internal interface BoxApiRest {

    @POST("oauth2/token")
    @FormUrlEncoded
    suspend fun getToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("grant_type") grantType: String = "authorization_code",
    ): ApiResult<AuthTokenResponse>

    @POST("oauth2/token")
    @FormUrlEncoded
    suspend fun refreshToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token",
    ): ApiResult<AuthTokenResponse>

    @POST("oauth2/revoke")
    @FormUrlEncoded
    suspend fun revokeToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("token") token: String,
    ): ApiResult<Unit>

    @GET("2.0/users/me")
    suspend fun getCurrentUserProfile(
        @Header("Authorization") authorization: String,
        @Query("fields") fields: String = "id,name,login,avatar_url,notification_email"
    ): ApiResult<UserProfileResponse>
}
