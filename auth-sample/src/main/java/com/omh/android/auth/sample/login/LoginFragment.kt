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

package com.omh.android.auth.sample.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.omh.android.auth.api.OmhAuthClient
import com.omh.android.auth.api.models.OmhAuthException
import com.omh.android.auth.sample.BuildConfig
import com.omh.android.auth.sample.R
import com.omh.android.auth.sample.databinding.FragmentLoginBinding
import com.omh.android.auth.sample.di.BoxAuthClient
import com.omh.android.auth.sample.di.DropboxAuthClient
import com.omh.android.auth.sample.di.GoogleAuthClient
import com.omh.android.auth.sample.di.MsLiveAuthClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val loginLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        /* contract = */ ActivityResultContracts.StartActivityForResult(),
        /* callback = */ ::handleLoginResult,
    )

    private var binding: FragmentLoginBinding? = null

    @Inject
    @GoogleAuthClient
    lateinit var googleAuthClient: OmhAuthClient

    @Inject
    @BoxAuthClient
    lateinit var boxOmhAuthClient: OmhAuthClient

    @Inject
    @MsLiveAuthClient
    lateinit var msLiveAuthClient: OmhAuthClient

    @Inject
    @DropboxAuthClient
    lateinit var dropboxAuthClient: OmhAuthClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.run {
            btnGoogleLogin.setOnClickListener { startGoogleLogin() }
            btnBoxLogin.setOnClickListener { startBoxLogin() }
            btnMsliveLogin.setOnClickListener { startMicrosoftLogin() }
            btnDropboxLogin.setOnClickListener { startDropboxLogin() }
            if ("DISABLED" == BuildConfig.GOOGLE_CLIENT_ID) {
                btnGoogleLogin.isEnabled = false
            }
            if ("DISABLED" == BuildConfig.BOX_CLIENT_ID || "DISABLED" == BuildConfig.BOX_CLIENT_SECRET) {
                btnBoxLogin.isEnabled = false
            }
            if ("DISABLED" == BuildConfig.MSLIVE_CLIENT_ID) {
                btnMsliveLogin.isEnabled = false
            }
            if ("DISABLED" == BuildConfig.DROPBOX_CLIENT_ID) {
                btnDropboxLogin.isEnabled = false
            }
        }
    }

    private fun startBoxLogin() {
        loginLauncher.launch(
            boxOmhAuthClient.getLoginIntent()
        )
    }

    private fun startMicrosoftLogin() {
        loginLauncher.launch(
            msLiveAuthClient.getLoginIntent()
        )
    }

    private fun startDropboxLogin() {
        loginLauncher.launch(
            dropboxAuthClient.getLoginIntent()
        )
    }

    private fun startGoogleLogin() {
        loginLauncher.launch(
            googleAuthClient.getLoginIntent()
        )
    }

    private fun navigateToLoggedIn(provider: String) {
        val bundle = bundleOf("provider" to provider)
        findNavController().navigate(R.id.action_login_fragment_to_logged_in_fragment, bundle)
    }

    private fun handleLoginResult(result: ActivityResult) {
        try {
            val intent = result.data
            val provider: String = result.data?.getStringExtra("provider") ?: "google"
            when (provider) {
                "mslive" -> msLiveAuthClient.getAccountFromIntent(intent)
                "box" -> boxOmhAuthClient.getAccountFromIntent(intent)
                "dropbox" -> dropboxAuthClient.getAccountFromIntent(intent)
                else -> googleAuthClient.getAccountFromIntent(intent)
            }
            navigateToLoggedIn(provider)
        } catch (exception: OmhAuthException) {
            handleException(exception)
        }
    }

    private fun handleException(exception: OmhAuthException) {
        exception.printStackTrace()
        val ctx = context ?: return
        AlertDialog.Builder(ctx)
            .setTitle("An error has occurred.")
            .setMessage(exception.message)
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}
