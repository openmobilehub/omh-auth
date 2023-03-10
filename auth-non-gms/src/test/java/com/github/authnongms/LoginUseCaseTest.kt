package com.github.authnongms

import com.github.authnongms.domain.auth.AuthRepository
import com.github.authnongms.domain.auth.LoginUseCase
import com.github.authnongms.domain.models.OAuthTokens
import com.github.authnongms.domain.utils.Pkce
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

internal class LoginUseCaseTest {

    private val authRepository: AuthRepository = mockk()
    private val pkce: Pkce = mockk() {
        every { codeVerifier } returns "codeverifier"
        every { generateCodeChallenge() } returns "codechallenge"
    }
    private val loginUseCase = LoginUseCase(authRepository, pkce).apply {
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
        val mockedResponse: OAuthTokens = mockk()

        coEvery {
            authRepository.requestTokens(
                clientId = any(),
                authCode = any(),
                redirectUri = any(),
                codeVerifier = any(),
            )
        } returns flow { emit(mockedResponse) }

        val result = loginUseCase.requestTokens(authCode, packageName).first()

        assertEquals(mockedResponse, result)
    }
}
