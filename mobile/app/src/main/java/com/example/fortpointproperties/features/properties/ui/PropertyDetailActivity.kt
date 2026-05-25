package com.example.fortpointproperties.features.properties.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.fortpointproperties.R
import com.example.fortpointproperties.features.auth.ui.LoginActivity
import com.example.fortpointproperties.features.properties.data.model.PropertyDetailDto
import com.example.fortpointproperties.features.properties.data.model.PropertyPhotoDto
import com.example.fortpointproperties.features.properties.data.model.PropertyUnitDto
import com.example.fortpointproperties.features.properties.data.repository.PropertyLoginRequiredException
import com.example.fortpointproperties.features.properties.data.repository.PropertyRepository
import com.example.fortpointproperties.features.properties.data.repository.PropertyRoleException
import com.example.fortpointproperties.shared.auth.TokenManager
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class PropertyDetailActivity : AppCompatActivity() {

    private val propertyRepository = PropertyRepository()
    private val priceFormatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale("en", "PH")).apply {
        currency = Currency.getInstance("PHP")
        minimumFractionDigits = 0
        maximumFractionDigits = 0
    }

    private lateinit var btnBack: ImageButton
    private lateinit var progressBar: View
    private lateinit var contentContainer: View
    private lateinit var stateContainer: View
    private lateinit var stateMessage: TextView
    private lateinit var stateAction: Button
    private lateinit var ivMainPhoto: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvPriceRange: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvTurnoverDate: TextView
    private lateinit var tvPetFriendly: TextView
    private lateinit var tvParkingAvailable: TextView
    private lateinit var tvPhotosEmpty: TextView
    private lateinit var layoutPhotoGallery: LinearLayout
    private lateinit var tvUnitsEmpty: TextView
    private lateinit var layoutUnits: LinearLayout
    private lateinit var chipListingTypes: LinearLayout
    private lateinit var chipFinancingTypes: LinearLayout
    private lateinit var chipAmenities: LinearLayout

    private var previewImages: List<String> = emptyList()
    private var currentPreviewIndex: Int = 0
    private var previewDialog: Dialog? = null

    private var propertyId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TokenManager.init(this)
        setContentView(R.layout.activity_property_detail)

        propertyId = intent.getStringExtra(EXTRA_PROPERTY_ID)
        bindViews()
        bindActions()

        if (propertyId.isNullOrBlank()) {
            showErrorState("Property details could not be opened.")
            return
        }

        loadPropertyDetails()
    }

    override fun onDestroy() {
        previewDialog?.dismiss()
        previewDialog = null
        super.onDestroy()
    }

    private fun bindViews() {
        btnBack = findViewById(R.id.btnBack)
        progressBar = findViewById(R.id.progressBar)
        contentContainer = findViewById(R.id.layoutContent)
        stateContainer = findViewById(R.id.layoutState)
        stateMessage = findViewById(R.id.tvStateMessage)
        stateAction = findViewById(R.id.btnStateAction)
        ivMainPhoto = findViewById(R.id.ivMainPhoto)
        tvName = findViewById(R.id.tvName)
        tvLocation = findViewById(R.id.tvLocation)
        tvPriceRange = findViewById(R.id.tvPriceRange)
        tvDescription = findViewById(R.id.tvDescription)
        tvTurnoverDate = findViewById(R.id.tvTurnoverDate)
        tvPetFriendly = findViewById(R.id.tvPetFriendly)
        tvParkingAvailable = findViewById(R.id.tvParkingAvailable)
        tvPhotosEmpty = findViewById(R.id.tvPhotosEmpty)
        layoutPhotoGallery = findViewById(R.id.layoutPhotoGallery)
        tvUnitsEmpty = findViewById(R.id.tvUnitsEmpty)
        layoutUnits = findViewById(R.id.layoutUnits)
        chipListingTypes = findViewById(R.id.chipListingTypes)
        chipFinancingTypes = findViewById(R.id.chipFinancingTypes)
        chipAmenities = findViewById(R.id.chipAmenities)
    }

    private fun bindActions() {
        btnBack.setOnClickListener { finish() }
        stateAction.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadPropertyDetails() {
        showLoading()
        lifecycleScope.launch {
            try {
                val property = propertyRepository.getPropertyDetails(propertyId.orEmpty())
                showProperty(property)
            } catch (error: PropertyLoginRequiredException) {
                TokenManager.clear()
                showAuthState(error.message ?: "Login required to view property details.")
            } catch (error: PropertyRoleException) {
                TokenManager.clear()
                showUnavailableState(error.message ?: "This mobile module is only for registered users.")
            } catch (error: Exception) {
                showErrorState(error.message ?: "Failed to load property details.")
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        contentContainer.visibility = View.GONE
        stateContainer.visibility = View.GONE
    }

    private fun showProperty(property: PropertyDetailDto) {
        progressBar.visibility = View.GONE
        stateContainer.visibility = View.GONE
        contentContainer.visibility = View.VISIBLE

        tvName.text = property.name?.takeIf { it.isNotBlank() } ?: "Untitled Property"
        tvLocation.text = property.location?.takeIf { it.isNotBlank() } ?: "Location unavailable"
        tvPriceRange.text = formatPriceRange(property.priceRangeMin, property.priceRangeMax)
        tvDescription.text = property.basicDescription?.takeIf { it.isNotBlank() } ?: "No description available."
        tvTurnoverDate.text = property.turnoverDate?.takeIf { it.isNotBlank() } ?: "No turnover date listed."
        tvPetFriendly.text = formatYesNo(property.petFriendly)
        tvParkingAvailable.text = formatYesNo(property.parkingAvailable)

        previewImages = buildPreviewImages(property.photos)
        currentPreviewIndex = 0

        loadMainPhoto(previewImages)
        ivMainPhoto.setOnClickListener {
            if (previewImages.isNotEmpty()) {
                showImagePreview(0)
            }
        }

        populateChipContainer(chipListingTypes, property.listingTypes, accentChips = true)
        populateChipContainer(chipFinancingTypes, property.financingTypes, accentChips = true)
        populateChipContainer(chipAmenities, property.amenities.mapNotNull { it.name })
        bindPhotoGallery(previewImages)
        bindUnitCards(property.units)
    }

    private fun showAuthState(message: String) {
        progressBar.visibility = View.GONE
        contentContainer.visibility = View.GONE
        stateContainer.visibility = View.VISIBLE
        stateMessage.text = message
        stateAction.text = "Open Login"
        stateAction.visibility = View.VISIBLE
        stateAction.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showUnavailableState(message: String) {
        progressBar.visibility = View.GONE
        contentContainer.visibility = View.GONE
        stateContainer.visibility = View.VISIBLE
        stateMessage.text = message
        stateAction.text = "Open Login"
        stateAction.visibility = View.VISIBLE
        stateAction.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showErrorState(message: String) {
        progressBar.visibility = View.GONE
        contentContainer.visibility = View.GONE
        stateContainer.visibility = View.VISIBLE
        stateMessage.text = message
        stateAction.text = "Retry"
        stateAction.visibility = View.VISIBLE
        stateAction.setOnClickListener { loadPropertyDetails() }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun loadMainPhoto(imageUrls: List<String>) {
        val firstPhotoUrl = imageUrls.firstOrNull()

        Glide.with(this)
            .load(firstPhotoUrl?.takeIf { it.isNotBlank() })
            .placeholder(R.drawable.bg_property_image_placeholder)
            .error(R.drawable.bg_property_image_placeholder)
            .fallback(R.drawable.bg_property_image_placeholder)
            .transform(CenterCrop(), RoundedCorners(dp(18)))
            .into(ivMainPhoto)
    }

    private fun bindPhotoGallery(imageUrls: List<String>) {
        layoutPhotoGallery.removeAllViews()

        val extraPhotos = imageUrls.drop(1)
        if (extraPhotos.isEmpty()) {
            tvPhotosEmpty.visibility = View.VISIBLE
            layoutPhotoGallery.visibility = View.GONE
            return
        }

        tvPhotosEmpty.visibility = View.GONE
        layoutPhotoGallery.visibility = View.VISIBLE

        extraPhotos.forEachIndexed { index, photoUrl ->
            val imageView = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(180)
                ).apply {
                    topMargin = if (index == 0) 0 else dp(12)
                }
                background = ContextCompat.getDrawable(this@PropertyDetailActivity, R.drawable.bg_property_image_placeholder)
                contentDescription = "Property photo ${index + 2}"
                scaleType = ImageView.ScaleType.CENTER_CROP
                clipToOutline = true
            }

            Glide.with(this)
                .load(photoUrl)
                .placeholder(R.drawable.bg_property_image_placeholder)
                .error(R.drawable.bg_property_image_placeholder)
                .fallback(R.drawable.bg_property_image_placeholder)
                .transform(CenterCrop(), RoundedCorners(dp(16)))
                .into(imageView)

            imageView.setOnClickListener {
                showImagePreview(index + 1)
            }

            layoutPhotoGallery.addView(imageView)
        }
    }

    private fun bindUnitCards(units: List<PropertyUnitDto>) {
        layoutUnits.removeAllViews()

        if (units.isEmpty()) {
            tvUnitsEmpty.visibility = View.VISIBLE
            return
        }

        tvUnitsEmpty.visibility = View.GONE

        units.forEach { unit ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                background = ContextCompat.getDrawable(this@PropertyDetailActivity, R.drawable.bg_card_rounded)
                setPadding(dp(16), dp(16), dp(16), dp(16))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(12)
                }
            }

            val unitType = unit.unitType?.takeIf { it.isNotBlank() } ?: "N/A"
            val totalPrice = formatPrice(unit.totalSellingPrice)

            val headerRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val unitTitle = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = unitType
                setTextColor(0xFF111827.toInt())
                setTextSize(15f)
                setTypeface(typeface, Typeface.BOLD)
            }

            val priceView = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                text = totalPrice
                setTextColor(0xFF1D4ED8.toInt())
                setTextSize(14f)
                setTypeface(typeface, Typeface.BOLD)
            }

            headerRow.addView(unitTitle)
            headerRow.addView(priceView)
            card.addView(headerRow)

            card.addView(View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(1)
                ).apply {
                    topMargin = dp(12)
                    bottomMargin = dp(12)
                }
                setBackgroundColor(0xFFE5E7EB.toInt())
            })

            addUnitRow(card, "Floor Area", formatNullableNumber(unit.floorArea, "sqm"))
            addUnitRow(card, "Lot Area", formatNullableNumber(unit.lotArea, "sqm"))
            addUnitRow(card, "Reservation Fee", formatPrice(unit.reservationFee))
            addUnitRow(card, "Equity Period", unit.equityPeriodMonths?.let { "$it months" } ?: "N/A")
            addUnitRow(card, "Monthly Equity", formatPrice(unit.monthlyEquity))

            layoutUnits.addView(card)
        }
    }

    private fun addUnitRow(parent: LinearLayout, label: String, value: String, emphasized: Boolean = false) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(8)
            }
        }

        val labelView = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.45f)
            text = "$label:"
            setTextColor(0xFF6B7280.toInt())
            setTextSize(12f)
            setTypeface(typeface, Typeface.BOLD)
        }

        val valueView = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.55f)
            text = value
            setTextColor(if (emphasized) 0xFF111827.toInt() else 0xFF374151.toInt())
            setTextSize(13f)
            setTypeface(typeface, if (emphasized) Typeface.BOLD else Typeface.NORMAL)
        }

        row.addView(labelView)
        row.addView(valueView)
        parent.addView(row)
    }

    private fun populateChipContainer(container: LinearLayout, items: List<String>, accentChips: Boolean = false) {
        container.removeAllViews()

        val labels = items
            .map { normalizeLabel(it) }
            .filter { it.isNotBlank() }

        if (labels.isEmpty()) {
            val empty = TextView(this).apply {
                text = "None listed"
                setTextColor(0xFF6B7280.toInt())
                setTextSize(13f)
            }
            container.addView(empty)
            return
        }

        val rows = labels.chunked(2)
        rows.forEachIndexed { rowIndex, rowItems ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = if (rowIndex == 0) 0 else dp(8)
                }
            }

            rowItems.forEach {
                row.addView(buildChip(it, accentChips))
            }

            container.addView(row)
        }
    }

    private fun buildChip(text: String, accent: Boolean = false): TextView {
        return TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                marginEnd = dp(6)
            }
            setBackgroundResource(if (accent) R.drawable.bg_chip_blue else R.drawable.bg_chip)
            setPadding(dp(12), dp(8), dp(12), dp(8))
            this.text = text
            setTextColor(0xFF1F2937.toInt())
            setTextSize(12f)
            setTypeface(typeface, Typeface.BOLD)
            gravity = Gravity.CENTER
        }
    }

    private fun normalizeLabel(value: String): String {
        val cleaned = value
            .trim()
            .replace('_', ' ')
            .replace(Regex("\\s+"), " ")

        return when (cleaned.lowercase(Locale.getDefault())) {
            "pre selling" -> "Pre-Selling"
            "rent to own" -> "Rent-To-Own"
            "ready for occupancy" -> "Ready for Occupancy"
            else -> cleaned
                .split(" ")
                .joinToString(" ") { part ->
                    when (part.uppercase(Locale.getDefault())) {
                        "RFO" -> "RFO"
                        "ROI" -> "ROI"
                        else -> part.lowercase(Locale.getDefault())
                            .replaceFirstChar { char -> char.uppercaseChar() }
                    }
                }
        }
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

    private fun formatPrice(value: Double?): String {
        return if (value == null) {
            "N/A"
        } else {
            priceFormatter.format(value)
        }
    }

    private fun formatNullableNumber(value: Double?, suffix: String): String {
        return if (value == null) {
            "N/A"
        } else {
            "${String.format(Locale.getDefault(), "%,.0f", value)} $suffix"
        }
    }

    private fun formatYesNo(value: Boolean?): String {
        return when (value) {
            true -> "Yes"
            false -> "No"
            null -> "Unavailable"
        }
    }

    private fun buildPreviewImages(photos: List<PropertyPhotoDto>): List<String> {
        // The backend can return duplicate or unordered photos; normalize before driving the preview dialog.
        return photos
            .sortedBy { it.displayOrder ?: 0 }
            .mapNotNull { it.photoUrl?.trim()?.takeIf { url -> url.isNotBlank() } }
            .distinct()
    }

    private fun showImagePreview(startIndex: Int) {
        if (previewImages.isEmpty()) {
            return
        }

        currentPreviewIndex = startIndex.coerceIn(0, previewImages.lastIndex)

        previewDialog?.dismiss()

        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_image_preview, null, false)
        dialog.setContentView(view)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        previewDialog = dialog

        val imageView = view.findViewById<ImageView>(R.id.ivPreviewImage)
        val closeButton = view.findViewById<Button>(R.id.btnPreviewClose)
        val previousButton = view.findViewById<Button>(R.id.btnPreviewPrevious)
        val nextButton = view.findViewById<Button>(R.id.btnPreviewNext)

        fun refreshPreview() {
            val imageUrl = previewImages.getOrNull(currentPreviewIndex)
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.bg_property_image_placeholder)
                .error(R.drawable.bg_property_image_placeholder)
                .fallback(R.drawable.bg_property_image_placeholder)
                .fitCenter()
                .into(imageView)

            val showNavigation = previewImages.size > 1
            previousButton.visibility = if (showNavigation) View.VISIBLE else View.GONE
            nextButton.visibility = if (showNavigation) View.VISIBLE else View.GONE
        }

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        previousButton.setOnClickListener {
            if (previewImages.size > 1) {
                currentPreviewIndex = if (currentPreviewIndex == 0) {
                    previewImages.lastIndex
                } else {
                    currentPreviewIndex - 1
                }
                refreshPreview()
            }
        }

        nextButton.setOnClickListener {
            if (previewImages.size > 1) {
                currentPreviewIndex = (currentPreviewIndex + 1) % previewImages.size
                refreshPreview()
            }
        }

        dialog.setOnDismissListener {
            if (previewDialog === dialog) {
                previewDialog = null
            }
        }

        refreshPreview()
        dialog.show()
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    companion object {
        const val EXTRA_PROPERTY_ID = "extra_property_id"
    }
}
