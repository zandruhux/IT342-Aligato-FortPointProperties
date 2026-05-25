package com.example.fortpointproperties.features.favorites.data.repository

import com.example.fortpointproperties.features.auth.network.AuthApi
import com.example.fortpointproperties.features.favorites.data.model.FavoriteDto
import com.example.fortpointproperties.features.favorites.network.FavoriteApi
import com.example.fortpointproperties.shared.auth.SessionManager
import com.example.fortpointproperties.shared.auth.TokenManager
import com.example.fortpointproperties.shared.network.ApiClient
import com.example.fortpointproperties.shared.network.ApiResponse
import retrofit2.Response

class FavoriteRepository(
    private val favoriteApi: FavoriteApi = ApiClient.retrofit.create(FavoriteApi::class.java),
    private val authApi: AuthApi = ApiClient.retrofit.create(AuthApi::class.java),
) {

    suspend fun getFavorites(): List<FavoriteDto> {
        requireRegisteredUserAccess()
        return extractList(favoriteApi.getFavorites(), "Failed to load favorite properties")
    }

    suspend fun getFavoriteIds(): Set<String> {
        return getFavorites()
            .mapNotNull { it.propertyId?.takeIf { propertyId -> propertyId.isNotBlank() } }
            .toSet()
    }

    suspend fun addFavorite(propertyId: String): Boolean {
        requireRegisteredUserAccess()
        val response = favoriteApi.addToFavorites(propertyId)
        return if (response.isSuccessful) {
            true
        } else {
            when (response.code()) {
                409 -> true
                401, 403 -> throw FavoriteLoginRequiredException("Session expired. Please log in again.")
                else -> throw FavoriteLoadException(parseErrorMessage(response, "Failed to add favorite"))
            }
        }
    }

    suspend fun removeFavorite(propertyId: String): Boolean {
        requireRegisteredUserAccess()
        val response = favoriteApi.removeFromFavorites(propertyId)
        return if (response.isSuccessful) {
            true
        } else {
            when (response.code()) {
                404 -> true
                401, 403 -> throw FavoriteLoginRequiredException("Session expired. Please log in again.")
                else -> throw FavoriteLoadException(parseErrorMessage(response, "Failed to remove favorite"))
            }
        }
    }

    suspend fun isFavorited(propertyId: String): Boolean {
        requireRegisteredUserAccess()
        return extractItem(favoriteApi.checkIfFavorited(propertyId), "Failed to check favorite status")
    }

    private suspend fun requireRegisteredUserAccess() {
        if (TokenManager.getAccessToken().isNullOrBlank()) {
            throw FavoriteLoginRequiredException("Login required to view favorites.")
        }

        val storedRole = SessionManager.getStoredRole()
        when {
            SessionManager.isRegisteredUserRole(storedRole) -> return
            SessionManager.isPrivilegedRole(storedRole) -> {
                throw FavoriteRoleException("This mobile module is only for registered users.")
            }
        }

        val profileResponse = authApi.getProfile()
        if (!profileResponse.isSuccessful) {
            if (profileResponse.code() == 401 || profileResponse.code() == 403) {
                throw FavoriteLoginRequiredException("Session expired. Please log in again.")
            }
            throw FavoriteLoadException(parseErrorMessage(profileResponse, "Unable to verify user access."))
        }

        val user = profileResponse.body()?.data
            ?: throw FavoriteLoadException("Unable to read the current user profile.")

        TokenManager.saveUserRole(user.role)

        if (!SessionManager.isRegisteredUserRole(user.role)) {
            throw FavoriteRoleException("This mobile module is only for registered users.")
        }
    }

    private fun <T> extractList(
        response: Response<ApiResponse<List<T>>>,
        fallbackMessage: String
    ): List<T> {
        if (!response.isSuccessful) {
            if (response.code() == 401 || response.code() == 403) {
                throw FavoriteLoginRequiredException("Session expired. Please log in again.")
            }
            throw FavoriteLoadException(parseErrorMessage(response, fallbackMessage))
        }

        return response.body()?.data ?: throw FavoriteLoadException("Server returned an empty response.")
    }

    private fun <T> extractItem(
        response: Response<ApiResponse<T>>,
        fallbackMessage: String
    ): T {
        if (!response.isSuccessful) {
            if (response.code() == 401 || response.code() == 403) {
                throw FavoriteLoginRequiredException("Session expired. Please log in again.")
            }
            throw FavoriteLoadException(parseErrorMessage(response, fallbackMessage))
        }

        return response.body()?.data ?: throw FavoriteLoadException("Server returned an empty response.")
    }

    private fun <T> parseErrorMessage(
        response: Response<ApiResponse<T>>,
        fallbackMessage: String
    ): String {
        val bodyMessage = response.body()?.error?.message?.takeIf { it.isNotBlank() }
        return bodyMessage ?: response.message().takeIf { it.isNotBlank() } ?: fallbackMessage
    }
}
