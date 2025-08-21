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
    private val defaultCountry: String? = null // News from Country if news wanted from specific country change here

    // Accepts optional type and language, defaulting to "technology" and defaultLanguage
    suspend fun getNews(
        type: String = "technology",
        language: String = defaultLanguage
    ): NewsResponse {
        return try {
            Log.d("NewsRepository", "Fetching News: type=$type, language=$language")

            val response: NewsResponse = NetworkModule.client.get(baseUrl) {
                url(baseUrl)
                parameter("apikey", apiKey)        // API key will be integrated
                parameter("language", language)    // Language will be integrated
                parameter("q", type)               // Type of news
                defaultCountry?.let { parameter("country", it) }  // Country will be integrated
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