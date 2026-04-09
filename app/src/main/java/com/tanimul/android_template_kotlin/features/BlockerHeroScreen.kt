package com.tanimul.android_template_kotlin.features

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockerHeroApp(viewModel: BlockerHeroViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // কালার প্যালেট (HTML ড্যাশবোর্ডের মতো প্রফেশনাল সাদা থিম)
    val bgColor = Color(0xFFF8FAFC)
    val cardColor = Color(0xFFFFFFFF)
    val primaryColor = Color(0xFF15AABF)
    val dangerColor = Color(0xFFEF4444)
    val successColor = Color(0xFF10B981)
    val textColor = Color(0xFF1E293B)
    val mutedColor = Color(0xFF64748B)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("👁️ Rasfocus Pro", fontWeight = FontWeight.Bold, color = textColor) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = cardColor,
                    titleContentColor = textColor
                )
            )
        },
        containerColor = bgColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // 1. DEVICE STATUS CARD
            Card(
                colors = CardDefaults.cardColors(containerColor = cardColor),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Device Status", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (uiState.isRemotelyLocked) {
                        Text("🔒 LOCKED BY ADMIN", color = dangerColor, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Text("Your device is strictly locked for focus.", color = mutedColor, fontSize = 14.sp)
                    } else {
                        Text("🔓 UNLOCKED", color = successColor, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Text("Your device is free to use.", color = mutedColor, fontSize = 14.sp)
                    }
                }
            }

            // 2. MAGICX FEATURES CARD (Adult Block Hidden)
            Card(
                colors = CardDefaults.cardColors(containerColor = cardColor),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("⚙️ MagicX Features", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
                    Text("Block distractive content instantly.", color = mutedColor, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    SettingSwitch(
                        title = "Block YouTube Shorts",
                        isChecked = uiState.blockYoutubeShorts,
                        onCheckedChange = { viewModel.toggleYoutubeShorts(it) },
                        primaryColor = primaryColor
                    )
                    Divider(color = Color(0xFFF1F5F9))
                    SettingSwitch(
                        title = "Block Facebook Reels",
                        isChecked = uiState.blockFacebookReels,
                        onCheckedChange = { viewModel.toggleFacebookReels(it) },
                        primaryColor = primaryColor
                    )
                }
            }

            // 3. STRICT SECURITY CARD
            Card(
                colors = CardDefaults.cardColors(containerColor = cardColor),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("🛡️ Strict Security", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
                    Text("Prevent bypassing focus mode.", color = mutedColor, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    SettingSwitch(
                        title = "App Uninstall Protection",
                        isChecked = uiState.uninstallProtection,
                        onCheckedChange = { viewModel.toggleUninstallProtection(it) },
                        primaryColor = primaryColor
                    )
                    Divider(color = Color(0xFFF1F5F9))
                    SettingSwitch(
                        title = "Block Phone Reboot",
                        isChecked = uiState.blockPhoneReboot,
                        onCheckedChange = { viewModel.togglePhoneReboot(it) },
                        primaryColor = primaryColor
                    )
                    Divider(color = Color(0xFFF1F5F9))
                    SettingSwitch(
                        title = "Block Recent Apps Screen",
                        isChecked = uiState.blockRecentAppsScreen,
                        onCheckedChange = { viewModel.toggleRecentAppsScreen(it) },
                        primaryColor = primaryColor
                    )
                    Divider(color = Color(0xFFF1F5F9))
                    SettingSwitch(
                        title = "Block Installing New Apps",
                        isChecked = uiState.blockNewInstalledApps,
                        onCheckedChange = { viewModel.toggleNewInstalledApps(it) },
                        primaryColor = primaryColor
                    )
                }
            }

            // 4. LIVE CHAT CARD
            var chatMessage by remember { mutableStateOf("") }
            Card(
                colors = CardDefaults.cardColors(containerColor = cardColor),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("💬 Live Chat Support", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
                    
                    if (uiState.adminMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color(0xFFFFFBEB),
                            border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Admin: ${uiState.adminMessage}", modifier = Modifier.padding(12.dp), color = Color(0xFF92400E), fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = chatMessage,
                        onValueChange = { chatMessage = it },
                        placeholder = { Text("Message to admin...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        trailingIcon = {
                            IconButton(onClick = {
                                if (chatMessage.isNotEmpty()) {
                                    viewModel.sendLiveChatMessage(chatMessage)
                                    chatMessage = ""
                                }
                            }) {
                                Icon(Icons.Default.Send, contentDescription = "Send", tint = primaryColor)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun SettingSwitch(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    primaryColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color(0xFF334155),
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = primaryColor,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCBD5E1),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}
