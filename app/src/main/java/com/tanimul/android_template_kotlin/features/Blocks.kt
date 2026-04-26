package com.tanimul.android_template_kotlin.features

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================
// C++ Colors Translation
// ==========================================
val SClrTeal = Color(0xFF0CA8B0)
val SClrWhite = Color(0xFFFFFFFF)
val SClrDark = Color(0xFF323232)
val SClrGrayText = Color(0xFF787878)
val SClrBg = Color(0xFFF8FAFC)
val SClrGreen = Color(0xFF5AAA14)
val SClrRed = Color(0xFFE74C3C)

// Data Class for Block List
data class BlockItem(val name: String, val isSystemLocked: Boolean = false)

// ==========================================
// NEW: ১০০% অ্যাকুরেট অ্যাপ লিস্ট ফেচ করার ফাংশন
// ==========================================
data class InstalledApp(val name: String, val packageName: String)

fun getInstalledAppsList(context: Context): List<InstalledApp> {
    val pm = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN, null).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }
    // যেসব অ্যাপের লঞ্চার আইকন আছে, শুধু সেগুলোই আনবে
    val resolveInfoList = pm.queryIntentActivities(intent, 0)
    return resolveInfoList.map {
        InstalledApp(
            name = it.loadLabel(pm).toString(),
            packageName = it.activityInfo.packageName
        )
    }.distinctBy { it.packageName }.sortedBy { it.name } // নাম অনুযায়ী সাজানো
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Blocks() {
    // --- State Variables ---
    var currentBlockTab by remember { mutableIntStateOf(0) }
    var controlMode by remember { mutableIntStateOf(0) } // 0: Self, 1: Friend
    var isFocusActive by remember { mutableStateOf(false) }
    var simpleBlockMode by remember { mutableIntStateOf(1) } // 0: Allow, 1: Block
    
    var showQuotes by remember { mutableStateOf(true) }
    var quoteLanguage by remember { mutableIntStateOf(0) } // 0: Bangla, 1: English
    
    var webInputText by remember { mutableStateOf("") }
    var appInputText by remember { mutableStateOf("") }
    
    // Lists
    val webList = remember { mutableStateListOf<BlockItem>() }
    val appList = remember { mutableStateListOf<BlockItem>() }
    
    // Overlays (Dialog States)
    var showTimeOverlay by remember { mutableStateOf(false) }
    var showPassOverlay by remember { mutableStateOf(false) }
    var showStoreOverlay by remember { mutableStateOf(false) }
    var showTitleOverlay by remember { mutableStateOf(false) }
    
    // Dialog Inputs
    var focusHours by remember { mutableIntStateOf(1) }
    var focusMins by remember { mutableIntStateOf(0) }
    var inputPassText by remember { mutableStateOf("") }
    var inputTitleText by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val context = LocalContext.current // Context for PackageManager

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SClrBg)
    ) {
        // ==========================================
        // 1. TOP TABS (Simple Blocks, Schedule, Device)
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth().background(SClrWhite).padding(10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val tabs = listOf("Simple Blocks", "Schedule Blocks", "Device Block")
            tabs.forEachIndexed { index, title ->
                Button(
                    onClick = { currentBlockTab = index },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentBlockTab == index) SClrTeal else Color(0xFFE6E6E6),
                        contentColor = if (currentBlockTab == index) SClrWhite else SClrDark
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp).height(40.dp)
                ) {
                    Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }
        }

        // ==========================================
        // MAIN CONTENT (If Simple Blocks Selected)
        // ==========================================
        if (currentBlockTab == 0) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(scrollState)) {
                
                // --- Controls Row (Self Control & Start Focus) ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    var expandedControl by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expandedControl, onExpandedChange = { if(!isFocusActive) expandedControl = it }) {
                        OutlinedTextField(
                            value = if (controlMode == 0) "Self Control" else "Friend Control",
                            onValueChange = {}, readOnly = true, enabled = !isFocusActive,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedControl) },
                            modifier = Modifier.menuAnchor().weight(1f).padding(end = 8.dp),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(expanded = expandedControl, onDismissRequest = { expandedControl = false }) {
                            DropdownMenuItem(text = { Text("Self Control") }, onClick = { controlMode = 0; expandedControl = false })
                            DropdownMenuItem(text = { Text("Friend Control") }, onClick = { controlMode = 1; expandedControl = false })
                        }
                    }

                    Button(
                        onClick = {
                            if (isFocusActive) {
                                if (controlMode == 1) showPassOverlay = true else isFocusActive = false
                            } else {
                                if (controlMode == 0) showTimeOverlay = true else showPassOverlay = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isFocusActive) SClrRed else SClrGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(56.dp)
                    ) {
                        Text(if (isFocusActive) "Stop Focus" else "Start Focus", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.LightGray)
                Spacer(modifier = Modifier.height(16.dp))

                // --- Select Mode ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Select Mode:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 8.dp))
                    var expandedMode by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expandedMode, onExpandedChange = { if(!isFocusActive) expandedMode = it }) {
                        OutlinedTextField(
                            value = if (simpleBlockMode == 0) "Allow Apps & Web" else "Block Apps & Web",
                            onValueChange = {}, readOnly = true, enabled = !isFocusActive,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMode) },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(expanded = expandedMode, onDismissRequest = { expandedMode = false }) {
                            DropdownMenuItem(text = { Text("Allow Apps & Web") }, onClick = { simpleBlockMode = 0; expandedMode = false })
                            DropdownMenuItem(text = { Text("Block Apps & Web") }, onClick = { simpleBlockMode = 1; expandedMode = false })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ==========================================
                // WEBSITES SECTION
                // ==========================================
                Text("Websites", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SClrDark)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = webInputText, onValueChange = { webInputText = it },
                        placeholder = { Text("e.g. facebook.com") },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp), enabled = !isFocusActive
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { if (webInputText.isNotEmpty()) { webList.add(BlockItem(webInputText)); webInputText = "" } },
                        colors = ButtonDefaults.buttonColors(containerColor = SClrTeal),
                        shape = RoundedCornerShape(8.dp), modifier = Modifier.height(56.dp), enabled = !isFocusActive
                    ) { Text("+ Add") }
                }
                
                Box(modifier = Modifier.fillMaxWidth().height(150.dp).background(Color.White).border(1.dp, Color.LightGray)) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        webList.forEach { item ->
                            Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(item.name, color = SClrDark)
                                if (!isFocusActive) {
                                    Icon(Icons.Default.Close, contentDescription = "Delete", tint = SClrRed, modifier = Modifier.clickable { webList.remove(item) })
                                }
                            }
                            Divider(color = Color(0xFFEEEEEE))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ==========================================
                // APPLICATIONS SECTION
                // ==========================================
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Applications", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SClrDark)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = showQuotes, onCheckedChange = { showQuotes = it })
                        Text("Quotes", fontSize = 12.sp)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = appInputText, onValueChange = { appInputText = it },
                        placeholder = { Text("e.g. com.whatsapp") },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp), enabled = !isFocusActive
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { if (appInputText.isNotEmpty()) { appList.add(BlockItem(appInputText)); appInputText = "" } },
                        colors = ButtonDefaults.buttonColors(containerColor = SClrTeal),
                        shape = RoundedCornerShape(8.dp), modifier = Modifier.height(56.dp), enabled = !isFocusActive
                    ) { Text("+ Add") }
                }

                Box(modifier = Modifier.fillMaxWidth().height(150.dp).background(Color.White).border(1.dp, Color.LightGray)) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        appList.forEach { item ->
                            Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(item.name, color = SClrDark)
                                if (!isFocusActive && !item.isSystemLocked) {
                                    Icon(Icons.Default.Close, contentDescription = "Delete", tint = SClrRed, modifier = Modifier.clickable { appList.remove(item) })
                                }
                            }
                            Divider(color = Color(0xFFEEEEEE))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = { /* File Picker */ }, colors = ButtonDefaults.buttonColors(SClrGreen), shape = RoundedCornerShape(4.dp), modifier = Modifier.weight(1f).padding(end = 4.dp), enabled = !isFocusActive) { Text("Add app...", fontSize = 10.sp) }
                    Button(onClick = { showStoreOverlay = true }, colors = ButtonDefaults.buttonColors(SClrGreen), shape = RoundedCornerShape(4.dp), modifier = Modifier.weight(1f).padding(horizontal = 2.dp), enabled = !isFocusActive) { Text("Apps List", fontSize = 10.sp) }
                    Button(onClick = { showTitleOverlay = true }, colors = ButtonDefaults.buttonColors(SClrGreen), shape = RoundedCornerShape(4.dp), modifier = Modifier.weight(1f).padding(start = 4.dp), enabled = !isFocusActive) { Text("Add title...", fontSize = 10.sp) }
                }
            }
        }
    }

    // ==========================================
    // OVERLAYS (Dialogs)
    // ==========================================

    if (showTimeOverlay) {
        AlertDialog(
            onDismissRequest = { showTimeOverlay = false },
            title = { Text("SET FOCUS DURATION", fontWeight = FontWeight.Bold) },
            text = { Text("Select hours and minutes for your focus session.") },
            confirmButton = { Button(onClick = { isFocusActive = true; showTimeOverlay = false }, colors = ButtonDefaults.buttonColors(SClrTeal)) { Text("Start Focus") } },
            dismissButton = { TextButton(onClick = { showTimeOverlay = false }) { Text("Cancel", color = SClrDark) } }
        )
    }

    if (showPassOverlay) {
        AlertDialog(
            onDismissRequest = { showPassOverlay = false },
            title = { Text(if (isFocusActive) "ENTER PASSWORD TO STOP" else "ENTER FRIEND'S PASSWORD", fontWeight = FontWeight.Bold) },
            text = { OutlinedTextField(value = inputPassText, onValueChange = { inputPassText = it }, placeholder = { Text("Password...") }, singleLine = true) },
            confirmButton = { Button(onClick = { isFocusActive = !isFocusActive; showPassOverlay = false; inputPassText = "" }, colors = ButtonDefaults.buttonColors(SClrTeal)) { Text("Confirm") } },
            dismissButton = { TextButton(onClick = { showPassOverlay = false }) { Text("Cancel", color = SClrDark) } }
        )
    }

    if (showTitleOverlay) {
        AlertDialog(
            onDismissRequest = { showTitleOverlay = false },
            title = { Text("ENTER WINDOW/KEYWORD", fontWeight = FontWeight.Bold) },
            text = { OutlinedTextField(value = inputTitleText, onValueChange = { inputTitleText = it }, placeholder = { Text("e.g. YouTube Shorts") }, singleLine = true) },
            confirmButton = { Button(onClick = { if(inputTitleText.isNotEmpty()) { appList.add(BlockItem("$inputTitleText (Keyword)")); inputTitleText = ""; showTitleOverlay = false } }, colors = ButtonDefaults.buttonColors(SClrTeal)) { Text("Add Title") } },
            dismissButton = { TextButton(onClick = { showTitleOverlay = false }) { Text("Cancel", color = SClrDark) } }
        )
    }

    // ==========================================
    // NEW: REAL APP LIST DIALOG (100% Accurate)
    // ==========================================
    if (showStoreOverlay) {
        // অ্যাপ লিস্ট একবার লোড হবে
        val installedApps = remember { getInstalledAppsList(context) }
        
        AlertDialog(
            onDismissRequest = { showStoreOverlay = false },
            title = { Text("SELECT APPS TO BLOCK", fontWeight = FontWeight.Bold) },
            text = {
                // LazyColumn ব্যবহার করা হয়েছে যাতে ২০০+ অ্যাপ থাকলেও ফোন ল্যাগ না করে
                LazyColumn(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                    items(installedApps) { app ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(app.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SClrDark) // অ্যাপের নাম (যেমন: Facebook)
                                Text(app.packageName, fontSize = 10.sp, color = Color.Gray) // প্যাকেজ নেম (যেমন: com.facebook.katana)
                            }
                            Button(
                                onClick = { 
                                    // যদি লিস্টে আগে থেকে না থাকে তবেই অ্যাড হবে
                                    val isAlreadyAdded = appList.any { it.name == app.packageName }
                                    if(!isAlreadyAdded) {
                                        appList.add(BlockItem(app.packageName, true)) 
                                    }
                                }, 
                                colors = ButtonDefaults.buttonColors(SClrGreen), 
                                modifier = Modifier.height(30.dp)
                            ) { 
                                Text("Add", fontSize = 10.sp) 
                            }
                        }
                        Divider(color = Color(0xFFEEEEEE))
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showStoreOverlay = false }) { Text("Close", color = SClrDark) } }
        )
    }
}
