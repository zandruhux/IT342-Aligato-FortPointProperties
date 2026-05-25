package com.example.fortpointproperties.features.properties.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Spinner
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
import com.example.fortpointproperties.features.favorites.data.repository.FavoriteLoginRequiredException
import com.example.fortpointproperties.features.favorites.data.repository.FavoriteRepository
import com.example.fortpointproperties.features.favorites.data.repository.FavoriteRoleException
import com.example.fortpointproperties.features.favorites.ui.FavoritesActivity
import com.example.fortpointproperties.features.properties.data.model.PropertyCardDto
import com.example.fortpointproperties.features.properties.data.repository.PropertyLoginRequiredException
import com.example.fortpointproperties.features.properties.data.repository.PropertyRepository
import com.example.fortpointproperties.features.properties.data.repository.PropertyRoleException
import com.example.fortpointproperties.features.properties.ui.adapter.PropertyAdapter
import com.example.fortpointproperties.shared.auth.SessionManager
import com.example.fortpointproperties.shared.auth.TokenManager
import com.example.fortpointproperties.shared.network.ApiClient
import com.example.fortpointproperties.shared.ui.MobileHeaderBinder
import kotlinx.coroutines.launch

class PropertyListActivity : AppCompatActivity() {

    private enum class PropertySearchField {
        PROJECT_NAME,
        LOCATION,
        DEVELOPER
    }

    private enum class LoadMode {
        DEFAULT,
        SEARCH
    }

    private val propertyRepository = PropertyRepository()
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
    private lateinit var spSearchType: Spinner
    private lateinit var etSearchQuery: EditText
    private lateinit var btnSearch: ImageButton
    private lateinit var btnClearSearch: ImageButton
    private lateinit var btnNavProperties: Button
    private lateinit var btnNavFavorites: Button
    private lateinit var btnNavArticles: Button
    private lateinit var btnNavCareer: Button
    private var currentSearchField = PropertySearchField.PROJECT_NAME
    private var lastLoadMode = LoadMode.DEFAULT
    private var lastSearchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TokenManager.init(this)
        authApi = ApiClient.retrofit.create(AuthApi::class.java)
        setContentView(R.layout.activity_property_list)

        bindViews()
        bindActions()
        setupSearchControls()
        setupNavigation()

        if (!TokenManager.isLoggedIn()) {
            showAuthState("Login required to browse properties.")
            return
        }

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
            loadProperties()
            return
        }

        resolveCurrentUserAndLoadHome()
    }

    private fun bindViews() {
        progressBar = findViewById(R.id.progressBar)
        listView = findViewById(R.id.lvProperties)
        emptyView = findViewById(R.id.tvEmptyState)
        stateContainer = findViewById(R.id.layoutState)
        stateMessage = findViewById(R.id.tvStateMessage)
        stateAction = findViewById(R.id.btnStateAction)
        layoutProfileHeader = findViewById(R.id.layoutProfileHeader)
        ivHeaderAvatar = findViewById(R.id.ivHeaderAvatar)
        tvHeaderAvatar = findViewById(R.id.tvHeaderAvatar)
        tvHeaderName = findViewById(R.id.tvHeaderName)
        btnMessages = findViewById(R.id.btnMessages)
        spSearchType = findViewById(R.id.spSearchType)
        etSearchQuery = findViewById(R.id.etSearchQuery)
        btnSearch = findViewById(R.id.btnSearch)
        btnClearSearch = findViewById(R.id.btnClearSearch)
        btnNavProperties = findViewById(R.id.btnNavProperties)
        btnNavFavorites = findViewById(R.id.btnNavFavorites)
        btnNavArticles = findViewById(R.id.btnNavArticles)
        btnNavCareer = findViewById(R.id.btnNavCareer)

        propertyAdapter = PropertyAdapter(
            this,
            emptyList(),
            { property -> openPropertyDetail(property) },
            PropertyAdapter.CardActionMode.TOGGLE_FAVORITE,
            { property -> toggleFavorite(property) }
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

    private fun setupSearchControls() {
        val labels = listOf(
            getString(R.string.search_project_name),
            getString(R.string.search_location),
            getString(R.string.search_developer),
        )
        val searchAdapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            labels
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return super.getView(position, convertView, parent).apply {
                    (this as? TextView)?.setTextColor(resources.getColor(android.R.color.white, theme))
                }
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                return super.getDropDownView(position, convertView, parent).apply {
                    (this as? TextView)?.setTextColor(resources.getColor(R.color.property_blue, theme))
                }
            }
        }.apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spSearchType.adapter = searchAdapter
        spSearchType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentSearchField = when (position) {
                    1 -> PropertySearchField.LOCATION
                    2 -> PropertySearchField.DEVELOPER
                    else -> PropertySearchField.PROJECT_NAME
                }
                updateSearchHint()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                currentSearchField = PropertySearchField.PROJECT_NAME
                updateSearchHint()
            }
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
        updateSearchHint()
    }

    private fun setupNavigation() {
        btnNavProperties.isEnabled = false
        btnNavProperties.alpha = 1f
        btnNavProperties.setOnClickListener { }

        btnNavFavorites.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        btnNavArticles.setOnClickListener {
            startActivity(Intent(this, ArticleListActivity::class.java))
        }

        btnNavCareer.setOnClickListener {
            startActivity(Intent(this, CareerApplicationActivity::class.java))
        }
    }

    private fun resolveCurrentUserAndLoadHome() {
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
                                loadProperties()
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
                showErrorState(error.message ?: "Failed to load properties.")
            }
        }
    }

    private fun loadProperties() {
        lastLoadMode = LoadMode.DEFAULT
        lastSearchQuery = ""
        showLoading()
        lifecycleScope.launch {
            try {
                val properties = propertyRepository.getPropertyCards()
                showProperties(properties, isSearch = false)
            } catch (error: PropertyLoginRequiredException) {
                TokenManager.clear()
                showAuthState(error.message ?: "Login required to browse properties.")
            } catch (error: PropertyRoleException) {
                showUnavailableState(error.message ?: "This mobile module is only for registered users.")
            } catch (error: Exception) {
                showErrorState(error.message ?: "Failed to load properties.")
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
                // The registered-user search endpoint accepts one active filter parameter at a time.
                val properties = when (currentSearchField) {
                    PropertySearchField.PROJECT_NAME -> {
                        propertyRepository.searchPropertyCards(name = query)
                    }

                    PropertySearchField.LOCATION -> {
                        propertyRepository.searchPropertyCards(location = query)
                    }

                    PropertySearchField.DEVELOPER -> {
                        propertyRepository.searchPropertyCards(developer = query)
                    }
                }
                showProperties(properties, isSearch = true)
            } catch (error: PropertyLoginRequiredException) {
                TokenManager.clear()
                showAuthState(error.message ?: "Login required to browse properties.")
            } catch (error: PropertyRoleException) {
                showUnavailableState(error.message ?: "This mobile module is only for registered users.")
            } catch (error: Exception) {
                showErrorState(error.message ?: "Failed to search properties.")
            }
        }
    }

    private fun bindHeader(user: UserResponse) {
        bindHeader(user.firstname, user.lastname, user.email, user.profileImageUrl)
        TokenManager.saveUserRole(user.role)
    }

    private fun bindHeader(firstName: String?, lastName: String?, email: String?, profileImageUrl: String?) {
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

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        listView.visibility = View.GONE
        emptyView.visibility = View.GONE
        stateContainer.visibility = View.GONE
    }

    private fun showProperties(properties: List<PropertyCardDto>, isSearch: Boolean) {
        progressBar.visibility = View.GONE
        stateContainer.visibility = View.GONE

        if (properties.isEmpty()) {
            listView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
            emptyView.text = if (isSearch) {
                getString(R.string.search_no_properties_found)
            } else {
                "No properties available at the moment."
            }
            return
        }

        emptyView.visibility = View.GONE
        listView.visibility = View.VISIBLE
        propertyAdapter.submitList(properties)
        loadFavoriteStates()
    }

    override fun onResume() {
        super.onResume()
        if (::propertyAdapter.isInitialized && listView.visibility == View.VISIBLE && propertyAdapter.count > 0) {
            loadFavoriteStates()
        }
    }

    private fun loadFavoriteStates() {
        lifecycleScope.launch {
            try {
                val favoriteIds = favoriteRepository.getFavoriteIds()
                propertyAdapter.submitFavoriteIds(favoriteIds)
            } catch (error: FavoriteLoginRequiredException) {
                TokenManager.clear()
                showAuthState(error.message ?: "Login required to manage favorites.")
            } catch (error: FavoriteRoleException) {
                showUnavailableState(error.message ?: "This mobile module is only for registered users.")
            } catch (_: Exception) {
                // Keep the property list usable even if favorite sync fails.
            }
        }
    }

    private fun toggleFavorite(property: PropertyCardDto) {
        val propertyId = property.id ?: return

        lifecycleScope.launch {
            try {
                val success = if (propertyAdapter.isFavorited(propertyId)) {
                    favoriteRepository.removeFavorite(propertyId)
                } else {
                    favoriteRepository.addFavorite(propertyId)
                }

                if (success) {
                    // Re-sync from the backend after toggles so searched and default lists share one source of truth.
                    loadFavoriteStates()
                }
            } catch (error: FavoriteLoginRequiredException) {
                TokenManager.clear()
                showAuthState(error.message ?: "Login required to manage favorites.")
            } catch (error: FavoriteRoleException) {
                showUnavailableState(error.message ?: "This mobile module is only for registered users.")
            } catch (error: Exception) {
                Toast.makeText(this@PropertyListActivity, error.message ?: "Unable to update favorite.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearSearchAndReload() {
        spSearchType.setSelection(0)
        etSearchQuery.setText("")
        currentSearchField = PropertySearchField.PROJECT_NAME
        updateSearchHint()
        btnClearSearch.visibility = View.GONE
        loadProperties()
    }

    private fun updateSearchHint() {
        etSearchQuery.hint = when (currentSearchField) {
            PropertySearchField.PROJECT_NAME -> getString(R.string.search_by_project_name_hint)
            PropertySearchField.LOCATION -> getString(R.string.search_by_location_hint)
            PropertySearchField.DEVELOPER -> getString(R.string.search_by_developer_hint)
        }
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
                LoadMode.DEFAULT -> loadProperties()
            }
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun openPropertyDetail(property: PropertyCardDto) {
        val propertyId = property.id ?: return
        val intent = Intent(this, PropertyDetailActivity::class.java).apply {
            putExtra(EXTRA_PROPERTY_ID, propertyId)
        }
        startActivity(intent)
    }

    private fun roleUnavailableMessage(role: String?): String {
        return when (SessionManager.normalizeRole(role)) {
            "ADMIN" -> "Admin mobile is not yet available."
            "AGENT" -> "Agent mobile is not yet available."
            else -> "This mobile module is only for registered users."
        }
    }

    companion object {
        const val EXTRA_PROPERTY_ID = "extra_property_id"
        const val EXTRA_USER_FIRSTNAME = LoginActivity.EXTRA_USER_FIRSTNAME
        const val EXTRA_USER_LASTNAME = LoginActivity.EXTRA_USER_LASTNAME
        const val EXTRA_USER_EMAIL = LoginActivity.EXTRA_USER_EMAIL
        const val EXTRA_USER_ROLE = LoginActivity.EXTRA_USER_ROLE
        const val EXTRA_USER_PROFILE_IMAGE_URL = LoginActivity.EXTRA_USER_PROFILE_IMAGE_URL
    }
}
