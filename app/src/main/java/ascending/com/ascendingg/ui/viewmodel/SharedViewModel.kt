package ascending.com.ascendingg.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import ascending.com.ascendingg.data.model.Habit
import ascending.com.ascendingg.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {

    // Expose habits as StateFlow for UI observation
    val habits = habitRepository.getAllHabits()
        .asFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun incrementHabitStreak(habitId: Long) {
        viewModelScope.launch {
            habitRepository.incrementStreak(habitId, System.currentTimeMillis())
        }
    }
}