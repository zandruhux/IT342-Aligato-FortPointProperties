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
import com.example.fortpointproperties.features.auth.data.RegisterRequest
import com.example.fortpointproperties.features.auth.data.UserResponse
import com.example.fortpointproperties.features.auth.network.AuthApi
import com.example.fortpointproperties.features.properties.ui.PropertyListActivity
import com.example.fortpointproperties.shared.auth.SessionManager
import com.example.fortpointproperties.shared.auth.TokenManager
import com.example.fortpointproperties.shared.network.ApiClient
import com.example.fortpointproperties.shared.ui.isHarmlessCancellation
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var api: AuthApi
    private lateinit var btnTogglePassword: ImageButton
    private lateinit var btnToggleConfirmPassword: ImageButton
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TokenManager.init(this)
        api = ApiClient.retrofit.create(AuthApi::class.java)

        if (TokenManager.isLoggedIn()) {
            when {
                SessionManager.isRegisteredUser() -> {
                    openRegisteredHome()
                    return
                }

                SessionManager.isPrivilegedRole(SessionManager.getStoredRole()) -> {
                    showUnavailableMessage(SessionManager.getStoredRole())
                    TokenManager.clear()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    return
                }
            }
        }

        setContentView(R.layout.activity_register)

        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etLastName = findViewById<EditText>(R.id.etLastName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnGoLogin = findViewById<Button>(R.id.btnGoLogin)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)
        btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword)

        btnRegister.setOnClickListener {
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@RegisterActivity, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this@RegisterActivity, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 8) {
                Toast.makeText(this@RegisterActivity, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = api.register(
                        RegisterRequest(firstName, lastName, email, password)
                    )

                    if (response.isSuccessful) {
                        val authData = response.body()?.data
                        if (authData != null) {
                            handleAuthenticatedUser(authData.user, authData.accessToken, authData.refreshToken)
                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Invalid response from server",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else if (response.code() == 409) {
                        Toast.makeText(this@RegisterActivity, "Email already registered", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@RegisterActivity, "Registration Failed", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    if (e.isHarmlessCancellation()) return@launch
                    Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnGoLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnTogglePassword.setOnClickListener {
            togglePasswordVisibility(etPassword, true)
        }

        btnToggleConfirmPassword.setOnClickListener {
            togglePasswordVisibility(etConfirmPassword, false)
        }
    }

    private fun handleAuthenticatedUser(user: UserResponse, accessToken: String, refreshToken: String) {
        TokenManager.saveTokens(accessToken, refreshToken, user.role)
        TokenManager.saveUserProfile(user.firstname, user.lastname, user.email, user.profileImageUrl)

        when {
            SessionManager.isRegisteredUserRole(user.role) -> openRegisteredHome(user)
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
            putExtra(LoginActivity.EXTRA_USER_FIRSTNAME, user?.firstname)
            putExtra(LoginActivity.EXTRA_USER_LASTNAME, user?.lastname)
            putExtra(LoginActivity.EXTRA_USER_EMAIL, user?.email)
            putExtra(LoginActivity.EXTRA_USER_ROLE, user?.role)
            putExtra(LoginActivity.EXTRA_USER_PROFILE_IMAGE_URL, user?.profileImageUrl)
        }
        startActivity(intent)
        finish()
    }

    private fun togglePasswordVisibility(editText: EditText, primaryField: Boolean) {
        val cursorPosition = editText.selectionEnd.coerceAtLeast(0)
        if (primaryField) {
            isPasswordVisible = !isPasswordVisible
            editText.transformationMethod = if (isPasswordVisible) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            btnTogglePassword.setImageResource(
                if (isPasswordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
            )
            btnTogglePassword.contentDescription = getString(
                if (isPasswordVisible) R.string.password_visibility_hide else R.string.password_visibility_show
            )
        } else {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            editText.transformationMethod = if (isConfirmPasswordVisible) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            btnToggleConfirmPassword.setImageResource(
                if (isConfirmPasswordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
            )
            btnToggleConfirmPassword.contentDescription = getString(
                if (isConfirmPasswordVisible) R.string.password_visibility_hide else R.string.password_visibility_show
            )
        }
        editText.setSelection(cursorPosition.coerceAtMost(editText.text?.length ?: 0))
    }

    private fun showUnavailableMessage(role: String?) {
        val label = when (SessionManager.normalizeRole(role)) {
            "ADMIN" -> "Admin"
            "AGENT" -> "Agent"
            else -> "This role"
        }
        Toast.makeText(this, "$label mobile is not yet available.", Toast.LENGTH_SHORT).show()
    }
}
