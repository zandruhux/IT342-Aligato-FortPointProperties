package com.example.fortpointproperties.features.favorites.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fortpointproperties.R
import com.example.fortpointproperties.features.auth.data.UserResponse
import com.example.fortpointproperties.features.auth.network.AuthApi
import com.example.fortpointproperties.features.auth.ui.LoginActivity
import com.example.fortpointproperties.features.auth.ui.ProfileActivity
import com.example.fortpointproperties.features.article.ui.ArticleListActivity
import com.example.fortpointproperties.features.careerApplication.ui.CareerApplicationActivity
import com.example.fortpointproperties.features.messaging.ui.ConversationsActivity
import com.example.fortpointproperties.features.favorites.data.model.FavoriteDto
import com.example.fortpointproperties.features.favorites.data.repository.FavoriteLoginRequiredException
import com.example.fortpointproperties.features.favorites.data.repository.FavoriteRepository
import com.example.fortpointproperties.features.favorites.data.repository.FavoriteRoleException
import com.example.fortpointproperties.features.properties.data.model.PropertyCardDto
import com.example.fortpointproperties.features.properties.ui.PropertyDetailActivity
import com.example.fortpointproperties.features.properties.ui.PropertyListActivity
import com.example.fortpointproperties.features.properties.ui.adapter.PropertyAdapter
import com.example.fortpointproperties.shared.auth.SessionManager
import com.example.fortpointproperties.shared.auth.TokenManager
import com.example.fortpointproperties.shared.network.ApiClient
import com.example.fortpointproperties.shared.ui.MobileHeaderBinder
import com.example.fortpointproperties.shared.ui.isHarmlessCancellation
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {

    private val favoriteRepository = FavoriteRepository()
    private lateinit var authApi: AuthApi
    private lateinit var propertyAdapter: PropertyAdapter
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
    private lateinit var btnNavProperties: Button
    private lateinit var btnNavFavorites: Button
    private lateinit var btnNavArticles: Button
    private lateinit var btnNavCareer: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TokenManager.init(this)
        authApi = ApiClient.retrofit.create(AuthApi::class.java)
        setContentView(R.layout.activity_favorites)

        bindViews()
        bindActions()
        setupNavigation()

        if (!TokenManager.isLoggedIn()) {
            showAuthState("Login required to view favorites.")
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
            bindHeader(
                firstName = providedFirstName,
                lastName = providedLastName,
                email = providedEmail,
                profileImageUrl = providedProfileImageUrl
            )
            loadFavorites()
            return
        }

        resolveCurrentUserAndLoadFavorites()
    }

    private fun bindViews() {
        progressBar = findViewById(R.id.progressBar)
        listView = findViewById(R.id.lvFavorites)
        emptyView = findViewById(R.id.tvEmptyState)
        stateContainer = findViewById(R.id.layoutState)
        stateMessage = findViewById(R.id.tvStateMessage)
        stateAction = findViewById(R.id.btnStateAction)
        layoutProfileHeader = findViewById(R.id.layoutProfileHeader)
        ivHeaderAvatar = findViewById(R.id.ivHeaderAvatar)
        tvHeaderAvatar = findViewById(R.id.tvHeaderAvatar)
        tvHeaderName = findViewById(R.id.tvHeaderName)
        btnMessages = findViewById(R.id.btnMessages)
        btnNavProperties = findViewById(R.id.btnNavProperties)
        btnNavFavorites = findViewById(R.id.btnNavFavorites)
        btnNavArticles = findViewById(R.id.btnNavArticles)
        btnNavCareer = findViewById(R.id.btnNavCareer)

        propertyAdapter = PropertyAdapter(
            this,
            emptyList(),
            onItemClick = { property -> openPropertyDetail(property) },
            actionMode = PropertyAdapter.CardActionMode.REMOVE_FAVORITE,
            onActionClick = { property -> removeFavorite(property) }
        )

        listView.adapter = propertyAdapter
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
    }

    private fun setupNavigation() {
        btnNavFavorites.isEnabled = false
        btnNavFavorites.alpha = 1f
        btnNavFavorites.setOnClickListener { }

        btnNavProperties.setOnClickListener {
            finish()
        }

        btnNavArticles.setOnClickListener {
            startActivity(Intent(this, ArticleListActivity::class.java))
        }

        btnNavCareer.setOnClickListener {
            startActivity(Intent(this, CareerApplicationActivity::class.java))
        }
    }

    private fun resolveCurrentUserAndLoadFavorites() {
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
                                loadFavorites()
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
                showErrorState(error.message ?: "Failed to load favorites.")
            }
        }
    }

    private fun loadFavorites() {
        showLoading()
        lifecycleScope.launch {
            try {
                val favorites = favoriteRepository.getFavorites()
                showFavorites(favorites)
            } catch (error: FavoriteLoginRequiredException) {
                TokenManager.clear()
                showAuthState(error.message ?: "Login required to view favorites.")
            } catch (error: FavoriteRoleException) {
                showUnavailableState(error.message ?: "This mobile module is only for registered users.")
            } catch (error: Exception) {
                if (error.isHarmlessCancellation()) return@launch
                showErrorState(error.message ?: "Failed to load favorite properties.")
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        listView.visibility = View.GONE
        emptyView.visibility = View.GONE
        stateContainer.visibility = View.GONE
    }

    private fun showFavorites(favorites: List<FavoriteDto>) {
        progressBar.visibility = View.GONE
        stateContainer.visibility = View.GONE

        if (favorites.isEmpty()) {
            propertyAdapter.submitFavoriteIds(emptySet())
            listView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
            emptyView.text = "No favorite properties yet."
            return
        }

        val properties = favorites.mapNotNull { favorite ->
            val propertyId = favorite.propertyId?.takeIf { it.isNotBlank() } ?: favorite.id?.takeIf { it.isNotBlank() }
            propertyId?.let {
                PropertyCardDto(
                    id = it,
                    name = favorite.propertyName,
                    basicDescription = favorite.description,
                    location = favorite.location,
                    priceRangeMin = favorite.priceRangeMin,
                    priceRangeMax = favorite.priceRangeMax,
                    listingTypes = emptyList(),
                    hasPromo = favorite.hasPromo,
                    coverPhotoUrl = favorite.coverPhotoUrl
                )
            }
        }

        if (properties.isEmpty()) {
            propertyAdapter.submitFavoriteIds(emptySet())
            listView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
            emptyView.text = "No favorite properties yet."
            return
        }

        emptyView.visibility = View.GONE
        listView.visibility = View.VISIBLE
        propertyAdapter.submitFavoriteIds(properties.mapNotNull { it.id }.toSet())
        propertyAdapter.submitList(properties)
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
        stateAction.setOnClickListener { loadFavorites() }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun openPropertyDetail(property: PropertyCardDto) {
        val propertyId = property.id ?: return
        val intent = Intent(this, PropertyDetailActivity::class.java).apply {
            putExtra(PropertyListActivity.EXTRA_PROPERTY_ID, propertyId)
        }
        startActivity(intent)
    }

    private fun removeFavorite(property: PropertyCardDto) {
        val propertyId = property.id ?: return

        lifecycleScope.launch {
            try {
                if (favoriteRepository.removeFavorite(propertyId)) {
                    loadFavorites()
                }
            } catch (error: FavoriteLoginRequiredException) {
                TokenManager.clear()
                showAuthState(error.message ?: "Login required to manage favorites.")
            } catch (error: FavoriteRoleException) {
                showUnavailableState(error.message ?: "This mobile module is only for registered users.")
            } catch (error: Exception) {
                if (error.isHarmlessCancellation()) return@launch
                Toast.makeText(this@FavoritesActivity, error.message ?: "Unable to update favorite.", Toast.LENGTH_SHORT).show()
            }
        }
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
