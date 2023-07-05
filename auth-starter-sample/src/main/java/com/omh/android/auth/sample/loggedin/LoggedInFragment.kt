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
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoggedInFragment : Fragment() {

    @Inject
    lateinit var omhAuthClient: OmhAuthClient

    private var binding: FragmentLoggedInBinding? = null

    private val cancellableCollector = CancellableCollector()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoggedInBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
        getToken()
    }

    private fun setupUI() {
        val profile = requireNotNull(omhAuthClient.getUser())
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
        // TODO: Add here the revoke token function
    }

    private fun getToken() = lifecycleScope.launch(Dispatchers.IO) {
        val token = when (val credentials = omhAuthClient.getCredentials()) {
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
        // TODO: add here the logout logic
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
        val newToken = when (val credentials = omhAuthClient.getCredentials()) {
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

    override fun onDestroy() {
        super.onDestroy()
        cancellableCollector.clear()
    }
}