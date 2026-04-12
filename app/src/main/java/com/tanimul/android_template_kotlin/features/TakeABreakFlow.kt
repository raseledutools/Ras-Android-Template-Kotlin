package com.tanimul.android_template_kotlin.features
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// নেভিগেশন স্টেট
enum class BreakFlowState { Config, Whitelist, ActiveTimer }

@Composable
fun TakeABreakMainScreen() {
    var currentScreen by remember { mutableStateOf(BreakFlowState.Config) }

    when (currentScreen) {
        BreakFlowState.Config -> TakeABreakConfigScreen(
            onOpenWhitelist = { currentScreen = BreakFlowState.Whitelist },
            onStartBreak = { currentScreen = BreakFlowState.ActiveTimer }
        )
        BreakFlowState.Whitelist -> AppWhitelistScreen(
            onSave = { currentScreen = BreakFlowState.Config }
        )
        BreakFlowState.ActiveTimer -> ActiveBreakScreen()
    }
}

// ================= ১. ব্রেক কনফিগারেশন স্ক্রিন (Image 1) =================
@Composable
fun TakeABreakConfigScreen(onOpenWhitelist: () -> Unit, onStartBreak: () -> Unit) {
    var hidePauseBtn by remember { mutableStateOf(false) }
    var blockNotifications by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F6FA))
    ) {
        // হেডার (গ্রাডিয়েন্ট ব্লু)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0xFF6B4EE6), Color(0xFF00D2FF))))
                .padding(top = 40.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Coffee, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Take a Break", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = { /* ক্লোজ লজিক */ }, modifier = Modifier.background(Color.White.copy(alpha = 0.3f), CircleShape)) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                }
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            // ডামি টাইম পিকার (ডিজাইনের জন্য)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("0 day       0 hr       0 min", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Select your break duration", color = Color.Gray, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // টগল অপশনস
            ToggleCard(title = "Hide Pause Button", icon = Icons.Default.Pause, isChecked = hidePauseBtn) { hidePauseBtn = it }
            Spacer(modifier = Modifier.height(12.dp))
            ToggleCard(title = "Block Notifications", icon = Icons.Default.NotificationsOff, isChecked = blockNotifications) { blockNotifications = it }
            
            Spacer(modifier = Modifier.height(16.dp))

            // অ্যাপ হোয়াইটলিস্ট বাটন
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onOpenWhitelist() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("App Whitelist", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color.Red, modifier = Modifier.size(24.dp))
                    }
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // স্টার্ট ব্রেক বাটন
            Button(
                onClick = onStartBreak,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text("Start Break", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ToggleCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).background(Color(0xFFF3F6FA), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = Color.DarkGray)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            Switch(checked = isChecked, onCheckedChange = onCheckedChange)
        }
    }
}

// ================= ২. অ্যাপ হোয়াইটলিস্ট স্ক্রিন (Image 2) =================
@Composable
fun AppWhitelistScreen(onSave: () -> Unit) {
    val dummyApps = listOf("BlockerHero", "AHA Games", "AI Gallery", "Calculator", "Calendar")
    
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF3F6FA))) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("App Whitelist", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onSave, modifier = Modifier.background(Color.White, CircleShape)) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Color.Black)
            }
        }

        Card(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 10.dp),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(dummyApps.size) { index ->
                    var isChecked by remember { mutableStateOf(index < 2) } // প্রথম ২টা চেক করা
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).background(Color.LightGray, CircleShape))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(dummyApps[index], fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text("Spent: 00 sec | 0 launches", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                        Checkbox(checked = isChecked, onCheckedChange = { isChecked = it })
                    }
                    Divider(color = Color(0xFFF3F6FA))
                }
            }
            
            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
            ) {
                Text("Save", fontSize = 18.sp)
            }
        }
    }
}

// ================= ৩. অ্যাক্টিভ ব্রেক স্ক্রিন (Image 3 - Strict Mode) =================
@Composable
fun ActiveBreakScreen() {
    // এই লজিকটি ব্যাক বাটন চাপলে কোনো কাজ করতে দেবে না (Back Button Blocked)
    BackHandler(enabled = true) {
        // Do nothing! User is trapped here until the timer ends.
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF4A54E1), Color(0xFF00D2FF))))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // মোটিভেশনাল টেক্সট
            Text(
                text = "Stay Focused on your GOALS, your PEACE & your HAPPINESS. Don't waste your TIME on anything that doesn't contribute to your GROWTH.",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(60.dp))

            // মেইন টাইমার সার্কেল
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Take a Break", color = Color.Gray, fontSize = 14.sp)
                    Text("00:57", color = Color(0xFF3B82F6), fontSize = 48.sp, fontWeight = FontWeight.ExtraBold)
                    Text("left until 11:13 PM", color = Color.Gray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // এক্সটেন্ড বাটন
            Button(
                onClick = { /* সময় বাড়ানোর লজিক */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(50.dp).width(200.dp)
            ) {
                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("EXTEND ->", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(60.dp))

            // অ্যালাউড অ্যাপস আইকন (কল এবং মেনু)
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Box(
                    modifier = Modifier.size(60.dp).background(Color(0xFF4ADE80), CircleShape).clickable { /* Call intent */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
                }
                Box(
                    modifier = Modifier.size(60.dp).background(Color.White, CircleShape).clickable { /* App drawer intent for allowed apps */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Apps, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(30.dp))
                }
            }
        }
    }
}
