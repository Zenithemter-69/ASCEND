package ascending.com.ascendingg.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import ascending.com.ascendingg.data.model.Habit
import ascending.com.ascendingg.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {

    val todaysHabits = habitRepository.getAllHabits()
        .asFlow()
        .map { habits ->
            habits.filter { it.isActive }.take(6)
        }

    fun incrementStreak(habitId: Long, timestamp: Long) {
        habitRepository.incrementStreak(habitId, timestamp)
    }
}