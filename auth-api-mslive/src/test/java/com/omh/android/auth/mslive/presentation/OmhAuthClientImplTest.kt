package com.omh.android.auth.mslive.presentation

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.omh.android.auth.mobileweb.presentation.redirect.RedirectActivity.Companion.CLIENT_ID
import com.omh.android.auth.mobileweb.presentation.redirect.RedirectActivity.Companion.SCOPES
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OmhAuthClientImplTest {

    @Test
    fun testDefaultScope() {
        val client = OmhAuthClientImpl.Builder("123456")
            .build(InstrumentationRegistry.getInstrumentation().context)

        val intent = client.getLoginIntent()
        assertEquals("123456", intent.extras?.getString(CLIENT_ID))
        assertEquals("offline_access profile", intent.extras?.getString(SCOPES))
    }

    @Test
    fun testAddCustomScope() {
        var client = OmhAuthClientImpl.Builder("123456")
            .addScope("Files.ReadWrite")
            .build(InstrumentationRegistry.getInstrumentation().context)

        var intent = client.getLoginIntent()
        assertEquals("123456", intent.extras?.getString(CLIENT_ID))
        assertEquals("offline_access profile Files.ReadWrite", intent.extras?.getString(SCOPES))

        client = OmhAuthClientImpl.Builder("123456")
            .addScope("Files.ReadWrite")
            .addScope("User.Read")
            .build(InstrumentationRegistry.getInstrumentation().context)

        intent = client.getLoginIntent()
        assertEquals("123456", intent.extras?.getString(CLIENT_ID))
        assertEquals("offline_access profile Files.ReadWrite User.Read", intent.extras?.getString(SCOPES))
    }
}
