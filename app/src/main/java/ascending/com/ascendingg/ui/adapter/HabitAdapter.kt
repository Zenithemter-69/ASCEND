package ascending.com.ascendingg.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ascending.com.ascendingg.data.model.Habit
import ascending.com.ascendingg.databinding.ItemHabitBinding

class HabitAdapter(
    private val onHabitClick: (Long) -> Unit
) : ListAdapter<Habit, HabitAdapter.HabitViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Habit>() {
            override fun areItemsTheSame(oldItem: Habit, newItem: Habit) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Habit, newItem: Habit) =
                oldItem == newItem
        }
    }

    inner class HabitViewHolder(private val binding: ItemHabitBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(habit: Habit) {
            binding.apply {
                tvHabitName.text = habit.name
                tvCategory.text = "${habit.category} • ${habit.timeOfDay}"
                cbHabit.setOnCheckedChangeListener(null)
                cbHabit.isChecked = habit.lastCompleted > 0
                cbHabit.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) onHabitClick(habit.id)
                }
                tvStreak.text = "${habit.completionStreak}d 🔥"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}