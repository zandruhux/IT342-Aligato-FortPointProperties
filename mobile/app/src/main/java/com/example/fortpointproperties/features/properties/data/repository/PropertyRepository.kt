package com.example.fortpointproperties.features.properties.data.repository

import com.example.fortpointproperties.features.auth.network.AuthApi
import com.example.fortpointproperties.features.properties.data.model.PropertyCardDto
import com.example.fortpointproperties.features.properties.data.model.PropertyDetailDto
import com.example.fortpointproperties.features.properties.network.PropertyApi
import com.example.fortpointproperties.shared.auth.SessionManager
import com.example.fortpointproperties.shared.auth.TokenManager
import com.example.fortpointproperties.shared.network.ApiClient
import com.example.fortpointproperties.shared.network.ApiResponse
import retrofit2.Response

class PropertyRepository(
    private val propertyApi: PropertyApi = ApiClient.retrofit.create(PropertyApi::class.java),
    private val authApi: AuthApi = ApiClient.retrofit.create(AuthApi::class.java),
) {

    suspend fun getPropertyCards(): List<PropertyCardDto> {
        requireRegisteredUserAccess()
        return extractList(propertyApi.getProperties(), "Failed to load property cards")
    }

    suspend fun searchPropertyCards(
        name: String? = null,
        location: String? = null,
        developer: String? = null,
    ): List<PropertyCardDto> {
        requireRegisteredUserAccess()
        return extractList(
            propertyApi.searchProperties(
                name = name?.trim()?.takeIf { it.isNotBlank() },
                location = location?.trim()?.takeIf { it.isNotBlank() },
                developer = developer?.trim()?.takeIf { it.isNotBlank() },
            ),
            "Failed to search properties"
        )
    }

    suspend fun getPropertyDetails(propertyId: String): PropertyDetailDto {
        requireRegisteredUserAccess()
        return extractItem(propertyApi.getPropertyDetails(propertyId), "Failed to load property details")
    }

    private suspend fun requireRegisteredUserAccess() {
        if (TokenManager.getAccessToken().isNullOrBlank()) {
            throw PropertyLoginRequiredException("Login required to browse properties.")
        }

        val storedRole = SessionManager.getStoredRole()
        when {
            SessionManager.isRegisteredUserRole(storedRole) -> return
            SessionManager.isPrivilegedRole(storedRole) -> {
                throw PropertyRoleException("This mobile module is only for registered users.")
            }
        }

        val profileResponse = authApi.getProfile()
        if (!profileResponse.isSuccessful) {
            if (profileResponse.code() == 401 || profileResponse.code() == 403) {
                throw PropertyLoginRequiredException("Session expired. Please log in again.")
            }
            throw PropertyLoadException(parseErrorMessage(profileResponse, "Unable to verify user access."))
        }

        val user = profileResponse.body()?.data
            ?: throw PropertyLoadException("Unable to read the current user profile.")

        TokenManager.saveUserRole(user.role)

        if (!SessionManager.isRegisteredUserRole(user.role)) {
            throw PropertyRoleException("This mobile module is only for registered users.")
        }
    }

    private fun <T> extractList(
        response: Response<ApiResponse<List<T>>>,
        fallbackMessage: String
    ): List<T> {
        if (!response.isSuccessful) {
            if (response.code() == 401 || response.code() == 403) {
                throw PropertyLoginRequiredException("Session expired. Please log in again.")
            }
            throw PropertyLoadException(parseErrorMessage(response, fallbackMessage))
        }

        val data = response.body()?.data
            ?: throw PropertyLoadException("Server returned an empty response.")
        return data
    }

    private fun <T> extractItem(
        response: Response<ApiResponse<T>>,
        fallbackMessage: String
    ): T {
        if (!response.isSuccessful) {
            if (response.code() == 401 || response.code() == 403) {
                throw PropertyLoginRequiredException("Session expired. Please log in again.")
            }
            throw PropertyLoadException(parseErrorMessage(response, fallbackMessage))
        }

        return response.body()?.data ?: throw PropertyLoadException("Server returned an empty response.")
    }

    private fun <T> parseErrorMessage(
        response: Response<ApiResponse<T>>,
        fallbackMessage: String
    ): String {
        val bodyMessage = response.body()?.error?.message?.takeIf { it.isNotBlank() }
        return bodyMessage ?: response.message().takeIf { it.isNotBlank() } ?: fallbackMessage
    }
}
