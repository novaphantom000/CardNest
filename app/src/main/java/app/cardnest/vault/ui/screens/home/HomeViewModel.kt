package app.cardnest.vault.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.vault.data.repository.AuthRepository
import app.cardnest.vault.data.repository.CardRepository
import app.cardnest.vault.domain.model.Card
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val cards: List<Card> = emptyList(),
    val recentCards: List<Card> = emptyList(),
    val cardCount: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        cardRepository.getAllCards(),
        cardRepository.getCardCount()
    ) { cards, count ->
        val recent = cards.sortedByDescending { it.lastAccessedAt }.take(3)
        HomeUiState(
            cards = cards,
            recentCards = recent,
            cardCount = count,
            isLoading = false
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeUiState()
    )

    fun lock() {
        viewModelScope.launch {
            authRepository.lock()
        }
    }
}
