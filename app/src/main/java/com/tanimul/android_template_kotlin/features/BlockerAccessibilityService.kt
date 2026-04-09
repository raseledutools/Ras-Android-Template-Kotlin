package com.tanimul.android_template_kotlin.features

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class BlockerAccessibilityService : AccessibilityService() {

    // এক্সপ্লিসিট কি-ওয়ার্ড লিস্ট
    private val explicitKeywords = listOf(
        "porn", "xxx", "sex", "nude", "nsfw", "xvideos", "pornhub", 
        "xnxx", "xhamster", "brazzers", "onlyfans", "playboy", 
        "mia khalifa", "bhabi", "chudai", "bangla choti", "magi"
    )

    // সিস্টেম এবং ইমার্জেন্সি অ্যাপ যেগুলো সব সময় চলবে
    private val systemAllowedPackages = listOf(
        "com.tanimul.android_template_kotlin",
        "com.ras.Rasfocus", 
        "com.android.dialer",
        "com.android.server.telecom",
        "com.android.messaging",
        "com.google.android.dialer",
        "com.android.systemui",
        "com.android.launcher",
        "com.google.android.apps.nexuslauncher"
    )

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val prefs = applicationContext.getSharedPreferences("BlockerPrefs", Context.MODE_PRIVATE)
        
        // ফায়ারবেস এবং অ্যাপ থেকে আসা কমান্ডগুলো পড়া
        val isRemotelyLocked = prefs.getBoolean("isRemotelyLocked", false)
        val isStrictBreakActive = prefs.getBoolean("isStrictBreakActive", false)
        val listMode = prefs.getString("list_mode", "BLOCK") ?: "BLOCK"
        val blockList = prefs.getStringSet("block_list", emptySet())?.toList() ?: emptyList()
        val allowList = prefs.getStringSet("allow_list", emptySet())?.toList() ?: emptyList()

        val blockAdult = prefs.getBoolean("blockAdult", false)
        val blockShorts = prefs.getBoolean("blockShorts", false)
        val blockReels = prefs.getBoolean("blockReels", false)
        val uninstallProtection = prefs.getBoolean("uninstallProtection", false)
        val blockRecentAppsScreen = prefs.getBoolean("blockRecentAppsScreen", false)
        val blockPhoneReboot = prefs.getBoolean("blockPhoneReboot", false)
        val blockNewInstalledApps = prefs.getBoolean("blockNewInstalledApps", false)

        val packageName = event.packageName?.toString() ?: ""
        val className = event.className?.toString() ?: ""
        
        // ১. ক্লিক করা টেক্সট ট্র্যাক করা
        var clickedText = ""
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            clickedText = (event.text?.joinToString(" ") ?: "") + " " + (event.contentDescription ?: "")
            clickedText = clickedText.lowercase()
        }

        // ২. টাইপ করা টেক্সট ট্র্যাক করা (হোয়াটসঅ্যাপ/টেলিগ্রাম/ক্রোমের সার্চ বারে টাইপিং ধরার জন্য)
        var typedText = ""
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            typedText = event.text?.joinToString(" ")?.lowercase() ?: ""
        }

        // ৩. স্ক্রিনের সব টেক্সট একসাথে করা
        val screenText = mutableListOf<String>()
        event.text?.forEach { screenText.add(it.toString().lowercase()) }
        val nodeText = getEventText(event.source).lowercase()
        val fullTextContext = screenText.joinToString(" ") + " " + nodeText + " " + typedText

        // ==========================================
        // 🚨 STRICT BREAK MODE (Whitelist Only)
        // ==========================================
        if (isStrictBreakActive) {
            val isSystemApp = systemAllowedPackages.any { packageName.contains(it) }
            val isUserAllowedApp = allowList.any { packageName.contains(it) || fullTextContext.contains(it) }
            
            if (!isSystemApp && !isUserAllowedApp) {
                performGlobalAction(GLOBAL_ACTION_HOME) // মিনিমাইজ করবে
                showAppMainScreen() // আমাদের মেইন অ্যাপের টাইমার স্ক্রিন সামনে আনবে
                return
            }
        }

        // ==========================================
        // 🚨 MASTER LOCK (Firebase Control)
        // ==========================================
        if (isRemotelyLocked && !isStrictBreakActive) {
            if (packageName.isNotEmpty() && !systemAllowedPackages.contains(packageName)) {
                performGlobalAction(GLOBAL_ACTION_HOME)
                showBlockScreen("LOCKED")
                return
            }
        }

        // ==========================================
        // 📜 REGULAR BLOCK / ALLOW LIST CHECK
        // ==========================================
        if (!isStrictBreakActive && !isRemotelyLocked) {
            if (listMode == "BLOCK" && blockList.isNotEmpty()) {
                val shouldBlock = blockList.any { packageName.contains(it) || fullTextContext.contains(it) }
                if (shouldBlock && !systemAllowedPackages.contains(packageName)) {
                    performGlobalAction(GLOBAL_ACTION_HOME)
                    showBlockScreen("LOCKED")
                    return
                }
            } else if (listMode == "ALLOW" && allowList.isNotEmpty()) {
                val isSystemApp = systemAllowedPackages.any { packageName.contains(it) }
                val isUserAllowedApp = allowList.any { packageName.contains(it) || fullTextContext.contains(it) }
                if (!isSystemApp && !isUserAllowedApp) {
                    performGlobalAction(GLOBAL_ACTION_HOME)
                    showBlockScreen("LOCKED")
                    return
                }
            }
        }

        // ==========================================
        // 🔞 ADULT CONTENT BLOCK (Typing & Screen Check)
        // ==========================================
        if (blockAdult && fullTextContext.isNotBlank()) {
            val regexPattern = "\\b(${explicitKeywords.joinToString("|")})\\b".toRegex(RegexOption.IGNORE_CASE)
            if (regexPattern.containsMatchIn(fullTextContext)) {
                performGlobalAction(GLOBAL_ACTION_HOME)
                showBlockScreen("ADULT")
                return
            }
        }

        // ==========================================
        // 🎥 YOUTUBE SHORTS & FACEBOOK REELS BLOCK
        // ==========================================
        if (blockShorts && packageName.contains("youtube")) {
            if (clickedText.contains("shorts") || clickedText.contains("শর্টস") || 
                fullTextContext.contains("like this short") || fullTextContext.contains("dislike this short")) {
                performGlobalAction(GLOBAL_ACTION_BACK)
                return
            }
        }

        if (blockReels && packageName.contains("facebook")) {
            if (clickedText.contains("reels") || clickedText.contains("রিলস") || clickedText.contains("reel") || 
                fullTextContext.contains("reels audio") || fullTextContext.contains("swipe up to show more reels")) {
                performGlobalAction(GLOBAL_ACTION_BACK)
                return
            }
        }

        // ==========================================
        // 🛡️ STRICT SECURITY (Uninstall, Reboot, etc.)
        // ==========================================
        if (uninstallProtection) {
            if (packageName == "com.android.settings" || packageName == "com.google.android.packageinstaller") {
                if ((fullTextContext.contains("uninstall") || fullTextContext.contains("delete") || fullTextContext.contains("remove")) && 
                    (fullTextContext.contains("rasfocus") || fullTextContext.contains("blockerhero") || fullTextContext.contains("android_template"))) {
                    performGlobalAction(GLOBAL_ACTION_HOME)
                    showBlockScreen("SECURITY")
                    return
                }
            }
        }

        if (blockRecentAppsScreen && packageName == "com.android.systemui" && className.contains("Recents")) {
            performGlobalAction(GLOBAL_ACTION_HOME)
            return
        }

        if (blockPhoneReboot && (fullTextContext.contains("power off") || fullTextContext.contains("restart") || className.contains("GlobalActionsDialog"))) {
            performGlobalAction(GLOBAL_ACTION_BACK)
            performGlobalAction(GLOBAL_ACTION_HOME)
            return
        }

        if (blockNewInstalledApps && packageName == "com.android.vending" && fullTextContext.contains("install")) {
            performGlobalAction(GLOBAL_ACTION_HOME)
            showBlockScreen("NEW_APP")
            return
        }
    }

    private fun getEventText(node: AccessibilityNodeInfo?): String {
        if (node == null) return ""
        var text = (node.text?.toString() ?: "") + " " + (node.contentDescription?.toString() ?: "")
        for (i in 0 until node.childCount) {
            text += " " + getEventText(node.getChild(i))
        }
        return text
    }

    private fun showBlockScreen(reason: String) {
        val intent = Intent(this, BlockActivity::class.java)
        intent.putExtra("BLOCK_REASON", reason)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    // Strict Break এ অন্য অ্যাপ থেকে আমাদের মেইন অ্যাপে ফেরত আনার জন্য
    private fun showAppMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onInterrupt() {}
}
