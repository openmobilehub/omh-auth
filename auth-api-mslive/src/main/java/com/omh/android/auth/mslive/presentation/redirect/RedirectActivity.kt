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

package com.omh.android.auth.mslive.presentation.redirect

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.viewModels
import androidx.browser.customtabs.CustomTabsIntent
import com.omh.android.auth.api.models.OmhAuthException
import com.omh.android.auth.api.models.OmhAuthStatusCodes
import com.omh.android.auth.mobileweb.presentation.redirect.RedirectActivity
import com.omh.android.auth.mobileweb.presentation.redirect.RedirectViewModel
import com.omh.android.auth.mslive.factories.ViewModelFactory
import com.omh.android.auth.mslive.utils.Constants.PROVIDER_MSLIVE

class RedirectActivity : RedirectActivity() {

    override val viewModel: RedirectViewModel by viewModels { ViewModelFactory() }

    override val providerShortName: String = PROVIDER_MSLIVE

    private var nonce: String = ""

    override fun openCustomTabLogin() {
        val scopes = intent.getStringExtra(SCOPES)
        if (scopes.isNullOrEmpty() || packageName.isNullOrEmpty()) {
            returnResult(
                Activity.RESULT_CANCELED,
                OmhAuthException.RecoverableLoginException(OmhAuthStatusCodes.DEVELOPER_ERROR),
            )
            return
        }
        val uri = viewModel.getLoginUrl(scopes, packageName, clientId)
        nonce = requireNotNull(uri.getQueryParameter("nonce"))

        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        val customTabsIntent: CustomTabsIntent = builder.build()
        customTabsIntent.intent.data = uri
        tabsLauncher.launch(customTabsIntent.intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        caughtRedirect = true
        val uri: Uri? = intent?.data
        // Microsoft's OAuth2 stores these parameters after the hash, hence we need to extract them
        // by hand.
        val data = uri?.fragment?.split('&')?.associate {
            val pair = it.split('=')
            Pair(pair[0], pair[1])
        }
        val authCode = data?.get("code")
        val error = data?.get("error")
        if (authCode == null) {
            handleLoginError(error)
            return
        }
        viewModel.requestTokens(authCode, packageName, clientId)
    }
}
