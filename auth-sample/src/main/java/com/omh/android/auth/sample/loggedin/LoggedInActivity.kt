package com.omh.android.auth.sample.loggedin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.omh.android.auth.api.CancellableCollector
import com.omh.android.auth.sample.login.LoginActivity
import com.omh.android.auth.api.OmhAuthClient
import com.omh.android.auth.api.OmhCredentials
import com.omh.android.auth.api.models.OmhAuthException
import com.omh.android.auth.api.models.OmhAuthStatusCodes
import com.omh.android.auth.sample.R
import com.omh.android.auth.sample.databinding.ActivityLoggedInBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoggedInActivity : AppCompatActivity() {

    @Inject
    lateinit var omhAuthClient: OmhAuthClient

    private val binding: ActivityLoggedInBinding by lazy {
        ActivityLoggedInBinding.inflate(layoutInflater)
    }

    private val cancellableCollector = CancellableCollector()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnLogout.setOnClickListener {
            logout()
        }
        binding.btnRefresh.setOnClickListener {
            refreshToken()
        }
        binding.btnRevoke.setOnClickListener {
            revokeToken()
        }

        val profile = requireNotNull(omhAuthClient.getUser())
        binding.tvEmail.text = getString(R.string.email_placeholder, profile.email)
        binding.tvName.text = getString(R.string.name_placeholder, profile.name)
        binding.tvSurname.text = getString(R.string.surname_placeholder, profile.surname)
        getToken()
    }

    private fun revokeToken() {
        val cancellable = omhAuthClient.revokeToken()
            .addOnFailure { showErrorDialog(it as OmhAuthException) }
            .addOnSuccess { navigateToLogin() }
            .execute()
        cancellableCollector.addCancellable(cancellable)
    }

    private fun getToken() = lifecycleScope.launch(Dispatchers.IO) {
        val token = when (val credentials = omhAuthClient.getCredentials()) {
            is OmhCredentials -> credentials.accessToken
            is GoogleAccountCredential -> credentials.token
            null -> return@launch
            else -> error("Unsupported credential type")
        }

        withContext(Dispatchers.Main) {
            binding.tvToken.text = getString(R.string.token_placeholder, token)
        }
    }

    private fun logout() {
        val cancellable = omhAuthClient.signOut()
            .addOnSuccess { navigateToLogin() }
            .addOnFailure { showErrorDialog(it as OmhAuthException) }
            .execute()
        cancellableCollector.addCancellable(cancellable)
    }

    private fun showErrorDialog(omhException: OmhAuthException) {
        val errorMessage = OmhAuthStatusCodes.getStatusCodeString(omhException.statusCode)
        AlertDialog.Builder(this)
            .setTitle("An error has occurred.")
            .setMessage(errorMessage)
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun refreshToken() = lifecycleScope.launch(Dispatchers.IO) {
        val newToken = when (val credentials = omhAuthClient.getCredentials()) {
            is OmhCredentials -> credentials.blockingRefreshToken()
            is GoogleAccountCredential -> credentials.token
            else -> error("Unsupported credential type")
        }

        if (newToken != null) {
            withContext(Dispatchers.Main) {
                binding.tvToken.text = getString(R.string.token_placeholder, newToken)
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancellableCollector.clear()
    }
}