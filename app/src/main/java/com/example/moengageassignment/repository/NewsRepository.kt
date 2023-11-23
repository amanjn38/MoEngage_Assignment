package com.example.moengageassignment.repository

import com.example.moengageassignment.api.NewsAPIService
import com.example.moengageassignment.models.NewsApiResponse
import com.example.moengageassignment.utilities.Resource
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NewsRepository {
    private val apiService: NewsAPIService = Retrofit.Builder()
        .baseUrl("https://candidate-test-data-moengage.s3.amazonaws.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NewsAPIService::class.java)

    suspend fun getNews(): Resource<NewsApiResponse> {
        return try {
            val response = apiService.getNews()
            if (response.isSuccessful) {
                Resource.Success(response.body())
            } else {
                Resource.Error("Error: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message}")
        }
    }
}

