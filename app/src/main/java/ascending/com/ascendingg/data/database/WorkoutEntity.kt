package ascending.com.ascendingg.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import ascending.com.ascendingg.data.utils.Constants

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long = Constants.DEFAULT_USER_ID,
    val type: String,
    val duration: Int,
    val exercises: String,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)