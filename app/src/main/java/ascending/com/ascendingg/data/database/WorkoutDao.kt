package ascending.com.ascendingg.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WorkoutDao {
    @Insert
    fun insertAll(workouts: Array<WorkoutEntity>): LongArray

    @Query("SELECT * FROM workouts WHERE userId = :userId ORDER BY timestamp DESC")
    fun getUserWorkouts(userId: Long): kotlinx.coroutines.flow.Flow<List<WorkoutEntity>>
}