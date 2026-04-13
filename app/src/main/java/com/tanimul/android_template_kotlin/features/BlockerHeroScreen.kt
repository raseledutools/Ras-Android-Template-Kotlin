package com.tanimul.android_template_kotlin.features

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

// =============================================================================
// RASFOCUS HOME SCREEN (Pro Unique Design with Actions)
// =============================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RasfocusHomeScreen(
    onNavigateToBlockedApps: () -> Unit,
    onNavigateToStats: () -> Unit
) {
    var showStartFocusDialog by remember { mutableStateOf(false) }
    var focusDuration by remember { mutableStateOf("25") }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Custom Welcome Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Welcome to Rasfocus",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Stay disciplined, stay focused.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    // Profile/Settings Icon
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable { /* Profile Action */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Person, contentDescription = "Profile", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }

            // 2. Unique Focus Timer Card (Gradient Look)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clickable { showStartFocusDialog = true },
                    shape = RoundedCornerShape(32.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Rounded.PlayArrow,
                                contentDescription = "Start",
                                modifier = Modifier.size(80.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "TAP TO START FOCUS",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // 3. Quick Actions Grid
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    RasfocusActionCard(icon = Icons.Rounded.Block, title = "Block List", onClick = onNavigateToBlockedApps, modifier = Modifier.weight(1f))
                    RasfocusActionCard(icon = Icons.Rounded.Analytics, title = "Statistics", onClick = onNavigateToStats, modifier = Modifier.weight(1f))
                    RasfocusActionCard(icon = Icons.Rounded.Security, title = "Strict Mode", onClick = { /* TODO */ }, modifier = Modifier.weight(1f))
                }
            }

            // 4. Daily Progress
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Today's Progress", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            ProgressItem("Apps Blocked", "124", Icons.Rounded.Shield)
                            ProgressItem("Focus Time", "4h 20m", Icons.Rounded.Timer)
                            ProgressItem("Streak", "7 Days", Icons.Rounded.LocalFireDepartment)
                        }
                    }
                }
            }
        }
    }

    // Interactive Dialog: Start Focus Mode
    if (showStartFocusDialog) {
        AlertDialog(
            onDismissRequest = { showStartFocusDialog = false },
            title = { Text("Start Focus Session", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("How many minutes do you want to stay focused?")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = focusDuration,
                        onValueChange = { focusDuration = it },
                        label = { Text("Minutes") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = { 
                    showStartFocusDialog = false 
                    // এখানে টাইমার শুরুর লজিক বসবে
                }) {
                    Text("Start Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartFocusDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun RasfocusActionCard(icon: ImageVector, title: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp).clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProgressItem(title: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
        Text(title, style = MaterialTheme.typography.bodySmall)
    }
}


// =============================================================================
// RASFOCUS BLOCKED APPS & WEBSITES SCREEN (Interactive & Premium)
// =============================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RasfocusBlockedScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    var showAddAppDialog by remember { mutableStateOf(false) }
    var urlToBlock by remember { mutableStateOf("") }
    val context = LocalContext.current

    // State for interactive switches
    var isAdultFilterEnabled by remember { mutableStateOf(true) }
    var isUninstallProtectionEnabled by remember { mutableStateOf(true) }
    var isAccessibilityGranted = true // Dummy state, use real check in production

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        
        // Critical Permission Warning (Interactive)
        if (!isAccessibilityGranted) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp).clickable { 
                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Warning, contentDescription = "Warning", tint = MaterialTheme.colorScheme.onErrorContainer)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Action Required", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                        Text("Tap to enable Accessibility to make Rasfocus work.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }
        }

        TabRow(selectedTabIndex = selectedTab, containerColor = MaterialTheme.colorScheme.surface) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Apps", fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Websites", fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Protection", fontWeight = FontWeight.Bold) })
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    item {
                        Button(
                            onClick = { showAddAppDialog = true },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Block New App")
                        }
                    }
                    // Interactive Dummy List
                    items(5) { index ->
                        var isBlocked by remember { mutableStateOf(true) }
                        BlockedListItem(
                            name = "Social Media App $index",
                            icon = Icons.Rounded.Apps,
                            isBlocked = isBlocked,
                            onToggle = { isBlocked = it }
                        )
                    }
                }
                1 -> {
                    item {
                        Button(
                            onClick = { showAddAppDialog = true },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Block New Website")
                        }
                    }
                    items(3) { index ->
                        var isBlocked by remember { mutableStateOf(true) }
                        BlockedListItem(
                            name = "www.distraction${index}.com",
                            icon = Icons.Rounded.Language,
                            isBlocked = isBlocked,
                            onToggle = { isBlocked = it }
                        )
                    }
                }
                2 -> {
                    item {
                        Text("Core Protections", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                        
                        // Interactive Toggle 1
                        ProtectionToggleCard(
                            title = "Strict Adult Content Blocker",
                            description = "Instantly blocks hardcore, medium, and slang keywords anywhere on the phone.",
                            isChecked = isAdultFilterEnabled,
                            onCheckedChange = { isAdultFilterEnabled = it }
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Interactive Toggle 2
                        ProtectionToggleCard(
                            title = "Uninstall Protection",
                            description = "Prevents you from deleting Rasfocus from phone settings when focus is active.",
                            isChecked = isUninstallProtectionEnabled,
                            onCheckedChange = { isUninstallProtectionEnabled = it }
                        )
                    }
                }
            }
        }
    }

    // Interactive Dialog: Add App or Website
    if (showAddAppDialog) {
        AlertDialog(
            onDismissRequest = { showAddAppDialog = false },
            title = { Text(if (selectedTab == 0) "Block App" else "Block Website", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = urlToBlock,
                    onValueChange = { urlToBlock = it },
                    label = { Text(if (selectedTab == 0) "Enter App Package Name" else "Enter URL (e.g., facebook.com)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = { 
                    showAddAppDialog = false
                    urlToBlock = ""
                    // এখানে লিস্টে অ্যাড করার লজিক বসবে
                }) {
                    Text("Add to Blocklist")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAppDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun BlockedListItem(name: String, icon: ImageVector, isBlocked: Boolean, onToggle: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Switch(checked = isBlocked, onCheckedChange = onToggle)
        }
    }
}

@Composable
fun ProtectionToggleCard(title: String, description: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = isChecked, onCheckedChange = onCheckedChange)
        }
    }
}
