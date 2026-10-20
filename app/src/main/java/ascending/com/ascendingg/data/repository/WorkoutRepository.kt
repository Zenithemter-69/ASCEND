package ascending.com.ascendingg.data.repository

import ascending.com.ascendingg.data.database.AppDatabase
import ascending.com.ascendingg.data.database.WorkoutEntity
import ascending.com.ascendingg.data.model.Workout
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.asLiveData
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepository @Inject constructor(
    private val database: AppDatabase
) {
    private val workoutDao = database.workoutDao()

    fun getWorkouts(userId: Long): LiveData<List<Workout>> = workoutDao.getUserWorkouts(userId)
        .map { it.map {
            Workout(
                it.id, it.userId, it.type, it.duration, it.exercises, it.notes, it.timestamp
            )
        } }.asLiveData()

    suspend fun addWorkout(workout: WorkoutEntity) = workoutDao.insertAll(workout)
}