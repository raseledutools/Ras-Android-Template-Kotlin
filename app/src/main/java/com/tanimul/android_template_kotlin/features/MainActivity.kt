package com.tanimul.android_template_kotlin.features

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings as AndroidSettings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge // 🟢 ফুল স্ক্রিনের জন্য ইমপোর্ট
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// DataManager ইমপোর্ট
import com.tanimul.android_template_kotlin.DataManager

// ==========================================
// C++ Colors Translation & Premium Palette
// ==========================================
private val ColTeal = Color(0xFF0CA8B0)
private val ColTealLight = Color(0xFFE0F2F1)
private val ColBgContent = Color(0xFFF1F5F9)
private val ColTextDark = Color(0xFF1E293B)
private val ColGradientStart = Color(0xFF0CA8B0)
private val ColGradientEnd = Color(0xFF007B83)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 🟢 Edge-to-Edge এনাবল করা হলো ফুল স্ক্রিনের জন্য
        enableEdgeToEdge()
        
        // ডাটাবেস ইঞ্জিন ইনিশিয়ালাইজ করা
        DataManager.init(this)
        
        setContent {
            MaterialTheme {
                AppRootNavigation()
            }
        }
    }
}

// ==========================================
// ১. মূল নেভিগেশন কন্ট্রোল (AppRootNavigation)
// ==========================================
@Composable
fun AppRootNavigation() {
    val context = LocalContext.current
    var permissionsGranted by remember { mutableStateOf(areAllPermissionsGranted(context)) }

    if (permissionsGranted) {
        RasFocusMainContent()
    } else {
        PermissionsPage(onAllGranted = { permissionsGranted = true })
    }
}

// ==========================================
// ৩. পারমিশন পেজ (ডিজাইন + লজিক)
// ==========================================
@Composable
fun PermissionsPage(onAllGranted: () -> Unit) {
    val context = LocalContext.current
    var accessibilityGranted by remember { mutableStateOf(false) }
    var overlayGranted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            accessibilityGranted = isAccessibilityServiceEnabled(context)
            overlayGranted = AndroidSettings.canDrawOverlays(context)
            if (accessibilityGranted && overlayGranted) {
                onAllGranted()
                break
            }
            delay(1000)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(ColBgContent).padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Required Permissions", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Enable these to start blocking distractions.", color = Color.Gray, modifier = Modifier.padding(bottom = 32.dp))

        PermissionCard("Accessibility Service", "To detect app opening", accessibilityGranted) {
            context.startActivity(Intent(AndroidSettings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        PermissionCard("Display Over Apps", "To show lock screen", overlayGranted) {
            context.startActivity(Intent(AndroidSettings.ACTION_MANAGE_OVERLAY_PERMISSION))
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        Text("Wait... Checking status automatically...", fontSize = 12.sp, color = ColTeal)
    }
}

@Composable
fun PermissionCard(title: String, desc: String, granted: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { if(!granted) onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(if(granted) Icons.Default.CheckCircle else Icons.Default.Settings, null, tint = if(granted) Color(0xFF10B981) else Color.Gray)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, color = ColTextDark)
                Text(desc, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

// ==========================================
// ৪. মেইন অ্যাপ লেআউট (সাইডবার সহ)
// ==========================================
@Composable
fun RasFocusMainContent() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    BackHandler(enabled = drawerState.isOpen) { scope.launch { drawerState.close() } }
    BackHandler(enabled = drawerState.isClosed) { (context as? Activity)?.moveTaskToBack(true) }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: "dashboard"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            RasFocusSidebar(currentRoute) { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
                scope.launch { drawerState.close() }
            }
        }
    ) {
        Scaffold { padding ->
            NavHost(navController, "dashboard", Modifier.padding(bottom = padding.calculateBottomPadding())) {
                composable("dashboard") { HomeMainScreen(navController) { scope.launch { drawerState.open() } } }
                
                // আপনার অন্যান্য পেজগুলো
                composable("blocks") { Blocks() }
                composable("adult_block") { Adult_block() }
                composable("deep_study") { Deep_study() }
                composable("special_feature") { Speacial() }
                composable("statistics") { Statistics() }
                composable("settings") { Settings() }
            }
        }
    }
}

// ==========================================
// ৫. সাইডবার ডিজাইন
// ==========================================
@Composable
fun RasFocusSidebar(currentRoute: String, onNavigate: (String) -> Unit) {
    ModalDrawerSheet(drawerContainerColor = ColTeal, modifier = Modifier.width(280.dp)) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Text("RasFocus", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Version 1.0 Pro", fontSize = 12.sp, color = Color(0xFFD0F0F0))
            Spacer(modifier = Modifier.height(32.dp))

            SidebarItem("Dashboard", Icons.Default.Dashboard, "dashboard", currentRoute, onNavigate)
            SidebarItem("App Blocks", Icons.Default.Shield, "blocks", currentRoute, onNavigate)
            SidebarItem("Adult Block", Icons.Default.Lock, "adult_block", currentRoute, onNavigate)
            SidebarItem("Deep Study", Icons.Default.Visibility, "deep_study", currentRoute, onNavigate)
            SidebarItem("Statistics", Icons.Default.BarChart, "statistics", currentRoute, onNavigate)
            SidebarItem("Settings", Icons.Default.Settings, "settings", currentRoute, onNavigate)
        }
    }
}

@Composable
fun SidebarItem(label: String, icon: ImageVector, route: String, currentRoute: String, onNavigate: (String) -> Unit) {
    val selected = currentRoute == route
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) Color.White else Color.Transparent)
            .clickable { onNavigate(route) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = if (selected) ColTeal else Color.White)
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, color = if (selected) ColTeal else Color.White, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}

// ==========================================
// ৬. হেল্পার ফাংশন (পারমিশন চেকিং)
// ==========================================
fun areAllPermissionsGranted(context: Context): Boolean {
    return isAccessibilityServiceEnabled(context) && AndroidSettings.canDrawOverlays(context)
}

fun isAccessibilityServiceEnabled(context: Context): Boolean {
    val expectedService = "${context.packageName}/${context.packageName}.features.BlockerAccessibilityService"
    val enabledServices = AndroidSettings.Secure.getString(context.contentResolver, AndroidSettings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
    return enabledServices?.contains(expectedService) == true
}

// ==========================================
// 🟢 ৭. NEW MAIN SCREEN DESIGN (Full Width Header & Bottom Nav) 
// ==========================================
@Composable
fun HomeMainScreen(navController: NavController, onOpenDrawer: () -> Unit) {
    val context = LocalContext.current
    var selectedBottomTab by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize().background(ColBgContent)) {
        
        // 🟢 মেইন স্ক্রোলিং কন্টেন্ট
        Column(
            modifier = Modifier
                .fillMaxSize()
                // 🟢 Bottom Nav এর জন্য নিচে একটু জায়গা ছেড়ে দেওয়া হলো
                .padding(bottom = 80.dp)
        ) {
            // ==========================================
            // ১. থিম কালার হেডার (Full Width Gradient)
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(ColGradientStart, ColGradientEnd)))
                    .statusBarsPadding() // 🟢 স্ট্যাটাস বারের নিচে নামানোর জন্য
                    .padding(vertical = 24.dp) 
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // হ্যামবার্গার মেনু আইকন
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    
                    // নোটিফিকেশন আইকন
                    IconButton(
                        onClick = { Toast.makeText(context, "No new notifications", Toast.LENGTH_SHORT).show() },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = "Alerts", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }
            }

            // ==========================================
            // ২. গ্রিটিংস ও ব্যানার
            // ==========================================
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(20.dp))
                Text("Welcome", fontSize = 14.sp, color = Color.Gray)
                Text("Good Morning", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = ColTextDark)
                
                Spacer(modifier = Modifier.height(20.dp))

                // পারমিশন ওয়ার্নিং ব্যানার (অপশনাল)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE4E1)),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Battery Optimisation", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                            Text("Disable to work properly", fontSize = 12.sp, color = Color(0xFFD32F2F).copy(alpha = 0.8f))
                        }
                        Button(
                            onClick = { Toast.makeText(context, "Go to Settings", Toast.LENGTH_SHORT).show() },
                            colors = ButtonDefaults.buttonColors(containerColor = ColTeal),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Disable")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ==========================================
                // ৩. অ্যানালিটিক্স গ্রিড
                // ==========================================
                Text("Analytics", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ColTextDark)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AnalyticsCard("Screen Time", "05 sec", "-99 percent", Icons.Default.Timer, Modifier.weight(1f))
                    AnalyticsCard("App Launches", "1", "-363 launches", Icons.Default.RocketLaunch, Modifier.weight(1f))
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Take a break banner
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { navController.navigate("deep_study") },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE6E6FA)),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Coffee, contentDescription = null, tint = Color(0xFF4B0082))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Take a Break", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF4B0082))
                    }
                }
            }
        }

        // ==========================================
        // 🟢 ৪. BOTTOM NAVIGATION BAR
        // ==========================================
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 12.dp, horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem("Dashboard", Icons.Default.Dashboard, selectedBottomTab == 0) { selectedBottomTab = 0 }
                BottomNavItem("Blocks", Icons.Default.Shield, selectedBottomTab == 1) { 
                    selectedBottomTab = 1
                    navController.navigate("blocks")
                }
                BottomNavItem("Study", Icons.Default.Visibility, selectedBottomTab == 2) { 
                    selectedBottomTab = 2
                    navController.navigate("deep_study")
                }
                BottomNavItem("Account", Icons.Default.Person, selectedBottomTab == 3) { selectedBottomTab = 3 }
            }
        }
    }
}

// ==========================================
// রি-ডিজাইন করা অ্যানালিটিক্স কার্ড
// ==========================================
@Composable
fun AnalyticsCard(title: String, value: String, subtitle: String, icon: ImageVector, modifier: Modifier) {
    Card(
        modifier = modifier.height(130.dp), 
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, contentDescription = null, tint = ColTextDark, modifier = Modifier.size(24.dp))
            Column {
                Text(title, fontSize = 12.sp, color = Color.Gray)
                Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = ColTextDark)
                Text(subtitle, fontSize = 10.sp, color = Color(0xFF10B981))
            }
        }
    }
}

// ==========================================
// বটম নেভিগেশন আইটেম ডিজাইন
// ==========================================
@Composable
fun BottomNavItem(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(if (isSelected) ColTealLight else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = if (isSelected) ColTeal else Color.Gray)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) ColTeal else Color.Gray)
    }
}
