package com.example.fortpointproperties.features.messaging.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.fortpointproperties.R
import com.example.fortpointproperties.features.messaging.data.model.MessageDto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

class MessageAdapter(
    private val context: Context,
) : BaseAdapter() {
    private val items = mutableListOf<MessageDto>()
    private var currentUserId: String? = null
    private var currentUserDisplayName: String? = null

    companion object {
        private const val VIEW_TYPE_RECEIVED = 0
        private const val VIEW_TYPE_SENT = 1
    }

    fun submitList(
        messages: List<MessageDto>,
        currentUserId: String?,
        currentUserDisplayName: String? = null,
    ) {
        this.currentUserId = currentUserId
        this.currentUserDisplayName = currentUserDisplayName
        items.clear()
        items.addAll(messages)
        notifyDataSetChanged()
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): MessageDto = items[position]

    override fun getItemId(position: Int): Long = items[position].id ?: position.toLong()

    override fun getViewTypeCount(): Int = 2

    override fun getItemViewType(position: Int): Int {
        return if (isMine(items[position])) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val message = getItem(position)
        val mine = isMine(message)
        val layoutId = if (mine) R.layout.item_message_sent else R.layout.item_message_received
        val view = convertView ?: LayoutInflater.from(context).inflate(layoutId, parent, false)
        val holder = (view.tag as? ViewHolder) ?: ViewHolder(view).also { view.tag = it }

        val senderName = resolveSenderName(message)
        bindAvatar(
            imageView = holder.ivAvatar,
            initialsView = holder.tvAvatar,
            displayName = senderName,
            profileImageUrl = message.senderProfileImageUrl,
        )
        holder.tvContent.text = message.content?.takeIf { it.isNotBlank() } ?: ""
        holder.tvSenderName.text = senderName
        holder.tvTime.text = formatDate(message.createdAt)
        return view
    }

    private fun isMine(message: MessageDto): Boolean {
        return !currentUserId.isNullOrBlank() && message.senderId == currentUserId
    }

    private fun resolveSenderName(message: MessageDto): String {
        val sender = message.senderName?.takeIf { it.isNotBlank() }
        if (!sender.isNullOrBlank()) {
            return sender
        }
        return if (isMine(message)) {
            currentUserDisplayName?.takeIf { it.isNotBlank() } ?: "You"
        } else {
            "Sender"
        }
    }

    private fun formatDate(value: String?): String {
        val raw = value?.takeIf { it.isNotBlank() } ?: return ""
        return try {
            val parsed = LocalDateTime.parse(raw)
            parsed.format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))
        } catch (_: DateTimeParseException) {
            raw
        } catch (_: Exception) {
            raw
        }
    }

    private fun bindAvatar(
        imageView: ImageView,
        initialsView: TextView,
        displayName: String?,
        profileImageUrl: String?,
    ) {
        MessagingAvatarBinder.bind(
            context = context,
            imageView = imageView,
            initialsView = initialsView,
            displayName = displayName,
            profileImageUrl = profileImageUrl,
        )
    }

    private class ViewHolder(view: View) {
        val ivAvatar: ImageView = view.findViewById(R.id.ivMessageAvatar)
        val tvAvatar: TextView = view.findViewById(R.id.tvMessageAvatar)
        val tvContent: TextView = view.findViewById(R.id.tvMessageContent)
        val tvSenderName: TextView = view.findViewById(R.id.tvMessageSenderName)
        val tvTime: TextView = view.findViewById(R.id.tvMessageTime)
    }
}
