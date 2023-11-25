package com.example.moengageassignment.api

import android.util.Log
import com.example.moengageassignment.models.Article
import com.example.moengageassignment.models.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object ApiService {
    private const val TAG = "ApiService"

    suspend fun getNews(): String  = withContext(Dispatchers.IO) {
        val apiUrl = "https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json"

        try {
            val url = URL(apiUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            // Get the response
            val responseCode = connection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                return@withContext response.toString()
            } else {
                Log.e(TAG, "Error: HTTP response code $responseCode")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.javaClass.simpleName}", e)
        }

        return@withContext ""
    }

    // Parse the JSON response
    fun parseNewsResponse(jsonResponse: String): List<Article> {
        val newsList = mutableListOf<Article>()

        try {
            val jsonObject = JSONObject(jsonResponse)
            val status = jsonObject.getString("status")

            if (status == "ok") {
                val articlesArray = jsonObject.getJSONArray("articles")

                for (i in 0 until articlesArray.length()) {
                    val articleObject = articlesArray.getJSONObject(i)

                    val sourceObject = articleObject.getJSONObject("source")
                    val source = Source(
                        id = sourceObject.getString("id"),
                        name = sourceObject.getString("name")
                    )

                    val newsArticle = Article(
                        source = source,
                        author = articleObject.getString("author"),
                        title = articleObject.getString("title"),
                        description = articleObject.getString("description"),
                        url = articleObject.getString("url"),
                        urlToImage = articleObject.getString("urlToImage"),
                        publishedAt = articleObject.getString("publishedAt"),
                        content = articleObject.getString("content")
                    )

                    newsList.add(newsArticle)
                }
            } else {
                Log.e(TAG, "Error: API status is not 'ok'")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing JSON: ${e.message}")
        }

        return newsList
    }
}