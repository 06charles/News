package com.example.news

import android.util.Log
import kotlinx.serialization.Serializable
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Serializable       // Information from data class needs to be transferred so serialization is used
//Serializable turns data class into memory that can be transferred
// Represents the full API response
data class NewsResponse(
    val status: String,           // API status ("Success/Fail")
    val totalResults: Int,        // Total number of results
    val results: List<Article>    // List of news articles
){
    init {
        Log.d("NewsResponse", "NewsResponse Created")
    }
}

@Serializable
@Parcelize
// Represents a single news article
data class Article(
    val title: String? = null,        // News title
    val link: String? = null,         // Link to full article
    val description: String? = null,  // Short description
    val image_url: String? = null,    // Image URL
    val pubDate: String? = null       // Published date
) : Parcelable {
    init {
        Log.d("Article", "Article Created")
    }
}