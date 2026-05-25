package com.example.fortpointproperties.features.properties.ui.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.fortpointproperties.R
import com.example.fortpointproperties.features.properties.data.model.PropertyCardDto
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class PropertyAdapter(
    private val context: Context,
    private var items: List<PropertyCardDto>,
    private val onItemClick: (PropertyCardDto) -> Unit,
    private val actionMode: CardActionMode = CardActionMode.NONE,
    private val onActionClick: ((PropertyCardDto) -> Unit)? = null,
) : BaseAdapter() {

    enum class CardActionMode {
        NONE,
        TOGGLE_FAVORITE,
        REMOVE_FAVORITE
    }

    private val priceFormatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale("en", "PH")).apply {
        currency = Currency.getInstance("PHP")
        minimumFractionDigits = 0
        maximumFractionDigits = 0
    }

    private var favoriteIds: Set<String> = emptySet()

    private data class ViewHolder(
        val imageContainer: FrameLayout,
        val coverImage: ImageView,
        val promoBadge: TextView,
        val favoriteButton: ImageButton,
        val priceView: TextView,
        val nameView: TextView,
        val locationView: TextView,
        val descriptionView: TextView,
        val listingTypesContainer: LinearLayout,
    )

    fun submitList(newItems: List<PropertyCardDto>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun submitFavoriteIds(newFavoriteIds: Set<String>) {
        favoriteIds = newFavoriteIds
        notifyDataSetChanged()
    }

    fun isFavorited(propertyId: String?): Boolean {
        return !propertyId.isNullOrBlank() && favoriteIds.contains(propertyId)
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_property_card, parent, false)

        val holder = (view.tag as? ViewHolder) ?: ViewHolder(
            imageContainer = view.findViewById(R.id.layoutImageContainer),
            coverImage = view.findViewById(R.id.ivCover),
            promoBadge = view.findViewById(R.id.tvPromoBadge),
            favoriteButton = view.findViewById(R.id.btnFavorite),
            priceView = view.findViewById(R.id.tvPrice),
            nameView = view.findViewById(R.id.tvName),
            locationView = view.findViewById(R.id.tvLocation),
            descriptionView = view.findViewById(R.id.tvDescription),
            listingTypesContainer = view.findViewById(R.id.llListingTypes),
        ).also { view.tag = it }

        val item = items[position]

        loadPropertyImage(holder.coverImage, item.coverPhotoUrl)
        holder.promoBadge.visibility = if (item.hasPromo == true) View.VISIBLE else View.GONE
        holder.priceView.text = formatPriceRange(item.priceRangeMin, item.priceRangeMax)
        holder.nameView.text = item.name?.takeIf { it.isNotBlank() } ?: "Untitled Property"
        holder.locationView.text = item.location?.takeIf { it.isNotBlank() } ?: "Location unavailable"
        holder.descriptionView.text = item.basicDescription?.takeIf { it.isNotBlank() } ?: "No description available."

        populateChipContainer(holder.listingTypesContainer, item.listingTypes)
        configureFavoriteButton(holder.favoriteButton, item)

        view.setOnClickListener { onItemClick(item) }
        return view
    }

    private fun configureFavoriteButton(button: ImageButton, item: PropertyCardDto) {
        if (actionMode == CardActionMode.NONE) {
            button.visibility = View.GONE
            button.setOnClickListener(null)
            return
        }

        button.visibility = View.VISIBLE
        button.setBackgroundResource(R.drawable.bg_favorite_circle)
        // The overlaid heart has its own listener so tapping it does not open PropertyDetailActivity.
        button.setOnClickListener { onActionClick?.invoke(item) }

        when (actionMode) {
            CardActionMode.TOGGLE_FAVORITE -> {
                val favorited = isFavorited(item.id)
                button.setImageResource(if (favorited) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border)
                button.contentDescription = context.getString(
                    if (favorited) R.string.favorite_remove_from_favorites else R.string.favorite_add_to_favorites
                )
            }

            CardActionMode.REMOVE_FAVORITE -> {
                button.setImageResource(R.drawable.ic_favorite_filled)
                button.contentDescription = context.getString(R.string.favorite_remove_from_favorites)
            }

            CardActionMode.NONE -> Unit
        }
    }

    private fun populateChipContainer(container: LinearLayout, values: List<String>) {
        container.removeAllViews()

        val chips = values
            .map { normalizeChipLabel(it) }
            .filter { it.isNotBlank() }

        if (chips.isEmpty()) {
            container.visibility = View.GONE
            return
        }

        container.visibility = View.VISIBLE

        chips.chunked(3).forEachIndexed { rowIndex, rowValues ->
            val row = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    if (rowIndex > 0) {
                        topMargin = dp(8)
                    }
                }
            }

            rowValues.forEachIndexed { index, chipText ->
                row.addView(createChipView(chipText).apply {
                    if (index < rowValues.lastIndex) {
                        (layoutParams as LinearLayout.LayoutParams).marginEnd = dp(8)
                    }
                })
            }

            container.addView(row)
        }
    }

    private fun createChipView(label: String): TextView {
        return TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundResource(R.drawable.bg_chip_blue)
            setTextColor(0xFF1D4ED8.toInt())
            text = label
            textSize = 11.5f
            setTypeface(typeface, Typeface.BOLD)
            includeFontPadding = false
            setPadding(dp(12), dp(6), dp(12), dp(6))
        }
    }

    private fun loadPropertyImage(imageView: ImageView, imageUrl: String?) {
        Glide.with(imageView)
            .load(imageUrl?.takeIf { it.isNotBlank() })
            .placeholder(R.drawable.bg_property_image_placeholder)
            .error(R.drawable.bg_property_image_placeholder)
            .fallback(R.drawable.bg_property_image_placeholder)
            .centerCrop()
            .into(imageView)
    }

    private fun formatPriceRange(min: Double?, max: Double?): String {
        return when {
            min == null && max == null -> "Price available upon request"
            min != null && max != null && min == max -> formatPrice(min)
            min != null && max != null -> "${formatPrice(min)} - ${formatPrice(max)}"
            min != null -> "From ${formatPrice(min)}"
            else -> "Up to ${formatPrice(max)}"
        }
    }

    private fun normalizeChipLabel(value: String): String {
        val raw = value.trim()
        if (raw.isBlank()) {
            return ""
        }

        val normalized = raw
            .replace('_', '-')
            .replace(Regex("\\s+"), "-")
            .lowercase(Locale.getDefault())

        return if (normalized == "rfo") {
            "RFO"
        } else {
            normalized
                .split('-')
                .filter { it.isNotBlank() }
                .joinToString("-") { part ->
                    part.replaceFirstChar { char -> char.uppercaseChar() }
                }
        }
    }

    private fun formatPrice(value: Double?): String {
        return if (value == null) {
            "Price available upon request"
        } else {
            priceFormatter.format(value)
        }
    }

    private fun dp(value: Int): Int {
        return (value * context.resources.displayMetrics.density).toInt()
    }
}
