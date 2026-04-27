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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import com.tanimul.android_template_kotlin.DataManager

private val ColTeal = Color(0xFF0CA8B0)
private val ColBgContent = Color(0xFFF1F5F9)
private val ColTextDark = Color(0xFF1E293B)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataManager.init(this)
        setContent {
            MaterialTheme {
                AppRootNavigation()
            }
        }
    }
}

@Composable
fun AppRootNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen {
                val nextDest = if (areAllPermissionsGranted(context)) "main_app" else "permissions"
                navController.navigate(nextDest) {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }

        composable("permissions") {
            PermissionsPage {
                navController.navigate("main_app") {
                    popUpTo("permissions") { inclusive = true }
                }
            }
        }

        composable("main_app") {
            RasFocusMainContent()
        }
    }
}

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1000)
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

@OptIn(ExperimentalMaterial3Api::class)
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
            NavHost(navController, "dashboard", Modifier.padding(padding)) {
                composable("dashboard") { MainScreen(navController) { scope.launch { drawerState.open() } } }
                composable("blocks") { Blocks() }
                composable("adult_block") { Adult_block() }
                composable("deep_study") { Deep_study() }
                // composable("special_feature") { Speacial() } // Uncomment if you have this
                // composable("statistics") { Statistics() }    // Uncomment if you have this
                // composable("settings") { Settings() }        // Uncomment if you have this
            }
        }
    }
}

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

fun areAllPermissionsGranted(context: Context): Boolean {
    return isAccessibilityServiceEnabled(context) && AndroidSettings.canDrawOverlays(context)
}

fun isAccessibilityServiceEnabled(context: Context): Boolean {
    val expectedService = "${context.packageName}/${context.packageName}.features.BlockerAccessibilityService"
    val enabledServices = AndroidSettings.Secure.getString(context.contentResolver, AndroidSettings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
    return enabledServices?.contains(expectedService) == true
}

@Composable
fun MainScreen(navController: NavController, onOpenDrawer: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColBgContent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColTeal) 
                .padding(top = 48.dp, bottom = 24.dp) 
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)) 
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

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            
            Spacer(modifier = Modifier.height(16.dp))

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

            Text("Control Center", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(16.dp))

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
