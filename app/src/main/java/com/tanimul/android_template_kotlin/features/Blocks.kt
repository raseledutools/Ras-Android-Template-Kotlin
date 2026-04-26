package com.tanimul.android_template_kotlin.features

import android.content.Context
import android.content.Intent
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
import com.tanimul.android_template_kotlin.DataManager

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

// ==========================================
// Real-time App List Function
// ==========================================
data class InstalledApp(val name: String, val packageName: String)

fun getInstalledAppsList(context: Context): List<InstalledApp> {
    val pm = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN, null).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }
    val resolveInfoList = pm.queryIntentActivities(intent, 0)
    return resolveInfoList.map {
        InstalledApp(
            name = it.loadLabel(pm).toString(),
            packageName = it.activityInfo.packageName
        )
    }.distinctBy { it.packageName }.sortedBy { it.name }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Blocks() {
    val context = LocalContext.current
    
    // --- DataManager Sync States ---
    var isFocusActive by remember { mutableStateOf(DataManager.isFocusActive) }
    var controlMode by remember { mutableIntStateOf(DataManager.controlMode) }
    var simpleBlockMode by remember { mutableIntStateOf(DataManager.simpleBlockMode) }
    var showQuotes by remember { mutableStateOf(DataManager.showQuotes) }
    
    // Lists Sync with DataManager
    val webList = remember { mutableStateListOf<String>().apply { addAll(DataManager.userWebList) } }
    val appList = remember { mutableStateListOf<String>().apply { addAll(DataManager.userAppList) } }

    // UI States
    var currentBlockTab by remember { mutableIntStateOf(0) }
    var webInputText by remember { mutableStateOf("") }
    var appInputText by remember { mutableStateOf("") }
    
    // Dialog States
    var showTimeOverlay by remember { mutableStateOf(false) }
    var showPassOverlay by remember { mutableStateOf(false) }
    var showStoreOverlay by remember { mutableStateOf(false) }
    var showTitleOverlay by remember { mutableStateOf(false) }
    var inputPassText by remember { mutableStateOf("") }
    var inputTitleText by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize().background(SClrBg)) {
        // 1. TOP TABS
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
                    Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }
        }

        if (currentBlockTab == 0) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(scrollState)) {
                
                // START/STOP FOCUS ROW
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    var expandedControl by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedControl,
                        onExpandedChange = { if(!isFocusActive) expandedControl = it },
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = if (controlMode == 0) "Self Control" else "Friend Control",
                            onValueChange = {}, readOnly = true, enabled = !isFocusActive,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedControl) },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(expanded = expandedControl, onDismissRequest = { expandedControl = false }) {
                            DropdownMenuItem(text = { Text("Self Control") }, onClick = { 
                                controlMode = 0
                                DataManager.controlMode = 0
                                expandedControl = false 
                            })
                            DropdownMenuItem(text = { Text("Friend Control") }, onClick = { 
                                controlMode = 1
                                DataManager.controlMode = 1
                                expandedControl = false 
                            })
                        }
                    }

                    Button(
                        onClick = {
                            if (isFocusActive) {
                                if (controlMode == 1) showPassOverlay = true else {
                                    isFocusActive = false
                                    DataManager.isFocusActive = false
                                }
                            } else {
                                if (controlMode == 0) showTimeOverlay = true else showPassOverlay = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isFocusActive) SClrRed else SClrGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(56.dp)
                    ) {
                        Text(if (isFocusActive) "Stop Focus" else "Start Focus", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.LightGray)
                Spacer(modifier = Modifier.height(16.dp))

                // SELECT MODE (Allow vs Block)
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
                            DropdownMenuItem(text = { Text("Allow (White-list)") }, onClick = { 
                                simpleBlockMode = 0
                                DataManager.simpleBlockMode = 0
                                expandedMode = false 
                            })
                            DropdownMenuItem(text = { Text("Block (Black-list)") }, onClick = { 
                                simpleBlockMode = 1
                                DataManager.simpleBlockMode = 1
                                expandedMode = false 
                            })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // WEBSITES SECTION
                Text("Websites", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SClrDark)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = webInputText, onValueChange = { webInputText = it },
                        placeholder = { Text("e.g. facebook") },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp), enabled = !isFocusActive
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { 
                            if (webInputText.isNotEmpty()) { 
                                webList.add(webInputText)
                                DataManager.userWebList = webList
                                webInputText = "" 
                            } 
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SClrTeal),
                        shape = RoundedCornerShape(8.dp), modifier = Modifier.height(56.dp), enabled = !isFocusActive
                    ) { Text("+ Add") }
                }
                
                Box(modifier = Modifier.fillMaxWidth().height(120.dp).background(Color.White).border(1.dp, Color.LightGray)) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        webList.forEach { site ->
                            Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(site, color = SClrDark)
                                if (!isFocusActive) {
                                    Icon(Icons.Default.Close, null, tint = SClrRed, modifier = Modifier.clickable { 
                                        webList.remove(site)
                                        DataManager.userWebList = webList
                                    })
                                }
                            }
                            HorizontalDivider(color = Color(0xFFEEEEEE))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // APPLICATIONS SECTION
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Applications", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SClrDark)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = showQuotes, onCheckedChange = { 
                            showQuotes = it 
                            DataManager.showQuotes = it
                        })
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
                        onClick = { 
                            if (appInputText.isNotEmpty()) { 
                                appList.add(appInputText)
                                DataManager.userAppList = appList
                                appInputText = "" 
                            } 
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SClrTeal),
                        shape = RoundedCornerShape(8.dp), modifier = Modifier.height(56.dp), enabled = !isFocusActive
                    ) { Text("+ Add") }
                }

                Box(modifier = Modifier.fillMaxWidth().height(120.dp).background(Color.White).border(1.dp, Color.LightGray)) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        appList.forEach { pkg ->
                            Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(pkg, color = SClrDark, fontSize = 12.sp)
                                if (!isFocusActive) {
                                    Icon(Icons.Default.Close, null, tint = SClrRed, modifier = Modifier.clickable { 
                                        appList.remove(pkg)
                                        DataManager.userAppList = appList
                                    })
                                }
                            }
                            HorizontalDivider(color = Color(0xFFEEEEEE))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = { showStoreOverlay = true }, colors = ButtonDefaults.buttonColors(SClrGreen), shape = RoundedCornerShape(4.dp), modifier = Modifier.weight(1f).padding(end = 4.dp), enabled = !isFocusActive) { 
                        Text("Select Installed Apps", fontSize = 10.sp) 
                    }
                    Button(onClick = { showTitleOverlay = true }, colors = ButtonDefaults.buttonColors(SClrGreen), shape = RoundedCornerShape(4.dp), modifier = Modifier.weight(1f).padding(start = 4.dp), enabled = !isFocusActive) { 
                        Text("Add by Keyword", fontSize = 10.sp) 
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // DIALOGS
    if (showTimeOverlay) {
        AlertDialog(
            onDismissRequest = { showTimeOverlay = false },
            title = { Text("SET FOCUS DURATION") },
            text = { Text("Activate focus mode now?") },
            confirmButton = { Button(onClick = { 
                isFocusActive = true
                DataManager.isFocusActive = true
                showTimeOverlay = false 
            }) { Text("Start") } },
            dismissButton = { TextButton(onClick = { showTimeOverlay = false }) { Text("Cancel") } }
        )
    }

    if (showPassOverlay) {
        AlertDialog(
            onDismissRequest = { showPassOverlay = false },
            title = { Text(if (isFocusActive) "STOP PROTECTION" else "START PROTECTION") },
            text = { OutlinedTextField(value = inputPassText, onValueChange = { inputPassText = it }, placeholder = { Text("Enter Password") }) },
            confirmButton = { Button(onClick = { 
                if(inputPassText == "1234") {
                    isFocusActive = !isFocusActive
                    DataManager.isFocusActive = isFocusActive
                    showPassOverlay = false
                    inputPassText = ""
                }
            }) { Text("Confirm") } }
        )
    }

    if (showTitleOverlay) {
        AlertDialog(
            onDismissRequest = { showTitleOverlay = false },
            title = { Text("BLOCK BY KEYWORD") },
            text = { OutlinedTextField(value = inputTitleText, onValueChange = { inputTitleText = it }, placeholder = { Text("e.g. Shorts") }) },
            confirmButton = { Button(onClick = { 
                if(inputTitleText.isNotEmpty()) {
                    appList.add(inputTitleText)
                    DataManager.userAppList = appList
                    inputTitleText = ""
                    showTitleOverlay = false
                }
            }) { Text("Add") } }
        )
    }

    if (showStoreOverlay) {
        val installedApps = remember { getInstalledAppsList(context) }
        AlertDialog(
            onDismissRequest = { showStoreOverlay = false },
            title = { Text("SELECT INSTALLED APPS") },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                    items(installedApps) { app ->
                        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(app.name, fontWeight = FontWeight.Bold)
                                Text(app.packageName, fontSize = 10.sp, color = Color.Gray)
                            }
                            Button(onClick = { 
                                if(!appList.contains(app.packageName)) {
                                    appList.add(app.packageName)
                                    DataManager.userAppList = appList
                                }
                            }, colors = ButtonDefaults.buttonColors(SClrGreen)) { Text("Add", fontSize = 10.sp) }
                        }
                        HorizontalDivider()
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showStoreOverlay = false }) { Text("Close") } }
        )
    }
}
