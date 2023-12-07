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

package com.omh.android.auth.nongms.presentation.redirect

import androidx.activity.viewModels
import com.omh.android.auth.mobileweb.presentation.redirect.RedirectActivity
import com.omh.android.auth.mobileweb.presentation.redirect.RedirectViewModel
import com.omh.android.auth.nongms.factories.ViewModelFactory
import com.omh.android.auth.nongms.utils.Constants.PROVIDER_GOOGLE

internal class RedirectActivity : RedirectActivity() {

    override val viewModel: RedirectViewModel by viewModels { ViewModelFactory() }

    override val providerShortName: String = PROVIDER_GOOGLE
}
