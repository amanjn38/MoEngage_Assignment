package com.example.moengageassignment.api

import com.example.moengageassignment.models.NewsApiResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface NewsAPIService {
    @GET("Android/news-api-feed/staticResponse.json")
    suspend fun getNews(): Response<NewsApiResponse>
}
