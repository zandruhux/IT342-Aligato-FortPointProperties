package com.example.fortpointproperties.features.careerApplication.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.fortpointproperties.R
import com.example.fortpointproperties.features.auth.data.UserResponse
import com.example.fortpointproperties.features.auth.network.AuthApi
import com.example.fortpointproperties.features.auth.ui.LoginActivity
import com.example.fortpointproperties.features.auth.ui.ProfileActivity
import com.example.fortpointproperties.features.article.ui.ArticleListActivity
import com.example.fortpointproperties.features.messaging.ui.ConversationsActivity
import com.example.fortpointproperties.features.careerApplication.data.model.CareerApplicationDto
import com.example.fortpointproperties.features.careerApplication.data.model.CareerApplicationStatusDto
import com.example.fortpointproperties.features.careerApplication.data.repository.CareerApplicationLoginRequiredException
import com.example.fortpointproperties.features.careerApplication.data.repository.CareerApplicationLoadException
import com.example.fortpointproperties.features.careerApplication.data.repository.CareerApplicationRepository
import com.example.fortpointproperties.features.careerApplication.data.repository.CareerApplicationRoleException
import com.example.fortpointproperties.features.careerApplication.data.repository.CareerApplicationSubmitException
import com.example.fortpointproperties.features.favorites.ui.FavoritesActivity
import com.example.fortpointproperties.features.properties.ui.PropertyListActivity
import com.example.fortpointproperties.shared.auth.SessionManager
import com.example.fortpointproperties.shared.auth.TokenManager
import com.example.fortpointproperties.shared.network.ApiClient
import com.example.fortpointproperties.shared.ui.MobileHeaderBinder
import com.example.fortpointproperties.shared.ui.isHarmlessCancellation
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

class CareerApplicationActivity : AppCompatActivity() {

    private val careerRepository = CareerApplicationRepository()
    private lateinit var authApi: AuthApi

    private lateinit var progressBar: ProgressBar
    private lateinit var scrollContent: View
    private lateinit var stateContainer: LinearLayout
    private lateinit var stateMessage: TextView
    private lateinit var stateAction: Button
    private lateinit var layoutProfileHeader: View
    private lateinit var ivHeaderAvatar: ImageView
    private lateinit var tvHeaderAvatar: TextView
    private lateinit var tvHeaderName: TextView
    private lateinit var btnMessages: ImageButton
    private lateinit var tvFormMessage: TextView
    private lateinit var tvStatusBadge: TextView
    private lateinit var tvStatusMessage: TextView
    private lateinit var layoutStatusDetails: LinearLayout
    private lateinit var tvStatusSubmitted: TextView
    private lateinit var tvStatusReviewed: TextView
    private lateinit var tvStatusReviewer: TextView
    private lateinit var tvStatusRemarks: TextView
    private lateinit var etPhoneNumber: EditText
    private lateinit var tvPhoneError: TextView
    private lateinit var btnPickResume: Button
    private lateinit var tvSelectedResume: TextView
    private lateinit var tvResumeError: TextView
    private lateinit var etCoverLetter: EditText
    private lateinit var tvCoverLetterCount: TextView
    private lateinit var tvCoverLetterError: TextView
    private lateinit var btnSubmit: Button
    private lateinit var btnNavProperties: Button
    private lateinit var btnNavFavorites: Button
    private lateinit var btnNavArticles: Button
    private lateinit var btnNavCareer: Button

    private var currentApplication: CareerApplicationDto? = null
    private var selectedResumeUri: Uri? = null
    private var selectedResumeName: String = ""
    private var selectedResumeMimeType: String = ""
    private var isSubmitting = false

    private val resumePickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        handleResumeSelected(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TokenManager.init(this)
        authApi = ApiClient.retrofit.create(AuthApi::class.java)
        setContentView(R.layout.activity_career_application)

        bindViews()
        bindActions()
        setupNavigation()

        if (!TokenManager.isLoggedIn()) {
            showAuthState(getString(R.string.career_application_login_required))
            return
        }
        bindCachedHeader()

        val providedRole = SessionManager.normalizeRole(intent.getStringExtra(EXTRA_USER_ROLE))
        if (SessionManager.isPrivilegedRole(providedRole)) {
            showUnavailableState(roleUnavailableMessage(providedRole))
            return
        }

        val providedFirstName = intent.getStringExtra(EXTRA_USER_FIRSTNAME)
        val providedLastName = intent.getStringExtra(EXTRA_USER_LASTNAME)
        val providedEmail = intent.getStringExtra(EXTRA_USER_EMAIL)
        val providedProfileImageUrl = intent.getStringExtra(EXTRA_USER_PROFILE_IMAGE_URL)
        val hasProvidedHeader = !providedFirstName.isNullOrBlank()
                || !providedLastName.isNullOrBlank()
                || !providedEmail.isNullOrBlank()
                || !providedProfileImageUrl.isNullOrBlank()

        if ((SessionManager.isRegisteredUserRole(providedRole) || SessionManager.isRegisteredUser()) && hasProvidedHeader) {
            bindHeader(providedFirstName, providedLastName, providedEmail, providedProfileImageUrl)
            loadCurrentApplication()
            return
        }

        resolveCurrentUserAndLoadScreen()
    }

    private fun bindViews() {
        progressBar = findViewById(R.id.progressBar)
        scrollContent = findViewById(R.id.scrollContent)
        stateContainer = findViewById(R.id.layoutState)
        stateMessage = findViewById(R.id.tvStateMessage)
        stateAction = findViewById(R.id.btnStateAction)
        layoutProfileHeader = findViewById(R.id.layoutProfileHeader)
        ivHeaderAvatar = findViewById(R.id.ivHeaderAvatar)
        tvHeaderAvatar = findViewById(R.id.tvHeaderAvatar)
        tvHeaderName = findViewById(R.id.tvHeaderName)
        btnMessages = findViewById(R.id.btnMessages)
        tvFormMessage = findViewById(R.id.tvFormMessage)
        tvStatusBadge = findViewById(R.id.tvStatusBadge)
        tvStatusMessage = findViewById(R.id.tvStatusMessage)
        layoutStatusDetails = findViewById(R.id.layoutStatusDetails)
        tvStatusSubmitted = findViewById(R.id.tvStatusSubmitted)
        tvStatusReviewed = findViewById(R.id.tvStatusReviewed)
        tvStatusReviewer = findViewById(R.id.tvStatusReviewer)
        tvStatusRemarks = findViewById(R.id.tvStatusRemarks)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        tvPhoneError = findViewById(R.id.tvPhoneError)
        btnPickResume = findViewById(R.id.btnPickResume)
        tvSelectedResume = findViewById(R.id.tvSelectedResume)
        tvResumeError = findViewById(R.id.tvResumeError)
        etCoverLetter = findViewById(R.id.etCoverLetter)
        tvCoverLetterCount = findViewById(R.id.tvCoverLetterCount)
        tvCoverLetterError = findViewById(R.id.tvCoverLetterError)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnNavProperties = findViewById(R.id.btnNavProperties)
        btnNavFavorites = findViewById(R.id.btnNavFavorites)
        btnNavArticles = findViewById(R.id.btnNavArticles)
        btnNavCareer = findViewById(R.id.btnNavCareer)

        tvCoverLetterCount.text = "0/5000"
        tvSelectedResume.text = getString(R.string.career_application_no_file_chosen)
    }

    private fun bindActions() {
        layoutProfileHeader.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        btnMessages.setOnClickListener {
            startActivity(Intent(this, ConversationsActivity::class.java))
        }

        stateAction.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnPickResume.setOnClickListener {
            resumePickerLauncher.launch(ALLOWED_RESUME_MIME_TYPES)
        }

        etCoverLetter.doAfterTextChanged { editable ->
            val length = editable?.length ?: 0
            tvCoverLetterCount.text = "$length/$MAX_COVER_LETTER_LENGTH"
        }

        btnSubmit.setOnClickListener {
            submitApplication()
        }
    }

    private fun setupNavigation() {
        btnNavCareer.isEnabled = false
        btnNavCareer.alpha = 1f
        btnNavCareer.setOnClickListener { }

        btnNavProperties.setOnClickListener {
            startActivity(Intent(this, PropertyListActivity::class.java))
            finish()
        }

        btnNavFavorites.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
            finish()
        }

        btnNavArticles.setOnClickListener {
            startActivity(Intent(this, ArticleListActivity::class.java))
            finish()
        }
    }

    private fun resolveCurrentUserAndLoadScreen() {
        showLoading()
        lifecycleScope.launch {
            try {
                val response = authApi.getProfile()
                if (response.isSuccessful) {
                    val user = response.body()?.data
                    if (user != null) {
                        TokenManager.saveUserRole(user.role)
                        when {
                            SessionManager.isRegisteredUserRole(user.role) -> {
                                resolveCurrentUserAndPrefillPhone(user)
                                loadCurrentApplication()
                            }

                            SessionManager.isPrivilegedRole(user.role) -> showUnavailableState(
                                roleUnavailableMessage(user.role)
                            )

                            else -> {
                                TokenManager.clear()
                                showAuthState(getString(R.string.career_application_only_registered))
                            }
                        }
                    } else {
                        showErrorState("Unable to read the current user profile.")
                    }
                } else if (response.code() == 401 || response.code() == 403) {
                    TokenManager.clear()
                    showAuthState(getString(R.string.career_application_session_expired))
                } else {
                    showErrorState("Unable to verify the current session.")
                }
            } catch (error: Exception) {
                if (error.isHarmlessCancellation()) return@launch
                showErrorState(error.message ?: "Failed to load career application.")
            }
        }
    }

    private fun loadCurrentApplication() {
        showLoading()
        lifecycleScope.launch {
            try {
                val application = careerRepository.getMyCareerApplication()
                renderApplication(application)
            } catch (error: CareerApplicationLoginRequiredException) {
                TokenManager.clear()
                showAuthState(error.message ?: getString(R.string.career_application_login_required))
            } catch (error: CareerApplicationRoleException) {
                showUnavailableState(error.message ?: getString(R.string.career_application_only_registered))
            } catch (error: CareerApplicationLoadException) {
                showErrorState(error.message ?: "Failed to load career application.")
            } catch (error: Exception) {
                if (error.isHarmlessCancellation()) return@launch
                showErrorState(error.message ?: "Failed to load career application.")
            }
        }
    }

    private fun renderApplication(application: CareerApplicationDto?) {
        currentApplication = application
        progressBar.visibility = View.GONE
        stateContainer.visibility = View.GONE
        scrollContent.visibility = View.VISIBLE

        updateStatusCard(application)
        updateFormForApplication(application)
    }

    private fun updateStatusCard(application: CareerApplicationDto?) {
        if (application == null) {
            tvStatusBadge.visibility = View.GONE
            tvStatusMessage.text = getString(R.string.career_application_no_application)
            layoutStatusDetails.visibility = View.GONE
            return
        }

        val status = application.status ?: CareerApplicationStatusDto.PENDING
        tvStatusBadge.visibility = View.VISIBLE
        tvStatusBadge.text = status.name
        tvStatusBadge.setBackgroundResource(statusBadgeBackground(status))

        tvStatusMessage.text = statusSummary(application)
        layoutStatusDetails.visibility = View.VISIBLE
        tvStatusSubmitted.text = "Submitted: ${formatDate(application.submittedAt)}"
        tvStatusReviewed.text = if (application.reviewedAt.isNullOrBlank()) {
            "Reviewed: Not yet reviewed"
        } else {
            "Reviewed: ${formatDate(application.reviewedAt)}"
        }
        tvStatusReviewer.text = if (application.reviewedBy.isNullOrBlank()) {
            "Reviewed by: Not available"
        } else {
            "Reviewed by: ${application.reviewedBy}"
        }

        if (application.adminRemarks.isNullOrBlank()) {
            tvStatusRemarks.visibility = View.GONE
        } else {
            tvStatusRemarks.visibility = View.VISIBLE
            tvStatusRemarks.text = "Remarks: ${application.adminRemarks}"
        }
    }

    private fun updateFormForApplication(application: CareerApplicationDto?) {
        val isPending = application?.status == CareerApplicationStatusDto.PENDING
        setFormEnabled(!isPending && !isSubmitting)
        if (isPending) {
            showFormInfo(getString(R.string.career_application_pending_notice))
        } else if (application == null) {
            clearGeneralMessage()
        }
    }

    private fun submitApplication() {
        clearFieldErrors()
        clearGeneralMessage()

        val phoneNumber = etPhoneNumber.text.toString().trim()
        val coverLetter = etCoverLetter.text.toString().trim()

        if (phoneNumber.isBlank()) {
            showPhoneError(getString(R.string.career_application_phone_required))
            return
        }

        if (!isValidPhoneNumber(phoneNumber)) {
            showPhoneError("Enter a valid phone number.")
            return
        }

        if (selectedResumeUri == null) {
            showResumeError(getString(R.string.career_application_invalid_resume_type))
            return
        }

        if (coverLetter.isBlank()) {
            showCoverLetterError(getString(R.string.career_application_cover_letter_required))
            return
        }

        if (coverLetter.length > MAX_COVER_LETTER_LENGTH) {
            showCoverLetterError(getString(R.string.career_application_cover_letter_too_long))
            return
        }

        val resumePart = createResumePart(selectedResumeUri!!)
        if (resumePart == null) {
            return
        }

        setSubmittingState(true)
        lifecycleScope.launch {
            try {
                val application = careerRepository.submitCareerApplication(
                    phoneNumber = phoneNumber,
                    coverLetter = coverLetter,
                    resumePart = resumePart
                )

                currentApplication = application
                showFormSuccess("Application submitted successfully.")
                loadCurrentApplication()
            } catch (error: CareerApplicationLoginRequiredException) {
                TokenManager.clear()
                showAuthState(error.message ?: getString(R.string.career_application_login_required))
            } catch (error: CareerApplicationRoleException) {
                showUnavailableState(error.message ?: getString(R.string.career_application_only_registered))
            } catch (error: CareerApplicationSubmitException) {
                handleSubmitError(error.message ?: "Failed to submit career application")
            } catch (error: Exception) {
                if (error.isHarmlessCancellation()) return@launch
                handleSubmitError(error.message ?: "Failed to submit career application")
            } finally {
                setSubmittingState(false)
            }
        }
    }

    private fun handleSubmitError(message: String) {
        when {
            message.contains("Phone number", ignoreCase = true) -> showPhoneError(message)
            message.contains("Cover letter", ignoreCase = true) -> showCoverLetterError(message)
            message.contains("Resume", ignoreCase = true) -> showResumeError(message)
            message.contains("pending", ignoreCase = true) -> showFormError(message)
            else -> showFormError(message)
        }
    }

    private fun handleResumeSelected(uri: Uri?) {
        if (uri == null) {
            return
        }

        val displayName = resolveDisplayName(uri)
        if (displayName.isNullOrBlank()) {
            clearSelectedResume()
            showResumeError(getString(R.string.career_application_invalid_resume_type))
            return
        }

        val mimeType = resolveAllowedResumeMimeType(displayName)
        if (mimeType == null) {
            clearSelectedResume()
            showResumeError(getString(R.string.career_application_invalid_resume_type))
            return
        }

        val fileSizeBytes = resolveFileSize(uri)
        if (fileSizeBytes != null && fileSizeBytes > MAX_RESUME_SIZE_BYTES) {
            clearSelectedResume()
            showResumeError(getString(R.string.career_application_resume_too_large))
            return
        }

        selectedResumeUri = uri
        selectedResumeName = displayName
        selectedResumeMimeType = mimeType
        tvSelectedResume.text = "${getString(R.string.career_application_file_selected_prefix)} $displayName"
        tvSelectedResume.setTextColor(ContextCompat.getColor(this, R.color.property_text_primary))
        tvResumeError.visibility = View.GONE
    }

    private fun createResumePart(uri: Uri): MultipartBody.Part? {
        val fileName = (selectedResumeName.takeIf { it.isNotBlank() } ?: resolveDisplayName(uri))
            ?.takeIf { it.isNotBlank() } ?: run {
            showResumeError(getString(R.string.career_application_invalid_resume_type))
            return null
        }

        val mimeType = selectedResumeMimeType.ifBlank { resolveAllowedResumeMimeType(fileName) ?: "" }
        if (mimeType.isBlank()) {
            showResumeError(getString(R.string.career_application_invalid_resume_type))
            return null
        }

        // Re-check size while building the multipart body because some document providers hide it in metadata.
        val bytes = try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (_: Exception) {
            null
        }

        if (bytes == null) {
            showResumeError("Unable to read the selected resume file.")
            return null
        }

        if (bytes.size > MAX_RESUME_SIZE_BYTES.toInt()) {
            showResumeError(getString(R.string.career_application_resume_too_large))
            return null
        }

        val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("resume", fileName, requestBody)
    }

    private fun resolveCurrentUserAndPrefillPhone(user: UserResponse) {
        bindHeader(user)
        prefillPhoneIfAvailable(user.phoneNumber)
    }

    private fun bindHeader(user: UserResponse) {
        bindHeader(user.firstname, user.lastname, user.email, user.profileImageUrl)
        TokenManager.saveUserRole(user.role)
    }

    private fun bindHeader(firstName: String?, lastName: String?, email: String?, profileImageUrl: String?) {
        if (!firstName.isNullOrBlank() || !lastName.isNullOrBlank() || !email.isNullOrBlank()) {
            TokenManager.saveUserProfile(firstName, lastName, email, profileImageUrl)
        }
        MobileHeaderBinder.bind(
            context = this,
            profileImageView = ivHeaderAvatar,
            initialsView = tvHeaderAvatar,
            nameView = tvHeaderName,
            firstName = firstName,
            lastName = lastName,
            email = email,
            profileImageUrl = profileImageUrl
        )
    }

    private fun bindCachedHeader() {
        bindHeader(
            firstName = TokenManager.getUserFirstName(),
            lastName = TokenManager.getUserLastName(),
            email = TokenManager.getUserEmail(),
            profileImageUrl = TokenManager.getUserProfileImageUrl()
        )
    }

    private fun prefillPhoneIfAvailable(phoneNumber: String?) {
        if (!phoneNumber.isNullOrBlank() && etPhoneNumber.text.isNullOrBlank()) {
            etPhoneNumber.setText(phoneNumber)
        }
    }

    private fun setFormEnabled(enabled: Boolean) {
        etPhoneNumber.isEnabled = enabled
        btnPickResume.isEnabled = enabled
        etCoverLetter.isEnabled = enabled
        btnSubmit.isEnabled = enabled && !isSubmitting
        val alpha = if (enabled) 1f else 0.7f
        etPhoneNumber.alpha = alpha
        btnPickResume.alpha = alpha
        etCoverLetter.alpha = alpha
        btnSubmit.alpha = if (isSubmitting) 0.7f else alpha
    }

    private fun setSubmittingState(submitting: Boolean) {
        isSubmitting = submitting
        progressBar.visibility = if (submitting) View.VISIBLE else View.GONE
        btnSubmit.text = if (submitting) {
            getString(R.string.career_application_submitting)
        } else {
            getString(R.string.career_application_submit)
        }
        setFormEnabled(currentApplication?.status != CareerApplicationStatusDto.PENDING && !submitting)
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        scrollContent.visibility = View.GONE
        stateContainer.visibility = View.GONE
    }

    private fun showAuthState(message: String) {
        progressBar.visibility = View.GONE
        scrollContent.visibility = View.GONE
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
        TokenManager.clear()
        progressBar.visibility = View.GONE
        scrollContent.visibility = View.GONE
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
        scrollContent.visibility = View.GONE
        stateContainer.visibility = View.VISIBLE
        stateMessage.text = message
        stateAction.text = "Retry"
        stateAction.visibility = View.VISIBLE
        stateAction.setOnClickListener { resolveCurrentUserAndLoadScreen() }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showFormMessage(message: String, colorRes: Int) {
        tvFormMessage.visibility = View.VISIBLE
        tvFormMessage.text = message
        tvFormMessage.setTextColor(ContextCompat.getColor(this, colorRes))
    }

    private fun showFormSuccess(message: String) {
        showFormMessage(message, R.color.career_green)
    }

    private fun showFormInfo(message: String) {
        showFormMessage(message, R.color.property_blue)
    }

    private fun showFormError(message: String) {
        showFormMessage(message, R.color.property_red)
    }

    private fun clearGeneralMessage() {
        tvFormMessage.visibility = View.GONE
        tvFormMessage.text = ""
    }

    private fun clearFieldErrors() {
        tvPhoneError.visibility = View.GONE
        tvPhoneError.text = ""
        tvResumeError.visibility = View.GONE
        tvResumeError.text = ""
        tvCoverLetterError.visibility = View.GONE
        tvCoverLetterError.text = ""
    }

    private fun showPhoneError(message: String) {
        tvPhoneError.visibility = View.VISIBLE
        tvPhoneError.text = message
    }

    private fun showResumeError(message: String) {
        tvResumeError.visibility = View.VISIBLE
        tvResumeError.text = message
    }

    private fun showCoverLetterError(message: String) {
        tvCoverLetterError.visibility = View.VISIBLE
        tvCoverLetterError.text = message
    }

    private fun clearSelectedResume() {
        selectedResumeUri = null
        selectedResumeName = ""
        selectedResumeMimeType = ""
        tvSelectedResume.text = getString(R.string.career_application_no_file_chosen)
        tvSelectedResume.setTextColor(ContextCompat.getColor(this, R.color.property_text_secondary))
    }

    private fun resolveDisplayName(uri: Uri): String? {
        val cursor = contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null
        ) ?: return uri.lastPathSegment?.substringAfterLast('/')

        cursor.use {
            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index >= 0 && it.moveToFirst()) {
                return it.getString(index)
            }
        }

        return uri.lastPathSegment?.substringAfterLast('/')
    }

    private fun resolveFileSize(uri: Uri): Long? {
        return try {
            contentResolver.openFileDescriptor(uri, "r")?.use { descriptor ->
                val size = descriptor.statSize
                if (size >= 0) size else null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun resolveAllowedResumeMimeType(fileName: String): String? {
        val extension = fileName.substringAfterLast('.', "").lowercase(Locale.ROOT)
        return when (extension) {
            "pdf" -> "application/pdf"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            else -> null
        }
    }

    private fun statusBadgeBackground(status: CareerApplicationStatusDto): Int {
        return when (status) {
            CareerApplicationStatusDto.PENDING -> R.drawable.bg_status_pending
            CareerApplicationStatusDto.ACCEPTED -> R.drawable.bg_status_accepted
            CareerApplicationStatusDto.REJECTED -> R.drawable.bg_status_rejected
        }
    }

    private fun statusSummary(application: CareerApplicationDto): String {
        return when (application.status ?: CareerApplicationStatusDto.PENDING) {
            CareerApplicationStatusDto.PENDING -> "Your application is currently pending review."
            CareerApplicationStatusDto.ACCEPTED -> "Your application has been accepted."
            CareerApplicationStatusDto.REJECTED -> "Your application has been rejected."
        }
    }

    private fun formatDate(value: String?): String {
        val raw = value?.takeIf { it.isNotBlank() } ?: return "Not available"

        return try {
            val parsed = LocalDateTime.parse(raw)
            parsed.format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault()))
        } catch (_: DateTimeParseException) {
            raw
        } catch (_: Exception) {
            raw
        }
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return phoneNumber.matches(Regex("^[0-9+()\\-\\s]{7,20}$"))
    }

    private fun roleUnavailableMessage(role: String?): String {
        return when (SessionManager.normalizeRole(role)) {
            "ADMIN" -> getString(R.string.career_application_admin_unavailable)
            "AGENT" -> getString(R.string.career_application_agent_unavailable)
            else -> getString(R.string.career_application_only_registered)
        }
    }

    companion object {
        private val ALLOWED_RESUME_MIME_TYPES = arrayOf(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        )

        private const val MAX_COVER_LETTER_LENGTH = 5000
        private const val MAX_RESUME_SIZE_BYTES = 5L * 1024L * 1024L

        const val EXTRA_USER_FIRSTNAME = LoginActivity.EXTRA_USER_FIRSTNAME
        const val EXTRA_USER_LASTNAME = LoginActivity.EXTRA_USER_LASTNAME
        const val EXTRA_USER_EMAIL = LoginActivity.EXTRA_USER_EMAIL
        const val EXTRA_USER_ROLE = LoginActivity.EXTRA_USER_ROLE
        const val EXTRA_USER_PROFILE_IMAGE_URL = LoginActivity.EXTRA_USER_PROFILE_IMAGE_URL
    }
}
