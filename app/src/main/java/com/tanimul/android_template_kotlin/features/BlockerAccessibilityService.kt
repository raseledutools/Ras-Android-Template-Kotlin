package com.tanimul.android_template_kotlin.features

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class BlockerAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val prefs = getSharedPreferences("BlockerPrefs", Context.MODE_PRIVATE)
        val isShortsBlocked = prefs.getBoolean("blockYoutubeShorts", false)

        if (isShortsBlocked && event.packageName == "com.google.android.youtube") {
            val rootNode = rootInActiveWindow ?: return
            val shortsNodes = rootNode.findAccessibilityNodeInfosByText("Shorts")
            
            if (shortsNodes.isNotEmpty()) {
                // ১. প্রথমে ইউজারকে হোম স্ক্রিনে (বা ব্যাক) পাঠিয়ে দেব
                performGlobalAction(GLOBAL_ACTION_HOME)
                
                // ২. এরপর ব্লক ওয়ার্নিং পেজটি ওপেন করব
                val intent = Intent(this, BlockActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                startActivity(intent)
            }
        }
    }

    override fun onInterrupt() {}
}
