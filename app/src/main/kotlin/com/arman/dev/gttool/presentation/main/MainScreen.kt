package com.arman.dev.gttool.presentation.main

import android.app.Application
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arman.dev.gttool.service.OverlayService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val viewModel = remember { MainViewModel(context.applicationContext as Application) }
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GT Tool") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(
                        onClick = {
                            val intent = Intent(context, OverlayService::class.java)
                            context.startService(intent)
                        }
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            "Test Overlay",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.Settings,
                            "Settings",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search games...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true
            )
            
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.games.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No games found")
                }
            } else {
                val filteredGames = if (searchQuery.isEmpty()) {
                    state.games
                } else {
                    state.games.filter {
                        it.name.contains(searchQuery, ignoreCase = true)
                    }
                }
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = filteredGames,
                        key = { it.packageName }
                    ) { game ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(0.85f)
                                .clickable {
                                    val launchIntent = context.packageManager
                                        .getLaunchIntentForPackage(game.packageName)
                                    launchIntent?.let { context.startActivity(it) }
                                },
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    null,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .align(Alignment.CenterHorizontally),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Column {
                                    Text(
                                        game.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 2
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "v${game.version}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}