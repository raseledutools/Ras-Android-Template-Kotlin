package com.tanimul.android_template_kotlin.features

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
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
    val blockedScreenCountdown: Boolean = false
)

// AndroidViewModel ব্যবহার করছি যাতে সহজেই লোকাল মেমোরি (SharedPreferences) ব্যবহার করা যায়
class BlockerHeroViewModel(application: Application) : AndroidViewModel(application) {
    
    // অ্যাপের মেমোরি তৈরি করা হলো
    private val prefs = application.getSharedPreferences("BlockerPrefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        BlockerHeroUiState(
            // অ্যাপ ওপেন হলে মেমোরি থেকে আগের সেভ করা ডেটা পড়বে
            blockYoutubeShorts = prefs.getBoolean("blockYoutubeShorts", false)
            // (বাকিগুলোও পরে এভাবে যোগ করা যাবে)
        )
    )
    val uiState: StateFlow<BlockerHeroUiState> = _uiState.asStateFlow()

    // যখনই ইউজার সুইচ অন/অফ করবে, তখন মেমোরিতে সেভ হয়ে যাবে
    fun updateYoutubeShorts(checked: Boolean) {
        prefs.edit().putBoolean("blockYoutubeShorts", checked).apply()
        _uiState.update { it.copy(blockYoutubeShorts = checked) }
    }

    fun updateAdultContent(checked: Boolean) { _uiState.update { it.copy(blockAdultContent = checked) } }
    fun updateImageSearch(checked: Boolean) { _uiState.update { it.copy(blockImageSearch = checked) } }
    fun updateUninstallProtection(checked: Boolean) { _uiState.update { it.copy(uninstallProtection = checked) } }
    fun updatePhoneReboot(checked: Boolean) { _uiState.update { it.copy(blockPhoneReboot = checked) } }
    fun updateRecentApps(checked: Boolean) { _uiState.update { it.copy(blockRecentAppsScreen = checked) } }
    fun updateUnsupportedBrowsers(checked: Boolean) { _uiState.update { it.copy(blockUnsupportedBrowsers = checked) } }
    fun updateNewInstalledApps(checked: Boolean) { _uiState.update { it.copy(blockNewInstalledApps = checked) } }
    fun updateNotificationPanel(checked: Boolean) { _uiState.update { it.copy(blockNotificationPanel = checked) } }
    fun updateScreenCountdown(checked: Boolean) { _uiState.update { it.copy(blockedScreenCountdown = checked) } }
}
