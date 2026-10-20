package ascending.com.ascendingg.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ascending.com.ascendingg.data.model.RoutineItem
import ascending.com.ascendingg.databinding.ItemRoutineBinding

class RoutineAdapter(
    private val onRoutineClick: (Int) -> Unit
) : ListAdapter<RoutineItem, RoutineViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RoutineItem>() {
            override fun areItemsTheSame(oldItem: RoutineItem, newItem: RoutineItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: RoutineItem, newItem: RoutineItem) =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val binding = ItemRoutineBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RoutineViewHolder(binding, onRoutineClick)
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class RoutineViewHolder(private val binding: ItemRoutineBinding, private val onRoutineClick: (Int) -> Unit) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: RoutineItem) {
        binding.apply {
            tvTaskName.text = item.name
            tvTime.text = item.timeSlot
            checkBoxCompleted.setOnCheckedChangeListener(null)
            checkBoxCompleted.isChecked = item.isCompleted
            checkBoxCompleted.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) onRoutineClick(item.id)
            }
        }
    }
}