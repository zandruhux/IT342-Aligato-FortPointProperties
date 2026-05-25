package com.example.fortpointproperties.features.article.network

import com.example.fortpointproperties.features.article.data.model.ArticleCardDto
import com.example.fortpointproperties.features.article.data.model.ArticleDetailDto
import com.example.fortpointproperties.shared.network.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ArticleApi {

    @GET("api/articles")
    suspend fun getArticles(
        @Query("title") title: String? = null,
    ): Response<ApiResponse<List<ArticleCardDto>>>

    @GET("api/articles/{id}")
    suspend fun getArticleById(@Path("id") id: String): Response<ApiResponse<ArticleDetailDto>>
}
