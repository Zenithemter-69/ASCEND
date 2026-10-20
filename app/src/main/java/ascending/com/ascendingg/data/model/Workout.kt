package ascending.com.ascendingg.data.model

data class Workout(
    val id: Long,
    val userId: Long,
    val type: String,
    val duration: Int,
    val exercises: String,
    val notes: String,
    val timestamp: Long
)