package ascending.com.ascendingg.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val timeOfDay: String,
    val isActive: Boolean = true,
    val targetFrequency: Int = 1,
    val completionStreak: Int = 0,
    val lastCompleted: Long = 0
)