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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ==========================================
// ১. আপনার তৈরি করা ফাইলগুলোর ইমপোর্ট লিঙ্ক
// ==========================================
import com.tanimul.android_template_kotlin.features.MainScreen
import com.tanimul.android_template_kotlin.features.Blocks
import com.tanimul.android_template_kotlin.features.Adult
import com.tanimul.android_template_kotlin.features.Focus
import com.tanimul.android_template_kotlin.features.Special
import com.tanimul.android_template_kotlin.features.Stats
import com.tanimul.android_template_kotlin.features.Settings

// ==========================================
// Color Palette
// ==========================================
val ColTeal = Color(0xFF0CA8B0)         // ColTeal(255, 12, 168, 176)
val ColBgContent = Color(0xFFF8FAFC)    // ColBgContent(255, 248, 250, 252)
val ColTextDark = Color(0xFF323232)
val ColUpgradeBtn = Color(0xFFF39C12)   

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                RootNavigation()
            }
        }
    }
}

// ==========================================
// ২. মূল নেভিগেশন (Splash -> Permission -> MainApp)
// ==========================================
@Composable
fun RootNavigation() {
    val rootNavController = rememberNavController()

    NavHost(navController = rootNavController, startDestination = "splash") {
        composable("splash") { SplashScreen(rootNavController) }
        composable("permissions") { PermissionsScreen(rootNavController) }
        composable("main_app") { RasFocusMainApp() }
    }
}

// ==========================================
// ৩. স্প্ল্যাশ স্ক্রিন (Splash Screen)
// ==========================================
@Composable
fun SplashScreen(navController: NavHostController) {
    // ২ সেকেন্ড পর অটোমেটিক পারমিশন পেজে চলে যাবে
    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate("permissions") {
            popUpTo("splash") { inclusive = true } // স্প্ল্যাশ পেজ ব্যাকস্ট্যাক থেকে মুছে ফেলা
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColTeal),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.GpsFixed,
                contentDescription = "App Logo",
                tint = Color.White,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("RasFocus Pro", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Protect your focus and time", fontSize = 14.sp, color = Color(0xFFD0F0F0))
            
            Spacer(modifier = Modifier.height(40.dp))
            CircularProgressIndicator(color = Color.White)
        }
    }
}

// ==========================================
// ৪. পারমিশন স্ক্রিন (Permissions Screen)
// ==========================================
@Composable
fun PermissionsScreen(navController: NavHostController) {
    var accGranted by remember { mutableStateOf(false) }
    var drawOverGranted by remember { mutableStateOf(false) }
    var adminGranted by remember { mutableStateOf(false) }

    val allGranted = accGranted && drawOverGranted && adminGranted

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColBgContent)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Setup Required", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = ColTextDark)
        Text("RasFocus needs these permissions to block apps and adult content effectively.", color = Color.Gray, modifier = Modifier.padding(top = 8.dp, bottom = 32.dp))

        PermissionItem("Accessibility Service", "Detects when you open a blocked app or website.", accGranted) { accGranted = !accGranted }
        PermissionItem("Display Over Other Apps", "Allows showing the lock screen over blocked content.", drawOverGranted) { drawOverGranted = !drawOverGranted }
        PermissionItem("Device Administrator", "Prevents unauthorized uninstallation of RasFocus.", adminGranted) { adminGranted = !adminGranted }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                if (allGranted) {
                    navController.navigate("main_app") {
                        popUpTo("permissions") { inclusive = true }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (allGranted) ColTeal else Color.LightGray),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Continue to App", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun PermissionItem(title: String, desc: String, isGranted: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if(isGranted) Color(0xFFD1FAE5) else Color(0xFFF1F5F9)), contentAlignment = Alignment.Center) {
                Icon(if(isGranted) Icons.Default.Check else Icons.Default.Settings, contentDescription = null, tint = if(isGranted) Color(0xFF10B981) else Color.Gray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ColTextDark)
                Text(desc, fontSize = 12.sp, color = Color.Gray)
            }
            Switch(checked = isGranted, onCheckedChange = { onClick() }, colors = SwitchDefaults.colors(checkedThumbColor = ColTeal, checkedTrackColor = ColTeal.copy(alpha = 0.5f)))
        }
    }
}

// ==========================================
// ৫. মেইন সাইডবার অ্যাপ
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RasFocusMainApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // পিসির System Tray এর মত ব্যাক বাটন লজিক
    BackHandler {
        val activity = context as? Activity
        activity?.moveTaskToBack(true)
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
            // ৬. আপনার তৈরি করা ফাইলগুলোর অরিজিনাল লিঙ্ক
            // ==========================================
            NavHost(
                navController = navController,
                startDestination = "dashboard",
                modifier = Modifier.padding(paddingValues)
            ) {
                // সব ফাইল এখন লাইভ! কোনো ডামি টেক্সট নেই
                composable("dashboard") { MainScreen(navController, drawerState, scope) }
                composable("blocks") { Blocks() }
                composable("adult_block") { Adult() }
                composable("deep_study") { Focus() }
                composable("special_feature") { Special() }
                composable("statistics") { Stats() }
                composable("settings") { Settings() }
            }
        }
    }
}

// ==========================================
// ৭. সাইডবার ডিজাইন
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
                onClick = { Toast.makeText(context, "Upgrade to Pro dialog will open here.", Toast.LENGTH_SHORT).show() },
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
