package com.example.fortpointproperties.features.auth.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.fortpointproperties.R
import com.example.fortpointproperties.features.auth.data.GoogleMobileLoginRequest
import com.example.fortpointproperties.features.auth.data.LoginRequest
import com.example.fortpointproperties.features.auth.data.UserResponse
import com.example.fortpointproperties.features.auth.network.AuthApi
import com.example.fortpointproperties.features.properties.ui.PropertyListActivity
import com.example.fortpointproperties.shared.auth.SessionManager
import com.example.fortpointproperties.shared.auth.TokenManager
import com.example.fortpointproperties.shared.network.ApiClient
import com.example.fortpointproperties.shared.ui.isHarmlessCancellation
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var api: AuthApi
    private lateinit var credentialManager: CredentialManager

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnTogglePassword: ImageButton
    private lateinit var btnLogin: Button
    private lateinit var btnGoogleSignIn: Button
    private lateinit var btnGoRegister: Button
    private lateinit var tvLoginError: TextView
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TokenManager.init(this)
        api = ApiClient.retrofit.create(AuthApi::class.java)
        credentialManager = CredentialManager.create(this)

        if (handleExistingRegisteredSession()) {
            return
        }

        setContentView(R.layout.activity_login)
        bindViews()
        bindActions()

        if (TokenManager.isLoggedIn() && SessionManager.getStoredRole().isNullOrBlank()) {
            resolveStoredSessionRole()
        }
    }

    private fun bindViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn)
        btnGoRegister = findViewById(R.id.btnGoRegister)
        tvLoginError = findViewById(R.id.tvLoginError)
    }

    private fun bindActions() {
        btnLogin.setOnClickListener {
            clearLoginError()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                showLoginError("Please fill all fields")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    setAuthLoading(true)
                    val response = api.login(LoginRequest(email, password))

                    if (response.isSuccessful) {
                        val loginData = response.body()?.data
                        if (loginData != null) {
                            handleAuthenticatedUser(
                                user = loginData.user,
                                accessToken = loginData.accessToken,
                                refreshToken = loginData.refreshToken
                            )
                        } else {
                            showLoginError("Invalid response from server")
                        }
                    } else {
                        showLoginError("Login failed. Please check your email and password.")
                    }
                } catch (e: Exception) {
                    if (e.isHarmlessCancellation()) return@launch
                    showLoginError(e.message ?: "Unable to sign in. Please try again.")
                } finally {
                    setAuthLoading(false)
                }
            }
        }

        btnGoogleSignIn.setOnClickListener {
            startGoogleSignIn()
        }

        btnGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        btnTogglePassword.setOnClickListener {
            togglePasswordVisibility()
        }
    }

    private fun startGoogleSignIn() {
        clearLoginError()

        val clientId = getString(R.string.google_web_client_id).trim()
        if (!isGoogleClientIdConfigured(clientId)) {
            showLoginError(getString(R.string.google_sign_in_not_configured))
            return
        }

        lifecycleScope.launch {
            try {
                setAuthLoading(true, getString(R.string.google_sign_in_loading))

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(clientId)
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    context = this@LoginActivity,
                    request = request
                )

                val credential = result.credential
                if (credential !is CustomCredential ||
                    credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    showLoginError(getString(R.string.google_sign_in_missing_token))
                    return@launch
                }

                val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleCredential.idToken
                if (idToken.isBlank()) {
                    showLoginError(getString(R.string.google_sign_in_missing_token))
                    return@launch
                }

                exchangeGoogleToken(idToken)
            } catch (e: GetCredentialCancellationException) {
                showLoginError(getString(R.string.google_sign_in_cancelled))
            } catch (e: GetCredentialException) {
                showLoginError(safeGoogleErrorMessage(e.message))
            } catch (e: GoogleIdTokenParsingException) {
                showLoginError(getString(R.string.google_sign_in_missing_token))
            } catch (e: Exception) {
                if (e.isHarmlessCancellation()) return@launch
                showLoginError(safeGoogleErrorMessage(e.message))
            } finally {
                setAuthLoading(false)
            }
        }
    }

    private fun isGoogleClientIdConfigured(clientId: String): Boolean {
        return clientId.isNotBlank() &&
            !clientId.startsWith("TODO_", ignoreCase = true) &&
            clientId.contains(".apps.googleusercontent.com")
    }

    private fun safeGoogleErrorMessage(message: String?): String {
        val cleanedMessage = message?.trim().orEmpty()
        return if (cleanedMessage.isBlank()) {
            getString(R.string.google_sign_in_failed)
        } else {
            "${getString(R.string.google_sign_in_failed)} $cleanedMessage"
        }
    }

    private suspend fun exchangeGoogleToken(idToken: String) {
        val response = api.loginWithGoogle(GoogleMobileLoginRequest(idToken))
        if (response.isSuccessful) {
            val loginData = response.body()?.data
            if (loginData != null) {
                handleAuthenticatedUser(
                    user = loginData.user,
                    accessToken = loginData.accessToken,
                    refreshToken = loginData.refreshToken
                )
            } else {
                showLoginError("Invalid response from server")
            }
        } else {
            showLoginError(getString(R.string.google_sign_in_failed))
        }
    }

    private fun handleExistingRegisteredSession(): Boolean {
        if (!TokenManager.isLoggedIn()) {
            return false
        }

        when {
            SessionManager.isRegisteredUser() -> {
                openRegisteredHome()
                return true
            }

            SessionManager.isPrivilegedRole(SessionManager.getStoredRole()) -> {
                showUnavailableMessage(SessionManager.getStoredRole())
                TokenManager.clear()
                return false
            }

            SessionManager.getStoredRole().isNullOrBlank() -> {
                return false
            }

            else -> {
                TokenManager.clear()
                return false
            }
        }
    }

    private fun resolveStoredSessionRole() {
        lifecycleScope.launch {
            try {
                val response = api.getProfile()
                if (response.isSuccessful) {
                    val user = response.body()?.data
                    if (user != null) {
                        TokenManager.saveUserRole(user.role)
                        handleSessionRole(user)
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Unable to restore the current session.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (response.code() == 401 || response.code() == 403) {
                    TokenManager.clear()
                    Toast.makeText(
                        this@LoginActivity,
                        "Session expired. Please log in again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                if (e.isHarmlessCancellation()) return@launch
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleAuthenticatedUser(
        user: UserResponse,
        accessToken: String,
        refreshToken: String
    ) {
        TokenManager.saveTokens(accessToken, refreshToken, user.role)
        TokenManager.saveUserProfile(user.firstname, user.lastname, user.email, user.profileImageUrl)
        handleSessionRole(user)
    }

    private fun handleSessionRole(user: UserResponse) {
        when {
            SessionManager.isRegisteredUserRole(user.role) -> {
                openRegisteredHome(user)
            }

            SessionManager.isPrivilegedRole(user.role) -> {
                showUnavailableMessage(user.role)
                TokenManager.clear()
            }

            else -> {
                TokenManager.clear()
                Toast.makeText(
                    this,
                    "This mobile module is only for registered users.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun openRegisteredHome(user: UserResponse? = null) {
        val intent = Intent(this, PropertyListActivity::class.java).apply {
            putExtra(EXTRA_USER_FIRSTNAME, user?.firstname)
            putExtra(EXTRA_USER_LASTNAME, user?.lastname)
            putExtra(EXTRA_USER_EMAIL, user?.email)
            putExtra(EXTRA_USER_ROLE, user?.role)
            putExtra(EXTRA_USER_PROFILE_IMAGE_URL, user?.profileImageUrl)
        }
        startActivity(intent)
        finish()
    }

    private fun togglePasswordVisibility() {
        val cursorPosition = etPassword.selectionEnd.coerceAtLeast(0)
        isPasswordVisible = !isPasswordVisible
        etPassword.transformationMethod = if (isPasswordVisible) {
            HideReturnsTransformationMethod.getInstance()
        } else {
            PasswordTransformationMethod.getInstance()
        }
        etPassword.setSelection(cursorPosition.coerceAtMost(etPassword.text?.length ?: 0))
        btnTogglePassword.setImageResource(
            if (isPasswordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
        )
        btnTogglePassword.contentDescription = getString(
            if (isPasswordVisible) R.string.password_visibility_hide else R.string.password_visibility_show
        )
    }

    private fun showUnavailableMessage(role: String?) {
        val label = when (SessionManager.normalizeRole(role)) {
            "ADMIN" -> "Admin"
            "AGENT" -> "Agent"
            else -> "This role"
        }
        Toast.makeText(this, "$label mobile is not yet available.", Toast.LENGTH_SHORT).show()
    }

    private fun showLoginError(message: String) {
        tvLoginError.text = message
        tvLoginError.visibility = View.VISIBLE
    }

    private fun clearLoginError() {
        if (::tvLoginError.isInitialized) {
            tvLoginError.text = ""
            tvLoginError.visibility = View.GONE
        }
    }

    private fun setAuthLoading(isLoading: Boolean, googleText: String? = null) {
        btnLogin.isEnabled = !isLoading
        btnGoogleSignIn.isEnabled = !isLoading
        btnGoRegister.isEnabled = !isLoading
        btnLogin.text = if (isLoading) "Signing in..." else "Login"
        btnGoogleSignIn.text = when {
            isLoading && googleText != null -> googleText
            else -> getString(R.string.google_sign_in)
        }
    }

    companion object {
        const val EXTRA_USER_FIRSTNAME = "extra_user_firstname"
        const val EXTRA_USER_LASTNAME = "extra_user_lastname"
        const val EXTRA_USER_EMAIL = "extra_user_email"
        const val EXTRA_USER_ROLE = "extra_user_role"
        const val EXTRA_USER_PROFILE_IMAGE_URL = "extra_user_profile_image_url"
    }
}
