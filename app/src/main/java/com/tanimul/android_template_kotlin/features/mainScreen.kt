package com.tanimul.android_template_kotlin.features

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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

// 'private' অ্যাড করা হয়েছে যাতে অন্য ফাইলের সাথে কনফ্লিক্ট না করে
private val ColTeal = Color(0xFF0CA8B0)         
private val ColBgContent = Color(0xFFF8FAFC)    

@Composable
fun MainScreen(navController: NavController, onOpenDrawer: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColBgContent)
    ) {
        // ==========================================
        // ১. টপবার এবং ৩-লাইনের মেনু (Hamburger)
        // ==========================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onOpenDrawer) {
                Icon(
                    imageVector = Icons.Default.Menu, 
                    contentDescription = "Menu", 
                    tint = Color(0xFF323232), 
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Dashboard", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color(0xFF323232))
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            // ওয়েলকাম মেসেজ
            Text("Welcome back! Your productivity control center is ready.", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(24.dp))

            // ==========================================
            // ২. টিয়াল (Teal) কালারের বড় ব্যানার
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ColTeal)
                    .padding(24.dp)
            ) {
                Column {
                    Text("Ready for Deep Work?", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Start an instant blocking session to eliminate all distractions.", fontSize = 14.sp, color = Color(0xFFD0F0F0))
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Button(
                        onClick = { Toast.makeText(context, "Easy Session coming soon!", Toast.LENGTH_SHORT).show() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(45.dp)
                    ) {
                        Text("Start Easy Session", color = ColTeal, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ==========================================
            // ৩. কুইক অ্যাকশন এবং সেটআপ কার্ড
            // ==========================================
            Text("Quick Actions & Setup", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF323232))
            Spacer(modifier = Modifier.height(16.dp))

            // প্রথম সারি
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuickActionCard("App & Web Blocks", "Manage your blocklist", Icons.Default.Shield, Modifier.weight(1f)) {
                    navController.navigate("blocks") 
                }
                QuickActionCard("Adult Filter", "Safe browsing setup", Icons.Default.Lock, Modifier.weight(1f)) {
                    navController.navigate("adult_block") 
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // দ্বিতীয় সারি (এখন এগুলোও সরাসরি নেভিগেট করবে)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuickActionCard("Deep Study", "Pomodoro & focus", Icons.Default.Visibility, Modifier.weight(1f)) {
                    navController.navigate("deep_study") // আপডেট করা হয়েছে
                }
                QuickActionCard("Statistics", "View your progress", Icons.Default.BarChart, Modifier.weight(1f)) {
                    navController.navigate("statistics") // আপডেট করা হয়েছে
                }
            }
        }
    }
}

// কার্ডের ডিজাইন ফাংশন (যাতে কোড দেখতে পরিষ্কার লাগে)
@Composable
fun QuickActionCard(title: String, subtitle: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .height(100.dp), 
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = ColTeal, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF323232))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(subtitle, fontSize = 12.sp, color = Color.Gray, maxLines = 1)
        }
    }
}
