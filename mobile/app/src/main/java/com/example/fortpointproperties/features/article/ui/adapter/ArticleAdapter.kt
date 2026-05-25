package com.example.fortpointproperties.features.article.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.fortpointproperties.R
import com.example.fortpointproperties.features.article.data.model.ArticleCardDto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

class ArticleAdapter(
    private val context: Context,
    private var items: List<ArticleCardDto>,
    private val onItemClick: (ArticleCardDto) -> Unit,
) : BaseAdapter() {

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    fun submitList(newItems: List<ArticleCardDto>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_article_card, parent, false)

        val item = items[position]

        val ivCover = view.findViewById<ImageView>(R.id.ivCover)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvDescription = view.findViewById<TextView>(R.id.tvDescription)
        val tvAuthor = view.findViewById<TextView>(R.id.tvAuthor)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)

        loadImage(ivCover, item.coverPhotoUrl)
        tvTitle.text = item.title?.takeIf { it.isNotBlank() } ?: "Untitled Blog"
        tvDescription.text = item.shortDescription?.takeIf { it.isNotBlank() } ?: "No description available."
        tvAuthor.text = item.authorName?.takeIf { it.isNotBlank() } ?: "Unknown author"
        tvDate.text = formatDate(item.latestDate)

        view.setOnClickListener { onItemClick(item) }
        return view
    }

    private fun loadImage(imageView: ImageView, imageUrl: String?) {
        Glide.with(imageView)
            .load(imageUrl?.takeIf { it.isNotBlank() })
            .placeholder(R.drawable.bg_property_image_placeholder)
            .error(R.drawable.bg_property_image_placeholder)
            .fallback(R.drawable.bg_property_image_placeholder)
            .centerCrop()
            .into(imageView)
    }

    private fun formatDate(dateValue: String?): String {
        val raw = dateValue?.takeIf { it.isNotBlank() } ?: return "No date"

        return try {
            val parsed = LocalDateTime.parse(raw)
            parsed.format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault()))
        } catch (_: DateTimeParseException) {
            raw
        } catch (_: Exception) {
            raw
        }
    }
}
