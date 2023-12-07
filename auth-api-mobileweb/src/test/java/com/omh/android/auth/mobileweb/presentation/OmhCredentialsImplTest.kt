package com.omh.android.auth.mobileweb.presentation

import com.omh.android.auth.api.utils.ThreadUtils
import com.omh.android.auth.mobileweb.domain.auth.AuthUseCase
import com.omh.android.auth.mobileweb.domain.models.ApiResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OmhCredentialsImplTest {

    @Test(expected = IllegalStateException::class)
    fun testBlockingRefreshTokenAtMainThread() {
        mockkObject(ThreadUtils) {
            every {
                ThreadUtils.checkForMainThread()
            } throws IllegalStateException("Running at main thread")

            mockk<AuthUseCase> {
                coEvery { blockingRefreshToken(any()) } returns ApiResult.Success("abcdef")
                runTest {
                    OmhCredentialsImpl(this@mockk, "123456").blockingRefreshToken()
                    fail("Should throw exception")
                }
            }
        }
    }

    @Test
    fun testBlockingRefreshTokenNormalCase() {
        mockkObject(ThreadUtils) {
            every { ThreadUtils.checkForMainThread() } returns Unit
            mockk<AuthUseCase> {
                coEvery { blockingRefreshToken(any()) } returns ApiResult.Success("abcdef")
                runTest {
                    val result = OmhCredentialsImpl(this@mockk, "123456")
                        .blockingRefreshToken()
                    assertEquals("abcdef", result)
                }
                coVerify {
                    blockingRefreshToken("123456")
                }
            }
        }
    }
}
