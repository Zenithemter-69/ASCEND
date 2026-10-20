package ascending.com.ascendingg.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ascending.com.ascendingg.data.model.Habit
import ascending.com.ascendingg.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {

    val todaysHabits: Flow<List<Habit>> = habitRepository.habits
        .map { habits ->
            habits.filter { it.isActive }.take(6)
        }

    fun incrementStreak(habitId: Long) {
        viewModelScope.launch {
            habitRepository.incrementStreak(habitId, System.currentTimeMillis())
        }
    }
}