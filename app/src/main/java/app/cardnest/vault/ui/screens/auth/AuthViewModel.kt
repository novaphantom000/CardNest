package app.cardnest.vault.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.vault.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val isUnlocked: StateFlow<Boolean> = authRepository.isUnlocked.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    fun unlock() {
        viewModelScope.launch {
            authRepository.unlock()
        }
    }

    fun lock() {
        viewModelScope.launch {
            authRepository.lock()
        }
    }
}
