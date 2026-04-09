package com.tanimul.android_template_kotlin.features

import android.app.Application
import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class BlockerHeroUiState(
    // Master Lock
    val isRemotelyLocked: Boolean = false,
    
    // MagicX Features
    val blockAdultContent: Boolean = false, // (Dashboard Only - No UI toggle)
    val blockYoutubeShorts: Boolean = false,
    val blockFacebookReels: Boolean = false,
    
    // Strict Security
    val uninstallProtection: Boolean = false,
    val blockPhoneReboot: Boolean = false, // Blocks Power Menu
    val blockRecentAppsScreen: Boolean = false,
    val blockUnsupportedBrowsers: Boolean = false,
    val blockNewInstalledApps: Boolean = false,
    
    // Pomodoro & Focus
    val isFocusActive: Boolean = false,
    val focusTimeLeft: String = "00:00",
    
    // Live Chat & Admin Msg
    val adminMessage: String = ""
)

class BlockerHeroViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("BlockerPrefs", Context.MODE_PRIVATE)
    private val deviceId: String = Settings.Secure.getString(application.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown_device"
    private val databaseRef = FirebaseDatabase.getInstance().getReference("mobile_controls").child(deviceId)

    private val _uiState = MutableStateFlow(
        BlockerHeroUiState(
            isRemotelyLocked = prefs.getBoolean("isRemotelyLocked", false),
            blockAdultContent = prefs.getBoolean("blockAdult", false),
            blockYoutubeShorts = prefs.getBoolean("blockShorts", false),
            blockFacebookReels = prefs.getBoolean("blockReels", false),
            uninstallProtection = prefs.getBoolean("uninstallProtection", false),
            blockPhoneReboot = prefs.getBoolean("blockPhoneReboot", false),
            blockRecentAppsScreen = prefs.getBoolean("blockRecentAppsScreen", false),
            blockUnsupportedBrowsers = prefs.getBoolean("blockUnsupportedBrowsers", false),
            blockNewInstalledApps = prefs.getBoolean("blockNewInstalledApps", false)
        )
    )
    val uiState: StateFlow<BlockerHeroUiState> = _uiState.asStateFlow()

    init {
        initializeFirebaseData()
        listenToRemoteCommands()
    }

    private fun initializeFirebaseData() {
        val initialData = mapOf(
            "deviceName" to android.os.Build.MODEL,
            "isLocked" to _uiState.value.isRemotelyLocked,
            "blockAdult" to _uiState.value.blockAdultContent,
            "blockShorts" to _uiState.value.blockYoutubeShorts,
            "blockReels" to _uiState.value.blockFacebookReels,
            "uninstallProtection" to _uiState.value.uninstallProtection,
            "blockPhoneReboot" to _uiState.value.blockPhoneReboot,
            "blockRecentAppsScreen" to _uiState.value.blockRecentAppsScreen,
            "blockUnsupportedBrowsers" to _uiState.value.blockUnsupportedBrowsers,
            "blockNewInstalledApps" to _uiState.value.blockNewInstalledApps
        )
        databaseRef.updateChildren(initialData)
    }

    private fun listenToRemoteCommands() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val isLocked = snapshot.child("isLocked").getValue(Boolean::class.java) ?: false
                    val bAdult = snapshot.child("blockAdult").getValue(Boolean::class.java) ?: false
                    val bShorts = snapshot.child("blockShorts").getValue(Boolean::class.java) ?: false
                    val bReels = snapshot.child("blockReels").getValue(Boolean::class.java) ?: false
                    val uProtect = snapshot.child("uninstallProtection").getValue(Boolean::class.java) ?: false
                    val bReboot = snapshot.child("blockPhoneReboot").getValue(Boolean::class.java) ?: false
                    val bRecent = snapshot.child("blockRecentAppsScreen").getValue(Boolean::class.java) ?: false
                    val bBrowser = snapshot.child("blockUnsupportedBrowsers").getValue(Boolean::class.java) ?: false
                    val bNewApps = snapshot.child("blockNewInstalledApps").getValue(Boolean::class.java) ?: false
                    val adminMsg = snapshot.child("adminMessage").getValue(String::class.java) ?: ""

                    // Save to SharedPreferences for Accessibility Service
                    prefs.edit().apply {
                        putBoolean("isRemotelyLocked", isLocked)
                        putBoolean("blockAdult", bAdult)
                        putBoolean("blockShorts", bShorts)
                        putBoolean("blockReels", bReels)
                        putBoolean("uninstallProtection", uProtect)
                        putBoolean("blockPhoneReboot", bReboot)
                        putBoolean("blockRecentAppsScreen", bRecent)
                        putBoolean("blockUnsupportedBrowsers", bBrowser)
                        putBoolean("blockNewInstalledApps", bNewApps)
                        apply()
                    }

                    // Update UI
                    _uiState.update { 
                        it.copy(
                            isRemotelyLocked = isLocked,
                            blockAdultContent = bAdult,
                            blockYoutubeShorts = bShorts,
                            blockFacebookReels = bReels,
                            uninstallProtection = uProtect,
                            blockPhoneReboot = bReboot,
                            blockRecentAppsScreen = bRecent,
                            blockUnsupportedBrowsers = bBrowser,
                            blockNewInstalledApps = bNewApps,
                            adminMessage = adminMsg
                        )
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("ViewModel", "Firebase Error", error.toException())
            }
        })
    }

    // Toggle Functions from App UI
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

    fun toggleUninstallProtection(checked: Boolean) {
        prefs.edit().putBoolean("uninstallProtection", checked).apply()
        _uiState.update { it.copy(uninstallProtection = checked) }
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

    fun toggleUnsupportedBrowsers(checked: Boolean) {
        prefs.edit().putBoolean("blockUnsupportedBrowsers", checked).apply()
        _uiState.update { it.copy(blockUnsupportedBrowsers = checked) }
        databaseRef.child("blockUnsupportedBrowsers").setValue(checked)
    }

    fun toggleNewInstalledApps(checked: Boolean) {
        prefs.edit().putBoolean("blockNewInstalledApps", checked).apply()
        _uiState.update { it.copy(blockNewInstalledApps = checked) }
        databaseRef.child("blockNewInstalledApps").setValue(checked)
    }

    // Chat System
    fun sendLiveChatMessage(message: String) {
        databaseRef.child("liveChatUser").setValue(message)
    }
}
