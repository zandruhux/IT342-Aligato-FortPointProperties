package com.example.userregistrationandauthentication.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.userregistrationandauthentication.R
import com.example.userregistrationandauthentication.data.LoginRequest
import com.example.userregistrationandauthentication.network.ApiClient
import com.example.userregistrationandauthentication.network.AuthApi
import com.example.userregistrationandauthentication.utils.TokenManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var api: AuthApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TokenManager.init(this)

        // Auto-login if token exists
        if (TokenManager.getToken() != null) {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }

        setContentView(R.layout.activity_login)

        api = ApiClient.retrofit.create(AuthApi::class.java)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGoRegister = findViewById<Button>(R.id.btnGoRegister)

        btnLogin.setOnClickListener {

            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@LoginActivity, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = api.login(LoginRequest(email, password))

                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        val loginData = apiResponse?.data
                        
                        if (loginData != null) {
                            TokenManager.saveTokens(loginData.accessToken, loginData.refreshToken)
                            startActivity(Intent(this@LoginActivity, ProfileActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "Invalid response from server", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Login Failed - Invalid credentials", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}