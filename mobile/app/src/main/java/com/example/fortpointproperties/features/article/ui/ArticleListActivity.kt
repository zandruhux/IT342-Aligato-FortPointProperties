package com.example.fortpointproperties.features.article.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fortpointproperties.R
import com.example.fortpointproperties.features.article.data.model.ArticleCardDto
import com.example.fortpointproperties.features.article.data.repository.ArticleLoginRequiredException
import com.example.fortpointproperties.features.article.data.repository.ArticleRepository
import com.example.fortpointproperties.features.article.data.repository.ArticleRoleException
import com.example.fortpointproperties.features.article.ui.adapter.ArticleAdapter
import com.example.fortpointproperties.features.auth.data.UserResponse
import com.example.fortpointproperties.features.auth.network.AuthApi
import com.example.fortpointproperties.features.auth.ui.LoginActivity
import com.example.fortpointproperties.features.auth.ui.ProfileActivity
import com.example.fortpointproperties.features.careerApplication.ui.CareerApplicationActivity
import com.example.fortpointproperties.features.messaging.ui.ConversationsActivity
import com.example.fortpointproperties.features.favorites.ui.FavoritesActivity
import com.example.fortpointproperties.features.properties.ui.PropertyListActivity
import com.example.fortpointproperties.shared.auth.SessionManager
import com.example.fortpointproperties.shared.auth.TokenManager
import com.example.fortpointproperties.shared.network.ApiClient
import com.example.fortpointproperties.shared.ui.MobileHeaderBinder
import com.example.fortpointproperties.shared.ui.isHarmlessCancellation
import kotlinx.coroutines.launch

class ArticleListActivity : AppCompatActivity() {

    private enum class LoadMode {
        DEFAULT,
        SEARCH
    }

    private val articleRepository = ArticleRepository()
    private lateinit var authApi: AuthApi
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var listView: ListView
    private lateinit var emptyView: TextView
    private lateinit var stateContainer: View
    private lateinit var stateMessage: TextView
    private lateinit var stateAction: Button
    private lateinit var layoutProfileHeader: View
    private lateinit var ivHeaderAvatar: ImageView
    private lateinit var tvHeaderAvatar: TextView
    private lateinit var tvHeaderName: TextView
    private lateinit var btnMessages: ImageButton
    private lateinit var etSearchQuery: EditText
    private lateinit var btnSearch: ImageButton
    private lateinit var btnClearSearch: ImageButton
    private lateinit var btnNavProperties: Button
    private lateinit var btnNavFavorites: Button
    private lateinit var btnNavArticles: Button
    private lateinit var btnNavCareer: Button
    private var lastLoadMode = LoadMode.DEFAULT
    private var lastSearchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TokenManager.init(this)
        authApi = ApiClient.retrofit.create(AuthApi::class.java)
        setContentView(R.layout.activity_article_list)

        bindViews()
        bindActions()
        setupNavigation()

        if (!TokenManager.isLoggedIn()) {
            showAuthState("Login required to view blogs.")
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
            loadArticles()
            return
        }

        resolveCurrentUserAndLoadArticles()
    }

    private fun bindViews() {
        progressBar = findViewById(R.id.progressBar)
        listView = findViewById(R.id.lvArticles)
        emptyView = findViewById(R.id.tvEmptyState)
        stateContainer = findViewById(R.id.layoutState)
        stateMessage = findViewById(R.id.tvStateMessage)
        stateAction = findViewById(R.id.btnStateAction)
        layoutProfileHeader = findViewById(R.id.layoutProfileHeader)
        ivHeaderAvatar = findViewById(R.id.ivHeaderAvatar)
        tvHeaderAvatar = findViewById(R.id.tvHeaderAvatar)
        tvHeaderName = findViewById(R.id.tvHeaderName)
        btnMessages = findViewById(R.id.btnMessages)
        etSearchQuery = findViewById(R.id.etSearchQuery)
        btnSearch = findViewById(R.id.btnSearch)
        btnClearSearch = findViewById(R.id.btnClearSearch)
        btnNavProperties = findViewById(R.id.btnNavProperties)
        btnNavFavorites = findViewById(R.id.btnNavFavorites)
        btnNavArticles = findViewById(R.id.btnNavArticles)
        btnNavCareer = findViewById(R.id.btnNavCareer)

        articleAdapter = ArticleAdapter(
            this,
            emptyList(),
            onItemClick = { article -> openArticleDetails(article) }
        )

        listView.adapter = articleAdapter
        listView.emptyView = emptyView
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

        btnSearch.setOnClickListener { performSearch() }
        btnClearSearch.setOnClickListener { clearSearchAndReload() }
        btnClearSearch.visibility = View.GONE
        etSearchQuery.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnClearSearch.visibility = if (s.isNullOrBlank()) View.GONE else View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
        etSearchQuery.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }
    }

    private fun setupNavigation() {
        btnNavArticles.isEnabled = false
        btnNavArticles.alpha = 1f
        btnNavArticles.setOnClickListener { }

        btnNavProperties.setOnClickListener {
            startActivity(Intent(this, PropertyListActivity::class.java))
            finish()
        }

        btnNavFavorites.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
            finish()
        }

        btnNavCareer.setOnClickListener {
            startActivity(Intent(this, CareerApplicationActivity::class.java))
            finish()
        }
    }

    private fun resolveCurrentUserAndLoadArticles() {
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
                                bindHeader(user)
                                loadArticles()
                            }

                            SessionManager.isPrivilegedRole(user.role) -> {
                                showUnavailableState(roleUnavailableMessage(user.role))
                            }

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
                showErrorState(error.message ?: "Failed to load blogs.")
            }
        }
    }

    private fun loadArticles() {
        lastLoadMode = LoadMode.DEFAULT
        lastSearchQuery = ""
        showLoading()
        lifecycleScope.launch {
            try {
                val articles = articleRepository.getArticles()
                showArticles(articles, isSearch = false)
            } catch (error: ArticleLoginRequiredException) {
                TokenManager.clear()
                showAuthState(error.message ?: "Login required to view blogs.")
            } catch (error: ArticleRoleException) {
                showUnavailableState(error.message ?: "This mobile module is only for registered users.")
            } catch (error: Exception) {
                if (error.isHarmlessCancellation()) return@launch
                showErrorState(error.message ?: "Failed to load blogs.")
            }
        }
    }

    private fun performSearch() {
        val query = etSearchQuery.text?.toString()?.trim().orEmpty()
        if (query.isBlank()) {
            clearSearchAndReload()
            return
        }

        lastLoadMode = LoadMode.SEARCH
        lastSearchQuery = query
        showLoading()
        lifecycleScope.launch {
            try {
                // Article search maps directly to the backend title query parameter.
                val articles = articleRepository.getArticles(query)
                showArticles(articles, isSearch = true)
            } catch (error: ArticleLoginRequiredException) {
                TokenManager.clear()
                showAuthState(error.message ?: "Login required to view blogs.")
            } catch (error: ArticleRoleException) {
                showUnavailableState(error.message ?: "This mobile module is only for registered users.")
            } catch (error: Exception) {
                if (error.isHarmlessCancellation()) return@launch
                showErrorState(error.message ?: "Failed to search blogs.")
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        listView.visibility = View.GONE
        emptyView.visibility = View.GONE
        stateContainer.visibility = View.GONE
    }

    private fun showArticles(articles: List<ArticleCardDto>, isSearch: Boolean) {
        progressBar.visibility = View.GONE
        stateContainer.visibility = View.GONE

        if (articles.isEmpty()) {
            listView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
            emptyView.text = if (isSearch) {
                getString(R.string.search_no_articles_found)
            } else {
                "No articles available at the moment."
            }
            return
        }

        emptyView.visibility = View.GONE
        listView.visibility = View.VISIBLE
        articleAdapter.submitList(articles)
    }

    private fun clearSearchAndReload() {
        etSearchQuery.setText("")
        btnClearSearch.visibility = View.GONE
        loadArticles()
    }

    private fun showAuthState(message: String) {
        progressBar.visibility = View.GONE
        listView.visibility = View.GONE
        emptyView.visibility = View.GONE
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
        listView.visibility = View.GONE
        emptyView.visibility = View.GONE
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
        listView.visibility = View.GONE
        emptyView.visibility = View.GONE
        stateContainer.visibility = View.VISIBLE
        stateMessage.text = message
        stateAction.text = "Retry"
        stateAction.visibility = View.VISIBLE
        stateAction.setOnClickListener {
            when (lastLoadMode) {
                LoadMode.SEARCH -> performSearch()
                LoadMode.DEFAULT -> loadArticles()
            }
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun openArticleDetails(article: ArticleCardDto) {
        val articleId = article.id ?: return
        val intent = Intent(this, ArticleDetailActivity::class.java).apply {
            putExtra(ArticleDetailActivity.EXTRA_ARTICLE_ID, articleId)
        }
        startActivity(intent)
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

    private fun roleUnavailableMessage(role: String?): String {
        return when (SessionManager.normalizeRole(role)) {
            "ADMIN" -> "Admin mobile is not yet available."
            "AGENT" -> "Agent mobile is not yet available."
            else -> "This mobile module is only for registered users."
        }
    }

    companion object {
        const val EXTRA_USER_FIRSTNAME = LoginActivity.EXTRA_USER_FIRSTNAME
        const val EXTRA_USER_LASTNAME = LoginActivity.EXTRA_USER_LASTNAME
        const val EXTRA_USER_EMAIL = LoginActivity.EXTRA_USER_EMAIL
        const val EXTRA_USER_ROLE = LoginActivity.EXTRA_USER_ROLE
        const val EXTRA_USER_PROFILE_IMAGE_URL = LoginActivity.EXTRA_USER_PROFILE_IMAGE_URL
    }
}
