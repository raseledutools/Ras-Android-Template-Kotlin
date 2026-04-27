package com.tanimul.android_template_kotlin.features

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings as AndroidSettings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// DataManager এবং আপনার ফিচারের ইমপোর্টগুলো
import com.tanimul.android_template_kotlin.DataManager
import com.tanimul.android_template_kotlin.features.*

// 'private' অ্যাড করা হয়েছে যাতে অন্য ফাইলের সাথে কনফ্লিক্ট না করে
private val ColTeal = Color(0xFF0CA8B0)
private val ColBgContent = Color(0xFFF8FAFC)
private val ColTextDark = Color(0xFF323232)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
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
// ১. মূল নেভিগেশন কন্ট্রোল
// ==========================================
@Composable
fun AppRootNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "splash") {
        // ১ সেকেন্ডের স্প্ল্যাশ স্ক্রিন
        composable("splash") {
            SplashScreen {
                // পারমিশন চেক করে গন্তব্য ঠিক করা
                val nextDest = if (areAllPermissionsGranted(context)) "main_app" else "permissions"
                navController.navigate(nextDest) {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }

        // পারমিশন পেজ
        composable("permissions") {
            PermissionsPage {
                navController.navigate("main_app") {
                    popUpTo("permissions") { inclusive = true }
                }
            }
        }

        // মেইন অ্যাপ (ড্যাশবোর্ড + সাইডবার)
        composable("main_app") {
            RasFocusMainContent()
        }
    }
}

// ==========================================
// ২. সুপার ফাস্ট স্প্ল্যাশ (১ সেকেন্ড)
// ==========================================
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1000) // মাত্র ১ সেকেন্ড
        onFinished()
    }

    Box(modifier = Modifier.fillMaxSize().background(ColTeal), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.GpsFixed, null, tint = Color.White, modifier = Modifier.size(80.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("RasFocus Pro", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

// ==========================================
// ৩. পারমিশন পেজ (ডিজাইন + লজিক)
// ==========================================
@Composable
fun PermissionsPage(onAllGranted: () -> Unit) {
    val context = LocalContext.current
    // পারমিশন স্টেটগুলো এখানে চেক হবে (রিয়েলটাইম আপডেটের জন্য)
    var accessibilityGranted by remember { mutableStateOf(false) }
    var overlayGranted by remember { mutableStateOf(false) }

    // প্রতিবার যখন ইউজার সেটিংস থেকে ফিরে আসবে, তখন রিফ্রেশ হবে
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RasFocusMainContent() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // ব্যাক বাটন হ্যান্ডলার
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
        Scaffold(
            topBar = {
                // শুধুমাত্র ড্যাশবোর্ড বাদে অন্য পেজে ব্যাক বাটন বা টাইটেল দেখাতে পারেন
            }
        ) { padding ->
            NavHost(navController, "dashboard", Modifier.padding(padding)) {
                composable("dashboard") { MainScreen(navController) { scope.launch { drawerState.open() } } }
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
fun SidebarItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, route: String, currentRoute: String, onNavigate: (String) -> Unit) {
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
