package com.example.fortpointproperties.shared.ui

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
import com.example.fortpointproperties.features.auth.data.UserResponse

object MobileHeaderBinder {

    fun bind(
        context: Context,
        profileImageView: ImageView,
        initialsView: TextView,
        nameView: TextView,
        firstName: String?,
        lastName: String?,
        email: String?,
        profileImageUrl: String?
    ) {
        nameView.text = buildDisplayName(firstName, lastName, email)
        bindAvatar(context, profileImageView, initialsView, buildInitials(firstName, lastName, email), profileImageUrl)
    }

    fun bind(
        context: Context,
        profileImageView: ImageView,
        initialsView: TextView,
        nameView: TextView,
        user: UserResponse
    ) {
        bind(
            context = context,
            profileImageView = profileImageView,
            initialsView = initialsView,
            nameView = nameView,
            firstName = user.firstname,
            lastName = user.lastname,
            email = user.email,
            profileImageUrl = user.profileImageUrl
        )
    }

    fun buildDisplayName(firstName: String?, lastName: String?, email: String?): String {
        val first = firstName?.trim().orEmpty()
        val last = lastName?.trim().orEmpty()
        val combined = listOf(first, last).filter { it.isNotBlank() }.joinToString(" ").trim()
        return when {
            combined.isNotBlank() -> combined
            !email.isNullOrBlank() -> email.substringBefore("@").replace('.', ' ').replace('_', ' ').trim()
                .ifBlank { "Registered User" }
            else -> "Registered User"
        }
    }

    fun buildInitials(firstName: String?, lastName: String?, email: String?): String {
        val names = listOfNotNull(
            firstName?.trim()?.takeIf { it.isNotBlank() },
            lastName?.trim()?.takeIf { it.isNotBlank() }
        )

        val initials = if (names.isNotEmpty()) {
            names.take(2).mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
        } else {
            email?.substringBefore("@")
                ?.split(Regex("[._\\-\\s]+"))
                ?.filter { it.isNotBlank() }
                ?.take(2)
                ?.mapNotNull { it.firstOrNull()?.uppercaseChar() }
                ?.joinToString("")
                .orEmpty()
        }

        return initials.ifBlank { "U" }
    }

    private fun bindAvatar(
        context: Context,
        profileImageView: ImageView,
        initialsView: TextView,
        initials: String,
        profileImageUrl: String?
    ) {
        initialsView.text = initials
        if (profileImageUrl.isNullOrBlank()) {
            profileImageView.setImageDrawable(null)
            profileImageView.visibility = View.GONE
            initialsView.visibility = View.VISIBLE
            return
        }

        profileImageView.visibility = View.VISIBLE
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
                    profileImageView.visibility = View.GONE
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
                    profileImageView.visibility = View.VISIBLE
                    initialsView.visibility = View.GONE
                    return false
                }
            })
            .into(profileImageView)
    }
}
