package com.tanimul.android_template_kotlin.features

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

class BlockActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // পাহারাদার (Service) থেকে কোন কারণে ব্লক হয়েছে, সেটা রিসিভ করা
        val reason = intent.getStringExtra("BLOCK_REASON") ?: "LOCKED"

        setContent {
            MaterialTheme {
                BlockScreen(reason)
            }
        }
    }
}

// ব্লক স্ক্রিনের স্টেট রাখার জন্য ডেটা ক্লাস
data class BlockState(
    val icon: ImageVector,
    val title: String,
    val color: Color,
    val quote: String
)

@Composable
fun BlockScreen(reason: String) {
    // C++ থেকে আনা ইসলামিক এবং টাইম কোটস
    val islamicQuotes = listOf(
        "\"মুমিনদের বলুন, তারা যেন তাদের দৃষ্টি নত রাখে এবং তাদের যৌনাঙ্গর হেফাযত করে।\"\n- (সূরা আন-নূর: ৩০)",
        "\"লজ্জাশীলতা কল্যাণ ছাড়া আর কিছুই বয়ে আনে না।\"\n- (সহীহ বুখারী)"
    )
    val timeQuotes = listOf(
        "\"যারা সময়কে মূল্যায়ন করে না, সময়ও তাদেরকে মূল্যায়ন করে না।\"\n- এ.পি.জে. আবদুল কালাম",
        "\"সফলতা কোনো ম্যাজিক নয়, এটি হলো ফোকাস এবং পরিশ্রমের ফল।\""
    )

    // ব্লকের কারণ অনুযায়ী স্ক্রিনের রঙ, আইকন এবং কোটেশন পরিবর্তন হবে
    val state = when (reason) {
        "ADULT" -> BlockState(
            icon = Icons.Default.Warning,
            title = "RESTRICTED CONTENT",
            color = Color(0xFFEF4444), // লাল
            quote = islamicQuotes[Random.nextInt(islamicQuotes.size)]
        )
        "SECURITY" -> BlockState(
            icon = Icons.Default.Security,
            title = "SECURITY ALERT",
            color = Color(0xFFF59E0B), // হলুদ/অরেঞ্জ
            quote = "Uninstalling or bypassing is not allowed while focus mode is active."
        )
        "NEW_APP" -> BlockState(
            icon = Icons.Default.Lock,
            title = "INSTALLATION BLOCKED",
            color = Color(0xFF3B82F6), // নীল
            quote = "You cannot install or open new apps during focus time."
        )
        else -> BlockState( // "LOCKED" বা রেগুলার ব্লকের জন্য
            icon = Icons.Default.Lock,
            title = "FOCUS MODE ACTIVE",
            color = Color(0xFF15AABF), // Rasfocus থিম কালার (টিয়াল)
            quote = timeQuotes[Random.nextInt(timeQuotes.size)]
        )
    }

    // আপনার রিকোয়ারমেন্ট অনুযায়ী একদম ক্লিন এবং হোয়াইট ব্যাকগ্রাউন্ড UI
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC) // হালকা গ্রে-সাদা ব্যাকগ্রাউন্ড
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // আইকনের সুন্দর একটি বৃত্তাকার ব্যাকগ্রাউন্ড
            Surface(
                shape = CircleShape,
                color = state.color.copy(alpha = 0.1f),
                modifier = Modifier.size(120.dp)
            ) {
                Icon(
                    imageVector = state.icon,
                    contentDescription = "Block Icon",
                    tint = state.color,
                    modifier = Modifier.padding(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = state.title,
                color = Color(0xFF1E293B),
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // উক্তি বা মেসেজ দেখানোর জন্য সুন্দর বর্ডার দেওয়া কার্ড
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(2.dp, state.color),
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = state.quote,
                    color = Color(0xFF334155),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp),
                    lineHeight = 28.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Go back to your work. Focus on what matters.",
                color = Color(0xFF64748B),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
