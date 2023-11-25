package com.example.moengageassignment.roomhelper

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moengageassignment.models.Article

@Dao
interface NewsArticleDao {
    @Query("SELECT * FROM news_articles")
    suspend fun getAllArticles(): List<Article>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<Article>)
}
