package com.example.news

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.news.ui.theme.NewsTheme

class MainActivity : ComponentActivity() {
    private val newsViewModel: NewsViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkMode by rememberSaveable { mutableStateOf(false) }
            NewsTheme(darkTheme = isDarkMode) {

                // Create NavController
                val navController = rememberNavController()

                // NavHost defines navigation graph
                NavHost(
                    navController = navController,
                    startDestination = "news"
                ) {
                    // News list screen
                    composable("news") {
                        NewsScreen(
                            viewModel = newsViewModel,
                            isDarkMode = isDarkMode,
                            onToggleTheme = { isDarkMode = !isDarkMode },
                            navController = navController
                        )
                    }

                    // Article detail screen
                    composable("article") {
                        // Retrieve article from savedStateHandle
                        val article =
                            navController.previousBackStackEntry?.savedStateHandle?.get<Article>("article")
                        if (article != null) {
                            ArticleScreen(
                                article = article,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
