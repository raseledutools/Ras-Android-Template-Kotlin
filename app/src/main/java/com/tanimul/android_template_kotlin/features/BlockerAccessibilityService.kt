package com.tanimul.android_template_kotlin.features

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class BlockerAccessibilityService : AccessibilityService() {

    // C++ কোড থেকে আনা এক্সপ্লিসিট কি-ওয়ার্ড লিস্ট
    private val explicitKeywords = listOf(
        "porn", "xxx", "sex", "nude", "nsfw", "xvideos", "pornhub", 
        "xnxx", "xhamster", "brazzers", "onlyfans", "playboy", 
        "mia khalifa", "bhabi", "chudai", "bangla choti", "magi", "sexy"
    )

    // ফোন লক থাকলে শুধু এই ইমার্জেন্সি অ্যাপগুলো চলবে (কল এবং মেসেজ)
    private val allowedPackagesWhenLocked = listOf(
        "com.tanimul.android_template_kotlin",
        "com.ras.Rasfocus", 
        "com.android.dialer",
        "com.android.server.telecom",
        "com.android.messaging",
        "com.google.android.dialer"
    )

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val prefs = applicationContext.getSharedPreferences("BlockerPrefs", Context.MODE_PRIVATE)
        
        // ফায়ারবেস (ViewModel) থেকে আসা কমান্ডগুলো পড়া
        val isRemotelyLocked = prefs.getBoolean("isRemotelyLocked", false)
        val blockAdult = prefs.getBoolean("blockAdult", false)
        val blockShorts = prefs.getBoolean("blockShorts", false)
        val blockReels = prefs.getBoolean("blockReels", false)
        val uninstallProtection = prefs.getBoolean("uninstallProtection", false)
        val blockRecentAppsScreen = prefs.getBoolean("blockRecentAppsScreen", false)
        val blockPhoneReboot = prefs.getBoolean("blockPhoneReboot", false)
        val blockNewInstalledApps = prefs.getBoolean("blockNewInstalledApps", false)

        val packageName = event.packageName?.toString() ?: ""
        val className = event.className?.toString() ?: ""
        
        // স্ক্রিনে থাকা সব লেখা (Text) একসাথে করা
        val screenText = mutableListOf<String>()
        event.text?.forEach { screenText.add(it.toString().lowercase()) }
        val nodeText = getEventText(event.source).lowercase()
        val fullTextContext = screenText.joinToString(" ") + " " + nodeText

        // ১. MASTER LOCK (C++ এর Focus Mode এর মতো)
        if (isRemotelyLocked) {
            if (packageName.isNotEmpty() && !allowedPackagesWhenLocked.contains(packageName) && packageName != "com.android.systemui") {
                showBlockScreen("LOCKED")
                return
            }
        }

        // ২. ADULT CONTENT BLOCK (C++ Anti-Porn Logic)
        if (blockAdult) {
            for (keyword in explicitKeywords) {
                // কেউ টাইপ করলে বা স্ক্রিনে খারাপ কিছু আসলে সাথে সাথে ব্লক
                if (fullTextContext.contains(keyword)) {
                    showBlockScreen("ADULT")
                    return
                }
            }
        }

        // ৩. YOUTUBE SHORTS BLOCK
        if (blockShorts && packageName.contains("youtube")) {
            if (fullTextContext.contains("shorts") || className.contains("Shorts")) {
                performGlobalAction(GLOBAL_ACTION_BACK) // অটো ব্যাক বাটনে প্রেস করবে
                return
            }
        }

        // ৪. FACEBOOK REELS BLOCK
        if (blockReels && packageName.contains("facebook")) {
            if (fullTextContext.contains("reels") || fullTextContext.contains("video")) {
                performGlobalAction(GLOBAL_ACTION_BACK)
                return
            }
        }

        // ৫. STRICT SECURITY: UNINSTALL PROTECTION
        if (uninstallProtection) {
            if (packageName == "com.android.settings" || packageName == "com.google.android.packageinstaller") {
                if ((fullTextContext.contains("uninstall") || fullTextContext.contains("delete") || fullTextContext.contains("remove")) && 
                    (fullTextContext.contains("rasfocus") || fullTextContext.contains("blockerhero"))) {
                    performGlobalAction(GLOBAL_ACTION_HOME)
                    showBlockScreen("SECURITY")
                    return
                }
            }
        }

        // ৬. STRICT SECURITY: BLOCK RECENT APPS (C++ Task Manager Block)
        if (blockRecentAppsScreen) {
            if (packageName == "com.android.systemui" && className.contains("Recents")) {
                performGlobalAction(GLOBAL_ACTION_HOME)
                return
            }
        }

        // ৭. STRICT SECURITY: BLOCK PHONE REBOOT (Power Menu Block)
        if (blockPhoneReboot) {
            if (fullTextContext.contains("power off") || fullTextContext.contains("restart") || className.contains("GlobalActionsDialog")) {
                performGlobalAction(GLOBAL_ACTION_BACK) // পাওয়ার মেনু আসলে অটো ব্যাক করে দেবে
                performGlobalAction(GLOBAL_ACTION_HOME)
                return
            }
        }

        // ৮. STRICT SECURITY: BLOCK INSTALLING NEW APPS
        if (blockNewInstalledApps) {
            if (packageName == "com.android.vending" && fullTextContext.contains("install")) {
                performGlobalAction(GLOBAL_ACTION_HOME)
                showBlockScreen("NEW_APP")
                return
            }
        }
    }

    // স্ক্রিনের ভেতরের চাইল্ড নোডগুলো থেকে টেক্সট এক্সট্রাক্ট করার ফাংশন
    private fun getEventText(node: AccessibilityNodeInfo?): String {
        if (node == null) return ""
        var text = node.text?.toString() ?: ""
        text += " " + (node.contentDescription?.toString() ?: "")
        for (i in 0 until node.childCount) {
            text += " " + getEventText(node.getChild(i))
        }
        return text
    }

    // ব্লক স্ক্রিনে পাঠানোর ফাংশন (যাতে কারণ অনুযায়ী মেসেজ দেখানো যায়)
    private fun showBlockScreen(reason: String) {
        val intent = Intent(this, BlockActivity::class.java)
        intent.putExtra("BLOCK_REASON", reason)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onInterrupt() {}
}
