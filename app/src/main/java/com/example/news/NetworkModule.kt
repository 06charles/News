package com.example.news

import io.ktor.client.*
import io.ktor.client.engine.okhttp.* // OkHttp engine for Android
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


object NetworkModule {  // Singleton object that holds our Ktor HttpClient

    val client = HttpClient(OkHttp) {   // Create a single HttpClient instance for the whole app

        install(ContentNegotiation) {   // Install plugin to automatically convert JSON to Kotlin objects
            json(Json {
                ignoreUnknownKeys = true    // Ignore fields we don't have in our data classes
                prettyPrint = true          // Makes logs easier to read
                isLenient = true            // Allows more flexible parsing
            })
        }

        install(Logging) {      // Install plugin to log API requests & responses to Logcat
            level = LogLevel.BODY       // Logs request and response bodies
        }
    }
}