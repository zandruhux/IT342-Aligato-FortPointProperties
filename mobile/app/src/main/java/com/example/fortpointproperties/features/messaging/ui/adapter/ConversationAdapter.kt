package com.example.fortpointproperties.features.messaging.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.fortpointproperties.R
import com.example.fortpointproperties.features.messaging.data.model.ConversationDto
import com.example.fortpointproperties.features.messaging.ui.adapter.MessagingAvatarBinder.bind
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

class ConversationAdapter(
    private val context: Context,
    private val onItemClick: (ConversationDto) -> Unit,
) : BaseAdapter() {
    private val items = mutableListOf<ConversationDto>()
    private var currentUserId: String? = null

    fun submitList(conversations: List<ConversationDto>, currentUserId: String?) {
        this.currentUserId = currentUserId
        items.clear()
        items.addAll(conversations)
        notifyDataSetChanged()
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): ConversationDto = items[position]

    override fun getItemId(position: Int): Long = items[position].id ?: position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false)
        val holder = (view.tag as? ViewHolder) ?: ViewHolder(view).also { view.tag = it }
        val conversation = getItem(position)
        val displayName = displayName(conversation)
        val preview = previewText(conversation)
        val unread = (conversation.unreadCount ?: 0L) > 0 || conversation.unread == true

        bind(
            context = context,
            imageView = holder.ivAvatar,
            initialsView = holder.tvAvatar,
            displayName = displayName,
            profileImageUrl = displayImageUrl(conversation)
        )

        holder.tvName.text = displayName
        holder.tvPreview.text = preview
        holder.tvPreview.setTextColor(
            context.getColor(if (unread) R.color.property_text_primary else R.color.property_text_secondary)
        )
        holder.tvName.setTextColor(
            context.getColor(if (unread) R.color.property_text_primary else R.color.property_text_primary)
        )
        holder.tvName.setTypeface(holder.tvName.typeface, if (unread) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)
        holder.tvPreview.setTypeface(holder.tvPreview.typeface, if (unread) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)

        val unreadCount = conversation.unreadCount ?: 0L
        if (unreadCount > 0) {
            holder.tvUnread.visibility = View.VISIBLE
            holder.tvUnread.text = if (unreadCount > 99) "99+" else unreadCount.toString()
        } else {
            holder.tvUnread.visibility = View.GONE
        }

        holder.tvTime.text = formatDate(conversation.latestMessageAt ?: conversation.updatedAt)
        holder.tvTime.visibility = if (holder.tvTime.text.isNullOrBlank()) View.GONE else View.VISIBLE

        view.setBackgroundResource(if (unread) R.drawable.bg_conversation_item_unread else R.drawable.bg_conversation_item)
        view.setOnClickListener { onItemClick(conversation) }
        return view
    }

    private fun displayName(conversation: ConversationDto): String {
        return conversation.assignedAgentName
            ?.takeIf { it.isNotBlank() }
            ?: if (conversation.latestMessageSenderId == conversation.assignedAgentId) {
                conversation.latestMessageSenderName?.takeIf { it.isNotBlank() }
            } else {
                null
            }
            ?: "Fort Point Properties"
    }

    private fun displayImageUrl(conversation: ConversationDto): String? {
        return conversation.assignedAgentProfileImageUrl
            ?.takeIf { it.isNotBlank() }
            ?: if (conversation.latestMessageSenderId == conversation.assignedAgentId) {
                conversation.latestMessageSenderProfileImageUrl?.takeIf { it.isNotBlank() }
            } else {
                null
            }
    }

    private fun previewText(conversation: ConversationDto): String {
        val preview = conversation.latestMessagePreview?.trim().orEmpty()
        if (preview.isBlank()) {
            return "No messages yet"
        }

        if (conversation.latestMessageSenderId == currentUserId) {
            return "You: $preview"
        }

        val senderName = conversation.latestMessageSenderName?.takeIf { it.isNotBlank() }
            ?: conversation.assignedAgentName?.takeIf { it.isNotBlank() }
            ?: conversation.registeredUserName?.takeIf { it.isNotBlank() }
        val firstName = senderName?.trim()?.split(Regex("\\s+"))?.firstOrNull().orEmpty()
        return if (firstName.isBlank()) preview else "$firstName: $preview"
    }

    private fun formatDate(value: String?): String {
        val raw = value?.takeIf { it.isNotBlank() } ?: return ""
        return try {
            val parsed = LocalDateTime.parse(raw)
            parsed.format(DateTimeFormatter.ofPattern("MMM d", Locale.getDefault()))
        } catch (_: DateTimeParseException) {
            raw
        } catch (_: Exception) {
            raw
        }
    }

    private class ViewHolder(view: View) {
        val ivAvatar: ImageView = view.findViewById(R.id.ivConversationAvatar)
        val tvAvatar: TextView = view.findViewById(R.id.tvConversationAvatar)
        val tvName: TextView = view.findViewById(R.id.tvConversationName)
        val tvPreview: TextView = view.findViewById(R.id.tvConversationPreview)
        val tvTime: TextView = view.findViewById(R.id.tvConversationTime)
        val tvUnread: TextView = view.findViewById(R.id.tvConversationUnread)
    }
}
