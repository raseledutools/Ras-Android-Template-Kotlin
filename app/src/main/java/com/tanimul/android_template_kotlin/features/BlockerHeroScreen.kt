// =============================================================================
// FOCUS BLOCKER APP - JETPACK COMPOSE UI (Lines 1-150)
// Inspired by StayFocusd / Cold Turkey Blocker
// Pure UI Only - Logic will be added later
// =============================================================================

package com.focusblocker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.selection.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.twotone.*
import androidx.compose.material.icons.sharp.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.runtime.livedata.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.input.*
import androidx.compose.ui.input.nestedscroll.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.input.rotary.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.text.intl.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.util.*
import androidx.compose.ui.viewinterop.*
import androidx.compose.ui.window.*
import androidx.constraintlayout.compose.*
import androidx.core.splashscreen.*
import androidx.lifecycle.*
import androidx.lifecycle.compose.*
import androidx.lifecycle.viewmodel.*
import androidx.lifecycle.viewmodel.compose.*
import androidx.navigation.*
import androidx.navigation.compose.*
import androidx.wear.compose.material.*
import coil.compose.*
import coil.request.*
import com.airbnb.lottie.compose.*
import com.google.accompanist.drawablepainter.*
import com.google.accompanist.flowlayout.*
import com.google.accompanist.insets.*
import com.google.accompanist.insets.ui.*
import com.google.accompanist.navigation.animation.*
import com.google.accompanist.navigation.material.*
import com.google.accompanist.pager.*
import com.google.accompanist.permissions.*
import com.google.accompanist.placeholder.*
import com.google.accompanist.swiperefresh.*
import com.google.accompanist.systemuicontroller.*
import com.google.accompanist.web.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import java.text.*
import java.time.*
import java.time.format.*

// =============================================================================
// MAIN ACTIVITY
// =============================================================================
class MainActivity : ComponentActivity() {
    
    private lateinit var splashScreen: androidx.core.splashscreen.SplashScreen
    
    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        
        splashScreen.setKeepOnScreenCondition { false }
        
        setContent {
            FocusBlockerTheme {
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = !isSystemInDarkTheme()
                
                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = Color.Transparent,
                        darkIcons = useDarkIcons,
                        isNavigationBarContrastEnforced = false
                    )
                }
                
                val windowSizeClass = calculateWindowSizeClass(this)
                val appState = rememberFocusBlockerAppState(windowSizeClass = windowSizeClass)
                
                FocusBlockerApp(
                    appState = appState,
                    windowSizeClass = windowSizeClass
                )
            }
        }
    }
}

// =============================================================================
// APP STATE HOLDER
// =============================================================================
class FocusBlockerAppState(
    val windowSizeClass: WindowSizeClass,
    val navController: NavHostController = rememberNavController()
) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination
    
    val shouldShowBottomBar: Boolean
        @Composable get() = when (currentDestination?.route) {
            Screen.Home.route,
            Screen.Stats.route,
            Screen.Schedule.route,
            Screen.Settings.route -> true
            else -> false
        }
    
    val shouldShowFab: Boolean
        @Composable get() = when (currentDestination?.route) {
            Screen.Home.route,
            Screen.BlockedApps.route -> true
            else -> false
        }
    
    fun navigateToHome() {
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Home.route) { inclusive = true }
            launchSingleTop = true
        }
    }
    
    fun navigateToBlockedApps() {
        navController.navigate(Screen.BlockedApps.route)
    }
    
    fun navigateToStats() {
        navController.navigate(Screen.Stats.route)
    }
    
    fun navigateToSchedule() {
        navController.navigate(Screen.Schedule.route)
    }
    
    fun navigateToSettings() {
        navController.navigate(Screen.Settings.route)
    }
    
    fun navigateToFocusSession() {
        navController.navigate(Screen.FocusSession.route)
    }
    
    fun navigateToAddBlockedApp() {
        navController.navigate(Screen.AddBlockedApp.route)
    }
    
    fun navigateToAppDetails(appId: String) {
        navController.navigate("${Screen.AppDetails.route}/$appId")
    }
    
    fun navigateBack() {
        navController.popBackStack()
    }
}

@Composable
fun rememberFocusBlockerAppState(
    windowSizeClass: WindowSizeClass,
    navController: NavHostController = rememberNavController()
): FocusBlockerAppState {
    return remember(navController, windowSizeClass) {
        FocusBlockerAppState(windowSizeClass, navController)
    }
}

// =============================================================================
// SCREEN DESTINATIONS
// =============================================================================
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Focus", Icons.Rounded.Timer)
    object BlockedApps : Screen("blocked_apps", "Blocked Apps", Icons.Rounded.Block)
    object Stats : Screen("stats", "Statistics", Icons.Rounded.BarChart)
    object Schedule : Screen("schedule", "Schedule", Icons.Rounded.CalendarMonth)
    object Settings : Screen("settings", "Settings", Icons.Rounded.Settings)
    object FocusSession : Screen("focus_session", "Focus Session", Icons.Rounded.PlayArrow)
    object AddBlockedApp : Screen("add_blocked_app", "Add App", Icons.Rounded.Add)
    object AppDetails : Screen("app_details/{appId}", "App Details", Icons.Rounded.Info)
    object StrictMode : Screen("strict_mode", "Strict Mode", Icons.Rounded.Security)
    object Whitelist : Screen("whitelist", "Whitelist", Icons.Rounded.CheckCircle)
    object BlockedWebsites : Screen("blocked_websites", "Blocked Websites", Icons.Rounded.Language)
    object Notifications : Screen("notifications", "Notifications", Icons.Rounded.Notifications)
    object Backup : Screen("backup", "Backup & Restore", Icons.Rounded.Backup)
    object About : Screen("about", "About", Icons.Rounded.Info)
    
    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}

// =============================================================================
// MAIN APP COMPOSABLE
// =============================================================================
@Composable
fun FocusBlockerApp(
    appState: FocusBlockerAppState,
    windowSizeClass: WindowSizeClass
) {
    val navController = appState.navController
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            FocusBlockerTopBar(
                appState = appState,
                windowSizeClass = windowSizeClass
            )
        },
        bottomBar = {
            if (appState.shouldShowBottomBar) {
                FocusBlockerBottomBar(
                    appState = appState,
                    windowSizeClass = windowSizeClass
                )
            }
        },
        floatingActionButton = {
            if (appState.shouldShowFab) {
                FocusBlockerFAB(
                    appState = appState
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToFocusSession = { appState.navigateToFocusSession() },
                    onNavigateToBlockedApps = { appState.navigateToBlockedApps() },
                    onNavigateToStats = { appState.navigateToStats() }
                )
            }
            
            composable(Screen.BlockedApps.route) {
                BlockedAppsScreen(
                    onNavigateBack = { appState.navigateBack() },
                    onNavigateToAddBlockedApp = { appState.navigateToAddBlockedApp() },
                    onNavigateToAppDetails = { appId -> appState.navigateToAppDetails(appId) }
                )
            }
            
            composable(Screen.Stats.route) {
                StatsScreen(
                    onNavigateBack = { appState.navigateBack() }
                )
            }
            
            composable(Screen.Schedule.route) {
                ScheduleScreen(
                    onNavigateBack = { appState.navigateBack() }
                )
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { appState.navigateBack() },
                    onNavigateToStrictMode = { navController.navigate(Screen.StrictMode.route) },
                    onNavigateToWhitelist = { navController.navigate(Screen.Whitelist.route) },
                    onNavigateToBlockedWebsites = { navController.navigate(Screen.BlockedWebsites.route) },
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                    onNavigateToBackup = { navController.navigate(Screen.Backup.route) },
                    onNavigateToAbout = { navController.navigate(Screen.About.route) }
                )
            }
            
            composable(Screen.FocusSession.route) {
                FocusSessionScreen(
                    onNavigateBack = { appState.navigateBack() }
                )
            }
            
            composable(Screen.AddBlockedApp.route) {
                AddBlockedAppScreen(
                    onNavigateBack = { appState.navigateBack() }
                )
            }
            
            composable(
                route = Screen.AppDetails.route,
                arguments = listOf(navArgument("appId") { type = NavType.StringType })
            ) { backStackEntry ->
                val appId = backStackEntry.arguments?.getString("appId") ?: ""
                AppDetailsScreen(
                    appId = appId,
                    onNavigateBack = { appState.navigateBack() }
                )
            }
            
            composable(Screen.StrictMode.route) {
                StrictModeScreen(
                    onNavigateBack = { appState.navigateBack() }
                )
            }
            
            composable(Screen.Whitelist.route) {
                WhitelistScreen(
                    onNavigateBack = { appState.navigateBack() }
                )
            }
            
            composable(Screen.BlockedWebsites.route) {
                BlockedWebsitesScreen(
                    onNavigateBack = { appState.navigateBack() }
                )
            }
            
            composable(Screen.Notifications.route) {
                NotificationsScreen(
                    onNavigateBack = { appState.navigateBack() }
                )
            }
            
            composable(Screen.Backup.route) {
                BackupScreen(
                    onNavigateBack = { appState.navigateBack() }
                )
            }
            
            composable(Screen.About.route) {
                AboutScreen(
                    onNavigateBack = { appState.navigateBack() }
                )
            }
        }
    }
}

// =============================================================================
// TOP BAR
// =============================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusBlockerTopBar(
    appState: FocusBlockerAppState,
    windowSizeClass: WindowSizeClass
) {
    val currentDestination = appState.currentDestination
    val title = when (currentDestination?.route) {
        Screen.Home.route -> Screen.Home.title
        Screen.BlockedApps.route -> Screen.BlockedApps.title
        Screen.Stats.route -> Screen.Stats.title
        Screen.Schedule.route -> Screen.Schedule.title
        Screen.Settings.route -> Screen.Settings.title
        Screen.FocusSession.route -> Screen.FocusSession.title
        Screen.AddBlockedApp.route -> Screen.AddBlockedApp.title
        Screen.StrictMode.route -> Screen.StrictMode.title
        Screen.Whitelist.route -> Screen.Whitelist.title
        Screen.BlockedWebsites.route -> Screen.BlockedWebsites.title
        Screen.Notifications.route -> Screen.Notifications.title
        Screen.Backup.route -> Screen.Backup.title
        Screen.About.route -> Screen.About.title
        else -> "Focus Blocker"
    }
    
    val canNavigateBack = currentDestination?.route !in listOf(
        Screen.Home.route,
        Screen.BlockedApps.route,
        Screen.Stats.route,
        Screen.Schedule.route,
        Screen.Settings.route
    )
    
    val isFocusSessionActive = currentDestination?.route == Screen.FocusSession.route
    
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (!canNavigateBack) {
                    Icon(
                        imageVector = Icons.Rounded.Timer,
                        contentDescription = null,
                        modifier = Modifier
                            .size(28.dp)
                            .padding(end = 8.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = { appState.navigateBack() }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Navigate back"
                    )
                }
            }
        },
        actions = {
            if (!isFocusSessionActive) {
                IconButton(onClick = { /* TODO: Search */ }) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search"
                    )
                }
                IconButton(onClick = { /* TODO: More options */ }) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "More options"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
        ),
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    )
}

// =============================================================================
// BOTTOM NAVIGATION BAR
// =============================================================================
@Composable
fun FocusBlockerBottomBar(
    appState: FocusBlockerAppState,
    windowSizeClass: WindowSizeClass
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
        tonalElevation = 0.dp
    ) {
        val navController = appState.navController
        val currentDestination = appState.currentDestination
        
        val items = listOf(
            Screen.Home,
            Screen.BlockedApps,
            Screen.Stats,
            Screen.Schedule,
            Screen.Settings
        )
        
        items.forEach { screen ->
            val selected = currentDestination?.route == screen.route
            
            NavigationBarItem(
                icon = {
                    BadgedBox(
                        badge = {
                            if (screen == Screen.Home) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                ) {
                                    Text(
                                        text = "3",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = screen.title,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                selected = selected,
                onClick = {
                    when (screen) {
                        Screen.Home -> appState.navigateToHome()
                        Screen.BlockedApps -> appState.navigateToBlockedApps()
                        Screen.Stats -> appState.navigateToStats()
                        Screen.Schedule -> appState.navigateToSchedule()
                        Screen.Settings -> appState.navigateToSettings()
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

// =============================================================================
// FLOATING ACTION BUTTON
// =============================================================================
@Composable
fun FocusBlockerFAB(
    appState: FocusBlockerAppState
) {
    val currentDestination = appState.currentDestination
    
    val (fabIcon, fabAction) = when (currentDestination?.route) {
        Screen.Home.route -> Pair(
            Icons.Rounded.PlayArrow,
            { appState.navigateToFocusSession() }
        )
        Screen.BlockedApps.route -> Pair(
            Icons.Rounded.Add,
            { appState.navigateToAddBlockedApp() }
        )
        else -> Pair(null, {})
    }
    
    if (fabIcon != null) {
        FloatingActionButton(
            onClick = fabAction,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(6.dp)
        ) {
            Icon(
                imageVector = fabIcon,
                contentDescription = "Action",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

// =============================================================================
// THEME
// =============================================================================
@Composable
fun FocusBlockerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme(
            primary = Color(0xFF6750A4),
            onPrimary = Color.White,
            primaryContainer = Color(0xFF4A3780),
            onPrimaryContainer = Color(0xFFEADDFF),
            secondary = Color(0xFF625B71),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFF4A4458),
            onSecondaryContainer = Color(0xFFE8DEF8),
            tertiary = Color(0xFF7D5260),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFF633B48),
            onTertiaryContainer = Color(0xFFFFD8E4),
            error = Color(0xFFF2B8B5),
            onError = Color(0xFF601410),
            errorContainer = Color(0xFF8C1D18),
            onErrorContainer = Color(0xFFF9DEDC),
            background = Color(0xFF1C1B1F),
            onBackground = Color(0xFFE6E1E5),
            surface = Color(0xFF1C1B1F),
            onSurface = Color(0xFFE6E1E5),
            surfaceVariant = Color(0xFF49454F),
            onSurfaceVariant = Color(0xFFCAC4D0),
            outline = Color(0xFF938F99),
            inverseSurface = Color(0xFFE6E1E5),
            inverseOnSurface = Color(0xFF313033),
            inversePrimary = Color(0xFF6750A4),
            surfaceTint = Color(0xFF6750A4)
        )
        else -> lightColorScheme(
            primary = Color(0xFF6750A4),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFEADDFF),
            onPrimaryContainer = Color(0xFF21005E),
            secondary = Color(0xFF625B71),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFE8DEF8),
            onSecondaryContainer = Color(0xFF1E192B),
            tertiary = Color(0xFF7D5260),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFFFD8E4),
            onTertiaryContainer = Color(0xFF31111D),
            error = Color(0xFFBA1A1A),
            onError = Color.White,
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),
            background = Color(0xFFFFFBFF),
            onBackground = Color(0xFF1C1B1F),
            surface = Color(0xFFFFFBFF),
            onSurface = Color(0xFF1C1B1F),
            surfaceVariant = Color(0xFFE7E0EC),
            onSurfaceVariant = Color(0xFF49454F),
            outline = Color(0xFF79747E),
            inverseSurface = Color(0xFF313033),
            inverseOnSurface = Color(0xFFF4EFF4),
            inversePrimary = Color(0xFFD0BCFF),
            surfaceTint = Color(0xFF6750A4)
        )
    }
    
    val typography = Typography(
        displayLarge = MaterialTheme.typography.displayLarge,
        displayMedium = MaterialTheme.typography.displayMedium,
        displaySmall = MaterialTheme.typography.displaySmall,
        headlineLarge = MaterialTheme.typography.headlineLarge,
        headlineMedium = MaterialTheme.typography.headlineMedium,
        headlineSmall = MaterialTheme.typography.headlineSmall,
        titleLarge = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        titleMedium = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
        titleSmall = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
        bodyLarge = MaterialTheme.typography.bodyLarge,
        bodyMedium = MaterialTheme.typography.bodyMedium,
        bodySmall = MaterialTheme.typography.bodySmall,
        labelLarge = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
        labelMedium = MaterialTheme.typography.labelMedium,
        labelSmall = MaterialTheme.typography.labelSmall
    )
    
    val shapes = Shapes(
        extraSmall = RoundedCornerShape(4.dp),
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(12.dp),
        large = RoundedCornerShape(16.dp),
        extraLarge = RoundedCornerShape(28.dp)
    )
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

// =============================================================================
// END OF PART 1 (Lines: ~150)
// Next Part: HomeScreen, Focus Timer Card, Today's Stats Card, App List
// =========================================




// =============================================================================
// FOCUS BLOCKER APP - JETPACK COMPOSE UI (Lines 151-1150)
// PART 2: HomeScreen, Focus Timer, Stats Cards, App Lists
// Pure UI Only - Logic will be added later
// =============================================================================

// =============================================================================
// HOME SCREEN - MAIN DASHBOARD
// =============================================================================
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onNavigateToFocusSession: () -> Unit,
    onNavigateToBlockedApps: () -> Unit,
    onNavigateToStats: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = false,
        onRefresh = { /* TODO: Refresh data */ }
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 80.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Focus Timer Card (Large)
            item {
                FocusTimerCard(
                    onStartFocus = onNavigateToFocusSession
                )
            }
            
            // Today's Stats Overview
            item {
                TodayStatsCard(
                    onViewDetails = onNavigateToStats
                )
            }
            
            // Quick Actions Grid
            item {
                QuickActionsGrid(
                    onStartFocus = onNavigateToFocusSession,
                    onViewBlockedApps = onNavigateToBlockedApps,
                    onViewStats = onNavigateToStats
                )
            }
            
            // Active Focus Session (if any)
            item {
                ActiveSessionCard()
            }
            
            // Daily Streak Card
            item {
                DailyStreakCard()
            }
            
            // Section Header: Most Blocked Apps
            item {
                SectionHeader(
                    title = "Most Blocked Apps",
                    actionText = "See All",
                    onActionClick = onNavigateToBlockedApps
                )
            }
            
            // Most Blocked Apps List
            items(5) { index ->
                MostBlockedAppItem(
                    appName = when(index) {
                        0 -> "Instagram"
                        1 -> "YouTube"
                        2 -> "Facebook"
                        3 -> "TikTok"
                        4 -> "Twitter"
                        else -> "Unknown"
                    },
                    blockCount = when(index) {
                        0 -> 47
                        1 -> 38
                        2 -> 25
                        3 -> 19
                        4 -> 12
                        else -> 0
                    },
                    timeWasted = when(index) {
                        0 -> "2h 30m"
                        1 -> "1h 45m"
                        2 -> "45m"
                        3 -> "30m"
                        4 -> "15m"
                        else -> "0m"
                    },
                    icon = when(index) {
                        0 -> Icons.Rounded.PhotoCamera
                        1 -> Icons.Rounded.SmartDisplay
                        2 -> Icons.Rounded.People
                        3 -> Icons.Rounded.MusicNote
                        4 -> Icons.Rounded.Chat
                        else -> Icons.Rounded.Apps
                    }
                )
            }
            
            // Section Header: Recent Activities
            item {
                SectionHeader(
                    title = "Recent Activities",
                    actionText = "View All",
                    onActionClick = { /* TODO: Navigate to activity log */ }
                )
            }
            
            // Recent Activities List
            items(8) { index ->
                RecentActivityItem(
                    activityType = when(index % 4) {
                        0 -> ActivityType.FOCUS_COMPLETED
                        1 -> ActivityType.APP_BLOCKED
                        2 -> ActivityType.GOAL_ACHIEVED
                        else -> ActivityType.STREAK_UPDATED
                    },
                    title = when(index % 4) {
                        0 -> "Focus session completed"
                        1 -> "Instagram blocked"
