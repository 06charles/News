package com.example.news

import android.util.Log
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

// This class file is responsible for fetching news from the API
class NewsRepository {

    private val apiKey = "pub_eba1001b999449e5bf2e0570ac9250ae" // API key
    private val baseUrl = "https://newsdata.io/api/1/news"      // Base API URL
    private val defaultLanguage = "en"  // Default language
    // Accepts optional type and language, defaulting to "technology" and defaultLanguage
    suspend fun getNews(
        type: String? = null,
        language: String = defaultLanguage,
        country: String? = null
    ): NewsResponse {
        return try {
            Log.d("NewsRepository", "Fetching News: type=$type, language=$language, country=$country")

            val response: NewsResponse = NetworkModule.client.get(baseUrl) {
                url(baseUrl)
                parameter("apikey", apiKey)        // API key will be integrated
                parameter("language", language)    // Language will be integrated
                if (!type.isNullOrBlank()) {       // Add "q" only if user selected a category
                    parameter("q", type)
                }                                 // Type of news
                country?.let { parameter("country", it) } // Country will be integrated
                contentType(ContentType.Application.Json)  // Specifies that we want JSON in response
            }.body() // Converts JSON response into data class for NewsRepository

            // Log API response details
            Log.d("NewsRepository", "API status=${response.status}")
            response
        } catch (exception: Exception) { // `exception` is the Exception object
            Log.e("NewsRepository", "Error fetching news", exception)
            throw Exception("Failed to fetch news", exception)
        }
    }
}