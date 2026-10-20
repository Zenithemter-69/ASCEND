package ascending.com.ascendingg.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ascending.com.ascendingg.data.model.Subject
import ascending.com.ascendingg.databinding.ItemSubjectBinding

class SubjectAdapter : ListAdapter<Subject, SubjectViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Subject>() {
            override fun areItemsTheSame(oldItem: Subject, newItem: Subject) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Subject, newItem: Subject) =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val binding = ItemSubjectBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SubjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SubjectViewHolder(private val binding: ItemSubjectBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(subject: Subject) {
        binding.apply {
            tvName.text = subject.name
            tvChapters.text = "${subject.chapters} chapters"
            tvHours.text = "${subject.hoursToday}h today"
            // Color-code progress indicator
            val color = Color.parseColor(subject.colorCode)
            viewColorIndicator.setBackgroundColor(color)
        }
    }
}