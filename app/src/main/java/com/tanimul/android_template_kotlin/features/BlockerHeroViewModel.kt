package com.tanimul.android_template_kotlin.features

import android.app.Application
import android.content.Context
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

data class BlockerHeroUiState(
    // Master Lock & App Entry Password
    val isRemotelyLocked: Boolean = false,
    val hasAppPassword: Boolean = false,
    
    // Strict Break Mode (Take a Break Flow)
    val isStrictBreakActive: Boolean = false,
    val breakTimeRemaining: String = "00:00:00",

    // Block & Allow Lists
    val listMode: String = "BLOCK", // "BLOCK" or "ALLOW"
    val blockList: List<String> = emptyList(),
    val allowList: List<String> = emptyList(),
    
    // UI Toggles (Dashboard)
    val blockKeywords: Boolean = false, // নতুন যোগ করা হয়েছে হোমপেজের জন্য
    val blockAdultContent: Boolean = false, 
    val blockYoutubeShorts: Boolean = false,
    val blockFacebookReels: Boolean = false,
    
    // Strict Security
    val uninstallProtection: Boolean = false,
    val uninstallProtectionDays: Int = 0,
    val blockPhoneReboot: Boolean = false,
    val blockRecentAppsScreen: Boolean = false,
    val blockUnsupportedBrowsers: Boolean = false,
    val blockNewInstalledApps: Boolean = false,
    
    // Live Chat & Admin Msg
    val adminMessage: String = ""
)

class BlockerHeroViewModel(application: Application) : AndroidViewModel(application) {

    // SharedPreferences দিয়ে ডেটা সেভ রাখা যাতে সার্ভিস সহজেই পড়তে পারে
    private val prefs = application.getSharedPreferences("BlockerPrefs", Context.MODE_PRIVATE)
    private val deviceId: String = Settings.Secure.getString(application.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown_device"
    private val databaseRef = FirebaseDatabase.getInstance().getReference("mobile_controls").child(deviceId)

    private var breakTimerJob: Job? = null

    private val _uiState = MutableStateFlow(
        BlockerHeroUiState(
            isRemotelyLocked = prefs.getBoolean("isRemotelyLocked", false),
            hasAppPassword = prefs.getString("app_master_password", "")?.isNotEmpty() == true,
            
            listMode = prefs.getString("list_mode", "BLOCK") ?: "BLOCK",
            blockList = prefs.getStringSet("block_list", emptySet())?.toList() ?: emptyList(),
            allowList = prefs.getStringSet("allow_list", emptySet())?.toList() ?: emptyList(),
            
            blockKeywords = prefs.getBoolean("blockKeywords", false), // নতুন
            blockAdultContent = prefs.getBoolean("blockAdult", false),
            blockYoutubeShorts = prefs.getBoolean("blockShorts", false),
            blockFacebookReels = prefs.getBoolean("blockReels", false),
            
            uninstallProtection = prefs.getBoolean("uninstallProtection", false),
            uninstallProtectionDays = prefs.getInt("uninstallProtectionDays", 0),
            blockPhoneReboot = prefs.getBoolean("blockPhoneReboot", false),
            blockRecentAppsScreen = prefs.getBoolean("blockRecentAppsScreen", false),
            blockNewInstalledApps = prefs.getBoolean("blockNewInstalledApps", false)
        )
    )
    val uiState: StateFlow<BlockerHeroUiState> = _uiState.asStateFlow()

    init {
        initializeFirebaseData()
        listenToRemoteCommands()
        checkAndResumeBreakTimer()
    }

    // --- Firebase Logic (Remote Control) ---
    private fun initializeFirebaseData() {
        val initialData = mapOf(
            "deviceName" to android.os.Build.MODEL,
            "isLocked" to _uiState.value.isRemotelyLocked,
            "blockKeywords" to _uiState.value.blockKeywords,
            "blockAdult" to _uiState.value.blockAdultContent,
            "blockShorts" to _uiState.value.blockYoutubeShorts,
            "blockReels" to _uiState.value.blockFacebookReels,
            "uninstallProtection" to _uiState.value.uninstallProtection
        )
        databaseRef.updateChildren(initialData)
    }

    private fun listenToRemoteCommands() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val isLocked = snapshot.child("isLocked").getValue(Boolean::class.java) ?: false
                    val bKeywords = snapshot.child("blockKeywords").getValue(Boolean::class.java) ?: false
                    val bAdult = snapshot.child("blockAdult").getValue(Boolean::class.java) ?: false
                    val bShorts = snapshot.child("blockShorts").getValue(Boolean::class.java) ?: false
                    val bReels = snapshot.child("blockReels").getValue(Boolean::class.java) ?: false
                    val uProtect = snapshot.child("uninstallProtection").getValue(Boolean::class.java) ?: false
                    val bReboot = snapshot.child("blockPhoneReboot").getValue(Boolean::class.java) ?: false
                    val bRecent = snapshot.child("blockRecentAppsScreen").getValue(Boolean::class.java) ?: false
                    val bNewApps = snapshot.child("blockNewInstalledApps").getValue(Boolean::class.java) ?: false
                    val adminMsg = snapshot.child("adminMessage").getValue(String::class.java) ?: ""

                    prefs.edit().apply {
                        putBoolean("isRemotelyLocked", isLocked)
                        putBoolean("blockKeywords", bKeywords)
                        putBoolean("blockAdult", bAdult)
                        putBoolean("blockShorts", bShorts)
                        putBoolean("blockReels", bReels)
                        putBoolean("uninstallProtection", uProtect)
                        putBoolean("blockPhoneReboot", bReboot)
                        putBoolean("blockRecentAppsScreen", bRecent)
                        putBoolean("blockNewInstalledApps", bNewApps)
                        apply()
                    }

                    _uiState.update { 
                        it.copy(
                            isRemotelyLocked = isLocked, 
                            blockKeywords = bKeywords,
                            blockAdultContent = bAdult,
                            blockYoutubeShorts = bShorts, 
                            blockFacebookReels = bReels,
                            uninstallProtection = uProtect, 
                            blockPhoneReboot = bReboot,
                            blockRecentAppsScreen = bRecent, 
                            blockNewInstalledApps = bNewApps,
                            adminMessage = adminMsg
                        )
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // --- Strict Break Timer Logic ---
    fun startStrictBreak(hours: Int, minutes: Int) {
        val totalMillis = TimeUnit.HOURS.toMillis(hours.toLong()) + TimeUnit.MINUTES.toMillis(minutes.toLong())
        val endTime = System.currentTimeMillis() + totalMillis
        
        prefs.edit().putLong("strict_break_end_time", endTime).putBoolean("isStrictBreakActive", true).apply()
        startTimerCoroutine(endTime)
    }

    private fun checkAndResumeBreakTimer() {
        val endTime = prefs.getLong("strict_break_end_time", 0L)
        if (endTime > System.currentTimeMillis()) {
            startTimerCoroutine(endTime)
        } else {
            endStrictBreak()
        }
    }

    private fun startTimerCoroutine(endTime: Long) {
        _uiState.update { it.copy(isStrictBreakActive = true) }
        breakTimerJob?.cancel()
        breakTimerJob = viewModelScope.launch(Dispatchers.IO) {
            while (System.currentTimeMillis() < endTime) {
                val remaining = endTime - System.currentTimeMillis()
                val h = TimeUnit.MILLISECONDS.toHours(remaining)
                val m = TimeUnit.MILLISECONDS.toMinutes(remaining) % 60
                val s = TimeUnit.MILLISECONDS.toSeconds(remaining) % 60
                
                _uiState.update { it.copy(breakTimeRemaining = String.format("%02d:%02d:%02d", h, m, s)) }
                delay(1000)
            }
            endStrictBreak()
        }
    }

    fun endStrictBreak() {
        prefs.edit().putLong("strict_break_end_time", 0L).putBoolean("isStrictBreakActive", false).apply()
        _uiState.update { it.copy(isStrictBreakActive = false, breakTimeRemaining = "00:00:00") }
    }

    // --- App Password Logic ---
    fun setMasterPassword(password: String) {
        prefs.edit().putString("app_master_password", password).apply()
        _uiState.update { it.copy(hasAppPassword = true) }
        databaseRef.child("isLocked").setValue(true)
    }

    fun verifyPassword(password: String): Boolean {
        return prefs.getString("app_master_password", "") == password
    }

    // --- Block & Allow List Logic ---
    fun setListMode(mode: String) {
        prefs.edit().putString("list_mode", mode).apply()
        _uiState.update { it.copy(listMode = mode) }
    }

    fun addToList(item: String) {
        val mode = _uiState.value.listMode
        val key = if (mode == "BLOCK") "block_list" else "allow_list"
        val currentSet = prefs.getStringSet(key, emptySet())?.toMutableSet() ?: mutableSetOf()
        
        currentSet.add(item.lowercase().trim())
        prefs.edit().putStringSet(key, currentSet).apply()
        
        if (mode == "BLOCK") _uiState.update { it.copy(blockList = currentSet.toList()) }
        else _uiState.update { it.copy(allowList = currentSet.toList()) }
    }

    fun removeFromList(mode: String, item: String) {
        val key = if (mode == "BLOCK") "block_list" else "allow_list"
        val currentSet = prefs.getStringSet(key, emptySet())?.toMutableSet() ?: mutableSetOf()
        
        currentSet.remove(item.lowercase().trim())
        prefs.edit().putStringSet(key, currentSet).apply()
        
        if (mode == "BLOCK") _uiState.update { it.copy(blockList = currentSet.toList()) }
        else _uiState.update { it.copy(allowList = currentSet.toList()) }
    }

    // --- Toggles ---
    fun toggleKeywords(checked: Boolean) {
        prefs.edit().putBoolean("blockKeywords", checked).apply()
        _uiState.update { it.copy(blockKeywords = checked) }
        databaseRef.child("blockKeywords").setValue(checked)
    }

    fun toggleAdultContent(checked: Boolean) {
        prefs.edit().putBoolean("blockAdult", checked).apply()
        _uiState.update { it.copy(blockAdultContent = checked) }
        databaseRef.child("blockAdult").setValue(checked)
    }

    fun toggleYoutubeShorts(checked: Boolean) {
        prefs.edit().putBoolean("blockShorts", checked).apply()
        _uiState.update { it.copy(blockYoutubeShorts = checked) }
        databaseRef.child("blockShorts").setValue(checked)
    }

    fun toggleFacebookReels(checked: Boolean) {
        prefs.edit().putBoolean("blockReels", checked).apply()
        _uiState.update { it.copy(blockFacebookReels = checked) }
        databaseRef.child("blockReels").setValue(checked)
    }

    fun toggleUninstallProtection(checked: Boolean, days: Int = 0) {
        prefs.edit().putBoolean("uninstallProtection", checked).putInt("uninstallProtectionDays", days).apply()
        _uiState.update { it.copy(uninstallProtection = checked, uninstallProtectionDays = days) }
        databaseRef.child("uninstallProtection").setValue(checked)
    }

    fun togglePhoneReboot(checked: Boolean) {
        prefs.edit().putBoolean("blockPhoneReboot", checked).apply()
        _uiState.update { it.copy(blockPhoneReboot = checked) }
        databaseRef.child("blockPhoneReboot").setValue(checked)
    }

    fun toggleRecentAppsScreen(checked: Boolean) {
        prefs.edit().putBoolean("blockRecentAppsScreen", checked).apply()
        _uiState.update { it.copy(blockRecentAppsScreen = checked) }
        databaseRef.child("blockRecentAppsScreen").setValue(checked)
    }

    fun toggleNewInstalledApps(checked: Boolean) {
        prefs.edit().putBoolean("blockNewInstalledApps", checked).apply()
        _uiState.update { it.copy(blockNewInstalledApps = checked) }
        databaseRef.child("blockNewInstalledApps").setValue(checked)
    }

    // --- Chat System ---
    fun sendLiveChatMessage(message: String) {
        databaseRef.child("liveChatUser").setValue(message)
    }
}
