package com.tanimul.android_template_kotlin.features

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
                Text(text = "Quick Actions", color = TextColorLight, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))

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

                Spacer(modifier = Modifier.height(10.dp))

                QuickActionToggleCard(
                    icon = Icons.Default.Spellcheck,
                    title = "Keywords Blocked",
                    isToggled = uiState.blockKeywords,
                    onToggle = { viewModel.toggleKeywords(it) },
                    onConfigure = { }
                )

                Spacer(modifier = Modifier.height(10.dp))

                QuickActionToggleCard(
                    icon = Icons.Default.Warning,
                    title = "Block Adult Content",
                    isToggled = uiState.blockAdultContent, 
                    onToggle = { viewModel.toggleAdultContent(it) },
                    onConfigure = { }
                )

                Spacer(modifier = Modifier.height(10.dp))

                QuickActionToggleCard(
                    icon = Icons.Default.SmartDisplay,
                    title = "Block Reels/Shorts",
                    isToggled = uiState.blockYoutubeShorts, 
                    onToggle = { 
                        viewModel.toggleYoutubeShorts(it)
                        viewModel.toggleFacebookReels(it)
                    },
                    onConfigure = { }
                )
                
                Spacer(modifier = Modifier.height(30.dp))
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
fun QuickActionToggleCard(icon: ImageVector, title: String, isToggled: Boolean, onToggle: (Boolean) -> Unit, onConfigure: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(45.dp).background(SecondaryColor, CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = PrimaryColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = if(isToggled) "On" else "Off", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextColorDark)
                Text(text = title, fontSize = 14.sp, color = TextColorDark)
                Spacer(modifier = Modifier.height(4.dp))
                TextButton(onClick = onConfigure, contentPadding = PaddingValues(0.dp), modifier = Modifier.height(24.dp)) {
                    Text("Configure ->", color = PrimaryColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
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
