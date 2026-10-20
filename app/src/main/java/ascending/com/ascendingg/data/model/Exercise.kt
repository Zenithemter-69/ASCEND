package ascending.com.ascendingg.data.model

data class Exercise(
    val id: Int,
    val name: String,
    val description: String,
    val instructions: String,
    val timeOfDay: String,
    val category: String,
    val completed: Boolean = false
)