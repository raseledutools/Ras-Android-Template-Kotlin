package com.tanimul.android_template_kotlin.features

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// গ্লোবাল কালার প্যালেট
val bgColor = Color(0xFFF8FAFC)
val cardColor = Color(0xFFFFFFFF)
val primaryColor = Color(0xFF15AABF)
val dangerColor = Color(0xFFEF4444)
val successColor = Color(0xFF10B981)
val textColor = Color(0xFF1E293B)
val mutedColor = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockerHeroApp(viewModel: BlockerHeroViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Session states
    var isUnlockedForSession by remember { mutableStateOf(false) }
    var isStrictBreakActive by remember { mutableStateOf(false) } // This will later come from ViewModel

    // ১. STRICT BREAK SCREEN (Back button blocked)
    if (isStrictBreakActive) {
        BackHandler(true) { /* Do nothing to prevent leaving */ }
        
        Surface(modifier = Modifier.fillMaxSize(), color = primaryColor) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = Color.White, modifier = Modifier.size(80.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Text("STRICT BREAK ACTIVE", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Time Remaining:", color = Color.White.copy(alpha = 0.8f), fontSize = 18.sp)
                Text("01:45:30", color = Color.White, fontSize = 56.sp, fontWeight = FontWeight.Bold) // Placeholder timer
                Spacer(modifier = Modifier.height(32.dp))
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Allowed Apps (Whitelist)", fontWeight = FontWeight.Bold, color = primaryColor)
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Text("• Calculator\n• Dictionary\n• Notes", color = textColor, lineHeight = 24.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                Text("You cannot leave this screen until the time is up. Focus on your break!", color = Color.White, textAlign = TextAlign.Center, fontSize = 14.sp)
            }
        }
        return // Don't show the rest of the app
    }

    // ২. APP ENTRY PASSWORD SCREEN (If locked)
    if (uiState.isRemotelyLocked && !isUnlockedForSession) {
        BackHandler(true) { /* Prevent skipping password */ }
        var entryPass by remember { mutableStateOf("") }
        var passError by remember { mutableStateOf(false) }

        Surface(modifier = Modifier.fillMaxSize(), color = bgColor) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = primaryColor, modifier = Modifier.size(80.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Text("App Locked", color = textColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = entryPass,
                    onValueChange = { entryPass = it; passError = false },
                    visualTransformation = PasswordVisualTransformation(),
                    label = { Text("Enter Password") },
                    isError = passError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (passError) {
                    Text("Incorrect password", color = dangerColor, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { 
                        // Dummy check for now, ViewModel will handle real logic
                        if (entryPass == "1234") { isUnlockedForSession = true } else { passError = true }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text("UNLOCK", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    // ৩. MAIN DASHBOARD
    val scrollState = rememberScrollState()
    
    // Dialog states
    var showBreakSetupDialog by remember { mutableStateOf(false) }
    var breakHours by remember { mutableStateOf("") }
    var breakMinutes by remember { mutableStateOf("") }
    
    var showUninstallDaysDialog by remember { mutableStateOf(false) }
    var uninstallDays by remember { mutableStateOf("") }
    
    var showPasswordDialog by remember { mutableStateOf(false) }
    var lockPassword by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Rasfocus Pro", fontWeight = FontWeight.ExtraBold, color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = primaryColor),
                navigationIcon = {
                    IconButton(onClick = { /* Menu */ }) { Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White) }
                },
                actions = {
                    IconButton(onClick = { /* Settings */ }) { Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White) }
                }
            )
        },
        containerColor = bgColor
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(scrollState).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // TAKE A BREAK BUTTON
            Button(
                onClick = { showBreakSetupDialog = true },
                modifier = Modifier.fillMaxWidth().height(65.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Icon(Icons.Default.Coffee, contentDescription = "Break", modifier = Modifier.size(28.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(12.dp))
                Text("TAKE A BREAK", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            }

            // MASTER LOCK
            Card(
                colors = CardDefaults.cardColors(containerColor = cardColor),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("🔒 Master Lock", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { 
                            if (uiState.isRemotelyLocked) {
                                // Add unlock logic here
                            } else {
                                showPasswordDialog = true 
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (uiState.isRemotelyLocked) dangerColor else primaryColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (uiState.isRemotelyLocked) "APP IS LOCKED" else "SET PASSWORD & LOCK", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // FEATURES & SECURITY
            Card(
                colors = CardDefaults.cardColors(containerColor = cardColor),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("⚙️ Features & Security", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    SettingSwitch("Block YouTube Shorts", uiState.blockYoutubeShorts, { viewModel.toggleYoutubeShorts(it) }, primaryColor)
                    Divider(color = Color(0xFFF1F5F9))
                    SettingSwitch("Block Facebook Reels", uiState.blockFacebookReels, { viewModel.toggleFacebookReels(it) }, primaryColor)
                    Divider(color = Color(0xFFF1F5F9))
                    SettingSwitch("Uninstall Protection", uiState.uninstallProtection, { isChecked -> 
                        if(isChecked) { showUninstallDaysDialog = true } else { viewModel.toggleUninstallProtection(false) }
                    }, primaryColor)
                }
            }

            // BLOCK / ALLOW LIST SECTION
            var listMode by remember { mutableStateOf("BLOCK") }
            var listInputText by remember { mutableStateOf("") }
            
            Card(
                colors = CardDefaults.cardColors(containerColor = cardColor),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("📜 Block & Allow List", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        FilterChip(
                            selected = listMode == "BLOCK", onClick = { listMode = "BLOCK" },
                            label = { Text("Block List") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = dangerColor.copy(alpha = 0.1f), selectedLabelColor = dangerColor)
                        )
                        FilterChip(
                            selected = listMode == "ALLOW", onClick = { listMode = "ALLOW" },
                            label = { Text("Allow List") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = successColor.copy(alpha = 0.1f), selectedLabelColor = successColor)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = listInputText,
                        onValueChange = { listInputText = it },
                        placeholder = { Text(if(listMode == "BLOCK") "Type app or website to block" else "Type app to allow") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { /* Save to ViewModel list */ listInputText = "" }) {
                                Icon(Icons.Default.Add, contentDescription = "Add", tint = if(listMode=="BLOCK") dangerColor else successColor)
                            }
                        }
                    )
                    
                    // Dummy list representation
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(if(listMode == "BLOCK") "Current Blocked: tiktok.com, snapchat" else "Current Allowed: calculator, notes", color = mutedColor, fontSize = 12.sp)
                }
            }
        }

        // --- DIALOGS ---

        if (showBreakSetupDialog) {
            AlertDialog(
                onDismissRequest = { showBreakSetupDialog = false },
                title = { Text("Setup Strict Break", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Enter duration. Only apps in your Allow List will work.")
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = breakHours, onValueChange = { breakHours = it }, label = { Text("Hours") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                            OutlinedTextField(value = breakMinutes, onValueChange = { breakMinutes = it }, label = { Text("Mins") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showBreakSetupDialog = false; isStrictBreakActive = true }, colors = ButtonDefaults.buttonColors(containerColor = dangerColor)) { Text("START BREAK") }
                },
                dismissButton = { TextButton(onClick = { showBreakSetupDialog = false }) { Text("Cancel") } }
            )
        }

        if (showUninstallDaysDialog) {
            AlertDialog(
                onDismissRequest = { showUninstallDaysDialog = false },
                title = { Text("Uninstall Protection", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("How many days do you want to protect the app?")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = uninstallDays, onValueChange = { uninstallDays = it }, label = { Text("Days") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Note: This will ask for Device Admin permission.", color = dangerColor, fontSize = 12.sp)
                    }
                },
                confirmButton = {
                    Button(onClick = { 
                        showUninstallDaysDialog = false
                        viewModel.toggleUninstallProtection(true)
                        // Trigger Device Admin Intent logic here later
                    }) { Text("ACTIVATE") }
                },
                dismissButton = { TextButton(onClick = { showUninstallDaysDialog = false }) { Text("Cancel") } }
            )
        }

        if (showPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showPasswordDialog = false },
                title = { Text("Set Master Password", fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(value = lockPassword, onValueChange = { lockPassword = it }, visualTransformation = PasswordVisualTransformation(), label = { Text("Password") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                },
                confirmButton = {
                    Button(onClick = { showPasswordDialog = false /* ViewModel lock logic */ }) { Text("LOCK DEVICE") }
                },
                dismissButton = { TextButton(onClick = { showPasswordDialog = false }) { Text("Cancel") } }
            )
        }
    }
}

@Composable
fun SettingSwitch(title: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit, primaryColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 16.sp, color = Color(0xFF334155), modifier = Modifier.weight(1f))
        Switch(checked = isChecked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedTrackColor = primaryColor))
    }
}
