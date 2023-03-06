package com.github.authnongms.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.github.authnongms.presentation.RedirectViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        // Get the Application object from extras
        val application = checkNotNull(extras[APPLICATION_KEY])
        val loginUseCase = UseCaseFactory.createLoginUseCase()
        val profileUseCase = UseCaseFactory.createUserProfileUseCase(application)
        return RedirectViewModel(loginUseCase, profileUseCase) as T
    }
}
