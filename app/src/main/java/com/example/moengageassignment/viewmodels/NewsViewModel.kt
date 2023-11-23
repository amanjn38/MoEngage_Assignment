package com.example.moengageassignment.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moengageassignment.models.NewsApiResponse
import com.example.moengageassignment.repository.NewsRepository
import com.example.moengageassignment.utilities.Resource
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    private val repository = NewsRepository()
    private val _newsLiveData = MutableLiveData<Resource<NewsApiResponse>>()
    val newsLiveData: LiveData<Resource<NewsApiResponse>> get() = _newsLiveData

    fun getNews() {
        viewModelScope.launch {
            _newsLiveData.value = Resource.Loading(null)
            _newsLiveData.value = repository.getNews()
        }
    }
}
