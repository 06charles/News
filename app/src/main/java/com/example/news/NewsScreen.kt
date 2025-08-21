package com.example.news

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    viewModel: NewsViewModel,       // Call Viewmodel for news
    isDarkMode: Boolean,            // Current theme mode
    onToggleTheme: () -> Unit,      // Callback to toggle theme
    navController: NavController    // Added NavController parameter
) {
    val newsList by viewModel.newsList // Observing news list
    var selectedLanguage by rememberSaveable { mutableStateOf("en") }   // Language State (Default EN)
    var selectedType by rememberSaveable { mutableStateOf("technology") }   // Category State ( Default Technology)
    var selectedCountry by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedType, selectedLanguage, selectedCountry) {
        viewModel.fetchNews(selectedType, selectedLanguage, selectedCountry)
    }

    Scaffold(                           // Main Layout Structure
        topBar = {                      // Top App Bar
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "News",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
                        )
                    }
                }
            )
        },
        content = { innerPadding ->     // Screen content with padding
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                if (newsList.isEmpty()) {       // If news content empty shows CircularProgressIndicator
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center         // Circular prog indi aligned to center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(                 // List of articles in lazyColumn
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),  // Space between items
                        contentPadding = PaddingValues(bottom = 20.dp)      // Screen bottom padding
                    ) {
                        items(newsList) { article ->           // iterates over article
                            NewsItem(article) {                        // Show news Card
                                // Save clicked article in back stack
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "article",
                                    article
                                )
                                // Navigate to article screen
                                navController.navigate("article")
                            }
                        }
                    }
                }
                // Floating Action Button at bottom left
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    FABMenu(
                        isDarkMode = isDarkMode,
                        onToggleTheme = onToggleTheme,
                        selectedLanguage = selectedLanguage,
                        onSelectLanguage = { lang ->
                            selectedLanguage = lang
                            viewModel.fetchNews(selectedType, selectedLanguage, selectedCountry) // re-fetch (Reloads News)
                        },
                        selectedType = selectedType,
                        onSelectType = { type ->
                            selectedType = type
                            viewModel.fetchNews(selectedType, selectedLanguage) // re-fetch (Reloads News)
                        },
                        selectedCountry = selectedCountry,
                        onSelectCountry = { country ->
                            selectedCountry = country
                            viewModel.fetchNews(selectedType, selectedLanguage, selectedCountry)
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun NewsItem(article: Article, onClick: () -> Unit) {

    Card(       // News Card
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable { onClick() },       // Open article on click
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(       // Article Title
                text = article.title ?: "No Title",
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(       // Article Description
                text = article.description ?: "No Description",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,       // Max Three lines will be shown
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleScreen(      // Article Screen open from main screen when clicked on article ( Complete Article )
    article: Article,       // Article Called from data class Article
    onBackClick: () -> Unit
) {

    val context = LocalContext.current      // Android Content (Current)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Article") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {       // Bottom Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(     // Opens article link in Browser
                    onClick = {
                        article.link?.let { url ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open in Browser")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())      // Scrollable Article
        ) {
            Text(                                           // Article Content
                text = article.title ?: "No Title",         // Article fetched from data class
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row {
                Text(
                    text = "Published: ${article.pubDate ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                article.pubDateTZ?.let {
                    Text(
                        text = " Time Zone: ($it)",                                   // Timezone right after pubDate
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            article.image_url?.let { imageUrl ->                // Image URL fetched from data class
                AsyncImage(                                     //  Article Image ( if available )
                    model = imageUrl,                           // Image call
                    contentDescription = article.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 16.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(                                                           // Article Description
                text = article.description ?: "No description available.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column {
                Text(
                    text = "Source: ${article.source_id ?: "Unknown"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                article.source_url?.let {
                    Text(
                        text = "URL: $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            article.country?.let { countries ->
                val countryText = if (countries.isNotEmpty()) countries.joinToString(", "){ it.uppercase() }
                else{ "N/A" }
                Text(
                    text = "Country: $countryText",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

        }
    }
}

@Composable
fun FABMenu(                                    // Floating Action Bar Menu
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    selectedLanguage: String,
    onSelectLanguage: (String) -> Unit,
    selectedType: String,
    onSelectType: (String) -> Unit,
    selectedCountry: String?,
    onSelectCountry: (String?) -> Unit
) {
    var isMenuExpanded by rememberSaveable { mutableStateOf(false) }            // Track FAB open/close
    var isLanguageMenuVisible by remember { mutableStateOf(false) }             // Track Language menu
    var isTypeMenuVisible by remember { mutableStateOf(false) }                 // Track Category Menu
    var isCountryMenuVisible by remember { mutableStateOf(false) }              // Track Country Menu

    val languages = listOf("en", "es", "fr", "de", "hi")                                // List of Languages
    val types = listOf(null, "technology", "games", "sports", "business", "health")     // List of Category
    val countries = listOf(null, "us", "in", "gb", "de", "fr")                          // List of Countries

    Box(                                                                                // Box to anchor FAB
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.BottomStart                                        // Aligned Bottom Start
    ) {

        AnimatedVisibility(visible = isMenuExpanded) {                                  // Show menu when expanded
            Column(
                modifier = Modifier
                    .offset(y = (-60).dp)                                               // anchored above FAB
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Dark mode toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleTheme() }
                        .padding(8.dp)
                ) {
                    Text("Appearance", modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Toggle Theme"
                    )
                }

                // Country selector
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isCountryMenuVisible = !isCountryMenuVisible }
                            .padding(8.dp)
                    ) {
                        Text("Country", modifier = Modifier.weight(1f))
                        Text(selectedCountry?.uppercase() ?: "ALL", fontWeight = FontWeight.Bold)
                    }

                    AnimatedVisibility(isCountryMenuVisible) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(8.dp)
                        ) {
                            countries.forEach { country ->
                                val display = country?.uppercase() ?: "ALL"
                                Text(
                                    text = display,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onSelectCountry(country)
                                            isCountryMenuVisible = false
                                            isMenuExpanded = false
                                        }
                                        .padding(8.dp)
                                )
                            }
                        }
                    }
                }

                // Language selector
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isLanguageMenuVisible = !isLanguageMenuVisible }   // When clicked makes the Language menu visible and invisible
                            .padding(8.dp)
                    ) {
                        Text("Language", modifier = Modifier.weight(1f))
                        Text(selectedLanguage.uppercase(), fontWeight = FontWeight.Bold)
                    }

                    AnimatedVisibility(isLanguageMenuVisible) {     // Expand Language list when clicked
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(8.dp)
                        ) {
                            languages.forEach { lang ->
                                Text(
                                    text = lang.uppercase(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {                    // Conditions for clickable
                                            onSelectLanguage(lang)      // Select Language
                                            isLanguageMenuVisible = false
                                            isMenuExpanded = false
                                        }
                                        .padding(8.dp)
                                )
                            }
                        }
                    }
                }

                // Category selector
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isTypeMenuVisible = !isTypeMenuVisible }       // When clicked makes the Category menu visible and invisible
                            .padding(8.dp)
                    ) {
                        Text("Category", modifier = Modifier.weight(1f))
                        Text(if (selectedType.isBlank()) "ALL"
                            else selectedType.replaceFirstChar { it.uppercase() },
                            fontWeight = FontWeight.Bold
                        )
                    }

                    AnimatedVisibility(isTypeMenuVisible) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(8.dp)
                        ) {
                            types.forEach { type ->
                                val display = type?.replaceFirstChar { it.uppercase() } ?: "ALL"
                                Text(
                                    text = display,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onSelectType(type ?: "") // Pass empty string for "ALL"
                                            isTypeMenuVisible = false
                                            isMenuExpanded = false
                                        }
                                        .padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // FAB with Three lines/X icon
        FloatingActionButton(
            onClick = { isMenuExpanded = !isMenuExpanded },     // Expand menu on click
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            // Animations
            val rotation1 by animateFloatAsState (targetValue = if (isMenuExpanded) 45f else 0f, label = "")
            val rotation2 by animateFloatAsState (targetValue = if (isMenuExpanded) -45f else 0f, label = "")
            val offsetY1 by animateFloatAsState (targetValue = if (isMenuExpanded) 0f else -6f, label = "")
            val offsetY2 by animateFloatAsState (targetValue = if (isMenuExpanded) 0f else 6f, label = "")

            Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                // Top line
                Box(
                    modifier = Modifier
                        .offset(y = offsetY1.dp)
                        .width(20.dp)
                        .height(3.dp)
                        .rotate(rotation1)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.onSecondary)
                )
                // Bottom line
                Box(
                    modifier = Modifier
                        .offset(y = offsetY2.dp)
                        .width(20.dp)
                        .height(3.dp)
                        .rotate(rotation2)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.onSecondary)
                )
            }
        }
    }
}
