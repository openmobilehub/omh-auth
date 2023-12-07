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

package com.omh.android.auth.dropbox.presentation.redirect

import android.content.Intent
import android.net.Uri
import androidx.activity.viewModels
import com.omh.android.auth.dropbox.factories.ViewModelFactory
import com.omh.android.auth.dropbox.utils.Constants.PROVIDER_DROPBOX
import com.omh.android.auth.mobileweb.presentation.redirect.RedirectActivity
import com.omh.android.auth.mobileweb.presentation.redirect.RedirectViewModel

class RedirectActivity : RedirectActivity() {

    override val viewModel: RedirectViewModel by viewModels { ViewModelFactory() }

    override val providerShortName: String = PROVIDER_DROPBOX

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        caughtRedirect = true
        val data: Uri? = intent?.data
        val authCode = data?.getQueryParameter("code")
        val error = data?.getQueryParameter("error")
        if (authCode == null) {
            handleLoginError(error)
            return
        }
        viewModel.requestTokens(authCode, packageName, clientId)
    }
}
