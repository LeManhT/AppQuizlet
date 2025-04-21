package com.example.appquizlet.adapter.newfeature

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.R
import com.example.appquizlet.model.newfeature.Message
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ChatAdapter(private var messages: List<Message>) :
    RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutId = if (viewType == 1) R.layout.item_message_sent else R.layout.item_message_received
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isSentByUser == true) 1 else 0
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageContent: TextView = itemView.findViewById(R.id.messageContent)
        private val messageTimestamp: TextView = itemView.findViewById(R.id.messageTimestamp)

        fun bind(message: Message) {
            messageContent.text = message.content
            messageTimestamp.text = formatTimestamp(message.timestamp)
        }
        private fun formatTimestamp(timestamp: Long): String {
            val messageDate = Calendar.getInstance().apply { timeInMillis = timestamp }
            val currentDate = Calendar.getInstance()

            return when {
                isSameDay(messageDate, currentDate) -> {
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(messageDate.time)
                }
                isYesterday(messageDate, currentDate) -> {
                    "Yesterday"
                }
                else -> {
                    SimpleDateFormat("EEE 'at' HH:mm", Locale.getDefault()).format(messageDate.time)
                }
            }
        }

        private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
        }

        private fun isYesterday(cal1: Calendar, cal2: Calendar): Boolean {
            cal2.add(Calendar.DAY_OF_YEAR, -1)
            return isSameDay(cal1, cal2)
        }
    }

    fun updateMessages(newMessages: List<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }
}