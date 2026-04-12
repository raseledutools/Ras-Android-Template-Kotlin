import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ================= ১. All Apps Selection Screen (Image 2) =================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectionScreen(onBackClick: () -> Unit) {
    // ডামি অ্যাপ লিস্ট (পরে আপনার রিয়েল প্যাকেজ ম্যানেজার থেকে আসবে)
    val appList = remember { 
        mutableStateListOf(
            AppItem("AHA Games", false),
            AppItem("AI Gallery", false),
            AppItem("BlockerHero", false),
            AppItem("Calculator", false),
            AppItem("Facebook", false),
            AppItem("YouTube", false)
        ) 
    }
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF3F6FA))) {
        // হেডার
        TopAppBar(
            title = { Text("All Apps", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
            navigationIcon = {
                IconButton(onClick = onBackClick, modifier = Modifier.background(Color.White, CircleShape)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // সার্চ বার ও ফিল্টার
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color.Transparent),
                modifier = Modifier.weight(1f).height(55.dp)
            )
            Button(
                onClick = { /* Sort Logic */ },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                modifier = Modifier.height(55.dp)
            ) {
                Text("App Name")
                Icon(Icons.Default.UnfoldMore, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // অ্যাপ লিস্ট কার্ড
        Card(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).padding(bottom = 20.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(appList.size) { index ->
                    val app = appList[index]
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // আইকন প্লেসহোল্ডার
                            Box(modifier = Modifier.size(45.dp).background(Color.LightGray, CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Android, tint = Color.DarkGray, contentDescription = null)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(app.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                                Text("Spent: 00 sec | 0 launches", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                        
                        // লক/আনলক বাটন লজিক
                        IconButton(
                            onClick = { 
                                // এখানে টগল হবে এবং রিয়েল টাইমে ডাটাবেসে সেভ হবে
                                appList[index] = app.copy(isLocked = !app.isLocked) 
                                // TODO: Update ViewModel/Database to start blocking immediately
                            }
                        ) {
                            Icon(
                                imageVector = if (app.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = "Lock Status",
                                tint = if (app.isLocked) Color(0xFF3B82F6) else Color.Gray // লক হলে ব্লু কালার
                            )
                        }
                    }
                    if (index < appList.size - 1) {
                        Divider(color = Color(0xFFF3F6FA), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}

// ================= ২. All Sites Selection Screen (Image 3) =================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteSelectionScreen(onBackClick: () -> Unit) {
    val siteList = remember { 
        mutableStateListOf(
            AppItem("facebook.com", false),
            AppItem("google.com", false),
            AppItem("instagram.com", false),
            AppItem("reddit.com", false)
        ) 
    }
    var searchQuery by remember { mutableStateOf("") }

    // হুবহু AppSelectionScreen এর মতই লেআউট, শুধু টেক্সটগুলো "Website" হবে।
    // (কোড ছোট রাখার জন্য আমি AppSelectionScreen এর মতই স্ট্রাকচার ব্যবহার করতে বলছি। আপনি চাইলে কপি করে শুধু নাম পরিবর্তন করে নিতে পারেন।)
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF3F6FA))) {
        TopAppBar(
            title = { Text("All Sites", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
            navigationIcon = {
                IconButton(onClick = onBackClick, modifier = Modifier.background(Color.White, CircleShape)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
        // ... (বাকি সার্চ বার এবং লিস্ট হুবহু AppSelectionScreen এর মত হবে)
    }
}

// ডাটা ক্লাস
data class AppItem(val name: String, val isLocked: Boolean)
