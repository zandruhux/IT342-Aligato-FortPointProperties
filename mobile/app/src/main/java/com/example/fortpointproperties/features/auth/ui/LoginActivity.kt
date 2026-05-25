package com.example.fortpointproperties.features.auth.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fortpointproperties.R
import com.example.fortpointproperties.features.auth.data.LoginRequest
import com.example.fortpointproperties.features.auth.data.UserResponse
import com.example.fortpointproperties.features.auth.network.AuthApi
import com.example.fortpointproperties.features.properties.ui.PropertyListActivity
import com.example.fortpointproperties.shared.auth.SessionManager
import com.example.fortpointproperties.shared.auth.TokenManager
import com.example.fortpointproperties.shared.network.ApiClient
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var api: AuthApi

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnTogglePassword: ImageButton
    private lateinit var btnLogin: Button
    private lateinit var btnGoRegister: Button
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TokenManager.init(this)
        api = ApiClient.retrofit.create(AuthApi::class.java)

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
        btnGoRegister = findViewById(R.id.btnGoRegister)
    }

    private fun bindActions() {
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
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
                            Toast.makeText(
                                this@LoginActivity,
                                "Invalid response from server",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login Failed - Invalid credentials",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        btnTogglePassword.setOnClickListener {
            togglePasswordVisibility()
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

    companion object {
        const val EXTRA_USER_FIRSTNAME = "extra_user_firstname"
        const val EXTRA_USER_LASTNAME = "extra_user_lastname"
        const val EXTRA_USER_EMAIL = "extra_user_email"
        const val EXTRA_USER_ROLE = "extra_user_role"
        const val EXTRA_USER_PROFILE_IMAGE_URL = "extra_user_profile_image_url"
    }
}
