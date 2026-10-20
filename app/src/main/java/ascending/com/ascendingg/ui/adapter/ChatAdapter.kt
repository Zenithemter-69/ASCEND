package ascending.com.ascendingg.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ascending.com.ascendingg.R
import ascending.com.ascendingg.databinding.ItemChatMessageBinding

class ChatAdapter : ListAdapter<ChatMessage, ChatViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ChatMessage>() {
            override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage) =
                oldItem == newItem
        }
    }

    fun addUserMessage(text: String) {
        submitList(currentList.toMutableList().apply {
            add(ChatMessage(id = System.currentTimeMillis(), text = text, isUser = true))
        })
    }

    fun addAIMessage(text: String) {
        submitList(currentList.toMutableList().apply {
            add(ChatMessage(id = System.currentTimeMillis() + 1, text = text, isUser = false))
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatMessageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ChatViewHolder(private val binding: ItemChatMessageBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(message: ChatMessage) {
        binding.apply {
            tvMessage.text = message.text
            root.setBackgroundResource(
                if (message.isUser) android.R.color.white
                else R.drawable.bg_ai_message
            )
        }
    }
}

data class ChatMessage(
    val id: Long,
    val text: String,
    val isUser: Boolean
)