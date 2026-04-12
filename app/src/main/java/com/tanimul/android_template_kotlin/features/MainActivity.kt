package com.tanimul.android_template_kotlin.features

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.text.TextUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

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

// ১. অ্যাক্সেসিবিলিটি সার্ভিস চেক
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

// ২. Usage Access (দাপ্তরিক তথ্য) চেক
fun isUsageStatsPermissionGranted(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
    return mode == AppOpsManager.MODE_ALLOWED
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

    // টপ বার এবং নেভিগেশন বারের প্রিমিয়াম ডিজাইন
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.parseColor("#15AABF") // Rasfocus Teal Theme
            window.navigationBarColor = android.graphics.Color.parseColor("#FFFFFF") // Clean White Bottom
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    // অ্যাপ ব্যাকগ্রাউন্ড থেকে সামনে আসলে পারমিশন রি-চেক করা
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasAccPermission = isAccessibilityServiceEnabled(context, BlockerAccessibilityService::class.java)
                hasUsagePermission = isUsageStatsPermissionGranted(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // ২ সেকেন্ড স্প্ল্যাশ স্ক্রিন দেখাবে
    LaunchedEffect(Unit) {
        delay(2000)
        showSplash = false
    }

    // স্ক্রিন রাউটিং লজিক
    when {
        showSplash -> SplashScreen()
        !hasAccPermission -> PermissionScreen(
            title = "Accessibility Permission",
            desc = "To block distractive apps and enforce focus mode, Rasfocus needs Accessibility permission.",
            action = Settings.ACTION_ACCESSIBILITY_SETTINGS
        )
        !hasUsagePermission -> PermissionScreen(
            title = "Usage Access Required",
            desc = "To provide rock-solid blocking and prevent bypasses, we need Usage Access permission.",
            action = Settings.ACTION_USAGE_ACCESS_SETTINGS
        )
        else -> BlockerHeroApp(viewModel) // সব পারমিশন থাকলে মেইন অ্যাপ ওপেন হবে
    }
}

// ================= অ্যাপের মূল রাস্তা (NavHost) =================
@Composable
fun BlockerHeroApp(viewModel: BlockerHeroViewModel) {
    val navController = rememberNavController()

    // NavHost হলো সেই রাস্তা, যার মাধ্যমে আমরা এক পেজ থেকে অন্য পেজে যাব
    NavHost(navController = navController, startDestination = "home") {
        
        // ১. হোম স্ক্রিন (Dashboard)
        composable("home") {
            // আপনার HomeScreenView এর কোডে বাটন ক্লিকের জায়গায় navController.navigate("take_a_break") ইত্যাদি বসাতে হবে
            HomeScreenView(viewModel = viewModel, navController = navController) 
        }

        // ২. Take a Break স্ক্রিন
        composable("take_a_break") {
            TakeABreakMainScreen(viewModel = viewModel, navController = navController)
        }

        // ৩. App Selection স্ক্রিন
        composable("app_whitelist") {
            AppSelectionScreen(viewModel = viewModel, onBackClick = { navController.popBackStack() })
        }

        // ৪. Site Selection স্ক্রিন
        composable("site_whitelist") {
            SiteSelectionScreen(viewModel = viewModel, onBackClick = { navController.popBackStack() })
        }
    }
}

// ================= স্প্ল্যাশ এবং পারমিশন স্ক্রিন =================

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF15AABF)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Visibility,
                contentDescription = "Splash Logo",
                tint = Color.White,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "RasFocus Pro", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Stay Focused. Stay Ahead.", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
        }
    }
}

@Composable
fun PermissionScreen(title: String, desc: String, action: String) {
    val context = LocalContext.current
    val primaryColor = Color(0xFF15AABF)

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF8FAFC)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = Icons.Default.Security, contentDescription = "Security", tint = primaryColor, modifier = Modifier.size(100.dp))
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = desc, fontSize = 16.sp, color = Color(0xFF64748B), textAlign = TextAlign.Center, lineHeight = 24.sp)
            Spacer(modifier = Modifier.height(40.dp))
            
            Button(
                onClick = {
                    val intent = Intent(action)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(55.dp)
            ) {
                Text("GRANT PERMISSION", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
