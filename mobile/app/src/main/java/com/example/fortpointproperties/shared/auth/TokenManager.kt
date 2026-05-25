package com.example.fortpointproperties.shared.auth

object TokenManager {

    private const val PREF_NAME = "auth_pref"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_USER_FIRSTNAME = "user_firstname"
    private const val KEY_USER_LASTNAME = "user_lastname"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_PROFILE_IMAGE_URL = "user_profile_image_url"

    private lateinit var prefs: android.content.SharedPreferences

    private fun getPrefsOrNull(): android.content.SharedPreferences? {
        return if (::prefs.isInitialized) prefs else null
    }

    fun init(context: android.content.Context) {
        // Shared auth state is app-wide, so activities can initialize this repeatedly without replacing behavior.
        prefs = context.getSharedPreferences(PREF_NAME, android.content.Context.MODE_PRIVATE)
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        val sharedPreferences = getPrefsOrNull() ?: return

        sharedPreferences.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
        }.apply()
    }

    fun saveTokens(accessToken: String, refreshToken: String, role: String?) {
        val sharedPreferences = getPrefsOrNull() ?: return

        sharedPreferences.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_USER_ROLE, role)
        }.apply()
    }

    fun saveToken(accessToken: String) {
        getPrefsOrNull()?.edit()?.putString(KEY_ACCESS_TOKEN, accessToken)?.apply()
    }

    fun saveUserRole(role: String?) {
        getPrefsOrNull()?.edit()?.putString(KEY_USER_ROLE, role)?.apply()
    }

    fun saveUserProfile(firstName: String?, lastName: String?, email: String?, profileImageUrl: String?) {
        getPrefsOrNull()?.edit()?.apply {
            putString(KEY_USER_FIRSTNAME, firstName)
            putString(KEY_USER_LASTNAME, lastName)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_PROFILE_IMAGE_URL, profileImageUrl)
        }?.apply()
    }

    fun getAccessToken(): String? {
        return getPrefsOrNull()?.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getToken(): String? {
        return getAccessToken()
    }

    fun getRefreshToken(): String? {
        return getPrefsOrNull()?.getString(KEY_REFRESH_TOKEN, null)
    }

    fun getUserRole(): String? {
        return getPrefsOrNull()?.getString(KEY_USER_ROLE, null)
    }

    fun getUserFirstName(): String? {
        return getPrefsOrNull()?.getString(KEY_USER_FIRSTNAME, null)
    }

    fun getUserLastName(): String? {
        return getPrefsOrNull()?.getString(KEY_USER_LASTNAME, null)
    }

    fun getUserEmail(): String? {
        return getPrefsOrNull()?.getString(KEY_USER_EMAIL, null)
    }

    fun getUserProfileImageUrl(): String? {
        return getPrefsOrNull()?.getString(KEY_USER_PROFILE_IMAGE_URL, null)
    }

    fun isLoggedIn(): Boolean {
        return !getAccessToken().isNullOrBlank()
    }

    fun clear() {
        getPrefsOrNull()?.edit()?.clear()?.apply()
    }
}
