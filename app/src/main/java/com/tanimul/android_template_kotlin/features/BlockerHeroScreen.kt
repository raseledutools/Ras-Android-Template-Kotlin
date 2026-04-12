import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Security
import kotlinx.coroutines.delay

// কালার প্যালেট (ইউনিক এবং প্রিমিয়াম থিম)
val PrimaryColor = Color(0xFF1E3A8A) // রয়্যাল ব্লু
val SecondaryColor = Color(0xFFE0F2FE) // হালকা লু
val TextColorDark = Color(0xFF111827)
val TextColorLight = Color(0xFF6B7280)

@Composable
fun BlockerHeroScreen() {
    // স্ক্রিন ম্যানেজ করার জন্য স্টেট
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Splash) }

    // স্ল্যাশ স্ক্রিনের টাইমার লজিক
    LaunchedEffect(key1 = currentScreen) {
        if (currentScreen is AppScreen.Splash) {
            delay(2500) // ২.৫ সেকেন্ডের স্ল্যাশ স্ক্রিন
            // সেটআপ চেক লজিক পরে অ্যাড হবে
            currentScreen = AppScreen.DataHandling
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            is AppScreen.Splash -> SplashScreenView()
            is AppScreen.DataHandling -> DataHandlingScreenView { currentScreen = AppScreen.UsagePermission }
            is AppScreen.UsagePermission -> UsagePermissionScreenView { currentScreen = AppScreen.AccessibilityPermission }
            is AppScreen.AccessibilityPermission -> AccessibilityPermissionScreenView { currentScreen = AppScreen.Home }
            is AppScreen.Home -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { 
                Text("Home Screen (২য় অংশের জন্য অপেক্ষা করুন)", color = PrimaryColor, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// স্ক্রিন টাইপ ডিফাইন করা
sealed class AppScreen {
    object Splash : AppScreen()
    object DataHandling : AppScreen()
    object UsagePermission : AppScreen()
    object AccessibilityPermission : AppScreen()
    object Home : AppScreen()
}

// ================= স্ল্যাশ স্ক্রিন ডিজাইন =================
@Composable
fun SplashScreenView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // আপনার অ্যাপের লোগো এখানে বসবে
            Icon(
                Icons.Default.Security,
                contentDescription = null,
                tint = PrimaryColor,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "RasFocus Pro",
                color = PrimaryColor,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ================= ১. ডাটা হ্যান্ডলিং স্ক্রিন ডিজাইন =================
// image_1.png-এর মতো লেআউট, কিন্তু আপনার নির্দেশমতো কন্টেন্ট দিয়ে ইউনিক
@Composable
fun DataHandlingScreenView(onAccept: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ইউনিক ইলাস্ট্রেশন/আইকন
        Box(
            modifier = Modifier
                .size(220.dp)
                .background(SecondaryColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Handshake,
                contentDescription = null,
                tint = PrimaryColor,
                modifier = Modifier.size(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // ইউনিক টাইটেল
        Text(
            text = "আমাদের ডাটা নিরাপত্তা অঙ্গীকার",
            color = TextColorDark,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // আপনার নির্দেশমতো বাংলা ডিসক্লোজার কন্টেন্ট
        Text(
            text = "RasFocus Pro আপনার প্রাইভেসির পূর্ণ সম্মান করে। আমরা শুধুমাত্র আপনার অভিজ্ঞতা উন্নত করতে এবং আমাদের ব্লকিং সার্ভিস কার্যকর রাখতে কিছু সীমিত তথ্য সংগ্রহ করি। এর মধ্যে রয়েছে আপনার ফোনের ধরণ, ব্যবহৃত অ্যাপের প্যাকেজ নেম এবং ব্যবহারের সময়।\n\nআমরা আপনার কোনো ব্যক্তিগত তথ্য যেমন: মেসেজ, পাসওয়ার্ড বা কল রেকর্ডিং সংগ্রহ করি না। এটি শুধুমাত্র অ্যাপ ব্লক করার স্ট্যাটিস্টিক্স তৈরির জন্য ব্যবহার করা হয়।",
            color = TextColorLight,
            fontSize = 16.sp,
            lineHeight = 26.sp,
            textAlign = TextAlign.Justify,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))

        // আই একসেপ্ট বাটন (ইউনিক ডিজাইন)
        Button(
            onClick = onAccept,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
        ) {
            Text(
                text = "আমি রাজি (I ACCEPT)",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // অপ্ট-আউট লিংক বাটন
        TextButton(
            onClick = { /* অ্যাপ বন্ধ করার লজিক বা হোম এ যাওয়ার লজিক */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "বাতিল করুন (Opt-out)",
                color = PrimaryColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ================= ২. দাপ্তরিক (Usage) পারমিশন স্ক্রিন =================
// image_2.png-এর মতো কিন্তু আরও পরিষ্কার ডিজাইন
@Composable
fun UsagePermissionScreenView(onProceed: () -> Unit) {
    PermissionSetupLayout(
        title = "অ্যাপ ব্যবহারের তথ্য অনুমতি",
        description = "সঠিকভাবে অ্যাপ ব্লক করতে এবং আপনার ফোকাস টাইম ট্র্যাক করতে, আমাদের অ্যাপ ব্যবহারের পরিসংখ্যান জানার অনুমতি দরকার। অনুগ্রহ করে নিচের বাটনটি চেপে অনুমতি দিন।",
        icon = Icons.Default.LockOpen,
        onProceed = onProceed,
        buttonText = "অনুমতি দিন (Enable Now)"
    )
}

// ================= ৩. অ্যাক্সেসিবিলিটি পারমিশন স্ক্রিন =================
@Composable
fun AccessibilityPermissionScreenView(onProceed: () -> Unit) {
    PermissionSetupLayout(
        title = "কি-ওয়ার্ড ব্লকিং অনুমতি",
        description = "আপনার দেওয়া খারাপ কি-ওয়ার্ড ডিটেক্ট করতে এবং ফেসবুক রিলস ব্লক করতে আমাদের অ্যাক্সেসিবিলিটি সার্ভিসের অনুমতি দরকার। অনুগ্রহ করে নিচের বাটনটি চেপে অনুমতি দিন।",
        icon = Icons.Default.Security,
        onProceed = onProceed,
        buttonText = "অনুমতি দিন (Enable Now)"
    )
}

// কমন পারমিশন সেটআপ লেআউট (ইউনিক এবং ক্লিন ডিজাইন)
@Composable
fun PermissionSetupLayout(
    title: String,
    description: String,
    icon: ImageVector,
    onProceed: () -> Unit,
    buttonText: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SecondaryColor.copy(alpha = 0.3f))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // আইকন এবং কার্ড ডিজাইন (ক্লিন এবং ইউনিক)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // পারমিশন আইকন
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(SecondaryColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(60.dp))
                }

                Spacer(modifier = Modifier.height(30.dp))

                // পারমিশন টাইটেল
                Text(
                    text = title,
                    color = TextColorDark,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                    
                )

                Spacer(modifier = Modifier.height(20.dp))

                // পারমিশন বর্ণনা
                Text(
                    text = description,
                    color = TextColorLight,
                    fontSize = 16.sp,
                    lineHeight = 26.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // প্রোসিড বাটন
        Button(
            onClick = onProceed,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
        ) {
            Text(
                text = buttonText,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

// ================= ৪. হোম স্ক্রিন ডিজাইন (মূল ড্যাশবোর্ড) =================

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenView() {
    // Scaffold ব্যবহার করা হয়েছে যাতে নিচে বটম নেভিগেশন বারটি সুন্দরভাবে সেট হয়
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

            // ২. কুইক অ্যাকশন সেকশন (অ্যানালিটিক্স ছাড়া)
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Quick Actions",
                    color = TextColorLight,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // অ্যাকশন কার্ডগুলো
                QuickActionClickableCard(
                    icon = Icons.Default.Block,
                    title = "0",
                    subtitle = "Apps Blocked",
                    onClick = { /* অ্যাপ ব্লক লিস্টে যাবে */ }
                )
                
                Spacer(modifier = Modifier.height(10.dp))
                
                QuickActionClickableCard(
                    icon = Icons.Default.Language,
                    title = "0",
                    subtitle = "Sites Blocked",
                    onClick = { /* সাইট ব্লক লিস্টে যাবে */ }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // টগল কার্ডগুলো (যেগুলো অন/অফ করা যায়)
                QuickActionToggleCard(
                    icon = Icons.Default.Spellcheck,
                    title = "Keywords Blocked",
                    isToggled = false, // ViewModel থেকে আসবে
                    onToggle = { /* টগল লজিক */ },
                    onConfigure = { /* কনফিগার পেজে যাবে */ }
                )

                Spacer(modifier = Modifier.height(10.dp))

                QuickActionToggleCard(
                    icon = Icons.Default.Warning,
                    title = "Block Adult Content",
                    isToggled = true, // ViewModel থেকে আসবে
                    onToggle = { /* টগল লজিক */ },
                    onConfigure = { /* কনফিগার পেজে যাবে */ }
                )

                Spacer(modifier = Modifier.height(10.dp))

                QuickActionToggleCard(
                    icon = Icons.Default.SmartDisplay,
                    title = "Block Reels/Shorts",
                    isToggled = false, // ViewModel থেকে আসবে
                    onToggle = { /* টগল লজিক */ },
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
    // বর্তমান সিলেক্টেড আইটেম ট্র্যাক করার জন্য (পরে ন্যাভিগেশন যুক্ত হবে)
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
