package ascending.com.ascendingg.data.model

data class RoutineItem(
    val id: Int,
    val name: String,
    val timeSlot: String,
    val isCompleted: Boolean = false
)