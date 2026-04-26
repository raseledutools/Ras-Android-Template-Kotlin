package com.tanimul.android_template_kotlin.features

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

// ==========================================
// C++ Colors Translation
// ==========================================
val ColTeal = Color(0xFF0CA8B0)         // ColTeal(255, 12, 168, 176)
val ColBgContent = Color(0xFFF8FAFC)    // ColBgContent(255, 248, 250, 252)
val ColTextDark = Color(0xFF323232)
val ColUpgradeBtn = Color(0xFFF39C12)   

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Single Instance (Mutex) লজিক অ্যান্ড্রয়েডে AndroidManifest.xml-এ android:launchMode="singleTask" দিয়ে করতে হয়।
        // Boot/AutoRun এর লজিক BroadcastReceiver দিয়ে হ্যান্ডেল হবে।

        setContent {
            MaterialTheme {
                RasFocusMainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RasFocusMainApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // C++ System Tray Hide Logic -> অ্যান্ড্রয়েডে ব্যাক বাটন চাপলে অ্যাপ হাইড হবে, ক্লোজ হবে না
    BackHandler {
        val activity = context as? Activity
        activity?.moveTaskToBack(true) // System Tray এর মত ব্যাকগ্রাউন্ডে চলে যাবে
    }

    val menuItems = listOf(
        DrawerMenuItem("Dashboard", Icons.Default.Dashboard, "dashboard"),
        DrawerMenuItem("Blocks", Icons.Default.Shield, "blocks"),
        DrawerMenuItem("Adult Block", Icons.Default.Lock, "adult_block"),
        DrawerMenuItem("Deep Study", Icons.Default.Visibility, "deep_study"),
        DrawerMenuItem("Special Feature", Icons.Default.Star, "special_feature"),
        DrawerMenuItem("Statistics", Icons.Default.BarChart, "statistics"),
        DrawerMenuItem("Settings", Icons.Default.Settings, "settings")
    )

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: "dashboard"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            RasFocusSidebar(menuItems, currentRoute) { route ->
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
                TopAppBar(
                    title = { 
                        Text(
                            text = menuItems.find { it.route == currentRoute }?.title ?: "RasFocus Pro",
                            fontWeight = FontWeight.Bold,
                            color = ColTextDark
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = ColTextDark)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = ColBgContent)
                )
            },
            containerColor = ColBgContent
        ) { paddingValues ->
            
            // ==========================================
            // Main Navigation Area (C++ DrawMainArea)
            // ==========================================
            NavHost(
                navController = navController,
                startDestination = "dashboard",
                modifier = Modifier.padding(paddingValues)
            ) {
                // পিসির মতো আলাদা ফাঁকা স্ক্রিন কল হচ্ছে
                composable("dashboard") { Home(navController) }
                composable("blocks") { Blocks() }
                composable("adult_block") { Adult() }
                composable("deep_study") { Focus() }
                composable("special_feature") { SpecialFeature() }
                composable("statistics") { Stats() }
                composable("settings") { Settings() }
            }
        }
    }
}

// ==========================================
// C++ Empty Tab Screens Translation (ফাঁকা স্ক্রিন)
// ==========================================

@Composable
fun Home(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColBgContent)
    ) {
        // ড্যাশবোর্ডের ডিজাইন এখানে আসবে...
        
        // ==========================================
        // C++ Secret DEBUG KILL BUTTON (Bottom Right)
        // ==========================================
        Button(
            onClick = { 
                // C++ taskkill /F /IM RasObserve.exe লজিক
                exitProcess(0) 
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE63232)), // C++ Color(255, 230, 50, 50)
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .size(width = 110.dp, height = 35.dp)
        ) {
            Text("DEBUG KILL", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun Blocks() {
    // C++ DrawBlocksTab()
    Box(modifier = Modifier.fillMaxSize().background(ColBgContent)) {
        // Blocks এর ডিজাইন হবে
    }
}

@Composable
fun Adult() {
    // C++ DrawAdultBlockTab()
    Box(modifier = Modifier.fillMaxSize().background(ColBgContent)) {
        // Adult Block এর ডিজাইন হবে
    }
}

@Composable
fun Focus() {
    // C++ DrawDeepStudyTab()
    Box(modifier = Modifier.fillMaxSize().background(ColBgContent)) {
        // Deep Study এর ডিজাইন হবে
    }
}

@Composable
fun SpecialFeature() {
    // C++ DrawSpecialFeatureTab()
    Box(modifier = Modifier.fillMaxSize().background(ColBgContent)) {
        // Special Feature এর ডিজাইন হবে
    }
}

@Composable
fun Stats() {
    // C++ DrawStatisticsTab()
    Box(modifier = Modifier.fillMaxSize().background(ColBgContent)) {
        // Statistics এর ডিজাইন হবে
    }
}

@Composable
fun Settings() {
    // C++ DrawSettingsTab()
    Box(modifier = Modifier.fillMaxSize().background(ColBgContent)) {
        // Settings এর ডিজাইন হবে
    }
}


// ==========================================
// C++ Sidebar Implementation
// ==========================================
@Composable
fun RasFocusSidebar(
    menuItems: List<DrawerMenuItem>,
    currentRoute: String,
    onItemClick: (String) -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(280.dp),
        drawerContainerColor = ColTeal 
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(vertical = 24.dp)) {
            // Logo
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.GpsFixed, contentDescription = "Logo", tint = Color.White, modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("RasFocus", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Adult & apps blocker", fontSize = 12.sp, color = Color(0xFFD0F0F0))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tabs
            menuItems.forEach { item ->
                val isSelected = currentRoute == item.route
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color.White else Color.Transparent)
                        .clickable { onItemClick(item.route) }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(item.icon, contentDescription = item.title, tint = if (isSelected) ColTeal else Color.White, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(item.title, fontSize = 16.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) ColTeal else Color.White)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            val context = LocalContext.current
            Button(
                onClick = { Toast.makeText(context, "Upgrade to Pro dialog will open here.", Toast.LENGTH_SHORT).show() }, // C++ MessageBox logic
                colors = ButtonDefaults.buttonColors(containerColor = ColUpgradeBtn),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp).height(50.dp)
            ) {
                Text("Upgrade to Pro", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

data class DrawerMenuItem(val title: String, val icon: ImageVector, val route: String)
