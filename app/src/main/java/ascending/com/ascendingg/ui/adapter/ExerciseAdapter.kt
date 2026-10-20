package ascending.com.ascendingg.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ascending.com.ascendingg.R
import ascending.com.ascendingg.data.model.Exercise
import ascending.com.ascendingg.databinding.ItemExerciseBinding

class ExerciseAdapter : ListAdapter<Exercise, ExerciseViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Exercise>() {
            override fun areItemsTheSame(oldItem: Exercise, newItem: Exercise) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Exercise, newItem: Exercise) =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemExerciseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ExerciseViewHolder(private val binding: ItemExerciseBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(exercise: Exercise) {
        binding.apply {
            tvName.text = exercise.name
            tvDescription.text = exercise.description
            tvCategory.text = "${exercise.category} • ${exercise.timeOfDay}"
            ivThumb.setImageResource(
                when(exercise.category) {
                    "Neck & Posture" -> R.drawable.ic_fitness_center_24
                    "Jaw Development" -> R.drawable.ic_fitness_center_24
                    "Breathing" -> R.drawable.ic_school_24
                    "Recovery" -> R.drawable.ic_school_24
                    else -> R.drawable.ic_fitness_center_24
                }
            )
            checkBoxCompleted.isChecked = exercise.completed
        }
    }
}