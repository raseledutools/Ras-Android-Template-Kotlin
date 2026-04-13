package com.tanimul.android_template_kotlin.features

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun BlockerHeroScreen(viewModel: BlockerHeroViewModel) {
    val context = LocalContext.current
    val isAccGranted by viewModel.isAccessibilityGranted.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkPermissions(context)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("অ্যাডাল্ট ব্লকার ও প্রোডাক্টিভিটি টুল", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(20.dp))

        if (!isAccGranted) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("অ্যাপটি চালাতে Accessibility পারমিশন আবশ্যক!")
                    Button(onClick = {
                        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    }) {
                        Text("Accessibility পারমিশন দিন")
                    }
                }
            }
        } else {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("সকল পারমিশন দেওয়া আছে। অ্যাপটি সুরক্ষিত।")
                    Button(onClick = {
                        context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
                    }) {
                        Text("Battery Optimization বন্ধ করুন")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Block List UI Example
        Text("Block List (Apps/Websites)", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Enter App Package or URL") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = { /* Add to list logic */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Add to Block List")
        }
    }
}
