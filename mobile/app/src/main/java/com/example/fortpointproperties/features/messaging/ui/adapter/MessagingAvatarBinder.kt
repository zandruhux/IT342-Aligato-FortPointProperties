package com.example.fortpointproperties.features.messaging.ui.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.fortpointproperties.R

object MessagingAvatarBinder {
    fun bind(
        context: Context,
        imageView: ImageView,
        initialsView: TextView,
        displayName: String?,
        profileImageUrl: String?,
    ) {
        val initials = buildInitials(displayName)
        initialsView.text = initials

        if (profileImageUrl.isNullOrBlank()) {
            imageView.setImageDrawable(null)
            imageView.visibility = View.GONE
            initialsView.visibility = View.VISIBLE
            return
        }

        imageView.visibility = View.VISIBLE
        initialsView.visibility = View.VISIBLE

        Glide.with(context)
            .load(profileImageUrl)
            .placeholder(R.drawable.bg_avatar_circle)
            .error(R.drawable.bg_avatar_circle)
            .circleCrop()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    imageView.visibility = View.GONE
                    initialsView.visibility = View.VISIBLE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    imageView.visibility = View.VISIBLE
                    initialsView.visibility = View.GONE
                    return false
                }
            })
            .into(imageView)
    }

    private fun buildInitials(displayName: String?): String {
        val parts = displayName
            ?.trim()
            .orEmpty()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }

        val initials = parts
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .joinToString("")

        return initials.ifBlank { "U" }
    }
}
