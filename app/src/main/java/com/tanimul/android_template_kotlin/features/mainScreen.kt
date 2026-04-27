package com.tanimul.android_template_kotlin.features

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// কালার প্যালেট
private val ColTeal = Color(0xFF0CA8B0)         
private val ColBgContent = Color(0xFFF1F5F9)    

@Composable
fun MainScreen(navController: NavController, onOpenDrawer: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColBgContent)
    ) {
        // ==========================================
        // ১. থিম কালার হেডার (Full Width & Teal)
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColTeal) // হেডার আপনার থিম কালারের হবে
                .padding(top = 48.dp, bottom = 24.dp) // স্ট্যাটাস বারের জন্য টপ প্যাডিং
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // হ্যামবার্গার মেনু আইকন
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)) // হালকা সাদা ব্যাকগ্রাউন্ড
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu, 
                        contentDescription = "Menu", 
                        tint = Color.White, 
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text("Dashboard", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Ready for deep work?", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                }
            }
        }

        // ==========================================
        // মেইন বডি কন্টেন্ট
        // ==========================================
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            
            // একটু নেগেটিভ মার্জিন দিয়ে ব্যানারটাকে হেডারের ওপরে তুলে দেওয়া হলো
            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // ২. স্ট্যাটাস/ইনফো ব্যানার
            // ==========================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(ColTeal.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = ColTeal)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Instant Session", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            Text("Eliminate all distractions now", fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Button(
                        onClick = { Toast.makeText(context, "Easy Session coming soon!", Toast.LENGTH_SHORT).show() },
                        colors = ButtonDefaults.buttonColors(containerColor = ColTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Start Easy Session", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ==========================================
            // ৩. কুইক অ্যাকশন এবং সেটআপ কার্ড
            // ==========================================
            Text("Control Center", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(16.dp))

            // প্রথম সারি
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuickActionCard("App & Web Blocks", "Manage blocklist", Icons.Default.Shield, Modifier.weight(1f)) {
                    navController.navigate("blocks") 
                }
                QuickActionCard("Adult Filter", "Safe browsing", Icons.Default.Lock, Modifier.weight(1f)) {
                    navController.navigate("adult_block") 
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // দ্বিতীয় সারি
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuickActionCard("Deep Study", "Pomodoro timer", Icons.Default.Visibility, Modifier.weight(1f)) {
                    navController.navigate("deep_study") 
                }
                QuickActionCard("Statistics", "View progress", Icons.Default.BarChart, Modifier.weight(1f)) {
                    navController.navigate("statistics") 
                }
            }
        }
    }
}

// ==========================================
// রি-ডিজাইন করা কার্ড ফাংশন
// ==========================================
@Composable
fun QuickActionCard(title: String, subtitle: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .height(110.dp), 
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = ColTeal, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B), maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 12.sp, color = Color.Gray, maxLines = 1)
        }
    }
}
