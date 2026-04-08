package com.tanimul.android_template_kotlin.features
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// কালার প্যালেট (ছবির সাথে মিলিয়ে)
val DarkBackground = Color(0xFF1E2428)
val AppBackground = Color(0xFFF0F0F0)
val SectionBlue = Color(0xFF4285F4)
val SectionRed = Color(0xFFFF5252)
val SectionGreen = Color(0xFF4CAF50)
val SwitchGreenON = Color(0xFF4CAF50)

class BlockerHeroViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BlockerHeroUiState())
    val uiState = _uiState.asStateFlow()

    fun updateAdultContent(newValue: Boolean) { _uiState.update { it.copy(blockAdultContent = newValue) } }
    fun updateImageSearch(newValue: Boolean) { _uiState.update { it.copy(blockImageSearch = newValue) } }
    fun updateYoutubeShorts(newValue: Boolean) { _uiState.update { it.copy(blockYoutubeShorts = newValue) } }
    fun updateUninstallProtection(newValue: Boolean) { _uiState.update { it.copy(uninstallProtection = newValue) } }
    fun updatePhoneReboot(newValue: Boolean) { _uiState.update { it.copy(blockPhoneReboot = newValue) } }
    fun updateRecentApps(newValue: Boolean) { _uiState.update { it.copy(blockRecentAppsScreen = newValue) } }
    fun updateUnsupportedBrowsers(newValue: Boolean) { _uiState.update { it.copy(blockUnsupportedBrowsers = newValue) } }
    fun updateNewInstalledApps(newValue: Boolean) { _uiState.update { it.copy(blockNewInstalledApps = newValue) } }
    fun updateNotificationPanel(newValue: Boolean) { _uiState.update { it.copy(blockNotificationPanel = newValue) } }
    fun updateScreenCountdown(newValue: Boolean) { _uiState.update { it.copy(blockedScreenCountdown = newValue) } }
}

data class BlockerHeroUiState(
    val blockAdultContent: Boolean = false,
    val blockImageSearch: Boolean = true, // ইমেজে এটি অন (সবুজ) আছে
    val blockYoutubeShorts: Boolean = false,
    val uninstallProtection: Boolean = false,
    val blockPhoneReboot: Boolean = false,
    val blockRecentAppsScreen: Boolean = false,
    val blockUnsupportedBrowsers: Boolean = false,
    val blockNewInstalledApps: Boolean = false,
    val blockNotificationPanel: Boolean = false,
    val blockedScreenCountdown: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = BlockerHeroViewModel()
        setContent {
            MaterialTheme {
                BlockerHeroApp(viewModel)
            }
        }
    }
}

@Composable
fun BlockerHeroApp(viewModel: BlockerHeroViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { BlockerHeroTopAppBar() },
        bottomBar = { BlockerHeroBottomNavigationBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground) // মেইন ব্যাকগ্রাউন্ড ডার্ক
                .padding(innerPadding)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
            ) {
                BlockingSettingsList(uiState, viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockerHeroTopAppBar() {
    TopAppBar(
        title = { /* টাইটেল নেই ফোনে */ },
        actions = {
            // GUIDE বাটন (লাল আউটলাইন)
            OutlinedButton(
                onClick = { /* GUIDE */ },
                border = androidx.compose.foundation.BorderStroke(1.dp, SectionRed),
                shape = CircleShape,
                modifier = Modifier.height(36.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
            ) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu", tint = SectionRed, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "GUIDE", color = SectionRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            // নোটিফিকেশন বেল উইথ রেড ডট
            BadgedBox(
                badge = {
                    Badge(containerColor = SectionRed, modifier = Modifier.size(8.dp)) {}
                },
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.Gray)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

@Composable
fun BlockerHeroBottomNavigationBar() {
    NavigationBar(containerColor = Color.White, modifier = Modifier.height(60.dp)) {
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(imageVector = Icons.Default.Block, contentDescription = "Blocking", tint = SectionRed) },
            label = { Text("Blocking", color = SectionRed, fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.White)
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(imageVector = Icons.Outlined.ListAlt, contentDescription = "Menu", tint = Color.Gray) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.White)
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { 
                Surface(
                    color = SectionRed,
                    shape = CircleShape,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Schedule, contentDescription = "Focus", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Focus Mo", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) // ইমেজে কিছুটা কাটা পড়েছে
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.White)
        )
    }
}

@Composable
fun BlockingSettingsList(uiState: BlockerHeroUiState, viewModel: BlockerHeroViewModel) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }
        
        item {
            SectionGroupCard("Accountability Partner", SectionBlue, Icons.Default.People) {
                PartnerInfoRow()
            }
        }

        item {
            SectionGroupCard("Content Blocking", SectionRed, Icons.Default.Block) {
                SettingToggleRow("Block Adult content", uiState.blockAdultContent, viewModel::updateAdultContent)
                SettingToggleRow("Block Image/Video search", uiState.blockImageSearch, viewModel::updateImageSearch)
                SettingToggleRow("Block YouTube shorts", uiState.blockYoutubeShorts, viewModel::updateYoutubeShorts)
            }
        }

        item {
            SectionGroupCard("Uninstall Protection", SectionGreen, Icons.Default.Lock) {
                SettingToggleRow("Uninstall & Settings Protection", uiState.uninstallProtection, viewModel::updateUninstallProtection)
                SettingToggleRow("Block Phone Reboot option", uiState.blockPhoneReboot, viewModel::updatePhoneReboot)
                SettingToggleRow("Block Recent Apps Screen", uiState.blockRecentAppsScreen, viewModel::updateRecentApps)
            }
        }

        item {
            SectionGroupCard("Advanced features", SectionBlue, Icons.Default.Settings) {
                SettingToggleRow("Block Unsupported Browsers", uiState.blockUnsupportedBrowsers, viewModel::updateUnsupportedBrowsers)
                SettingToggleRow("Block New Installed Apps", uiState.blockNewInstalledApps, viewModel::updateNewInstalledApps)
                SettingToggleRow("Block Notification panel", uiState.blockNotificationPanel, viewModel::updateNotificationPanel)
                SettingToggleRow("Blocked Screen Countdown", uiState.blockedScreenCountdown, viewModel::updateScreenCountdown)
            }
        }
    }
}

// এই ফাংশনটি দিয়ে ওই বর্ডার এবং ওভারল্যাপ করা পিল ডিজাইনটি করা হয়েছে
@Composable
fun SectionGroupCard(title: String, color: Color, icon: ImageVector, content: @Composable () -> Unit) {
    Box(modifier = Modifier.padding(vertical = 12.dp)) {
        // বর্ডার যুক্ত কন্টেইনার
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp) // পিলের জন্য জায়গা রাখা হয়েছে
                .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(top = 20.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
        ) {
            content()
        }

        // পিল (Pill) যা বর্ডারের উপরে ভাসমান অবস্থায় থাকবে
        Surface(
            color = color,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-4).dp) // পজিশন ঠিক করার জন্য
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }
    }
}

@Composable
fun PartnerInfoRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("My Partner", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
            Text("shaxxxxxxxxxx@gmail.com", fontSize = 12.sp, color = Color.Gray)
        }
        OutlinedButton(
            onClick = { /* রিমুভ লজিক */ },
            border = androidx.compose.foundation.BorderStroke(1.dp, SectionRed.copy(alpha = 0.5f)),
            shape = CircleShape,
            modifier = Modifier.height(32.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            Text("Remove", color = SectionRed, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Icon(imageVector = Icons.Outlined.HelpOutline, contentDescription = "Help", tint = Color.Gray, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun SettingToggleRow(title: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = { onCheckedChange(it) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = SwitchGreenON, // অন থাকলে সবুজ
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray // অফ থাকলে গ্রে
            ),
            modifier = Modifier.scale(0.8f) // সুইচের সাইজ ইমেজের মত ছোট করার জন্য
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Outlined.HelpOutline, // (?) আইকন
            contentDescription = "Help",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}
