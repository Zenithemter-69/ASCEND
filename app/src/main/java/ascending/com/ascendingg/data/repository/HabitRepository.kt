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

    fun getAllHabits(): LiveData<List<Habit>> = habitDao.getAllHabits()
        .map { it.map {
            Habit(
                it.id, it.name, it.category, it.timeOfDay,
                it.isActive, it.targetFrequency, it.completionStreak, it.lastCompleted
            )
        } }.asLiveData()

    suspend fun addHabit(habit: HabitEntity) = habitDao.insertAll(habit)

    suspend fun updateHabit(habit: HabitEntity) = habitDao.update(habit)

    suspend fun incrementStreak(id: Long, timestamp: Long) =
        habitDao.incrementStreak(id, timestamp)
}