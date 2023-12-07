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

import android.net.Uri
import com.omh.android.auth.box.data.login.AuthRepositoryImpl
import com.omh.android.auth.box.data.login.models.AuthTokenResponse
import com.omh.android.auth.mobileweb.data.login.datasource.AuthDataSource
import com.omh.android.auth.mobileweb.domain.auth.AuthRepository
import com.omh.android.auth.mobileweb.domain.models.ApiResult
import com.omh.android.auth.mobileweb.domain.models.OAuthTokens
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class AuthRepositoryTest {

    private val boxAuthDataSource = mockk<AuthDataSource<AuthTokenResponse>>() {
        every { storeToken(any(), any()) } returns Unit
    }

    @Test
    fun `GIVEN all parameters, WHEN response successful, THEN tokens stored and returned`() =
        runTest {
            val authRepository = createAuthRepository()
            val mAccessToken = "access token"
            val mRefreshToken = "refresh token"
            val clientId = "client ID"
            val authCode = "auth code"
            val redirectUri = "redirect uri"
            val codeVerifier = "code verifier"

            val authTokenResponse = mockk<AuthTokenResponse>() {
                every { accessToken } returns mAccessToken
                every { refreshToken } returns mRefreshToken
            }
            val expectedResponse: ApiResult<AuthTokenResponse> =
                ApiResult.Success(authTokenResponse)
            val expectedResult = OAuthTokens(
                accessToken = authTokenResponse.accessToken,
                refreshToken = authTokenResponse.refreshToken!!,
                idToken = "N/A",
            )

            coEvery {
                boxAuthDataSource.getToken(
                    clientId = any(),
                    authCode = any(),
                    redirectUri = any(),
                    codeVerifier = any(),
                )
            } returns expectedResponse

            val result: ApiResult<OAuthTokens> = authRepository.requestTokens(
                clientId = clientId,
                authCode = authCode,
                redirectUri = redirectUri,
                codeVerifier = codeVerifier,
            )
            coVerify {
                boxAuthDataSource.storeToken(
                    tokenType = AuthDataSource.ACCESS_TOKEN,
                    token = mAccessToken,
                )
                boxAuthDataSource.storeToken(
                    tokenType = AuthDataSource.REFRESH_TOKEN,
                    token = mRefreshToken,
                )
            }
            assertEquals(result.extractResult(), expectedResult)
        }

    private fun TestScope.createAuthRepository(): AuthRepository {
        val ioDispatcher = UnconfinedTestDispatcher(testScheduler)
        return AuthRepositoryImpl(boxAuthDataSource, ioDispatcher, mockk())
    }

    @Test
    fun `GIVEN all parameters, WHEN build URL requested, THEN login URL returned`() = runTest {
        val authRepository = createAuthRepository()
        val clientId = "client ID"
        val scopes = "scopes"
        val codeChallenge = "code challenge"
        val redirectUri = "redirect uri"
        val expectedUrl = "URL"
        val uri = mockk<Uri>() {
            every { this@mockk.toString() } returns expectedUrl
        }

        every { boxAuthDataSource.buildLoginUrl(any(), any(), any(), any()) } returns uri

        val result = authRepository.buildLoginUrl(scopes, clientId, codeChallenge, redirectUri)
        assertEquals(result, expectedUrl)
    }

    @Test
    fun `GIVEN all parameters, WHEN access token requested, THEN access token returned`() =
        runTest {
            val authRepository = createAuthRepository()
            val accessToken = "access token"

            every { boxAuthDataSource.getToken(any()) } returns accessToken

            val result = authRepository.getAccessToken()
            assertEquals(result, accessToken)
        }

    @Test
    fun `GIVEN client ID, WHEN token refresh requested, THEN access token returned and stored`() =
        runTest {
            val authRepository = createAuthRepository()
            val clientId = "client ID"
            val accessToken = "access token"
            val authTokenResponse = mockk<AuthTokenResponse>() {
                every { this@mockk.accessToken } returns accessToken
            }
            val responseResult = ApiResult.Success(authTokenResponse)

            coEvery { boxAuthDataSource.refreshAccessToken(any()) } returns responseResult

            val result: ApiResult<String> = authRepository.refreshAccessToken(clientId)

            coEvery { boxAuthDataSource.storeToken(AuthDataSource.ACCESS_TOKEN, accessToken) }
            assertEquals(result.extractResult(), accessToken)
        }

    @Test
    fun `GIVEN access token WHEN token revoke requested, THEN token revoked`() = runTest {
        val authRepository = createAuthRepository()
        val accessToken = "access token"
        val clientId = "12345678"

        every { boxAuthDataSource.getToken(any()) } returns accessToken
        coEvery { boxAuthDataSource.revokeToken(any(), any()) } returns ApiResult.Success(Unit)

        val result: ApiResult<Unit> = authRepository.revokeToken(clientId)
        assert(result is ApiResult.Success<*>)
    }

    @Test
    fun `GIVEN no access token WHEN token revoke requested, THEN exception thrown`() = runTest {
        val authRepository = createAuthRepository()
        val clientId = "12345678"

        every { boxAuthDataSource.getToken(any()) } returns null

        val result: ApiResult<Unit> = authRepository.revokeToken(clientId)
        assert(result is ApiResult.Error.RuntimeError)
    }

    @Test
    fun `WHEN clear data requested THEN data cleared`() = runTest {
        val authRepository = createAuthRepository()

        every { boxAuthDataSource.clearData() } returns Unit

        authRepository.clearData()

        verify { boxAuthDataSource.clearData() }
    }
}
