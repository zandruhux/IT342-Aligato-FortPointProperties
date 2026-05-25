package com.example.fortpointproperties.features.careerApplication.data.repository

import com.example.fortpointproperties.features.auth.network.AuthApi
import com.example.fortpointproperties.features.careerApplication.data.model.CareerApplicationDto
import com.example.fortpointproperties.features.careerApplication.network.CareerApplicationApi
import com.example.fortpointproperties.shared.auth.SessionManager
import com.example.fortpointproperties.shared.auth.TokenManager
import com.example.fortpointproperties.shared.network.ApiClient
import com.example.fortpointproperties.shared.network.ApiResponse
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class CareerApplicationRepository(
    private val careerApplicationApi: CareerApplicationApi = ApiClient.retrofit.create(CareerApplicationApi::class.java),
    private val authApi: AuthApi = ApiClient.retrofit.create(AuthApi::class.java),
) {

    suspend fun getMyCareerApplication(): CareerApplicationDto? {
        requireRegisteredUserAccess()
        return extractNullableItem(
            careerApplicationApi.getMyCareerApplication(),
            "Failed to load career application"
        )
    }

    suspend fun submitCareerApplication(
        phoneNumber: String,
        coverLetter: String,
        resumePart: MultipartBody.Part
    ): CareerApplicationDto {
        requireRegisteredUserAccess()
        return extractItem(
            careerApplicationApi.submitCareerApplication(
                phoneNumber = phoneNumber.toTextRequestBody(),
                resume = resumePart,
                coverLetter = coverLetter.toTextRequestBody()
            ),
            "Failed to submit career application"
        )
    }

    private suspend fun requireRegisteredUserAccess() {
        if (TokenManager.getAccessToken().isNullOrBlank()) {
            throw CareerApplicationLoginRequiredException("Login required to access career applications.")
        }

        val storedRole = SessionManager.getStoredRole()
        when {
            SessionManager.isRegisteredUserRole(storedRole) -> return
            SessionManager.isPrivilegedRole(storedRole) -> {
                throw CareerApplicationRoleException("This mobile module is only for registered users.")
            }
        }

        val profileResponse = authApi.getProfile()
        if (!profileResponse.isSuccessful) {
            if (profileResponse.code() == 401 || profileResponse.code() == 403) {
                throw CareerApplicationLoginRequiredException("Session expired. Please log in again.")
            }
            throw CareerApplicationLoadException(parseErrorMessage(profileResponse, "Unable to verify user access."))
        }

        val user = profileResponse.body()?.data
            ?: throw CareerApplicationLoadException("Unable to read the current user profile.")

        TokenManager.saveUserRole(user.role)

        if (!SessionManager.isRegisteredUserRole(user.role)) {
            throw CareerApplicationRoleException("This mobile module is only for registered users.")
        }
    }

    private fun <T> extractNullableItem(
        response: Response<ApiResponse<T>>,
        fallbackMessage: String
    ): T? {
        if (!response.isSuccessful) {
            if (response.code() == 401 || response.code() == 403) {
                throw CareerApplicationLoginRequiredException("Session expired. Please log in again.")
            }
            throw CareerApplicationLoadException(parseErrorMessage(response, fallbackMessage))
        }

        val body = response.body() ?: throw CareerApplicationLoadException("Server returned an empty response.")
        return body.data
    }

    private fun <T> extractItem(
        response: Response<ApiResponse<T>>,
        fallbackMessage: String
    ): T {
        if (!response.isSuccessful) {
            if (response.code() == 401 || response.code() == 403) {
                throw CareerApplicationLoginRequiredException("Session expired. Please log in again.")
            }
            throw CareerApplicationSubmitException(parseErrorMessage(response, fallbackMessage))
        }

        val body = response.body() ?: throw CareerApplicationSubmitException("Server returned an empty response.")
        return body.data ?: throw CareerApplicationSubmitException("Server returned an empty response.")
    }

    private fun <T> parseErrorMessage(
        response: Response<ApiResponse<T>>,
        fallbackMessage: String
    ): String {
        val bodyMessage = response.body()?.error?.message?.takeIf { it.isNotBlank() }
        return bodyMessage ?: response.message().takeIf { it.isNotBlank() } ?: fallbackMessage
    }

    private fun String.toTextRequestBody() =
        this.toRequestBody("text/plain; charset=utf-8".toMediaTypeOrNull())
}
