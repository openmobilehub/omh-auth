package com.github.authnongms.presentation

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.authnongms.domain.auth.LoginUseCase
import com.github.authnongms.domain.user.ProfileUseCase
import com.github.authnongms.utils.EventWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class RedirectViewModel(
    private val loginUseCase: LoginUseCase,
    private val profileUseCase: ProfileUseCase
) : ViewModel() {

    private val _tokenResponseEvent = MutableLiveData<EventWrapper<Boolean>>()
    val tokenResponseEvent: LiveData<EventWrapper<Boolean>> = _tokenResponseEvent

    fun getLoginUrl(scopes: String): Uri {
        return loginUseCase.getLoginUrl(scopes)
    }

    fun requestTokens(
        authCode: String,
    ) = viewModelScope.launch(Dispatchers.IO) {
        val response = loginUseCase.requestTokens(authCode)
        if (response.isSuccessful && response.body() != null) {
            val clientId = checkNotNull(loginUseCase.clientId)
            profileUseCase.resolveIdToken(response.body()!!.idToken, clientId)
        }
        _tokenResponseEvent.postValue(EventWrapper(response.isSuccessful))
    }

    fun setClientId(clientId: String) {
        loginUseCase.clientId = clientId
    }
}
