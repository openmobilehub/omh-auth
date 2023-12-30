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

package com.omh.android.auth.sample.loggedin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.omh.android.auth.api.OmhAuthClient
import com.omh.android.auth.api.OmhCredentials
import com.omh.android.auth.api.async.CancellableCollector
import com.omh.android.auth.sample.R
import com.omh.android.auth.sample.databinding.FragmentLoggedInBinding
import com.omh.android.auth.sample.di.BoxAuthClient
import com.omh.android.auth.sample.di.DropboxAuthClient
import com.omh.android.auth.sample.di.GoogleAuthClient
import com.omh.android.auth.sample.di.MsLiveAuthClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException
import javax.inject.Inject

@AndroidEntryPoint
class LoggedInFragment : Fragment() {

    @Inject
    @GoogleAuthClient
    lateinit var googleAuthClient: OmhAuthClient

    @Inject
    @BoxAuthClient
    lateinit var boxAuthClient: OmhAuthClient

    @Inject
    @MsLiveAuthClient
    lateinit var msLiveAuthClient: OmhAuthClient

    @Inject
    @DropboxAuthClient
    lateinit var dropboxAuthClient: OmhAuthClient

    private var binding: FragmentLoggedInBinding? = null

    private val cancellableCollector = CancellableCollector()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLoggedInBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
        getToken()
    }

    private fun setupUI() {
        val profile = requireNotNull(selectOmhAuthClient().getUser())
        binding?.run {
            btnLogout.setOnClickListener { logout() }
            btnRefresh.setOnClickListener { refreshToken() }
            btnRevoke.setOnClickListener { revokeToken() }
            tvEmail.text = getString(R.string.email_placeholder, profile.email)
            tvName.text = getString(R.string.name_placeholder, profile.name)
            tvSurname.text = getString(R.string.surname_placeholder, profile.surname)
        }
    }

    private fun revokeToken() {
        val cancellable = selectOmhAuthClient().revokeToken()
            .addOnFailure(::showErrorDialog)
            .addOnSuccess { navigateToLogin() }
            .execute()
        cancellableCollector.addCancellable(cancellable)
    }

    private fun getToken() = lifecycleScope.launch(Dispatchers.IO) {
        val token = when (val credentials = selectOmhAuthClient().getCredentials()) {
            is OmhCredentials -> credentials.accessToken
            is GoogleAccountCredential -> {
                requestGoogleToken(credentials)
            }

            null -> return@launch
            else -> error("Unsupported credential type")
        }

        withContext(Dispatchers.Main) {
            binding?.tvToken?.text = getString(R.string.token_placeholder, token)
        }
    }

    private fun requestGoogleToken(credentials: GoogleAccountCredential): String? {
        return try {
            credentials.token
        } catch (e: UserRecoverableAuthException) {
            e.printStackTrace()
            logout()
            null
        }
    }

    private fun logout() {
        val cancellable = selectOmhAuthClient().signOut()
            .addOnSuccess { navigateToLogin() }
            .addOnFailure(::showErrorDialog)
            .execute()
        cancellableCollector.addCancellable(cancellable)
    }

    private fun showErrorDialog(exception: Throwable) {
        exception.printStackTrace()
        val ctx = context ?: return
        AlertDialog.Builder(ctx)
            .setTitle("An error has occurred.")
            .setMessage(exception.message)
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun refreshToken() = lifecycleScope.launch(Dispatchers.IO) {
        val newToken = when (val credentials = selectOmhAuthClient().getCredentials()) {
            is OmhCredentials -> credentials.blockingRefreshToken()
            is GoogleAccountCredential -> requestGoogleToken(credentials)
            else -> error("Unsupported credential type")
        } ?: return@launch

        withContext(Dispatchers.Main) {
            binding?.tvToken?.text = getString(R.string.token_placeholder, newToken)
        }
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_logged_in_fragment_to_login_fragment)
    }

    private fun selectOmhAuthClient(): OmhAuthClient {
        return when (arguments?.getString("provider")) {
            "mslive" -> msLiveAuthClient
            "dropbox" -> dropboxAuthClient
            "box" -> boxAuthClient
            "google" -> googleAuthClient
            else -> throw IllegalArgumentException("Unsupported provider")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancellableCollector.clear()
    }
}
