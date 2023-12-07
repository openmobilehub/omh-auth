package com.omh.android.auth.dropbox.data.login.datasource

import android.content.Context
import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.omh.android.auth.api.models.OmhAuthException
import com.omh.android.auth.mobileweb.data.login.datasource.AuthDataSource
import com.omh.android.auth.mobileweb.domain.models.ApiResult
import com.omh.android.auth.dropbox.R
import com.omh.android.auth.dropbox.data.login.DropboxApiRest
import com.omh.android.auth.dropbox.data.login.DropboxAuthRest
import com.omh.android.auth.dropbox.data.login.models.AuthTokenResponse
import com.omh.android.auth.dropbox.utils.Constants.PROVIDER_DROPBOX
import com.omh.android.auth.mobileweb.data.utils.getEncryptedSharedPrefs
import com.omh.android.auth.test.FakeAndroidKeyStoreProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class DropboxAuthDataSourceTest {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var context: Context

    companion object {
        @BeforeClass
        @JvmStatic
        fun bootstrap() {
            FakeAndroidKeyStoreProvider.setup()
        }
    }

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().context
        sharedPreferences = getEncryptedSharedPrefs(context, PROVIDER_DROPBOX)
    }

    @After
    fun tearDown() {
        sharedPreferences.edit().clear().commit()
    }

    @Test
    fun testBuildLoginUrl() {
        val instance = DropboxAuthDataSource(context, mockk(), mockk(), sharedPreferences)
        val result = instance.buildLoginUrl(
            "foobar.workflow",
            "12345",
            "abcdef",
            "omh://auth.oauth2.dropbox/redirect",
        )
        assertEquals("offline", result.getQueryParameter("token_access_type"))
        assertEquals("code", result.getQueryParameter("response_type"))
        assertEquals("foobar.workflow", result.getQueryParameter("scope"))
        assertEquals("12345", result.getQueryParameter("client_id"))
        assertEquals("abcdef", result.getQueryParameter("code_challenge"))
        assertEquals("omh://auth.oauth2.dropbox/redirect", result.getQueryParameter("redirect_uri"))
        assertFalse(true == result.getQueryParameter("nonce")?.isEmpty())
        assertEquals("S256", result.getQueryParameter("code_challenge_method"))
        assertFalse(true == result.getQueryParameter("code_challenge")?.isEmpty())
    }

    @Test
    fun `Test store, get and clean tokens`() {
        val instance = DropboxAuthDataSource(context, mockk(), mockk(), sharedPreferences)
        instance.storeToken(AuthDataSource.ACCESS_TOKEN, "abcdef")
        instance.storeToken(AuthDataSource.REFRESH_TOKEN, "123456")
        assertEquals("abcdef", instance.getToken(AuthDataSource.ACCESS_TOKEN))
        assertEquals("123456", instance.getToken(AuthDataSource.REFRESH_TOKEN))
        instance.clearData()
        assertNull(instance.getToken(AuthDataSource.ACCESS_TOKEN))
        assertNull(instance.getToken(AuthDataSource.REFRESH_TOKEN))
    }

    @Test
    fun testGetRedirectUriDefault() {
        val result = DropboxAuthDataSource.formatRedirectUri(context)
        assertEquals("omh://auth.oauth2.dropbox/redirect", result)
    }

    @Test
    fun testGetRedirectUriOverrideString() {
        val mockContext = mockk<Context>() {
            every {
                getString(R.string.com_omh_android_auth_dropbox_oauth2_redirect_scheme)
            } returns "https"
            every {
                getString(R.string.com_omh_android_auth_dropbox_oauth2_redirect_host)
            } returns "auth.openmobilehub.org"
            every {
                getString(R.string.com_omh_android_auth_dropbox_oauth2_redirect_pathPrefix)
            } returns "/oauth2-redirect"
        }
        val result = DropboxAuthDataSource.formatRedirectUri(mockContext)
        assertEquals("https://auth.openmobilehub.org/oauth2-redirect", result)
    }

    @Test
    fun testGetToken() {
        val authTokenResponse = mockk<AuthTokenResponse>() {
            every { accessToken } returns "12345678"
            every { tokenType } returns "Bearer"
            every { expiresIn } returns 9999
            every { refreshToken } returns "abcdefabcdef"
            every { idToken } returns "id token"
        }
        val expectedResponse = ApiResult.Success(authTokenResponse)

        val authRest = mockk<DropboxAuthRest>()
        coEvery {
            authRest.getToken(any(), any(), any(), any(), any())
        } returns expectedResponse

        val instance = DropboxAuthDataSource(context, authRest, mockk(), sharedPreferences)

        runTest {
            val result = instance.getToken(
                "123456",
                "xxxxxxxx",
                "omh://auth.oauth2.dropbox/redirect",
                "code verify",
            )
            result.extractResult().run {
                assertEquals("12345678", accessToken)
                assertEquals("Bearer", tokenType)
                assertEquals(9999, expiresIn)
                assertEquals("abcdefabcdef", refreshToken)
                assertEquals("id token", idToken)
            }

            coVerify {
                authRest.getToken(
                    "123456",
                    "xxxxxxxx",
                    "omh://auth.oauth2.dropbox/redirect",
                    "code verify",
                    "authorization_code"
                )
            }
        }
    }

    @Test(expected = OmhAuthException.ApiException::class)
    fun testRefreshTokenWhenNoTokenFoundInSharedPrefs() {
        val instance = DropboxAuthDataSource(context, mockk(), mockk(), sharedPreferences)
        runTest {
            val result = instance.refreshAccessToken("12345")
            assertTrue(result.javaClass.isAssignableFrom(ApiResult.Error.RuntimeError::class.java))
            result.extractResult()
        }
    }

    @Test
    fun testRefreshToken() {
        val authTokenResponse = mockk<AuthTokenResponse>() {
            every { accessToken } returns "12345678"
            every { tokenType } returns "Bearer"
            every { expiresIn } returns 9999
            every { refreshToken } returns "abcdefabcdef"
            every { idToken } returns "id token"
        }
        val expectedResponse = ApiResult.Success(authTokenResponse)

        val authRest = mockk<DropboxAuthRest>()
        coEvery {
            authRest.refreshToken(any(), any())
        } returns expectedResponse

        val instance = DropboxAuthDataSource(context, authRest, mockk(), sharedPreferences)
        instance.storeToken(AuthDataSource.REFRESH_TOKEN, "abcdef")

        runTest {
            val result = instance.refreshAccessToken("123456")
            result.extractResult().run {
                assertEquals("12345678", accessToken)
                assertEquals("Bearer", tokenType)
                assertEquals(9999, expiresIn)
                assertEquals("abcdefabcdef", refreshToken)
                assertEquals("id token", idToken)
            }

            coVerify {
                authRest.refreshToken("123456", "abcdef")
            }
        }
    }

    @Test
    fun testRevokeToken() {
        val expectedResponse: ApiResult<Unit> = ApiResult.Success(Unit)

        val apiRest = mockk<DropboxApiRest>()
        coEvery {
            apiRest.revokeToken(any())
        } returns expectedResponse

        val instance = DropboxAuthDataSource(context, mockk(), apiRest, sharedPreferences)
        instance.storeToken(AuthDataSource.ACCESS_TOKEN, "abcdef")

        runTest {
            val result = instance.revokeToken("123456", "abcdef")
            assertTrue(result.javaClass.isAssignableFrom(ApiResult.Success::class.java))

            coVerify {
                apiRest.revokeToken(
                    "Bearer abcdef",
                )
            }
        }
    }
}
