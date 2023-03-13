package com.github.omhauthdemo.loggedin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.omhauthdemo.R
import com.github.omhauthdemo.databinding.ActivityLoggedInBinding
import com.github.omhauthdemo.login.LoginActivity
import com.github.openmobilehub.auth.OmhAuthClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoggedInActivity : AppCompatActivity() {

    @Inject
    lateinit var omhAuthClient: OmhAuthClient
    private val credentials by lazy { omhAuthClient.getCredentials(this) }

    private val binding: ActivityLoggedInBinding by lazy {
        ActivityLoggedInBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnLogout.setOnClickListener {
            revokeToken()
        }
        binding.btnRefresh.setOnClickListener {
            refreshToken()
        }

        val profile = requireNotNull(omhAuthClient.getUser(this))
        binding.tvEmail.text = getString(R.string.email_placeholder, profile.email)
        binding.tvName.text = getString(R.string.name_placeholder, profile.name)
        binding.tvSurname.text = getString(R.string.surname_placeholder, profile.surname)
        binding.tvToken.text = getString(R.string.token_placeholder, credentials.accessToken)
    }

    private fun revokeToken() = lifecycleScope.launch(Dispatchers.IO) {
        credentials.revokeToken { e2 ->
            launch(Dispatchers.Main) { showRevokeException(e2) }
        }
        navigateToLogin()
    }

    private fun refreshToken() = lifecycleScope.launch(Dispatchers.IO) {
        val newToken = credentials.refreshAccessToken { e ->
            launch(Dispatchers.Main) {
                Toast.makeText(
                    applicationContext,
                    "Couldn't refresh token: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
            credentials.revokeToken { e2 ->
                launch(Dispatchers.Main) { showRevokeException(e2) }
            }
            navigateToLogin()
        }

        if (newToken != null) {
            binding.tvToken.text = getString(R.string.token_placeholder, newToken)
        }
    }

    private fun showRevokeException(e2: Exception) {
        Toast.makeText(
            applicationContext,
            "Couldn't revoke token: ${e2.message}",
            Toast.LENGTH_LONG
        ).show()
    }


    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}