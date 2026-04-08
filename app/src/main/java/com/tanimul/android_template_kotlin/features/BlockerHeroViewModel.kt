package com.tanimul.android_template_kotlin.features

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// এই অংশটুকু অ্যাপের সুইচের ডাটা সেভ রাখবে
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

class BlockerHeroViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BlockerHeroUiState())
    val uiState: StateFlow<BlockerHeroUiState> = _uiState.asStateFlow()

    fun updateAdultContent(checked: Boolean) { _uiState.update { it.copy(blockAdultContent = checked) } }
    fun updateImageSearch(checked: Boolean) { _uiState.update { it.copy(blockImageSearch = checked) } }
    fun updateYoutubeShorts(checked: Boolean) { _uiState.update { it.copy(blockYoutubeShorts = checked) } }
    fun updateUninstallProtection(checked: Boolean) { _uiState.update { it.copy(uninstallProtection = checked) } }
    fun updatePhoneReboot(checked: Boolean) { _uiState.update { it.copy(blockPhoneReboot = checked) } }
    fun updateRecentApps(checked: Boolean) { _uiState.update { it.copy(blockRecentAppsScreen = checked) } }
    fun updateUnsupportedBrowsers(checked: Boolean) { _uiState.update { it.copy(blockUnsupportedBrowsers = checked) } }
    fun updateNewInstalledApps(checked: Boolean) { _uiState.update { it.copy(blockNewInstalledApps = checked) } }
    fun updateNotificationPanel(checked: Boolean) { _uiState.update { it.copy(blockNotificationPanel = checked) } }
    fun updateScreenCountdown(checked: Boolean) { _uiState.update { it.copy(blockedScreenCountdown = checked) } }
}
