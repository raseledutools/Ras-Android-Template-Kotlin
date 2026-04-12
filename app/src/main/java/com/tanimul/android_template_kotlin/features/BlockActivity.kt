package com.tanimul.android_template_kotlin.features

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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

        // পাহারাদার (Service) থেকে পাঠানো ডেটা রিসিভ করা
        val reason = intent.getStringExtra("BLOCK_REASON") ?: "FOCUS MODE ACTIVE"
        val hadithText = intent.getStringExtra("HADITH_TEXT") // সার্ভিস থেকে পাঠানো হাদিস

        setContent {
            MaterialTheme {
                // ব্যাক বাটন ব্লক করা (যাতে ইউজার ব্যাক করে পালাতে না পারে)
                BackHandler(enabled = true) { }
                BlockScreen(reason, hadithText)
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
fun BlockScreen(reason: String, passedHadith: String?) {
    // আপনার পছন্দমতো ইসলামিক এবং টাইম কোটস (C++ লজিক অনুযায়ী)
    val islamicQuotes = listOf(
        "\"মুমিনদের বলুন, তারা যেন তাদের দৃষ্টি নত রাখে এবং তাদের যৌনাঙ্গর হেফাযত করে।\"\n- (সূরা আন-নূর: ৩০)",
        "\"লজ্জাশীলতা কল্যাণ ছাড়া আর কিছুই বয়ে আনে না।\"\n- (সহীহ বুখারী)",
        "\"চোখের যিনা হলো (হারাম জিনিসের দিকে) তাকানো।\"\n- (মুসলিম)"
    )
    val timeQuotes = listOf(
        "\"যারা সময়কে মূল্যায়ন করে না, সময়ও তাদেরকে মূল্যায়ন করে না।\"\n- এ.পি.জে. আবদুল কালাম",
        "\"সফলতা কোনো ম্যাজিক নয়, এটি হলো ফোকাস এবং পরিশ্রমের ফল।\"",
        "\"সময়ের সঠিক ব্যবহারই জীবনকে সুন্দর করে।\""
    )

    // ব্লকের কারণ অনুযায়ী স্ক্রিনের রঙ, আইকন এবং কোটেশন পরিবর্তন
    // আমরা চেক করছি রিজনটা কি অ্যাডাল্ট/খারাপ কি-ওয়ার্ড রিলেটেড নাকি নরমাল অ্যাপ ব্লক
    val isAdultOrBadWord = reason.contains("খারাপ") || reason.contains("ADULT") || reason.contains("অশ্লীল")
    val isSecurity = reason.contains("SECURITY")
    val isNewApp = reason.contains("NEW_APP")

    val state = when {
        isAdultOrBadWord -> BlockState(
            icon = Icons.Default.Warning,
            title = "CONTENT RESTRICTED",
            color = Color(0xFFEF4444), // লাল (সতর্কবার্তা)
            quote = passedHadith ?: islamicQuotes[Random.nextInt(islamicQuotes.size)] // সার্ভিস থেকে হাদিস না এলে রেন্ডম
        )
        isSecurity -> BlockState(
            icon = Icons.Default.Security,
            title = "SECURITY PROTECTION",
            color = Color(0xFFF59E0B), // অরেঞ্জ
            quote = passedHadith ?: "Uninstalling or bypassing protection is active for the selected duration."
        )
        isNewApp -> BlockState(
            icon = Icons.Default.Lock,
            title = "INSTALLATION BLOCKED",
            color = Color(0xFF3B82F6), // নীল
            quote = passedHadith ?: "New app installation is currently restricted in Focus Mode."
        )
        else -> BlockState(
            icon = Icons.Default.Lock,
            title = "FOCUS MODE ACTIVE",
            color = Color(0xFF15AABF), // Rasfocus থিম কালার
            quote = passedHadith ?: timeQuotes[Random.nextInt(timeQuotes.size)]
        )
    }

    // হোয়াইট এবং ক্লিন প্রফেশনাল UI
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // আইকনের বৃত্তাকার ব্যাকগ্রাউন্ড
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

            Spacer(modifier = Modifier.height(8.dp))

            // সার্ভিস থেকে যে বাংলা মেসেজটা পাঠানো হয়েছে (যেমন: "খারাপ শব্দ টাইপ করা নিষেধ!")
            Text(
                text = reason,
                color = state.color,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // উক্তি দেখানোর জন্য বর্ডার দেওয়া কার্ড
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(2.dp, state.color),
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = state.quote,
                    color = Color(0xFF334155),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(28.dp),
                    lineHeight = 28.sp
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Keep focusing on your goal.",
                color = Color(0xFF64748B),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}
