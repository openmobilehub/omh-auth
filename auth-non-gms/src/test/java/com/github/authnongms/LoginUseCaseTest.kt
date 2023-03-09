package com.github.authnongms

import android.util.Base64
import com.github.authnongms.data.login.models.AuthTokenResponse
import com.github.authnongms.domain.models.DataResponse
import com.github.authnongms.domain.auth.AuthRepository
import com.github.authnongms.domain.auth.LoginUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class LoginUseCaseTest {

    companion object {
        @JvmStatic
        @BeforeClass
        fun setup() {
            mockStatic(Base64::class.java)
                .`when`<String> { Base64.encodeToString(any(), anyInt()) }
                .doReturn("encoded string")
        }
    }

    private val authRepository: AuthRepository = mock()
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
        whenever(
            authRepository.buildLoginUrl(
                scopes = anyString(),
                clientId = anyString(),
                codeChallenge = anyString(),
                redirectUri = anyString(),
            )
        ).doReturn(expectedResult)

        val result: String = loginUseCase.getLoginUrl(scope, packageName)

        assertTrue(result.contains(scope))
        assertTrue(result.contains(expectedRedirect))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when given auth code and package name an AuthTokenResponse is returned`() = runTest {
        val authCode = "auth code"
        val packageName = "com.package.name"
        val mockedResponse: AuthTokenResponse = mock()
        val expectedResult = DataResponse(mockedResponse)

        whenever(
            authRepository.requestTokens(
                clientId = anyString(),
                authCode = anyString(),
                redirectUri = anyString(),
                codeVerifier = anyString(),
            )
        ).doReturn(expectedResult)

        loginUseCase.requestTokens(authCode, packageName)

        assertTrue(expectedResult.isSuccessful)
    }
}
