package com.omh.android.auth.dropbox.data.user.datasource

import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.omh.android.auth.dropbox.utils.Constants
import com.omh.android.auth.dropbox.utils.Constants.PROVIDER_DROPBOX
import com.omh.android.auth.mobileweb.data.utils.getEncryptedSharedPrefs
import com.omh.android.auth.test.FakeAndroidKeyStoreProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DropboxUserDataSourceTest {

    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        @BeforeClass
        @JvmStatic
        fun bootstrap() {
            FakeAndroidKeyStoreProvider.setup()
        }
    }

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().context
        sharedPreferences = getEncryptedSharedPrefs(context, PROVIDER_DROPBOX)
    }

    @After
    fun tearDown() {
        sharedPreferences.edit().clear().commit()
    }

    @Test
    fun testParseIdToken() {
        val token = String(javaClass.classLoader.getResourceAsStream("idtoken.txt").readBytes())
        runBlocking {
            launch {
                DropboxUserDataSource(sharedPreferences).handleIdToken(
                    token,
                    "test dropbox app id",
                )
                assertEquals(
                    "John",
                    sharedPreferences.getString(Constants.NAME_KEY, null),
                )
                assertEquals(
                    "Foobar",
                    sharedPreferences.getString(Constants.SURNAME_KEY, null),
                )
                assertEquals(
                    "test@test.com",
                    sharedPreferences.getString(Constants.EMAIL_KEY, null),
                )
                assertEquals(
                    "dbid:test dropbox account id",
                    sharedPreferences.getString(Constants.ID_KEY, null),
                )
                assertNull(sharedPreferences.getString(Constants.PICTURE_KEY, null))
            }
        }
    }
}
