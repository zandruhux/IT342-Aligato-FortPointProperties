package com.example.fortpointproperties.features.article.data.repository

import com.example.fortpointproperties.features.article.data.model.ArticleCardDto
import com.example.fortpointproperties.features.article.data.model.ArticleDetailDto
import com.example.fortpointproperties.features.article.network.ArticleApi
import com.example.fortpointproperties.features.auth.network.AuthApi
import com.example.fortpointproperties.shared.auth.SessionManager
import com.example.fortpointproperties.shared.auth.TokenManager
import com.example.fortpointproperties.shared.network.ApiClient
import com.example.fortpointproperties.shared.network.ApiResponse
import retrofit2.Response

class ArticleRepository(
    private val articleApi: ArticleApi = ApiClient.retrofit.create(ArticleApi::class.java),
    private val authApi: AuthApi = ApiClient.retrofit.create(AuthApi::class.java),
) {

    suspend fun getArticles(title: String? = null): List<ArticleCardDto> {
        requireRegisteredUserAccess()
        return extractList(
            articleApi.getArticles(title?.trim()?.takeIf { it.isNotBlank() }),
            "Failed to load blogs"
        )
    }

    suspend fun getArticleDetails(id: String): ArticleDetailDto {
        requireRegisteredUserAccess()
        return extractItem(articleApi.getArticleById(id), "Failed to load blog details")
    }

    private suspend fun requireRegisteredUserAccess() {
        if (TokenManager.getAccessToken().isNullOrBlank()) {
            throw ArticleLoginRequiredException("Login required to view blogs.")
        }

        val storedRole = SessionManager.getStoredRole()
        when {
            SessionManager.isRegisteredUserRole(storedRole) -> return
            SessionManager.isPrivilegedRole(storedRole) -> {
                throw ArticleRoleException("This mobile module is only for registered users.")
            }
        }

        val profileResponse = authApi.getProfile()
        if (!profileResponse.isSuccessful) {
            if (profileResponse.code() == 401 || profileResponse.code() == 403) {
                throw ArticleLoginRequiredException("Session expired. Please log in again.")
            }
            throw ArticleLoadException(parseErrorMessage(profileResponse, "Unable to verify user access."))
        }

        val user = profileResponse.body()?.data
            ?: throw ArticleLoadException("Unable to read the current user profile.")

        TokenManager.saveUserRole(user.role)

        if (!SessionManager.isRegisteredUserRole(user.role)) {
            throw ArticleRoleException("This mobile module is only for registered users.")
        }
    }

    private fun <T> extractList(
        response: Response<ApiResponse<List<T>>>,
        fallbackMessage: String,
    ): List<T> {
        if (!response.isSuccessful) {
            if (response.code() == 401 || response.code() == 403) {
                throw ArticleLoginRequiredException("Session expired. Please log in again.")
            }
            throw ArticleLoadException(parseErrorMessage(response, fallbackMessage))
        }

        return response.body()?.data ?: throw ArticleLoadException("Server returned an empty response.")
    }

    private fun <T> extractItem(
        response: Response<ApiResponse<T>>,
        fallbackMessage: String,
    ): T {
        if (!response.isSuccessful) {
            if (response.code() == 401 || response.code() == 403) {
                throw ArticleLoginRequiredException("Session expired. Please log in again.")
            }
            throw ArticleLoadException(parseErrorMessage(response, fallbackMessage))
        }

        return response.body()?.data ?: throw ArticleLoadException("Server returned an empty response.")
    }

    private fun <T> parseErrorMessage(
        response: Response<ApiResponse<T>>,
        fallbackMessage: String,
    ): String {
        val bodyMessage = response.body()?.error?.message?.takeIf { it.isNotBlank() }
        return bodyMessage ?: response.message().takeIf { it.isNotBlank() } ?: fallbackMessage
    }
}
