package ascending.com.ascendingg.data.model

data class Habit(
    val id: Long,
    val name: String,
    val category: String,
    val timeOfDay: String,
    val isActive: Boolean,
    val targetFrequency: Int,
    val completionStreak: Int,
    val lastCompleted: Long
)
