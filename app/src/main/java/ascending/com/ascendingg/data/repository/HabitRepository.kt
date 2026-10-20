package ascending.com.ascendingg.data.repository

import ascending.com.ascendingg.data.database.AppDatabase
import ascending.com.ascendingg.data.database.HabitEntity
import ascending.com.ascendingg.data.model.Habit
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.asLiveData
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepository @Inject constructor(
    private val database: AppDatabase
) {
    private val habitDao = database.habitDao()

    // Fetch all habits as LiveData (observed by ViewModel/HomeFragment)
    fun getAllHabits(): LiveData<List<Habit>> = habitDao.getAllHabits()
        .map { it.map {
            Habit(
                it.id, it.name, it.category, it.timeOfDay,
                it.isActive, it.targetFrequency, it.completionStreak, it.lastCompleted
            )
        } }.asLiveData()

    // Insert a new habit (e.g., from UI creation flow)
    suspend fun addHabit(habit: HabitEntity) = habitDao.insertAll(habit)

    // Update an existing habit (e.g., when completion state changes)
    suspend fun updateHabit(habit: HabitEntity) = habitDao.update(habit)

    // Increment the completion streak and set lastCompleted timestamp
    suspend fun incrementStreak(id: Long, timestamp: Long) =
        habitDao.incrementStreak(id, timestamp)
}