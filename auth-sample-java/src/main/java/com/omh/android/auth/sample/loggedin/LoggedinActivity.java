package com.omh.android.auth.sample.loggedin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.omh.android.auth.api.OmhAuthClient;
import com.omh.android.auth.api.OmhCredentials;
import com.omh.android.auth.api.async.CancellableCollector;
import com.omh.android.auth.api.async.OmhCancellable;
import com.omh.android.auth.api.async.OmhSuccessListener;
import com.omh.android.auth.api.models.OmhUserProfile;
import com.omh.android.auth.sample.R;
import com.omh.android.auth.sample.databinding.ActivityLoggedInBinding;
import com.omh.android.auth.sample.login.LoginActivity;

import java.io.IOException;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.Unit;

@AndroidEntryPoint
public class LoggedinActivity extends AppCompatActivity {

    private ActivityLoggedInBinding binding;

    @Inject
    OmhAuthClient omhAuthClient;

    private final CompositeDisposable cd = new CompositeDisposable();
    private final CancellableCollector cc = new CancellableCollector();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoggedInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnRefresh.setOnClickListener(v -> refreshToken());
        binding.btnLogout.setOnClickListener(v -> logout());
        binding.btnRevoke.setOnClickListener(v -> revokeToken());

        OmhUserProfile profile = omhAuthClient.getUser();
        if (profile != null) {
            binding.tvEmail.setText(getString(R.string.email_placeholder, profile.getEmail()));
            binding.tvName.setText(getString(R.string.name_placeholder, profile.getName()));
            binding.tvSurname.setText(getString(R.string.surname_placeholder, profile.getSurname()));
        }

        getToken();
    }

    private void getToken() {
        Disposable disposable = Single.<String>create(
                        emitter -> {
                            try {
                                String token = requestAccessToken();
                                emitter.onSuccess(token);
                            } catch (GoogleAuthException | IOException authException) {
                                emitter.onError(authException);
                            }
                        }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(token -> binding.tvToken.setText(getString(R.string.token_placeholder, token)))
                .doOnError(error -> showErrorDialog(error.getMessage()))
                .subscribe();
        cd.add(disposable);
    }

    private void showErrorDialog(String errorMessage) {
        new AlertDialog.Builder(this)
                .setTitle("An error has occurred.")
                .setMessage(errorMessage)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void refreshToken() {
        Disposable disposable = Single.<String>create(
                        emitter -> {
                            try {
                                String token = requestRefreshToken();
                                emitter.onSuccess(token);
                            } catch (GoogleAuthException | IOException authException) {
                                emitter.onError(authException);
                            }
                        }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(token -> binding.tvToken.setText(getString(R.string.token_placeholder, token)))
                .doOnError(error -> showErrorDialog(error.getMessage()))
                .subscribe();
        cd.add(disposable);
    }

    @Nullable
    private String requestRefreshToken() throws GoogleAuthException, IOException {
        Object credentials = omhAuthClient.getCredentials();
        String token;
        if (credentials instanceof OmhCredentials) {
            OmhCredentials omhCredentials = (OmhCredentials) credentials;
            token = omhCredentials.blockingRefreshToken();
        } else if (credentials instanceof GoogleAccountCredential) {
            GoogleAccountCredential accountCredential = (GoogleAccountCredential) credentials;
            token = accountCredential.getToken();
        } else {
            throw new IllegalStateException("Unsupported credential type");
        }
        return token;
    }

    @Nullable
    private String requestAccessToken() throws IOException, GoogleAuthException {
        Object credentials = omhAuthClient.getCredentials();
        String token;
        if (credentials instanceof OmhCredentials) {
            OmhCredentials omhCredentials = (OmhCredentials) credentials;
            token = omhCredentials.getAccessToken();
        } else if (credentials instanceof GoogleAccountCredential) {
            GoogleAccountCredential accountCredential = (GoogleAccountCredential) credentials;
            token = accountCredential.getToken();
        } else {
            throw new IllegalStateException("Unsupported credential type");
        }
        return token;
    }

    private void logout() {
        OmhCancellable cancellable = omhAuthClient.signOut()
                .addOnFailure(exception -> showErrorDialog(exception.getMessage()))
                .addOnSuccess(result -> navigateToLogin())
                .execute();
        cc.addCancellable(cancellable);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void revokeToken() {
        OmhCancellable cancellable = omhAuthClient.revokeToken()
                .addOnFailure(exception -> showErrorDialog(exception.getMessage()))
                .addOnSuccess(result -> navigateToLogin())
                .execute();
        cc.addCancellable(cancellable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cd.dispose();
        cc.clear();
    }
}
