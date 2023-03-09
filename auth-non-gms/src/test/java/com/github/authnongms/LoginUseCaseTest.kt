package com.github.authnongms

import android.util.Base64
import com.github.authnongms.data.login.models.AuthTokenResponse
import com.github.authnongms.domain.auth.AuthRepository
import com.github.authnongms.domain.auth.LoginUseCase
import com.github.authnongms.domain.models.DataResponse
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test

internal class LoginUseCaseTest {

    companion object {
        @JvmStatic
        @BeforeClass
        fun setup() {
            mockkStatic(Base64::class)
            every { Base64.encodeToString(any(), any()) } returns "encoded string"
        }
    }

    private val authRepository: AuthRepository = mockk()
    private val loginUseCase = LoginUseCase(authRepository).apply {
        clientId = "clientid"
    }

    // TODO when there is more use case logic, create useful tests.

    @Test
    fun `when given scope and packageName a correct Uri is returned`() {
        val scope = "scope"
        val packageName = "com.package.name"
        val expectedRedirect: String = LoginUseCase.REDIRECT_FORMAT.format(packageName)

        val expectedResult = "www.link.com/path?scopes=$scope&redirect=$expectedRedirect"
        every {
            authRepository.buildLoginUrl(
                scopes = any(),
                clientId = any(),
                codeChallenge = any(),
                redirectUri = any()
            )
        } returns expectedResult

        val result: String = loginUseCase.getLoginUrl(scope, packageName)

        assertTrue(result.contains(scope))
        assertTrue(result.contains(expectedRedirect))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when given auth code and package name an AuthTokenResponse is returned`() = runTest {
        val authCode = "auth code"
        val packageName = "com.package.name"
        val mockedResponse: AuthTokenResponse = mockk()
        val expectedResult = DataResponse(mockedResponse)

        coEvery {
            authRepository.requestTokens(
                clientId = any(),
                authCode = any(),
                redirectUri = any(),
                codeVerifier = any(),
            )
        } returns expectedResult

        loginUseCase.requestTokens(authCode, packageName)

        assertTrue(expectedResult.isSuccessful)
    }
}
