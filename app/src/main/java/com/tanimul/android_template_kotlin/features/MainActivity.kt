package com.tanimul.android_template_kotlin.features

import android.app.Activity
import android.app.AppOpsManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

// =================================================================
// CRITICAL IMPORT: এই লাইনটির অভাবেই আপনার সব এরর আসছিল!
import androidx.compose.runtime.collectAsState
// =================================================================

class MainActivity : ComponentActivity() {
    private val viewModel: BlockerHeroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppNavigation(viewModel)
            }
        }
    }
}

// ================= পারমিশন চেকার ফাংশন =================
fun isAccessibilityServiceEnabled(context: Context, service: Class<out android.accessibilityservice.AccessibilityService>): Boolean {
    val enabledServices = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
    if (enabledServices.isNullOrEmpty()) return false
    val colonSplitter = TextUtils.SimpleStringSplitter(':')
    colonSplitter.setString(enabledServices)
    while (colonSplitter.hasNext()) {
        val componentName = colonSplitter.next()
        if (componentName.equals("${context.packageName}/${service.name}", ignoreCase = true)) {
            return true
        }
    }
    return false
}

fun isUsageStatsPermissionGranted(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
    return mode == AppOpsManager.MODE_ALLOWED
}

fun isOverlayPermissionGranted(context: Context): Boolean {
    return Settings.canDrawOverlays(context)
}

// ================= মেইন নেভিগেশন কন্ট্রোলার =================
@Composable
fun AppNavigation(viewModel: BlockerHeroViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val view = LocalView.current

    var showSplash by remember { mutableStateOf(true) }
    var hasAccPermission by remember { mutableStateOf(isAccessibilityServiceEnabled(context, BlockerAccessibilityService::class.java)) }
    var hasUsagePermission by remember { mutableStateOf(isUsageStatsPermissionGranted(context)) }
    var hasOverlayPermission by remember { mutableStateOf(isOverlayPermissionGranted(context)) }

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.parseColor("#15AABF")
            window.navigationBarColor = android.graphics.Color.parseColor("#FFFFFF")
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasAccPermission = isAccessibilityServiceEnabled(context, BlockerAccessibilityService::class.java)
                hasUsagePermission = isUsageStatsPermissionGranted(context)
                hasOverlayPermission = isOverlayPermissionGranted(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        delay(2000)
        showSplash = false
    }

    when {
        showSplash -> SplashScreen()
        !hasAccPermission -> PermissionScreen("Accessibility Permission", "To block distractive apps and enforce strict focus mode.", Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), Icons.Default.Security)
        !hasUsagePermission -> PermissionScreen("Usage Access Required", "To provide rock-solid blocking and prevent bypasses.", Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), Icons.Default.Visibility)
        !hasOverlayPermission -> PermissionScreen("Display Over Apps", "Required to instantly show the Block Screen.", Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}")), Icons.Default.Layers)
        else -> BlockerHeroApp(viewModel)
    }
}

// ================= অ্যাপের মূল রাস্তা (NavHost) =================
@Composable
fun BlockerHeroApp(viewModel: BlockerHeroViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            RasfocusHomeScreen(viewModel = viewModel, navController = navController)
        }
        composable("blocked_screen") {
            RasfocusBlockedScreen(viewModel = viewModel, onBackClick = { navController.popBackStack() })
        }
        composable("take_a_break") {
            TakeABreakMainScreen(viewModel = viewModel, navController = navController)
        }
    }
}

// ================= স্প্ল্যাশ এবং পারমিশন স্ক্রিন =================
@Composable
fun SplashScreen() {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF15AABF)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Visibility, contentDescription = "Splash", tint = Color.White, modifier = Modifier.size(100.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("RasFocus Pro", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Stay Focused. Stay Ahead.", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
        }
    }
}

@Composable
fun PermissionScreen(title: String, desc: String, actionIntent: Intent, icon: ImageVector) {
    val context = LocalContext.current
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF8FAFC)) {
        Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, contentDescription = "Icon", tint = Color(0xFF15AABF), modifier = Modifier.size(100.dp))
            Spacer(modifier = Modifier.height(32.dp))
            Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Text(desc, fontSize = 16.sp, color = Color(0xFF64748B), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = {
                    actionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(actionIntent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15AABF)),
                modifier = Modifier.fillMaxWidth().height(55.dp)
            ) {
                Text("GRANT PERMISSION", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

// ================= ১. হোম স্ক্রিন (Main Dashboard) =================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RasfocusHomeScreen(viewModel: BlockerHeroViewModel, navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("RasFocus Pro Max", fontWeight = FontWeight.Bold, color = Color.White) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF15AABF))) },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = if (uiState.uninstallProtection) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (uiState.uninstallProtection) Icons.Default.VerifiedUser else Icons.Default.GppBad, contentDescription = null, tint = if (uiState.uninstallProtection) Color(0xFF2E7D32) else Color(0xFFC62828), modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Protection Status", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(if (uiState.uninstallProtection) "Uninstall Protection ACTIVE" else "Vulnerable - Turn On Protection", color = if (uiState.uninstallProtection) Color(0xFF2E7D32) else Color(0xFFC62828), fontSize = 14.sp)
                        }
                    }
                }
            }
            item { Text("Core Features", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp)) }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ActionCard(icon = Icons.Rounded.Block, title = "Block List", modifier = Modifier.weight(1f)) { navController.navigate("blocked_screen") }
                    ActionCard(icon = Icons.Rounded.Timer, title = "Take a Break", modifier = Modifier.weight(1f)) { navController.navigate("take_a_break") }
                }
            }
            item { Text("Strict Filters", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp)) }
            item {
                ToggleItem("Hardcore Adult Filter", "Instantly blocks explicit sites & typing", uiState.blockAdultContent) { viewModel.toggleAdultContent(it) }
                ToggleItem("Facebook Reels Blocker", "Kills dopamine-scrolling on FB", uiState.blockFacebookReels) { viewModel.toggleFacebookReels(it) }
                ToggleItem("YouTube Shorts Blocker", "Stops you from wasting hours on YT", uiState.blockYoutubeShorts) { viewModel.toggleYoutubeShorts(it) }
            }
        }
    }
}

@Composable
fun ActionCard(icon: ImageVector, title: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(modifier = modifier.height(100.dp).clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, contentDescription = title, tint = Color(0xFF15AABF), modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF334155))
        }
    }
}

@Composable
fun ToggleItem(title: String, desc: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
                Text(desc, fontSize = 12.sp, color = Color(0xFF64748B))
            }
            Switch(checked = isChecked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF15AABF)))
        }
    }
}

// ================= ২. ব্লকলিস্ট এবং প্রোটেকশন স্ক্রিন =================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RasfocusBlockedScreen(viewModel: BlockerHeroViewModel, onBackClick: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    var inputUrl by remember { mutableStateOf("") }
    val context = LocalContext.current
    val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val componentName = ComponentName(context, MyDeviceAdminReceiver::class.java)

    Scaffold(
        topBar = { TopAppBar(title = { Text("Block List & Security", color = Color.White) }, navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF15AABF))) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF8FAFC))) {
            TabRow(selectedTabIndex = selectedTab, containerColor = Color.White) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Apps", fontWeight = FontWeight.Bold) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Websites", fontWeight = FontWeight.Bold) })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Protection", fontWeight = FontWeight.Bold) })
            }

            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                when (selectedTab) {
                    0, 1 -> { // App and Website Logic combined for simplicity
                        item {
                            OutlinedTextField(
                                value = inputUrl, onValueChange = { inputUrl = it },
                                label = { Text(if (selectedTab == 0) "App Package (e.g. com.facebook.katana)" else "Website URL (e.g. facebook.com)") },
                                modifier = Modifier.fillMaxWidth(), singleLine = true
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = { if (inputUrl.isNotEmpty()) { viewModel.addToList(inputUrl); inputUrl = "" } },
                                modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15AABF))
                            ) { Text("Add to Blocklist") }
                            Spacer(Modifier.height(16.dp))
                        }
                        items(uiState.blockList) { item ->
                            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(item, fontWeight = FontWeight.Medium)
                                    IconButton(onClick = { viewModel.removeFromList("BLOCK", item) }) { Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red) }
                                }
                            }
                        }
                    }
                    2 -> { // Protection Logic
                        item {
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                    Text("Uninstall Protection", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Prevents you from deleting Rasfocus from phone settings.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    Button(
                                        onClick = {
                                            if (!devicePolicyManager.isAdminActive(componentName)) {
                                                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                                                    putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
                                                    putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Rasfocus needs Admin rights to prevent uninstallation.")
                                                }
                                                context.startActivity(intent)
                                            } else {
                                                viewModel.enableUninstallProtection(1)
                                                Toast.makeText(context, "Protection Enabled for 24 Hours!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth().height(50.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15AABF))
                                    ) { Text("Turn On Uninstall Protection", fontWeight = FontWeight.Bold, color = Color.White) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ================= ৩. Take a Break স্ক্রিন =================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TakeABreakMainScreen(viewModel: BlockerHeroViewModel, navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()
    var hours by remember { mutableStateOf("0") }
    var minutes by remember { mutableStateOf("25") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Strict Break Mode", color = Color.White) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF15AABF))) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(Icons.Rounded.Timer, contentDescription = "Timer", modifier = Modifier.size(80.dp), tint = Color(0xFF15AABF))
            Spacer(modifier = Modifier.height(16.dp))
            
            if (uiState.isStrictBreakActive) {
                Text("Focus Session Active!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                Text(uiState.breakTimeRemaining, fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
                Text("Your phone is restricted until the timer runs out.", textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
            } else {
                Text("Set Focus Duration", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(value = hours, onValueChange = { hours = it }, label = { Text("Hours") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = minutes, onValueChange = { minutes = it }, label = { Text("Minutes") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { viewModel.startStrictBreak(hours.toIntOrNull() ?: 0, minutes.toIntOrNull() ?: 0) }, modifier = Modifier.fillMaxWidth().height(55.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15AABF))) {
                    Text("START FOCUS MODE", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
