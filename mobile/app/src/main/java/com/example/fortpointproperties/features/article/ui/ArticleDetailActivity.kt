package com.example.fortpointproperties.features.article.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.fortpointproperties.R
import com.example.fortpointproperties.features.article.data.model.ArticleDetailDto
import com.example.fortpointproperties.features.article.data.repository.ArticleLoginRequiredException
import com.example.fortpointproperties.features.article.data.repository.ArticleRepository
import com.example.fortpointproperties.features.article.data.repository.ArticleRoleException
import com.example.fortpointproperties.features.auth.network.AuthApi
import com.example.fortpointproperties.features.auth.ui.LoginActivity
import com.example.fortpointproperties.shared.auth.SessionManager
import com.example.fortpointproperties.shared.auth.TokenManager
import com.example.fortpointproperties.shared.network.ApiClient
import com.example.fortpointproperties.shared.ui.isHarmlessCancellation
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

class ArticleDetailActivity : AppCompatActivity() {

    private val articleRepository = ArticleRepository()
    private lateinit var authApi: AuthApi
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutState: View
    private lateinit var tvStateMessage: TextView
    private lateinit var btnStateAction: Button
    private lateinit var layoutContent: View
    private lateinit var btnBack: ImageButton
    private lateinit var tvTitle: TextView
    private lateinit var ivCover: ImageView
    private lateinit var tvAuthor: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TokenManager.init(this)
        authApi = ApiClient.retrofit.create(AuthApi::class.java)
        setContentView(R.layout.activity_article_detail)

        bindViews()
        bindActions()

        if (!TokenManager.isLoggedIn()) {
            showAuthState("Login required to view blogs.")
            return
        }

        val providedRole = SessionManager.normalizeRole(intent.getStringExtra(EXTRA_USER_ROLE))
        if (SessionManager.isPrivilegedRole(providedRole)) {
            showUnavailableState(roleUnavailableMessage(providedRole))
            return
        }

        val articleId = intent.getStringExtra(EXTRA_ARTICLE_ID)?.takeIf { it.isNotBlank() }
        if (articleId == null) {
            showErrorState("Article not found.")
            return
        }

        val providedFirstName = intent.getStringExtra(EXTRA_USER_FIRSTNAME)
        val providedLastName = intent.getStringExtra(EXTRA_USER_LASTNAME)
        val providedEmail = intent.getStringExtra(EXTRA_USER_EMAIL)
        val hasProvidedHeader = !providedFirstName.isNullOrBlank()
            || !providedLastName.isNullOrBlank()
            || !providedEmail.isNullOrBlank()

        if ((SessionManager.isRegisteredUserRole(providedRole) || SessionManager.isRegisteredUser()) && hasProvidedHeader) {
            loadArticle(articleId)
            return
        }

        resolveCurrentUserAndLoadArticle(articleId)
    }

    private fun bindViews() {
        progressBar = findViewById(R.id.progressBar)
        layoutState = findViewById(R.id.layoutState)
        tvStateMessage = findViewById(R.id.tvStateMessage)
        btnStateAction = findViewById(R.id.btnStateAction)
        layoutContent = findViewById(R.id.layoutContent)
        btnBack = findViewById(R.id.btnBack)
        tvTitle = findViewById(R.id.tvTitle)
        ivCover = findViewById(R.id.ivCover)
        tvAuthor = findViewById(R.id.tvAuthor)
        tvDate = findViewById(R.id.tvDate)
        tvDescription = findViewById(R.id.tvDescription)
    }

    private fun bindActions() {
        btnBack.setOnClickListener { finish() }
        btnStateAction.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun resolveCurrentUserAndLoadArticle(articleId: String) {
        showLoading()
        lifecycleScope.launch {
            try {
                val response = authApi.getProfile()

                if (response.isSuccessful) {
                    val user = response.body()?.data
                    if (user != null) {
                        TokenManager.saveUserRole(user.role)
                        when {
                            SessionManager.isRegisteredUserRole(user.role) -> loadArticle(articleId)
                            SessionManager.isPrivilegedRole(user.role) -> showUnavailableState(roleUnavailableMessage(user.role))
                            else -> {
                                TokenManager.clear()
                                showAuthState("This mobile module is only for registered users.")
                            }
                        }
                    } else {
                        showErrorState("Unable to read the current user profile.")
                    }
                } else if (response.code() == 401 || response.code() == 403) {
                    TokenManager.clear()
                    showAuthState("Session expired. Please log in again.")
                } else {
                    showErrorState("Unable to verify the current session.")
                }
            } catch (error: Exception) {
                if (error.isHarmlessCancellation()) return@launch
                showErrorState(error.message ?: "Failed to load blog details.")
            }
        }
    }

    private fun loadArticle(articleId: String) {
        showLoading()
        lifecycleScope.launch {
            try {
                val article = articleRepository.getArticleDetails(articleId)
                showArticle(article)
            } catch (error: ArticleLoginRequiredException) {
                TokenManager.clear()
                showAuthState(error.message ?: "Login required to view blogs.")
            } catch (error: ArticleRoleException) {
                showUnavailableState(error.message ?: "This mobile module is only for registered users.")
            } catch (error: Exception) {
                if (error.isHarmlessCancellation()) return@launch
                showErrorState(error.message ?: "Failed to load blog details.")
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        layoutState.visibility = View.GONE
        layoutContent.visibility = View.GONE
    }

    private fun showArticle(article: ArticleDetailDto) {
        progressBar.visibility = View.GONE
        layoutState.visibility = View.GONE
        layoutContent.visibility = View.VISIBLE

        tvTitle.text = article.title?.takeIf { it.isNotBlank() } ?: "Untitled Blog"
        tvAuthor.text = article.authorName?.takeIf { it.isNotBlank() } ?: "Unknown author"
        tvDate.text = formatDate(article.latestDate ?: article.updatedAt ?: article.createdAt)
        tvDescription.text = article.description?.takeIf { it.isNotBlank() } ?: "No content available."

        Glide.with(this)
            .load(article.coverPhotoUrl?.takeIf { it.isNotBlank() })
            .placeholder(R.drawable.bg_property_image_placeholder)
            .error(R.drawable.bg_property_image_placeholder)
            .fallback(R.drawable.bg_property_image_placeholder)
            .centerCrop()
            .into(ivCover)
    }

    private fun showAuthState(message: String) {
        progressBar.visibility = View.GONE
        layoutContent.visibility = View.GONE
        layoutState.visibility = View.VISIBLE
        tvStateMessage.text = message
        btnStateAction.text = "Open Login"
        btnStateAction.visibility = View.VISIBLE
        btnStateAction.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showUnavailableState(message: String) {
        TokenManager.clear()
        progressBar.visibility = View.GONE
        layoutContent.visibility = View.GONE
        layoutState.visibility = View.VISIBLE
        tvStateMessage.text = message
        btnStateAction.text = "Open Login"
        btnStateAction.visibility = View.VISIBLE
        btnStateAction.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showErrorState(message: String) {
        progressBar.visibility = View.GONE
        layoutContent.visibility = View.GONE
        layoutState.visibility = View.VISIBLE
        tvStateMessage.text = message
        btnStateAction.text = "Retry"
        btnStateAction.visibility = View.VISIBLE
        btnStateAction.setOnClickListener { loadArticle(intent.getStringExtra(EXTRA_ARTICLE_ID).orEmpty()) }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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

    private fun roleUnavailableMessage(role: String?): String {
        return when (SessionManager.normalizeRole(role)) {
            "ADMIN" -> "Admin mobile is not yet available."
            "AGENT" -> "Agent mobile is not yet available."
            else -> "This mobile module is only for registered users."
        }
    }

    companion object {
        const val EXTRA_ARTICLE_ID = "extra_article_id"
        const val EXTRA_USER_FIRSTNAME = LoginActivity.EXTRA_USER_FIRSTNAME
        const val EXTRA_USER_LASTNAME = LoginActivity.EXTRA_USER_LASTNAME
        const val EXTRA_USER_EMAIL = LoginActivity.EXTRA_USER_EMAIL
        const val EXTRA_USER_ROLE = LoginActivity.EXTRA_USER_ROLE
    }
}
