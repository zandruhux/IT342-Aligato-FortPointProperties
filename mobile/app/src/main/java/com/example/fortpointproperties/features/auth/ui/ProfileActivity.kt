package com.example.fortpointproperties.features.auth.ui

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.fortpointproperties.R
import com.example.fortpointproperties.features.auth.data.UpdateProfileRequest
import com.example.fortpointproperties.features.auth.data.UserResponse
import com.example.fortpointproperties.features.auth.network.AuthApi
import com.example.fortpointproperties.shared.auth.SessionManager
import com.example.fortpointproperties.shared.auth.TokenManager
import com.example.fortpointproperties.shared.network.ApiClient
import com.example.fortpointproperties.shared.network.ApiResponse
import com.example.fortpointproperties.shared.ui.MobileHeaderBinder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.IOException
import java.util.Locale
import org.json.JSONObject

class ProfileActivity : AppCompatActivity() {

    private companion object {
        private const val MAX_PROFILE_IMAGE_BYTES = 2L * 1024L * 1024L
        private val ALLOWED_IMAGE_MIME_TYPES = setOf(
            "image/jpeg",
            "image/png",
            "image/webp"
        )

        private val ALLOWED_IMAGE_EXTENSIONS = setOf(
            "jpg",
            "jpeg",
            "png",
            "webp"
        )
    }

    private data class SelectedProfileImage(
        val uri: Uri,
        val displayName: String,
        val mimeType: String,
        val sizeBytes: Long
    )

    private sealed class ImageValidationResult {
        data class Success(val image: SelectedProfileImage) : ImageValidationResult()
        data class Error(val message: String) : ImageValidationResult()
    }

    private sealed class SaveResult {
        data class Success(val user: UserResponse) : SaveResult()
        data class Error(val message: String, val field: ProfileField? = null) : SaveResult()
        data object SessionExpired : SaveResult()
    }

    private enum class ProfileField {
        FIRST_NAME,
        LAST_NAME,
        PHONE,
        IMAGE,
        GENERAL
    }

    private lateinit var api: AuthApi
    private lateinit var btnBack: ImageButton
    private lateinit var ivProfileAvatar: ImageView
    private lateinit var tvProfileAvatar: TextView
    private lateinit var btnChangePhoto: ImageButton
    private lateinit var tvFullName: TextView
    private lateinit var tvProfileHelper: TextView
    private lateinit var tvAvatarError: TextView
    private lateinit var tvProfileFormError: TextView
    private lateinit var progressSaving: ProgressBar
    private lateinit var btnRemovePhoto: Button
    private lateinit var tvFirstNameValue: TextView
    private lateinit var etFirstNameValue: EditText
    private lateinit var tvFirstNameError: TextView
    private lateinit var tvLastNameValue: TextView
    private lateinit var etLastNameValue: EditText
    private lateinit var tvLastNameError: TextView
    private lateinit var tvEmailValue: TextView
    private lateinit var tvPhoneValue: TextView
    private lateinit var etPhoneValue: EditText
    private lateinit var tvPhoneError: TextView
    private lateinit var tvRoleValue: TextView
    private lateinit var btnPrimaryAction: Button
    private lateinit var btnCancelEdit: Button
    private lateinit var btnLogout: Button

    private var currentUser: UserResponse? = null
    private var isEditMode = false
    private var isSaving = false
    private var pendingProfileImage: SelectedProfileImage? = null

    private val profileImagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                handlePickedProfileImage(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TokenManager.init(this)
        api = ApiClient.retrofit.create(AuthApi::class.java)

        if (!TokenManager.isLoggedIn()) {
            redirectToLogin()
            return
        }

        if (SessionManager.isPrivilegedRole(SessionManager.getStoredRole())) {
            showUnavailableAndRedirect(SessionManager.getStoredRole())
            return
        }

        setContentView(R.layout.activity_profile)
        bindViews()
        bindActions()
        loadUserProfile()
    }

    private fun bindViews() {
        btnBack = findViewById(R.id.btnBack)
        ivProfileAvatar = findViewById(R.id.ivProfileAvatar)
        tvProfileAvatar = findViewById(R.id.tvProfileAvatar)
        btnChangePhoto = findViewById(R.id.btnChangePhoto)
        tvFullName = findViewById(R.id.tvFullName)
        tvProfileHelper = findViewById(R.id.tvProfileHelper)
        tvAvatarError = findViewById(R.id.tvAvatarError)
        tvProfileFormError = findViewById(R.id.tvProfileFormError)
        progressSaving = findViewById(R.id.progressSaving)
        btnRemovePhoto = findViewById(R.id.btnRemovePhoto)
        tvFirstNameValue = findViewById(R.id.tvFirstNameValue)
        etFirstNameValue = findViewById(R.id.etFirstNameValue)
        tvFirstNameError = findViewById(R.id.tvFirstNameError)
        tvLastNameValue = findViewById(R.id.tvLastNameValue)
        etLastNameValue = findViewById(R.id.etLastNameValue)
        tvLastNameError = findViewById(R.id.tvLastNameError)
        tvEmailValue = findViewById(R.id.tvEmailValue)
        tvPhoneValue = findViewById(R.id.tvPhoneValue)
        etPhoneValue = findViewById(R.id.etPhoneValue)
        tvPhoneError = findViewById(R.id.tvPhoneError)
        tvRoleValue = findViewById(R.id.tvRoleValue)
        btnPrimaryAction = findViewById(R.id.btnPrimaryAction)
        btnCancelEdit = findViewById(R.id.btnCancelEdit)
        btnLogout = findViewById(R.id.btnLogout)
    }

    private fun bindActions() {
        btnBack.setOnClickListener { finish() }

        btnPrimaryAction.setOnClickListener {
            if (isSaving) return@setOnClickListener
            if (isEditMode) {
                saveProfileChanges()
            } else {
                enterEditMode()
            }
        }

        btnCancelEdit.setOnClickListener {
            if (isSaving) return@setOnClickListener
            cancelEditMode()
        }

        btnLogout.setOnClickListener {
            if (!isSaving) {
                showLogoutConfirmationDialog()
            }
        }

        btnChangePhoto.setOnClickListener {
            if (isEditMode && !isSaving) {
                profileImagePickerLauncher.launch(
                    arrayOf(
                        "image/jpeg",
                        "image/png",
                        "image/webp"
                    )
                )
            }
        }

        btnRemovePhoto.setOnClickListener {
            if (isEditMode && !isSaving) {
                removeProfilePhoto()
            }
        }
    }

    private fun loadUserProfile() {
        lifecycleScope.launch {
            setLoadingState(true)
            try {
                val response = api.getProfile()

                if (response.isSuccessful) {
                    val user = response.body()?.data
                    if (user != null) {
                        TokenManager.saveUserRole(user.role)
                        handleProfileUser(user)
                    } else {
                        showProfileFormError(getString(R.string.profile_update_failed))
                    }
                } else if (response.code() == 401 || response.code() == 403) {
                    TokenManager.clear()
                    showAuthToast("Session expired. Please log in again.")
                    redirectToLogin()
                } else {
                    showProfileFormError(extractBackendMessage(response) ?: getString(R.string.profile_update_failed))
                }
            } catch (e: Exception) {
                showProfileFormError(e.message ?: getString(R.string.profile_update_failed))
            } finally {
                setLoadingState(false)
            }
        }
    }

    private fun handleProfileUser(user: UserResponse) {
        when {
            SessionManager.isRegisteredUserRole(user.role) -> {
                currentUser = user
                renderProfile(user)
            }

            SessionManager.isPrivilegedRole(user.role) -> {
                showUnavailableAndRedirect(user.role)
            }

            else -> {
                TokenManager.clear()
                showAuthToast("This mobile module is only for registered users.")
                redirectToLogin()
            }
        }
    }

    private fun renderProfile(user: UserResponse) {
        currentUser = user

        tvFullName.text = MobileHeaderBinder.buildDisplayName(
            firstName = user.firstname,
            lastName = user.lastname,
            email = user.email
        )
        tvFirstNameValue.text = user.firstname
        tvLastNameValue.text = user.lastname
        tvEmailValue.text = user.email
        tvPhoneValue.text = user.phoneNumber?.takeIf { it.isNotBlank() }
            ?: getString(R.string.profile_no_phone)
        tvRoleValue.text = formatRoleLabel(user.role)

        etFirstNameValue.setText(user.firstname)
        etLastNameValue.setText(user.lastname)
        etPhoneValue.setText(user.phoneNumber.orEmpty())

        clearErrorStates()
        renderAvatar(user)
        applyModeUi(editMode = false)
    }

    private fun renderAvatar(user: UserResponse) {
        val initials = MobileHeaderBinder.buildInitials(
            firstName = user.firstname,
            lastName = user.lastname,
            email = user.email
        )

        tvProfileAvatar.text = initials

        val imageSource: Any? = pendingProfileImage?.uri ?: user.profileImageUrl
        if (imageSource == null || (imageSource is String && imageSource.isBlank())) {
            ivProfileAvatar.setImageDrawable(null)
            ivProfileAvatar.visibility = View.GONE
            tvProfileAvatar.visibility = View.VISIBLE
            return
        }

        ivProfileAvatar.visibility = View.VISIBLE
        tvProfileAvatar.visibility = View.GONE

        Glide.with(this)
            .load(imageSource)
            .placeholder(R.drawable.bg_avatar_circle)
            .error(R.drawable.bg_avatar_circle)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .circleCrop()
            .into(ivProfileAvatar)
    }

    private fun enterEditMode() {
        val user = currentUser ?: return
        isEditMode = true
        pendingProfileImage = null
        clearErrorStates()
        etFirstNameValue.setText(user.firstname)
        etLastNameValue.setText(user.lastname)
        etPhoneValue.setText(user.phoneNumber.orEmpty())
        applyModeUi(editMode = true)
    }

    private fun cancelEditMode() {
        val user = currentUser ?: return
        pendingProfileImage = null
        clearErrorStates()
        isEditMode = false
        etFirstNameValue.setText(user.firstname)
        etLastNameValue.setText(user.lastname)
        etPhoneValue.setText(user.phoneNumber.orEmpty())
        renderProfile(user)
    }

    private fun applyModeUi(editMode: Boolean) {
        isEditMode = editMode
        val displayVisibility = if (editMode) View.GONE else View.VISIBLE
        val editVisibility = if (editMode) View.VISIBLE else View.GONE

        tvFirstNameValue.visibility = displayVisibility
        etFirstNameValue.visibility = editVisibility
        tvLastNameValue.visibility = displayVisibility
        etLastNameValue.visibility = editVisibility
        tvPhoneValue.visibility = displayVisibility
        etPhoneValue.visibility = editVisibility
        btnChangePhoto.visibility = editVisibility
        btnRemovePhoto.visibility = if (editMode && hasRemovablePhoto()) View.VISIBLE else View.GONE
        btnCancelEdit.visibility = if (editMode) View.VISIBLE else View.GONE
        btnPrimaryAction.text = if (editMode) {
            getString(R.string.profile_save_changes)
        } else {
            getString(R.string.profile_edit)
        }

        etFirstNameValue.isEnabled = editMode
        etLastNameValue.isEnabled = editMode
        etPhoneValue.isEnabled = editMode
    }

    private fun hasRemovablePhoto(): Boolean {
        return !currentUser?.profileImageUrl.isNullOrBlank() || pendingProfileImage != null
    }

    private fun validateForm(): Boolean {
        clearFieldErrors()

        var valid = true

        val firstName = etFirstNameValue.text?.toString()?.trim().orEmpty()
        val lastName = etLastNameValue.text?.toString()?.trim().orEmpty()
        val phone = etPhoneValue.text?.toString()?.trim().orEmpty()

        if (firstName.isBlank()) {
            tvFirstNameError.text = getString(R.string.profile_first_name_required)
            tvFirstNameError.visibility = View.VISIBLE
            valid = false
        }

        if (lastName.isBlank()) {
            tvLastNameError.text = getString(R.string.profile_last_name_required)
            tvLastNameError.visibility = View.VISIBLE
            valid = false
        }

        if (phone.isNotBlank() && !isValidPhoneNumber(phone)) {
            tvPhoneError.text = getString(R.string.profile_phone_invalid)
            tvPhoneError.visibility = View.VISIBLE
            valid = false
        }

        return valid
    }

    private fun saveProfileChanges() {
        val user = currentUser ?: return

        if (!validateForm()) {
            showProfileFormError(getString(R.string.profile_form_error))
            return
        }

        val firstName = etFirstNameValue.text?.toString()?.trim().orEmpty()
        val lastName = etLastNameValue.text?.toString()?.trim().orEmpty()
        val phoneNumber = etPhoneValue.text?.toString()?.trim().orEmpty()

        lifecycleScope.launch {
            setSavingState(true)
            clearProfileMessages()

            try {
                val profileResult = updateProfileOnBackend(
                    email = user.email,
                    firstname = firstName,
                    lastname = lastName,
                    phoneNumber = phoneNumber
                )

                when (profileResult) {
                    is SaveResult.Success -> {
                        val imageSelection = pendingProfileImage
                        if (imageSelection != null) {
                            when (val imageResult = uploadProfileImage(imageSelection)) {
                                is SaveResult.Success -> Unit

                                is SaveResult.Error -> {
                                    showFieldMessage(imageResult.field, imageResult.message)
                                    return@launch
                                }

                                SaveResult.SessionExpired -> {
                                    TokenManager.clear()
                                    showAuthToast("Session expired. Please log in again.")
                                    redirectToLogin()
                                    return@launch
                                }
                            }
                        }

                        pendingProfileImage = null
                        refreshProfileFromServer(showSuccessToast = true)
                    }

                    is SaveResult.Error -> {
                        showFieldMessage(profileResult.field, profileResult.message)
                    }

                    SaveResult.SessionExpired -> {
                        TokenManager.clear()
                        showAuthToast("Session expired. Please log in again.")
                        redirectToLogin()
                    }
                }
            } catch (e: Exception) {
                showProfileFormError(e.message ?: getString(R.string.profile_update_failed))
            } finally {
                setSavingState(false)
            }
        }
    }

    private suspend fun updateProfileOnBackend(
        email: String,
        firstname: String,
        lastname: String,
        phoneNumber: String
    ): SaveResult {
        val response = api.updateProfile(
            UpdateProfileRequest(
                email = email,
                firstname = firstname,
                lastname = lastname,
                phoneNumber = phoneNumber.takeIf { it.isNotBlank() }
            )
        )

        return if (response.isSuccessful) {
            val updatedUser = response.body()?.data
            if (updatedUser != null) {
                SaveResult.Success(updatedUser)
            } else {
                SaveResult.Error(getString(R.string.profile_update_failed), ProfileField.GENERAL)
            }
        } else if (response.code() == 401 || response.code() == 403) {
            SaveResult.SessionExpired
        } else {
            val backendMessage = extractBackendMessage(response)
            SaveResult.Error(
                backendMessage ?: getString(R.string.profile_update_failed),
                mapBackendMessageToField(backendMessage)
            )
        }
    }

    private suspend fun uploadProfileImage(selection: SelectedProfileImage): SaveResult {
        // Stream the selected image into multipart form data so large files are not duplicated in memory.
        val requestBody = object : RequestBody() {
            override fun contentType() = selection.mimeType.toMediaTypeOrNull()

            override fun contentLength(): Long = selection.sizeBytes

            override fun writeTo(sink: BufferedSink) {
                contentResolver.openInputStream(selection.uri)?.use { input ->
                    sink.writeAll(input.source())
                } ?: throw IOException("Unable to open selected image.")
            }
        }

        val part = MultipartBody.Part.createFormData("image", selection.displayName, requestBody)
        val response = api.updateProfileImage(part)

        return if (response.isSuccessful) {
            val user = currentUser
            if (user != null) {
                SaveResult.Success(user)
            } else {
                SaveResult.Error(getString(R.string.profile_update_failed), ProfileField.IMAGE)
            }
        } else if (response.code() == 401 || response.code() == 403) {
            SaveResult.SessionExpired
        } else {
            val backendMessage = extractBackendMessage(response)
            SaveResult.Error(
                backendMessage ?: getString(R.string.profile_update_failed),
                ProfileField.IMAGE
            )
        }
    }

    private fun handlePickedProfileImage(uri: Uri) {
        lifecycleScope.launch {
            tvAvatarError.visibility = View.GONE
            when (val result = validateSelectedImage(uri)) {
                is ImageValidationResult.Success -> {
                    pendingProfileImage = result.image
                    renderPendingAvatarPreview(result.image.uri)
                }

                is ImageValidationResult.Error -> {
                    pendingProfileImage = null
                    showAvatarError(result.message)
                    currentUser?.let { renderAvatar(it) }
                }
            }
        }
    }

    private suspend fun validateSelectedImage(uri: Uri): ImageValidationResult {
        return withContext(Dispatchers.IO) {
            // Match backend upload rules before sending multipart data, then surface errors inline.
            val mimeType = contentResolver.getType(uri)?.lowercase(Locale.ROOT)
                ?: return@withContext ImageValidationResult.Error(getString(R.string.profile_image_invalid_type))

            if (!ALLOWED_IMAGE_MIME_TYPES.contains(mimeType)) {
                return@withContext ImageValidationResult.Error(getString(R.string.profile_image_invalid_type))
            }

            val sizeBytes = resolveSelectedImageSize(uri)
            if (sizeBytes > MAX_PROFILE_IMAGE_BYTES) {
                return@withContext ImageValidationResult.Error(getString(R.string.profile_image_too_large))
            }

            val displayName = resolveDisplayName(uri, mimeType)
            val normalizedName = normalizeFileName(displayName, mimeType)

            ImageValidationResult.Success(
                SelectedProfileImage(
                    uri = uri,
                    displayName = normalizedName,
                    mimeType = mimeType,
                    sizeBytes = sizeBytes
                )
            )
        }
    }

    private fun renderPendingAvatarPreview(uri: Uri) {
        ivProfileAvatar.visibility = View.VISIBLE
        tvProfileAvatar.visibility = View.GONE

        Glide.with(this)
            .load(uri)
            .placeholder(R.drawable.bg_avatar_circle)
            .error(R.drawable.bg_avatar_circle)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .circleCrop()
            .into(ivProfileAvatar)
    }

    private suspend fun resolveSelectedImageSize(uri: Uri): Long {
        return withContext(Dispatchers.IO) {
            contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { cursor ->
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst() && sizeIndex >= 0 && !cursor.isNull(sizeIndex)) {
                    val queriedSize = cursor.getLong(sizeIndex)
                    if (queriedSize > 0) {
                        return@withContext queriedSize
                    }
                }
            }

            contentResolver.openAssetFileDescriptor(uri, "r")?.use { descriptor ->
                val length = descriptor.length
                if (length > 0) {
                    return@withContext length
                }
            }

            contentResolver.openInputStream(uri)?.use { input ->
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                var total = 0L
                while (true) {
                    val read = input.read(buffer)
                    if (read < 0) {
                        break
                    }
                    total += read
                }
                total
            } ?: 0L
        }
    }

    private fun resolveDisplayName(uri: Uri, mimeType: String): String {
        val fromCursor = contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && index >= 0 && !cursor.isNull(index)) {
                cursor.getString(index)
            } else {
                null
            }
        }

        return fromCursor ?: "profile-image.${extensionFromMimeType(mimeType)}"
    }

    private fun normalizeFileName(displayName: String, mimeType: String): String {
        val sanitized = displayName.trim().ifBlank { "profile-image" }
        val allowedExtension = extensionFromMimeType(mimeType)

        return if (hasAllowedExtension(sanitized)) {
            sanitized
        } else {
            val baseName = sanitized.substringBeforeLast('.', sanitized)
            "$baseName.$allowedExtension"
        }
    }

    private fun extensionFromMimeType(mimeType: String): String {
        return when (mimeType.lowercase(Locale.ROOT)) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> "jpg"
        }
    }

    private fun hasAllowedExtension(fileName: String): Boolean {
        val extension = fileName.substringAfterLast('.', "").lowercase(Locale.ROOT)
        return extension in ALLOWED_IMAGE_EXTENSIONS
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        val cleaned = phone.trim()
        return cleaned.length in 6..30 && Regex("^[+()0-9\\-\\s]+$").matches(cleaned)
    }

    private fun formatRoleLabel(role: String?): String {
        return when (SessionManager.normalizeRole(role)) {
            "REGISTERED_USER" -> "User"
            "ADMIN" -> "Admin"
            "AGENT" -> "Agent"
            else -> "User"
        }
    }

    private fun clearErrorStates() {
        clearFieldErrors()
        tvAvatarError.visibility = View.GONE
        tvProfileFormError.visibility = View.GONE
    }

    private fun clearFieldErrors() {
        tvFirstNameError.visibility = View.GONE
        tvFirstNameError.text = ""
        tvLastNameError.visibility = View.GONE
        tvLastNameError.text = ""
        tvPhoneError.visibility = View.GONE
        tvPhoneError.text = ""
    }

    private fun clearProfileMessages() {
        tvAvatarError.visibility = View.GONE
        tvProfileFormError.visibility = View.GONE
        clearFieldErrors()
    }

    private fun showAvatarError(message: String) {
        tvAvatarError.text = message
        tvAvatarError.visibility = View.VISIBLE
    }

    private fun showProfileFormError(message: String) {
        tvProfileFormError.text = message
        tvProfileFormError.visibility = View.VISIBLE
    }

    private fun showFieldMessage(field: ProfileField?, message: String) {
        when (field) {
            ProfileField.FIRST_NAME -> {
                tvFirstNameError.text = message
                tvFirstNameError.visibility = View.VISIBLE
            }

            ProfileField.LAST_NAME -> {
                tvLastNameError.text = message
                tvLastNameError.visibility = View.VISIBLE
            }

            ProfileField.PHONE -> {
                tvPhoneError.text = message
                tvPhoneError.visibility = View.VISIBLE
            }

            ProfileField.IMAGE -> showAvatarError(message)
            ProfileField.GENERAL, null -> showProfileFormError(message)
        }
    }

    private fun mapBackendMessageToField(message: String?): ProfileField? {
        val normalized = message?.lowercase(Locale.ROOT).orEmpty()
        return when {
            normalized.contains("first") -> ProfileField.FIRST_NAME
            normalized.contains("last") -> ProfileField.LAST_NAME
            normalized.contains("phone") -> ProfileField.PHONE
            normalized.contains("image") || normalized.contains("photo") -> ProfileField.IMAGE
            else -> ProfileField.GENERAL
        }
    }

    private fun extractBackendMessage(response: retrofit2.Response<*>): String? {
        val errorBody = response.errorBody()?.string()?.trim().orEmpty()
        if (errorBody.isBlank()) {
            return null
        }

        return runCatching {
            if (!errorBody.startsWith("{")) {
                return@runCatching errorBody
            }

            val jsonObject = JSONObject(errorBody)
            when {
                jsonObject.has("error") && jsonObject.optJSONObject("error") != null -> {
                    val errorObject = jsonObject.optJSONObject("error")
                    errorObject?.optString("message")?.takeIf { it.isNotBlank() }
                        ?: errorObject?.optString("code")?.takeIf { it.isNotBlank() }
                }

                jsonObject.has("message") -> jsonObject.optString("message").takeIf { it.isNotBlank() }
                jsonObject.has("error") -> {
                    jsonObject.optString("error").takeIf { it.isNotBlank() }
                }

                else -> errorBody
            }
        }.getOrNull()
    }

    private fun removeProfilePhoto() {
        val hasServerPhoto = !currentUser?.profileImageUrl.isNullOrBlank()
        if (!hasServerPhoto && pendingProfileImage == null) {
            showAuthToast(getString(R.string.profile_no_image_to_remove))
            return
        }

        if (!hasServerPhoto) {
            pendingProfileImage = null
            currentUser?.let { renderProfile(it) }
            showAuthToast(getString(R.string.profile_no_image_to_remove))
            return
        }

        lifecycleScope.launch {
            setSavingState(true)
            clearProfileMessages()

            try {
                val response = api.removeProfileImage()

                if (response.isSuccessful) {
                    pendingProfileImage = null
                    refreshProfileFromServer(showSuccessToast = false)
                    showAuthToast(getString(R.string.profile_image_removed))
                } else if (response.code() == 401 || response.code() == 403) {
                    TokenManager.clear()
                    showAuthToast("Session expired. Please log in again.")
                    redirectToLogin()
                } else {
                    val backendMessage = extractBackendMessage(response)
                    showFieldMessage(ProfileField.IMAGE, backendMessage ?: getString(R.string.profile_update_failed))
                }
            } catch (e: Exception) {
                showFieldMessage(ProfileField.IMAGE, e.message ?: getString(R.string.profile_update_failed))
            } finally {
                setSavingState(false)
                applyModeUi(isEditMode)
            }
        }
    }

    private suspend fun refreshProfileFromServer(showSuccessToast: Boolean) {
        val response = api.getProfile()
        if (response.isSuccessful) {
            val user = response.body()?.data
            if (user != null) {
                currentUser = user
                TokenManager.saveUserRole(user.role)
                renderProfile(user)
                if (showSuccessToast) {
                    showAuthToast(getString(R.string.profile_update_success))
                }
                return
            }
        }

        showProfileFormError(getString(R.string.profile_update_failed))
    }

    private fun setLoadingState(isLoading: Boolean) {
        progressSaving.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnPrimaryAction.isEnabled = !isLoading
        btnCancelEdit.isEnabled = !isLoading
        btnLogout.isEnabled = !isLoading
        btnBack.isEnabled = !isLoading
    }

    private fun setSavingState(isLoading: Boolean) {
        isSaving = isLoading
        setLoadingState(isLoading)
        btnPrimaryAction.text = if (isLoading) {
            getString(R.string.profile_saving)
        } else if (isEditMode) {
            getString(R.string.profile_save_changes)
        } else {
            getString(R.string.profile_edit)
        }
        btnChangePhoto.isEnabled = !isLoading
        btnRemovePhoto.isEnabled = !isLoading
        etFirstNameValue.isEnabled = isEditMode && !isLoading
        etLastNameValue.isEnabled = isEditMode && !isLoading
        etPhoneValue.isEnabled = isEditMode && !isLoading
    }

    private fun showLogoutConfirmationDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Confirm Logout")
            .setMessage("Are you sure you want to logout? You will need to login again to access your account.")
            .setPositiveButton("Logout") { dialog, _ ->
                dialog.dismiss()
                performLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performLogout() {
        TokenManager.clear()
        redirectToLogin()
    }

    private fun showAuthToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showUnavailableAndRedirect(role: String?) {
        val label = when (SessionManager.normalizeRole(role)) {
            "ADMIN" -> "Admin"
            "AGENT" -> "Agent"
            else -> "This role"
        }
        showAuthToast("$label mobile is not yet available.")
        TokenManager.clear()
        redirectToLogin()
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
