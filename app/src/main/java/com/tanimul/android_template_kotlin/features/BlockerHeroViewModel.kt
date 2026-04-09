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
    val blockAdultContent: Boolean = false,
    val blockImageSearch: Boolean = false,
    val blockYoutubeShorts: Boolean = false,
    val uninstallProtection: Boolean = false,
    val blockPhoneReboot: Boolean = false,
    val blockRecentAppsScreen: Boolean = false,
    val blockUnsupportedBrowsers: Boolean = false,
    val blockNewInstalledApps: Boolean = false,
    val blockNotificationPanel: Boolean = false,
    val blockedScreenCountdown: Boolean = false,
    val isRemotelyLocked: Boolean = false // রিমোট লকের জন্য নতুন স্ট্যাটাস
)

class BlockerHeroViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("BlockerPrefs", Context.MODE_PRIVATE)
    
    // ডিভাইসের ইউনিক আইডি বের করা
    private val deviceId: String = Settings.Secure.getString(application.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown_device"

    // ফায়ারবেস ডাটাবেসের রেফারেন্স (mobile_controls -> DeviceID)
    private val databaseRef = FirebaseDatabase.getInstance().getReference("mobile_controls").child(deviceId)

    private val _uiState = MutableStateFlow(
        BlockerHeroUiState(
            blockYoutubeShorts = prefs.getBoolean("blockYoutubeShorts", false),
            blockAdultContent = prefs.getBoolean("blockAdultContent", false),
            isRemotelyLocked = prefs.getBoolean("isRemotelyLocked", false)
        )
    )
    val uiState: StateFlow<BlockerHeroUiState> = _uiState.asStateFlow()

    init {
        // প্রথমবার অ্যাপ ওপেন হলে ফায়ারবেসে ডিভাইসের তথ্য পাঠিয়ে দেওয়া
        initializeFirebaseData()
        
        // ফায়ারবেসের পরিবর্তনের দিকে নজর রাখা (Listener)
        listenToRemoteCommands()
    }

    private fun initializeFirebaseData() {
        val initialData = mapOf(
            "deviceName" to android.os.Build.MODEL,
            "isLocked" to _uiState.value.isRemotelyLocked,
            "blockShorts" to _uiState.value.blockYoutubeShorts,
            "blockAdult" to _uiState.value.blockAdultContent
        )
        // ডাটাবেসে আপডেট করে দিচ্ছি
        databaseRef.updateChildren(initialData)
    }

    private fun listenToRemoteCommands() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val isLocked = snapshot.child("isLocked").getValue(Boolean::class.java) ?: false
                    val blockShorts = snapshot.child("blockShorts").getValue(Boolean::class.java) ?: false
                    val blockAdult = snapshot.child("blockAdult").getValue(Boolean::class.java) ?: false

                    // ফায়ারবেস থেকে ডাটা আসলে মেমোরিতে সেভ করা
                    prefs.edit().apply {
                        putBoolean("isRemotelyLocked", isLocked)
                        putBoolean("blockYoutubeShorts", blockShorts)
                        putBoolean("blockAdultContent", blockAdult)
                        apply()
                    }

                    // UI স্ট্যাটাস আপডেট করা
                    _uiState.update { currentState ->
                        currentState.copy(
                            isRemotelyLocked = isLocked,
                            blockYoutubeShorts = blockShorts,
                            blockAdultContent = blockAdult
                        )
                    }
                    Log.d("FirebaseSync", "Data updated from remote: Locked=$isLocked, Shorts=$blockShorts")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseSync", "Failed to read value.", error.toException())
            }
        })
    }

    // অ্যাপ থেকে ম্যানুয়ালি চেঞ্জ করলে মেমোরি এবং ফায়ারবেস দুটোই আপডেট হবে
    fun updateYoutubeShorts(checked: Boolean) {
        prefs.edit().putBoolean("blockYoutubeShorts", checked).apply()
        _uiState.update { it.copy(blockYoutubeShorts = checked) }
        databaseRef.child("blockShorts").setValue(checked)
    }

    fun updateAdultContent(checked: Boolean) {
        prefs.edit().putBoolean("blockAdultContent", checked).apply()
        _uiState.update { it.copy(blockAdultContent = checked) }
        databaseRef.child("blockAdult").setValue(checked)
    }

    // বাকি ফাংশনগুলো
    fun updateImageSearch(checked: Boolean) { _uiState.update { it.copy(blockImageSearch = checked) } }
    fun updateUninstallProtection(checked: Boolean) { _uiState.update { it.copy(uninstallProtection = checked) } }
    fun updatePhoneReboot(checked: Boolean) { _uiState.update { it.copy(blockPhoneReboot = checked) } }
    fun updateRecentApps(checked: Boolean) { _uiState.update { it.copy(blockRecentAppsScreen = checked) } }
    fun updateUnsupportedBrowsers(checked: Boolean) { _uiState.update { it.copy(blockUnsupportedBrowsers = checked) } }
    fun updateNewInstalledApps(checked: Boolean) { _uiState.update { it.copy(blockNewInstalledApps = checked) } }
    fun updateNotificationPanel(checked: Boolean) { _uiState.update { it.copy(blockNotificationPanel = checked) } }
    fun updateScreenCountdown(checked: Boolean) { _uiState.update { it.copy(blockedScreenCountdown = checked) } }
}
