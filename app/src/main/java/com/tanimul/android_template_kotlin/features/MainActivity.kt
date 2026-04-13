// =====================================================================
// রিয়েল এবং প্রফেশনাল স্ক্রিন (RasFocus Pro Max)
// =====================================================================

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

val RasFocusTeal = Color(0xFF15AABF)

// ================= ১. হোম স্ক্রিন (Main Dashboard) =================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenView(viewModel: BlockerHeroViewModel, navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RasFocus Pro Max", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RasFocusTeal)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Active Status Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = if (uiState.uninstallProtection) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (uiState.uninstallProtection) Icons.Default.VerifiedUser else Icons.Default.GppBad,
                            contentDescription = "Status",
                            tint = if (uiState.uninstallProtection) Color(0xFF2E7D32) else Color(0xFFC62828),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Protection Status", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(
                                text = if (uiState.uninstallProtection) "Uninstall Protection ACTIVE" else "Vulnerable - Turn On Protection",
                                color = if (uiState.uninstallProtection) Color(0xFF2E7D32) else Color(0xFFC62828),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Quick Actions
            item { Text("Core Features", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp)) }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ActionCard(icon = Icons.Rounded.Apps, title = "Block Apps", modifier = Modifier.weight(1f)) { navController.navigate("app_whitelist") }
                    ActionCard(icon = Icons.Rounded.Language, title = "Block Sites", modifier = Modifier.weight(1f)) { navController.navigate("site_whitelist") }
                    ActionCard(icon = Icons.Rounded.Timer, title = "Take a Break", modifier = Modifier.weight(1f)) { navController.navigate("take_a_break") }
                }
            }

            // Toggles
            item { Text("Strict Filters", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp)) }
            item {
                ToggleItem("Hardcore Adult Filter", "Instantly blocks explicit sites & typing", uiState.blockAdultContent) { viewModel.toggleAdultContent(it) }
                ToggleItem("Facebook Reels Blocker", "Kills dopamine-scrolling on FB", uiState.blockFacebookReels) { viewModel.toggleFacebookReels(it) }
                ToggleItem("YouTube Shorts Blocker", "Stops you from wasting hours on YT", uiState.blockYoutubeShorts) { viewModel.toggleYoutubeShorts(it) }
            }
        }
    }
}

@Composable
fun ActionCard(icon: ImageVector, title: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(100.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = title, tint = RasFocusTeal, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF334155))
        }
    }
}

@Composable
fun ToggleItem(title: String, desc: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
                Text(desc, fontSize = 12.sp, color = Color(0xFF64748B))
            }
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = RasFocusTeal)
            )
        }
    }
}

// ================= ২. Take a Break স্ক্রিন =================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TakeABreakMainScreen(viewModel: BlockerHeroViewModel, navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()
    var hours by remember { mutableStateOf("0") }
    var minutes by remember { mutableStateOf("25") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Strict Break Mode") }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Rounded.Timer, contentDescription = "Timer", modifier = Modifier.size(80.dp), tint = RasFocusTeal)
            Spacer(modifier = Modifier.height(16.dp))
            
            if (uiState.isStrictBreakActive) {
                Text("Focus Session Active!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                Text(uiState.breakTimeRemaining, fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
                Text("Your phone is restricted until the timer runs out.", textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
            } else {
                Text("Set Focus Duration", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = hours, onValueChange = { hours = it },
                        label = { Text("Hours") }, modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = minutes, onValueChange = { minutes = it },
                        label = { Text("Minutes") }, modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { viewModel.startStrictBreak(hours.toIntOrNull() ?: 0, minutes.toIntOrNull() ?: 0) },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RasFocusTeal)
                ) {
                    Text("START FOCUS MODE", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ================= ৩. App Selection স্ক্রিন =================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectionScreen(viewModel: BlockerHeroViewModel, onBackClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    var appPackage by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Block Apps") }, navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = appPackage, onValueChange = { appPackage = it },
                label = { Text("Enter App Package Name (e.g., com.whatsapp)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { if(appPackage.isNotEmpty()) { viewModel.addToList(appPackage); appPackage = "" } },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = RasFocusTeal)
            ) { Text("Add to Blocklist") }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("Currently Blocked Apps:", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn {
                items(uiState.blockList.filter { it.contains(".") }) { item -> // Assuming package names have dots
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(item, fontWeight = FontWeight.Medium)
                            IconButton(onClick = { viewModel.removeFromList("BLOCK", item) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ================= ৪. Site Selection স্ক্রিন =================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteSelectionScreen(viewModel: BlockerHeroViewModel, onBackClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    var siteUrl by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Block Websites") }, navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = siteUrl, onValueChange = { siteUrl = it },
                label = { Text("Enter Website URL (e.g., facebook.com)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { if(siteUrl.isNotEmpty()) { viewModel.addToList(siteUrl); siteUrl = "" } },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = RasFocusTeal)
            ) { Text("Block Website") }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("Currently Blocked Websites:", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn {
                items(uiState.blockList.filter { !it.contains("com.") || it.contains("www") }) { item -> 
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(item, fontWeight = FontWeight.Medium)
                            IconButton(onClick = { viewModel.removeFromList("BLOCK", item) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}
