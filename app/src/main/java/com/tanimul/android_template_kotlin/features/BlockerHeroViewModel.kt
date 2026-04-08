package com.tanimul.android_template_kotlin.features

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BlockerHeroViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BlockerHeroUiState())
    val uiState: StateFlow<BlockerHeroUiState> = _uiState.asStateFlow()

    fun updateAdultContent(value: Boolean) { _uiState.update { it.copy(blockAdultContent = value) } }
    fun updateImageSearch(value: Boolean) { _uiState.update { it.copy(blockImageSearch = value) } }
    fun updateYoutubeShorts(value: Boolean) { _uiState.update { it.copy(blockYoutubeShorts = value) } }
    fun updateUninstallProtection(value: Boolean) { _uiState.update { it.copy(uninstallProtection = value) } }
    fun updatePhoneReboot(value: Boolean) { _uiState.update { it.copy(blockPhoneReboot = value) } }
    fun updateRecentApps(value: Boolean) { _uiState.update { it.copy(blockRecentAppsScreen = value) } }
    fun updateUnsupportedBrowsers(value: Boolean) { _uiState.update { it.copy(blockUnsupportedBrowsers = value) } }
    fun updateNewInstalledApps(value: Boolean) { _uiState.update { it.copy(blockNewInstalledApps = value) } }
    fun updateNotificationPanel(value: Boolean) { _uiState.update { it.copy(blockNotificationPanel = value) } }
    fun updateScreenCountdown(value: Boolean) { _uiState.update { it.copy(blockedScreenCountdown = value) } }
}
