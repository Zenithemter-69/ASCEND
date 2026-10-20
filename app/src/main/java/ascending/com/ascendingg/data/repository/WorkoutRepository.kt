package ascending.com.ascendingg.data.repository

import ascending.com.ascendingg.data.database.AppDatabase
import ascending.com.ascendingg.data.database.WorkoutEntity
import ascending.com.ascendingg.data.model.Workout
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.viewModelScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepository @Inject constructor(
    private val database: AppDatabase
) {
    private val workoutDao = database.workoutDao()

    val workouts: LiveData<List<Workout>> = workoutDao.getUserWorkouts(Constants.DEFAULT_USER_ID)
        .map { it.map {
            Workout(
                it.id, it.userId, it.type, it.duration, it.exercises, it.notes, it.timestamp
            )
        } }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    suspend fun addWorkout(workout: WorkoutEntity) = workoutDao.insertAll(arrayOf(workout))
}