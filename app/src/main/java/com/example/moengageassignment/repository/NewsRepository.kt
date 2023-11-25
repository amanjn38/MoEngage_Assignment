package com.example.moengageassignment.repository

import com.example.moengageassignment.api.ApiService
import com.example.moengageassignment.models.Article
import com.example.moengageassignment.models.toNewsArticle
import com.example.moengageassignment.roomhelper.NewsArticleDao
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val apiService: ApiService,
    private val newsDao: NewsArticleDao
) {

    suspend fun fetchNewsFromInternet(): List<Article> {
        // Fetch data from the internet using ApiService
        val jsonResponse = apiService.getNews()
        val articles = apiService.parseNewsResponse(jsonResponse)

        // Save fetched data to Room for future offline access
        saveNewsToDatabase(articles)

        return articles
    }

    suspend fun getCachedNews(): List<Article> {
        // Fetch data from Room database
        return newsDao.getAllArticles().map { it.toNewsArticle() }
    }

    private suspend fun saveNewsToDatabase(articles: List<Article>) {
        // Save data to Room database
        newsDao.insertArticles(articles)
    }
}
