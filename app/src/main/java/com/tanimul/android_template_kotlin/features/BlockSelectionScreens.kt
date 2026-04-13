package com.tanimul.android_template_kotlin.features

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectionScreen(viewModel: BlockerHeroViewModel, onBackClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    val appList = listOf("AHA Games", "AI Gallery", "BlockerHero", "Calculator", "Facebook", "YouTube")

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF3F6FA))) {
        TopAppBar(
            title = { Text("All Apps", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
            navigationIcon = {
                IconButton(onClick = onBackClick, modifier = Modifier.background(Color.White, CircleShape)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search Apps") },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        )

        Card(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 10.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            LazyColumn {
                items(appList.size) { index ->
                    val appName = appList[index]
                    val isLocked = uiState.blockList.contains(appName.lowercase())

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(appName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { 
                            if (isLocked) viewModel.removeFromList("BLOCK", appName) 
                            else viewModel.addToList(appName) 
                        }) {
                            Icon(
                                imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                tint = if (isLocked) Color(0xFF1E3A8A) else Color.Gray,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteSelectionScreen(viewModel: BlockerHeroViewModel, onBackClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    var siteUrl by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF3F6FA))) {
        TopAppBar(
            title = { Text("All Sites", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
            navigationIcon = {
                IconButton(onClick = onBackClick, modifier = Modifier.background(Color.White, CircleShape)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = siteUrl,
                onValueChange = { siteUrl = it },
                placeholder = { Text("Enter Website URL") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { 
                if (siteUrl.isNotEmpty()) {
                    viewModel.addToList(siteUrl)
                    siteUrl = ""
                }
            }) {
                Text("Add")
            }
        }

        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(uiState.blockList.size) { index ->
                val site = uiState.blockList[index]
                if (site.contains(".")) { 
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(site)
                            Icon(Icons.Default.Delete, modifier = Modifier.clickable { viewModel.removeFromList("BLOCK", site) }, tint = Color.Red, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}
