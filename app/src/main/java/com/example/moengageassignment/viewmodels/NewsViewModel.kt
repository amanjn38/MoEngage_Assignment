package com.example.moengageassignment.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moengageassignment.models.Article
import com.example.moengageassignment.repository.NewsRepository
import com.example.moengageassignment.utilities.Resource
import com.example.moengageassignment.utilities.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {
    private val _news = MutableLiveData<Resource<List<Article>>>()
    val news: LiveData<Resource<List<Article>>> get() = _news
    private var currentSortOrder = SortOrder.NEW_TO_OLD // Default sorting order

    private val _filteredNews = MutableLiveData<List<Article>?>(null)
    val filteredNews: LiveData<List<Article>?> get() = _filteredNews
    private val _searchLoading = MutableLiveData<Boolean>()
    val searchLoading: LiveData<Boolean> get() = _searchLoading
    private var isNetworkAvailable: Boolean = false

    init {
        getNews(true)
    }

    fun getNews(isInternetAvailable: Boolean) {

        isNetworkAvailable = isInternetAvailable
        viewModelScope.launch {
            _news.value = Resource.Loading()

            try {
                if (isInternetAvailable) {
                    getNewsFromInternet()
                } else {
                    getNewsFromDatabase()
                }
            } catch (e: Exception) {
                _news.value = Resource.Error("An error occurred", null)
            }
        }
    }

    private fun getNewsFromInternet() {
        viewModelScope.launch {
            _news.value = Resource.Loading()
            try {
                val newsList = repository.fetchNewsFromInternet()
                _news.value = Resource.Success(newsList)
            } catch (e: Exception) {
                _news.value = Resource.Error("An error occurred", null)
            }
        }
    }

    // Fetch data from the database
    private fun getNewsFromDatabase() {
        viewModelScope.launch {
            _news.value = Resource.Loading()
            try {
                val cachedNews = repository.getCachedNews()
                if (cachedNews.isNotEmpty()) {
                    _news.value = Resource.Success(cachedNews)
                } else {
                    _news.value = Resource.Error(
                        "No internet connection, and no cached data available",
                        null
                    )
                }
            } catch (e: Exception) {
                _news.value = Resource.Error("An error occurred", null)
            }
        }
    }

    // Sort news list based on the current sorting order
    private fun sortNewsList(newsList: List<Article>) {

        val comparator: Comparator<Article> = when (currentSortOrder) {
            SortOrder.OLD_TO_NEW -> compareBy { parseIso8601Date(it.publishedAt) }
            SortOrder.NEW_TO_OLD -> compareByDescending { parseIso8601Date(it.publishedAt) }
        }

        viewModelScope.launch {
            val sortedList = newsList.sortedWith(comparator)
            _news.value = Resource.Success(sortedList)
        }
    }

    // Change sorting order and refresh news
    fun setSortOrder(order: SortOrder) {
        _searchLoading.value = true

        currentSortOrder = order
        _news.value!!.data?.let { sortNewsList(it) } ?: getNews(isNetworkAvailable)
    }

    // Search for news based on the query
    fun searchNews(query: String?) {
        viewModelScope.launch {
            try {
                _searchLoading.value = true

                val newsList = if (isNetworkAvailable) {
                    repository.fetchNewsFromInternet()
                } else {
                    repository.getCachedNews()
                }

                val filteredList = if (!query.isNullOrBlank()) {
                    newsList.filter { article ->
                        article.title?.contains(query, ignoreCase = true) == true ||
                                article.description?.contains(query, ignoreCase = true) == true
                    }
                } else {
                    newsList
                }
                _filteredNews.value = filteredList
                _news.value = Resource.Success(filteredList)
            } catch (e: Exception) {
                _news.value = Resource.Error("An error occurred", null)
            } finally {
                _searchLoading.value = false
            }
        }
    }


    private fun parseIso8601Date(iso8601Date: String?): Date {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        return iso8601Date?.let { formatter.parse(it) } ?: Date(0)
    }
}
