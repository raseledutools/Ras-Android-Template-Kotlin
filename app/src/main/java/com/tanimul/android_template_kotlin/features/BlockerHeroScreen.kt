package com.tanimul.android_template_kotlin.features

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// ================= কালার প্যালেট =================
val PrimaryColor = Color(0xFF1E3A8A) 
val SecondaryColor = Color(0xFFE0F2FE) 
val TextColorDark = Color(0xFF111827)
val TextColorLight = Color(0xFF6B7280)

// ================= হোম স্ক্রিন (মূল ড্যাশবোর্ড) =================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenView(viewModel: BlockerHeroViewModel, navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = { HomeBottomNavigationBar() },
        containerColor = Color(0xFFF3F6FA) 
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            HomeHeaderSection()
            Spacer(modifier = Modifier.height(20.dp))

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                
                // --- Take a Break Card ---
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { navController.navigate("take_a_break") },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD8B4E2))
                ) {
                    Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Coffee, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Take a Break", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                // --- Lists Block ---
                Text(text = "Block Lists", color = TextColorLight, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                
                QuickActionClickableCard(
                    icon = Icons.Default.Block,
                    title = uiState.blockList.size.toString(), 
                    subtitle = "Apps Blocked",
                    onClick = { navController.navigate("app_whitelist") }
                )
                Spacer(modifier = Modifier.height(10.dp))
                QuickActionClickableCard(
                    icon = Icons.Default.Language,
                    title = "Sites", 
                    subtitle = "Sites Blocked",
                    onClick = { navController.navigate("site_whitelist") }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // --- Content Blocking (From your Image & PC version) ---
                Text(text = "Content Blocking", color = Color(0xFFE53935), fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))

                QuickActionToggleCard(
                    icon = Icons.Default.Warning,
                    title = "Block Adult content",
                    isToggled = uiState.blockAdultContent, 
                    onToggle = { viewModel.toggleAdultContent(it) }
                )
                Spacer(modifier = Modifier.height(10.dp))

                QuickActionToggleCard(
                    icon = Icons.Default.Search,
                    title = "Filter Search results",
                    isToggled = uiState.filterSearchResults, 
                    onToggle = { viewModel.toggleFilterSearchResults(it) }
                )
                Spacer(modifier = Modifier.height(10.dp))

                QuickActionToggleCard(
                    icon = Icons.Default.ImageSearch,
                    title = "Block Image/Video search",
                    isToggled = uiState.blockImageVideoSearch, 
                    onToggle = { viewModel.toggleImageVideoSearch(it) }
                )
                Spacer(modifier = Modifier.height(10.dp))

                QuickActionToggleCard(
                    icon = Icons.Default.SmartDisplay,
                    title = "Block Instagram/Facebook reels",
                    isToggled = uiState.blockInstaFbReels, 
                    onToggle = { viewModel.toggleInstaFbReels(it) }
                )
                Spacer(modifier = Modifier.height(10.dp))

                QuickActionToggleCard(
                    icon = Icons.Default.FindInPage,
                    title = "Block Instagram search",
                    isToggled = uiState.blockInstaSearch, 
                    onToggle = { viewModel.toggleInstaSearch(it) }
                )
                Spacer(modifier = Modifier.height(10.dp))

                QuickActionToggleCard(
                    icon = Icons.Default.PlayCircleOutline,
                    title = "Block Youtube shorts",
                    isToggled = uiState.blockYoutubeShorts, 
                    onToggle = { viewModel.toggleYoutubeShorts(it) }
                )
                Spacer(modifier = Modifier.height(10.dp))

                QuickActionToggleCard(
                    icon = Icons.Default.Send,
                    title = "Block Telegram search",
                    isToggled = uiState.blockTelegramSearch, 
                    onToggle = { viewModel.toggleTelegramSearch(it) }
                )
                
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

// ================= অ্যাপ সিলেকশন স্ক্রিন (একই ফাইলে) =================
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
                                tint = if (isLocked) PrimaryColor else Color.Gray,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}

// ================= সাইট সিলেকশন স্ক্রিন (একই ফাইলে) =================
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
            }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)) {
                Text("Add", color = Color.White)
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

// ================= সাব-কম্পোনেন্ট ডিজাইন =================
@Composable
fun HomeHeaderSection() {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)).background(PrimaryColor).padding(top = 40.dp, bottom = 30.dp, start = 20.dp, end = 20.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { }, modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)) {
                    Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
                }
                IconButton(onClick = { }, modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Welcome", color = SecondaryColor, fontSize = 16.sp)
            Text(text = "Good Evening", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionClickableCard(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(45.dp).background(SecondaryColor, CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = PrimaryColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextColorDark)
                Text(text = subtitle, fontSize = 14.sp, color = TextColorLight)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun QuickActionToggleCard(icon: ImageVector, title: String, isToggled: Boolean, onToggle: (Boolean) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(45.dp).background(SecondaryColor, CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = PrimaryColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextColorDark)
            }
            Switch(checked = isToggled, onCheckedChange = onToggle, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryColor, uncheckedThumbColor = Color.White, uncheckedTrackColor = Color.LightGray))
        }
    }
}

@Composable
fun HomeBottomNavigationBar() {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Dashboard", "Modes", "Analytics", "Account")
    val icons = listOf(Icons.Default.Dashboard, Icons.Default.ElectricBolt, Icons.Default.Analytics, Icons.Default.Person)

    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item, fontSize = 10.sp, fontWeight = if (selectedItem == index) FontWeight.Bold else FontWeight.Normal) },
                selected = selectedItem == index,
                onClick = { selectedItem = index },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryColor, selectedTextColor = PrimaryColor, indicatorColor = SecondaryColor, unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)
            )
        }
    }
}
