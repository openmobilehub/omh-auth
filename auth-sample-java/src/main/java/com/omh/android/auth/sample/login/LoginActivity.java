package com.omh.android.auth.sample.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.omh.android.auth.api.OmhAuthClient;
import com.omh.android.auth.api.models.OmhAuthException;
import com.omh.android.auth.api.models.OmhAuthStatusCodes;
import com.omh.android.auth.sample.databinding.ActivityLoginBinding;
import com.omh.android.auth.sample.loggedin.LoggedinActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    @Inject
    OmhAuthClient omhAuthClient;

    private ActivityResultLauncher<Intent> loginLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loginLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleLoginResult);
        binding.btnLogin.setOnClickListener(v -> startLogin());

        if (omhAuthClient.getUser() != null) {
            navigateToLoggedIn();
        }
    }

    private void handleLoginResult(ActivityResult result) {
        try {
            omhAuthClient.getAccountFromIntent(result.getData());
            navigateToLoggedIn();
        } catch (OmhAuthException exception) {
            String errorMessage = OmhAuthStatusCodes.getStatusCodeString(exception.getStatusCode());
            new AlertDialog.Builder(this)
                    .setTitle("An error has occurred.")
                    .setMessage(errorMessage)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        }
    }

    private void startLogin() {
        Intent loginIntent = omhAuthClient.getLoginIntent();
        loginLauncher.launch(loginIntent);
    }

    private void navigateToLoggedIn() {
        Intent intent = new Intent(this, LoggedinActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
