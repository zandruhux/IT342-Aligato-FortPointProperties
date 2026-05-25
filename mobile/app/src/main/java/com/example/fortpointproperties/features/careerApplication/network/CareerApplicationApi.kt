package com.example.fortpointproperties.features.careerApplication.network

import com.example.fortpointproperties.features.careerApplication.data.model.CareerApplicationDto
import com.example.fortpointproperties.shared.network.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CareerApplicationApi {

    @Multipart
    @POST("api/career-applications")
    suspend fun submitCareerApplication(
        @Part("phoneNumber") phoneNumber: RequestBody,
        @Part resume: MultipartBody.Part,
        @Part("coverLetter") coverLetter: RequestBody,
    ): Response<ApiResponse<CareerApplicationDto>>

    @GET("api/career-applications/me")
    suspend fun getMyCareerApplication(): Response<ApiResponse<CareerApplicationDto>>
}
