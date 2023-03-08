package com.github.omhauthdemo.loggedin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.github.omhauthdemo.databinding.ActivityLoggedInBinding
import com.github.omhauthdemo.login.LoginActivity
import com.github.openmobilehub.auth.OmhAuthClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoggedInActivity : AppCompatActivity() {

    @Inject
    lateinit var omhAuthClient: OmhAuthClient

    private val binding: ActivityLoggedInBinding by lazy {
        ActivityLoggedInBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnLogout.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}