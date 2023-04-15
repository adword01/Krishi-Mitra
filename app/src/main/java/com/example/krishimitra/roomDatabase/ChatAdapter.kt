package com.example.krishimitra.roomDatabase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.krishimitra.R
import com.example.krishimitra.models.chatMessage

class ChatAdapter(private val messages: List<chatMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_USER_MESSAGE = 1
    private val TYPE_BOT_MESSAGE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_USER_MESSAGE -> UserMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_user_message, parent, false))
            TYPE_BOT_MESSAGE -> BotMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_bot_message, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (getItemViewType(position)) {
            TYPE_USER_MESSAGE -> {
                val userHolder = holder as UserMessageViewHolder
                userHolder.bind(message)
            }
            TYPE_BOT_MESSAGE -> {
                val botHolder = holder as BotMessageViewHolder
                botHolder.bind(message)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.isBotMessage) TYPE_BOT_MESSAGE else TYPE_USER_MESSAGE
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    private inner class UserMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.user_tv_message)

        fun bind(chatMessage: chatMessage) {
            messageTextView.text = chatMessage.message
        }
    }

    private inner class BotMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.bot_tv_message)

        fun bind(chatMessage: chatMessage) {
            messageTextView.text = chatMessage.message
        }
    }
}

