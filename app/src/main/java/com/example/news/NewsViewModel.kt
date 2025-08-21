package com.example.news

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() { // ViewModel responsible for fetching news data from the repository and exposes to UI as state

    private val repo = NewsRepository() // Create an instance of the repository to handle API calls
    val newsList = mutableStateOf<List<Article>>(emptyList())
    // Holds the list of news articles as a state so the UI can observe changes
    // Starts with an empty list until the API call completes

    init {   // init block runs as soon as the ViewModel is created
        fetchNews("", "en") // Default initial fetch
    }

    // Function to fetch news dynamically based on type and language
    fun fetchNews(type: String, language: String, country: String? = null) {
        viewModelScope.launch { // Launch a coroutine in the ViewModel's scope to fetch data asynchronously
            try {
                Log.d("NewsViewModel", "Fetching news of type=$type, language=$language, country=$country from repository...")
                val news = repo.getNews(type, language, country) // Fetch the news from the repository with parameters
                newsList.value = news.results          // Update the state with the fetched results automatically trigger UI recomposition
                Log.d("NewsViewModel", "News fetched successfully: ${news.results.size} articles")
            } catch (e: Exception) {                  // Print error in case of API/network failure
                e.printStackTrace()
                Log.e("NewsViewModel","Error fetching news",e)
            }
        }
    }
}