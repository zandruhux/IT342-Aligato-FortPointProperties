package com.example.fortpointproperties.features.auth.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fortpointproperties.R
import com.example.fortpointproperties.features.auth.network.AuthApi
import com.example.fortpointproperties.shared.auth.TokenManager
import com.example.fortpointproperties.shared.network.ApiClient
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var api: AuthApi
    private val TAG = "ProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TokenManager.init(this)

        if (TokenManager.getToken() == null) {
            redirectToLogin()
            return
        }

        setContentView(R.layout.activity_profile)

        api = ApiClient.retrofit.create(AuthApi::class.java)

        val tvFullName = findViewById<TextView>(R.id.tvFullName)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // Clear default text
        tvFullName.text = "Loading..."
        tvEmail.text = "Loading..."

        loadUserProfile(tvFullName, tvEmail)

        btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

        private fun loadUserProfile(tvFullName: TextView, tvEmail: TextView) {
        lifecycleScope.launch {
            try {
                val token = TokenManager.getAccessToken()
                Log.d(TAG, "Token: $token")
                Log.d(TAG, "Token length: ${token?.length}")

                val response = api.getProfile()

                Log.d(TAG, "Response Code: ${response.code()}")
                Log.d(TAG, "Response Message: ${response.message()}")
                Log.d(TAG, "Response Headers: ${response.headers()}")

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.d(TAG, "API Response: $apiResponse")

                    val user = apiResponse?.data

                    if (user != null) {
                        val fullName = "${user.firstname} ${user.lastname}"
                        Log.d(TAG, "Setting profile: $fullName, ${user.email}")
                        tvFullName.text = fullName
                        tvEmail.text = user.email
                        Toast.makeText(this@ProfileActivity, "Profile loaded successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.w(TAG, "User data is null")
                        tvFullName.text = "Error loading profile"
                        Toast.makeText(this@ProfileActivity, "Failed to load profile data", Toast.LENGTH_SHORT).show()
                    }

                } else if (response.code() == 403) {
                    Log.w(TAG, "403 Forbidden - JWT validation failed")
                    tvFullName.text = "Authentication failed"
                    Toast.makeText(this@ProfileActivity, "Session expired - please login again", Toast.LENGTH_SHORT).show()
                    TokenManager.clear()
                    redirectToLogin()
                } else if (response.code() == 401) {
                    Log.w(TAG, "401 Unauthorized - Token invalid or expired")
                    tvFullName.text = "Session expired"
                    TokenManager.clear()
                    redirectToLogin()
                } else {
                    Log.e(TAG, "API Error: ${response.code()} - ${response.message()}")
                    tvFullName.text = "Error: ${response.code()}"
                    Toast.makeText(this@ProfileActivity, "Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading profile", e)
                tvFullName.text = "Error: ${e.localizedMessage}"
                Toast.makeText(this@ProfileActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirm Logout")
            .setMessage("Are you sure you want to logout? You will need to login again to access your account.")
            .setPositiveButton("Logout") { dialog, _ ->
                dialog.dismiss()
                performLogout()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun performLogout() {
        TokenManager.clear()
        redirectToLogin()
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}