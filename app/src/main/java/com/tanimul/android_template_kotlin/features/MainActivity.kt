package com.tanimul.android_template_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import com.tanimul.android_template_kotlin.features.BlockerHeroApp
import com.tanimul.android_template_kotlin.features.BlockerHeroViewModel

class MainActivity : ComponentActivity() {
    
    // ভিউমডেল কল করা হচ্ছে
    private val viewModel: BlockerHeroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // আপনার UI স্ক্রিনকে কল করা হচ্ছে
                BlockerHeroApp(viewModel = viewModel)
            }
        }
    }
}
