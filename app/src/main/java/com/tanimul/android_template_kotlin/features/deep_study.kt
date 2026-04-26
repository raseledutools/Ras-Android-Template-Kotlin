package com.tanimul.android_template_kotlin.features

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================
// C++ Colors Translation
// ==========================================
val DClrTeal = Color(0xFF0CA8B0)
val DClrWhite = Color(0xFFFFFFFF)
val DClrDark = Color(0xFF323232)
val DClrGrayText = Color(0xFF787878)
val DClrBg = Color(0xFFF8FAFC)
val DClrRed = Color(0xFFE74C3C)
val DClrGreen = Color(0xFF5AAA14)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Deep_study() {
    // --- Sub Tab States ---
    var activeSubTab by remember { mutableIntStateOf(0) } // 0 = Pomodoro, 1 = Active Recall, 2 = Spaced Repetition

    // --- State Variables ---
    var isFocusMode by remember { mutableStateOf(false) }
    var isBreak by remember { mutableStateOf(false) }
    
    // Timer Settings
    var focusMin by remember { mutableIntStateOf(25) }
    var restMin by remember { mutableIntStateOf(5) }
    var totalSessions by remember { mutableIntStateOf(4) }
    var currentSession by remember { mutableIntStateOf(1) }

    // Toggles
    var chkSound by remember { mutableStateOf(false) }
    var chkFloat by remember { mutableStateOf(false) }
    var chkNet by remember { mutableStateOf(false) }
    var chkSet by remember { mutableStateOf(true) }
    var chkTask by remember { mutableStateOf(true) }
    var chkBlockBreak by remember { mutableStateOf(false) }
    var chkStrict by remember { mutableStateOf(false) }
    var chkHideBreakClose by remember { mutableStateOf(false) }

    // Sound Setup
    var soundType by remember { mutableIntStateOf(0) }
    val soundOptions = listOf("White Noise", "Classic Brown", "Deep Brown", "Warm Brown", "Heavy Rain", "Waterfall", "Wind", "Deep Focus", "Space Drone", "Cosmic Brown")

    // Allow Lists
    var webInputText by remember { mutableStateOf("") }
    var appInputText by remember { mutableStateOf("") }
    val allowWebs = remember { mutableStateListOf<BlockItem>() }
    val allowApps = remember { mutableStateListOf<BlockItem>() }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DClrBg)
    ) {
        // ==========================================
        // 1. TOP TABS
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth().background(DClrWhite).padding(10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val tabs = listOf("Pomodoro", "Active Recall", "Spaced Repetition")
            tabs.forEachIndexed { index, title ->
                Button(
                    onClick = { activeSubTab = index },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeSubTab == index) DClrTeal else Color(0xFFE6E6E6),
                        contentColor = if (activeSubTab == index) DClrWhite else DClrDark
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp).height(40.dp)
                ) {
                    Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }
        }

        // ==========================================
        // MAIN CONTENT (Pomodoro - SubTab 0)
        // ==========================================
        if (activeSubTab == 0) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(scrollState)) {

                // --- TOP TIMER UI ---
                Card(
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isBreak) Color(0xFF10B981) else DClrTeal),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(if (isFocusMode) "$focusMin:00" else "00:00", fontSize = 56.sp, fontWeight = FontWeight.Bold, color = DClrWhite)
                        Text(if (isBreak) "Break Time!" else "Session $currentSession of $totalSessions", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DClrWhite)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- START/STOP BUTTON ---
                Button(
                    onClick = { 
                        if (!(isFocusMode && chkStrict)) {
                            isFocusMode = !isFocusMode 
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFocusMode && chkStrict) Color(0xFFC81E1E) else if (isFocusMode) DClrRed else DClrTeal
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth().height(55.dp)
                ) {
                    Text(
                        text = if (isFocusMode && chkStrict) "STRICT MODE: LOCKED" else if (isFocusMode) "STOP POMODORO" else "START POMODORO",
                        fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DClrWhite
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = Color.LightGray)
                Spacer(modifier = Modifier.height(16.dp))

                // --- CUSTOM SESSION SETUP (+/- Buttons) ---
                Text("Custom Session Setup", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DClrDark)
                Spacer(modifier = Modifier.height(12.dp))
                
                TimerSetupRow("Focus (Minutes):", focusMin, 5, 120, 5, !isFocusMode) { focusMin = it }
                TimerSetupRow("Rest (Minutes):", restMin, 1, 30, 1, !isFocusMode) { restMin = it }
                TimerSetupRow("Total Sessions:", totalSessions, 1, 10, 1, !isFocusMode) { totalSessions = it }

                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = Color.LightGray)
                Spacer(modifier = Modifier.height(16.dp))

                // --- TOGGLES & SOUND ---
                Text("Settings & Blocking", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DClrDark)
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = chkSound, onCheckedChange = { chkSound = it }, enabled = !(isFocusMode && chkStrict))
                    Text("Ambient Sound", fontSize = 15.sp, color = DClrDark, modifier = Modifier.weight(1f))
                    
                    var expandedSound by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.width(140.dp)) {
                        ExposedDropdownMenuBox(expanded = expandedSound, onExpandedChange = { if(chkSound) expandedSound = it }) {
                            OutlinedTextField(
                                value = soundOptions[soundType], onValueChange = {}, readOnly = true, enabled = chkSound,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSound) },
                                modifier = Modifier.menuAnchor().height(50.dp),
                                shape = RoundedCornerShape(8.dp), textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                            )
                            ExposedDropdownMenu(expanded = expandedSound, onDismissRequest = { expandedSound = false }) {
                                soundOptions.forEachIndexed { index, option ->
                                    DropdownMenuItem(text = { Text(option, fontSize = 12.sp) }, onClick = { soundType = index; expandedSound = false })
                                }
                            }
                        }
                    }
                }

                DeepCheckbox("Floating Stopwatch", chkFloat, !(isFocusMode && chkStrict)) { chkFloat = it }
                DeepCheckbox("Block Internet", chkNet, !(isFocusMode && chkStrict)) { chkNet = it }
                DeepCheckbox("Block Settings / AppData", chkSet, !(isFocusMode && chkStrict)) { chkSet = it }
                DeepCheckbox("Block Task Manager", chkTask, !(isFocusMode && chkStrict)) { chkTask = it }
                DeepCheckbox("Keep Blocking During Break", chkBlockBreak, !(isFocusMode && chkStrict)) { chkBlockBreak = it }
                
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Checkbox(checked = chkStrict, onCheckedChange = { chkStrict = it }, enabled = !(isFocusMode && chkStrict), colors = CheckboxDefaults.colors(checkedColor = Color(0xFFC81E1E)))
                    Text("STRICT MODE (Allow List Only)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if(chkStrict) Color(0xFFC81E1E) else DClrDark)
                }
                
                DeepCheckbox("Hide Close Btn in Break", chkHideBreakClose, !(isFocusMode && chkStrict)) { chkHideBreakClose = it }

                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = Color.LightGray)
                Spacer(modifier = Modifier.height(16.dp))

                // --- ALLOWED WEBSITES & APPS ---
                Text("Allowed Websites", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DClrDark)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = webInputText, onValueChange = { webInputText = it },
                        placeholder = { Text("e.g. wikipedia.org") },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp), enabled = !(isFocusMode && chkStrict)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { if (webInputText.isNotEmpty()) { allowWebs.add(BlockItem(webInputText)); webInputText = "" } },
                        colors = ButtonDefaults.buttonColors(containerColor = DClrTeal),
                        shape = RoundedCornerShape(8.dp), modifier = Modifier.height(56.dp), enabled = !(isFocusMode && chkStrict)
                    ) { Text("Add") }
                }
                Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color.White).border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        allowWebs.forEach { item ->
                            Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(item.name, color = DClrDark)
                                if (!(isFocusMode && chkStrict)) {
                                    Icon(Icons.Default.Close, contentDescription = "Delete", tint = DClrRed, modifier = Modifier.clickable { allowWebs.remove(item) })
                                }
                            }
                            Divider(color = Color(0xFFEEEEEE))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Allowed Apps", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DClrDark)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = appInputText, onValueChange = { appInputText = it },
                        placeholder = { Text("e.g. word.exe") },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp), enabled = !(isFocusMode && chkStrict)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { if (appInputText.isNotEmpty()) { allowApps.add(BlockItem(appInputText)); appInputText = "" } },
                        colors = ButtonDefaults.buttonColors(containerColor = DClrTeal),
                        shape = RoundedCornerShape(8.dp), modifier = Modifier.height(56.dp), enabled = !(isFocusMode && chkStrict)
                    ) { Text("Add") }
                }
                Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color.White).border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        allowApps.forEach { item ->
                            Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(item.name, color = DClrDark)
                                if (!(isFocusMode && chkStrict)) {
                                    Icon(Icons.Default.Close, contentDescription = "Delete", tint = DClrRed, modifier = Modifier.clickable { allowApps.remove(item) })
                                }
                            }
                            Divider(color = Color(0xFFEEEEEE))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
        else if (activeSubTab == 1) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Active Recall tools will be added here...", color = DClrGrayText)
            }
        }
        else if (activeSubTab == 2) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Spaced Repetition tools will be added here...", color = DClrGrayText)
            }
        }
    }
}

// --- Helper Composable for Checkboxes ---
@Composable
fun DeepCheckbox(title: String, checked: Boolean, enabled: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled, colors = CheckboxDefaults.colors(checkedColor = DClrTeal))
        Text(title, fontSize = 15.sp, color = if(enabled) DClrDark else DClrGrayText, modifier = Modifier.clickable(enabled = enabled) { onCheckedChange(!checked) })
    }
}

// --- Helper Composable for +/- Setup ---
@Composable
fun TimerSetupRow(label: String, value: Int, minVal: Int, maxVal: Int, step: Int, enabled: Boolean, onValueChange: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 15.sp, color = DClrDark, modifier = Modifier.weight(1f))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (enabled && value > minVal) onValueChange(value - step) }, modifier = Modifier.background(Color.White, RoundedCornerShape(4.dp)).border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)).size(35.dp)) {
                Icon(Icons.Default.Remove, contentDescription = "Minus", tint = DClrDark)
            }
            Text("$value", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(horizontal = 16.dp))
            IconButton(onClick = { if (enabled && value < maxVal) onValueChange(value + step) }, modifier = Modifier.background(Color.White, RoundedCornerShape(4.dp)).border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)).size(35.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Plus", tint = DClrDark)
            }
        }
    }
}
