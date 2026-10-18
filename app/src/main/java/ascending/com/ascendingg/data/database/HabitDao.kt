package ascending.com.ascendingg.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE isActive = 1 ORDER BY id")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Insert
    suspend fun insertAll(varargs habits: HabitEntity): Long

    @Update
    suspend fun update(habit: HabitEntity)

    @Query("UPDATE habits SET completionStreak = completionStreak + 1, lastCompleted = :timestamp WHERE id = :id")
    suspend fun incrementStreak(id: Long, timestamp: Long)
}
