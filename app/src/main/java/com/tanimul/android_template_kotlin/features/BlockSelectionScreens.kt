package com.tanimul.android_template_kotlin.features

// ================= সব ইম্পোর্ট একসাথে (ফাইলের শুরুতে) =================
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

// ================= কালার প্যালেট (ইউনিক এবং প্রিমিয়াম থিম) =================
val PrimaryColor = Color(0xFF1E3A8A) // রয়্যাল ব্লু
val SecondaryColor = Color(0xFFE0F2FE) // হালকা ব্লু
val TextColorDark = Color(0xFF111827)
val TextColorLight = Color(0xFF6B7280)

// ================= ৪. হোম স্ক্রিন ডিজাইন (মূল ড্যাশবোর্ড) =================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenView(viewModel: BlockerHeroViewModel, navController: NavController) {
    // ViewModel থেকে লাইভ ডাটা পড়ার জন্য
    val uiState by viewModel.uiState.collectAsState()

    // Scaffold ব্যবহার করা হয়েছে যাতে নিচে বটম নেভিগেশন বারটি সুন্দরভাবে সেট হয়
    Scaffold(
        bottomBar = { HomeBottomNavigationBar() },
        containerColor = Color(0xFFF3F6FA) // হালকা গ্রে-ব্লু ব্যাকগ্রাউন্ড
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // ১. হেডার সেকশন (৩ লাইনের ডিজাইন)
            HomeHeaderSection()

            Spacer(modifier = Modifier.height(20.dp))

            // ২. মেইন কন্টেন্ট
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                
                // --- Take a Break Card ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("take_a_break") },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD8B4E2))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Coffee, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Take a Break", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Quick Actions",
                    color = TextColorLight,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // অ্যাকশন কার্ডগুলো (ডাটাবেস থেকে সাইজ দেখাবে)
                QuickActionClickableCard(
                    icon = Icons.Default.Block,
                    title = uiState.blockList.size.toString(),
                    subtitle = "Apps Blocked",
                    onClick = { navController.navigate("app_whitelist") }
                )
                
                Spacer(modifier = Modifier.height(10.dp))
                
                QuickActionClickableCard(
                    icon = Icons.Default.Language,
                    title = "Sites", // ওয়েবসাইটের কাউন্ট বা সাইট লেখা থাকবে
                    subtitle = "Sites Blocked",
                    onClick = { navController.navigate("site_whitelist") }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // টগল কার্ডগুলো (যেগুলো অন/অফ করা যায়)
                QuickActionToggleCard(
                    icon = Icons.Default.Spellcheck,
                    title = "Keywords Blocked",
                    isToggled = uiState.blockKeywords,
                    onToggle = { viewModel.toggleKeywords(it) },
                    onConfigure = { /* কনফিগার পেজে যাবে */ }
                )

                Spacer(modifier = Modifier.height(10.dp))

                QuickActionToggleCard(
                    icon = Icons.Default.Warning,
                    title = "Block Adult Content",
                    isToggled = uiState.blockAdultContent, 
                    onToggle = { viewModel.toggleAdultContent(it) },
                    onConfigure = { /* কনফিগার পেজে যাবে */ }
                )

                Spacer(modifier = Modifier.height(10.dp))

                QuickActionToggleCard(
                    icon = Icons.Default.SmartDisplay,
                    title = "Block Reels/Shorts",
                    isToggled = uiState.blockYoutubeShorts, 
                    onToggle = { 
                        viewModel.toggleYoutubeShorts(it)
                        viewModel.toggleFacebookReels(it)
                    },
                    onConfigure = { /* কনফিগার পেজে যাবে */ }
                )
                
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

// ================= সাব-কম্পোনেন্ট ডিজাইন =================

// হেডার ডিজাইন (ইউনিক ব্লু কার্ভ)
@Composable
fun HomeHeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(PrimaryColor)
            .padding(top = 40.dp, bottom = 30.dp, start = 20.dp, end = 20.dp)
    ) {
        Column {
            // টপ বার (মেনু এবং নোটিফিকেশন)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { /* মেনু ওপেন */ },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
                }
                
                IconButton(
                    onClick = { /* নোটিফিকেশন */ },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // গ্রিটিং টেক্সট
            Text(text = "Welcome", color = SecondaryColor, fontSize = 16.sp)
            Text(
                text = "Good Evening", 
                color = Color.White, 
                fontSize = 28.sp, 
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ক্লিকেবল কার্ড (Apps Blocked, Sites Blocked এর জন্য)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionClickableCard(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(45.dp).background(SecondaryColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = PrimaryColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextColorDark)
                Text(text = subtitle, fontSize = 14.sp, color = TextColorLight)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

// টগল কার্ড (Keywords, Adult Content এর জন্য)
@Composable
fun QuickActionToggleCard(
    icon: ImageVector, 
    title: String, 
    isToggled: Boolean, 
    onToggle: (Boolean) -> Unit, 
    onConfigure: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(45.dp).background(SecondaryColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = PrimaryColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = if(isToggled) "On" else "Off", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextColorDark)
                Text(text = title, fontSize = 14.sp, color = TextColorDark)
                Spacer(modifier = Modifier.height(4.dp))
                TextButton(
                    onClick = onConfigure, 
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(24.dp)
                ) {
                    Text("Configure ->", color = PrimaryColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Switch(
                checked = isToggled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryColor,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }
    }
}

// বটম নেভিগেশন (৪টি বাটন)
@Composable
fun HomeBottomNavigationBar() {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Dashboard", "Modes", "Analytics", "Account")
    val icons = listOf(Icons.Default.Dashboard, Icons.Default.ElectricBolt, Icons.Default.Analytics, Icons.Default.Person)

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item, fontSize = 10.sp, fontWeight = if (selectedItem == index) FontWeight.Bold else FontWeight.Normal) },
                selected = selectedItem == index,
                onClick = { selectedItem = index },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryColor,
                    selectedTextColor = PrimaryColor,
                    indicatorColor = SecondaryColor,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

// ================= অন্যান্য অপ্রয়োজনীয় স্ক্রিন (ভবিষ্যতের জন্য রাখা হলো) =================
@Composable
fun DataHandlingScreenView(onAccept: () -> Unit) { /* MainActivity এখন এটা হ্যান্ডেল করে */ }
@Composable
fun UsagePermissionScreenView(onProceed: () -> Unit) { /* MainActivity এখন এটা হ্যান্ডেল করে */ }
@Composable
fun AccessibilityPermissionScreenView(onProceed: () -> Unit) { /* MainActivity এখন এটা হ্যান্ডেল করে */ }
@Composable
fun PermissionSetupLayout(title: String, description: String, icon: ImageVector, onProceed: () -> Unit, buttonText: String) {}
