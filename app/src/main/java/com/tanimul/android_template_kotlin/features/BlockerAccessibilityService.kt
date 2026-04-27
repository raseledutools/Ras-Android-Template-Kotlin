package com.tanimul.android_template_kotlin.features

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.app.NotificationCompat
import com.tanimul.android_template_kotlin.DataManager
import kotlin.random.Random

class BlockerAccessibilityService : AccessibilityService() {

    companion object {
        // UI থেকে সরাসরি সার্ভিসকে কল করার জন্য
        var instance: BlockerAccessibilityService? = null
    }

    // ==========================================
    // C++ Databases & Quotes
    // ==========================================
    private val hardcoreKeywords = listOf(
        "porn", "xxx", "sex", "nude", "nsfw", "sexy", "hentai", "rule34", "milf",
        "blowjob", "tits", "boobs", "pussy", "dick", "cock", "escort", "bdsm",
        "fetish", "erotica", "dildo", "webcam", "camgirls", "xvideos", "pornhub",
        "xnxx", "xhamster", "brazzers", "onlyfans", "playboy", "chaturbate",
        "stripchat", "eporner", "spankbang", "redtube", "youporn", "mia khalifa",
        "sunny leone", "dani daniels", "johnny sins", "kendra lust",
        "চটি", "পর্ণ", "সেক্স", "নগ্ন", "উলঙ্গ", "বেশ্যা", "মাগি", "খানকি",
        "যৌন", "পর্ণগ্রাফি", "রেন্ডি", "চোদাচুতি", "গরম ভিডিও", "খারাপ ছবি",
        "যৌন মিলন", "যৌনাঙ্গ", "চুদো", "নগ্নতা"
    )

    private val romanticKeywords = listOf(
        "hot dance", "seductive dance", "item song", "belly dance", "hot",
        "kissing scene", "bikini", "swimsuit", "sexy dance", "cleavage", "hot scene",
        "romantic kiss", "bedroom scene", "bath scene", "rain dance", "bold scene",
        "semi nude", "lingerie", "erotic", "hot song", "romantic video hot",
        "navel show", "deep neck", "short dress sexy", "unfaithful scene"
    )

    private val adultWebsites = listOf(
        "pornhub.com", "xvideos.com", "xnxx.com", "xhamster.com", "redtube.com",
        "youporn.com", "brazzers.com", "spankbang.com", "eporner.com", "chaturbate.com"
    )

    // Religious & Motivational Quotes
    private val muslimQuotesBn = listOf("মুমিনদের বলুন, তারা যেন তাদের দৃষ্টি নত রাখে...", "লজ্জাশীলতা ঈমানের অঙ্গ।")
    private val muslimQuotesEn = listOf("Tell the believing men to reduce their vision...", "Modesty is a branch of faith.")
    private val hinduQuotesBn = listOf("যে মনকে নিয়ন্ত্রণ করতে পারে না, তার মন তার সবচেয়ে বড় শত্রু।", "কাম, ক্রোধ এবং লোভ—এই তিনটি নরকের দ্বার।")
    private val hinduQuotesEn = listOf("For him who has conquered the mind, the mind is the best of friends.", "Lust, anger, and greed are the three doors to hell.")
    private val christianQuotesBn = listOf("খারাপ সাহচর্য ভালো চরিত্র নষ্ট করে।", "অহংকার পতনের মূল।")
    private val christianQuotesEn = listOf("Bad company ruins good morals.", "Pride goes before destruction.")
    private val motivationalQuotesBn = listOf(
        "সময়ের মূল্য বোঝো, জীবন তোমার মূল্য বুঝবে।",
        "সফলতা আসে ফোকাস থেকে, ডিস্ট্রাকশন থেকে নয়।",
        "আজকের সময় নষ্ট মানে, কালকের স্বপ্ন নষ্ট।",
        "যে নিজের মনকে নিয়ন্ত্রণ করতে পারে, সে পৃথিবী জয় করতে পারে।"
    )
    private val motivationalQuotesEn = listOf(
        "Understand the value of time, life will understand your value.",
        "Success comes from focus, not from distraction.",
        "Wasting time today means ruining tomorrow's dreams.",
        "He who can control his mind can conquer the world."
    )

    // ==========================================
    // Service Private Engine States
    // ==========================================
    private var lastPeriodicPopupTime: Long = System.currentTimeMillis()
    
    // Deep Study (Pomodoro) Engine Variables
    private var isDeepStudyActive = false
    private var isDeepStudyBreak = false
    private var dsAllowApps = mutableListOf("com.android.chrome", "com.google.android.youtube")
    private var dsAllowWebs = mutableListOf("wikipedia.org")

    // Overlays & Timers
    private var windowManager: android.view.WindowManager? = null
    private var overlayView: android.view.View? = null
    private var dsTimer: android.os.CountDownTimer? = null
    private var dsTimeLeftMillis: Long = 0
    private var mediaPlayer: android.media.MediaPlayer? = null
    private var floatingTimerView: android.view.View? = null
    private var timerTextView: android.widget.TextView? = null
    private var breakScreenView: android.view.View? = null

    // ==========================================
    // STEP 1: Initialization & Anti-Kill Foreground
    // ==========================================
    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this // UI থেকে কল করার জন্য
        DataManager.init(this) // DataManager চালু করা হলো

        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            notificationTimeout = 100
        }
        this.serviceInfo = info

        startForegroundServiceNotification()
    }

    private fun startForegroundServiceNotification() {
        val channelId = "rasfocus_service_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "RasFocus Protection Active",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("RasFocus Pro")
            .setContentText("Focus is active. Protecting your productivity.")
            .setSmallIcon(android.R.drawable.ic_secure)
            .setOngoing(true) 
            .build()

        startForeground(1001, notification)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    fun getInstalledApps(context: Context): List<String> {
        val pm = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        return packages.map { it.loadLabel(pm).toString() + " (${it.packageName})" }.sorted()
    }

    // ==========================================
    // STEP 2: Core Brain - Reading Screen & URL
    // ==========================================
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || (!DataManager.isFocusActive && !DataManager.isAdultFocusActive && !isDeepStudyActive)) return

        val packageName = event.packageName?.toString() ?: return
        var currentUrl = ""
        var screenText = ""

        // 24-Hour Lock & Periodic Popup Checker
        if (DataManager.is24HourLockActive) {
            if (System.currentTimeMillis() >= DataManager.lock24hEndTime) {
                DataManager.is24HourLockActive = false
                DataManager.isAdultFocusActive = false 
            } else {
                DataManager.isAdultFocusActive = true 
            }
        }

        if (DataManager.isPeriodicPopupsActive && DataManager.isAdultFocusActive) {
            val timePassed = System.currentTimeMillis() - lastPeriodicPopupTime
            if (timePassed >= 25 * 60 * 1000) { 
                showWarningPopup(getReligiousQuote(), false) 
                lastPeriodicPopupTime = System.currentTimeMillis()
            }
        }

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED || 
            event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            
            val rootNode = rootInActiveWindow ?: return
            
            if (packageName.contains("chrome") || packageName.contains("browser") || packageName.contains("edge")) {
                currentUrl = extractUrlFromBrowser(rootNode).lowercase()
            }
            
            screenText = event.text.joinToString(" ").lowercase()
            
            checkAndBlockContent(packageName, currentUrl, screenText)
            rootNode.recycle()
        }
    }

    private fun extractUrlFromBrowser(nodeInfo: AccessibilityNodeInfo?): String {
        if (nodeInfo == null) return ""
        
        if (nodeInfo.className == "android.widget.EditText") {
            val id = nodeInfo.viewIdResourceName
            if (id != null && (id.contains("url_bar") || id.contains("address_bar"))) {
                return nodeInfo.text?.toString() ?: ""
            }
        }
        
        for (i in 0 until nodeInfo.childCount) {
            val childNode = nodeInfo.getChild(i)
            val url = extractUrlFromBrowser(childNode)
            childNode?.recycle()
            if (url.isNotEmpty()) return url
        }
        return ""
    }

    // ==========================================
    // STEP 3: Advanced Filtering Logic (Syncs with DataManager)
    // ==========================================
    private fun checkAndBlockContent(packageName: String, url: String, screenText: String) {
        var shouldBlock = false
        var isSecurityWarning = false
        var blockReason = ""

        // ১. Strict Protection (Settings / Uninstall Block)
        if (DataManager.blockSettingsAndUninstall) {
            if (packageName.contains("com.android.settings") || packageName.contains("packageinstaller")) {
                shouldBlock = true
                isSecurityWarning = true
                blockReason = "Security Alert: Settings & Uninstallation are blocked during focus!"
            }
        }

        // ২. Adult & Hardcore Checking
        if (DataManager.isAdultFocusActive && !shouldBlock) {
            if (adultWebsites.any { url.contains(it) }) {
                shouldBlock = true; isSecurityWarning = true; blockReason = "Adult website detected!"
            }
            else if (hardcoreKeywords.any { url.contains(it) || screenText.contains(it) }) {
                shouldBlock = true; isSecurityWarning = true; blockReason = "Inappropriate content detected!"
            }
            else if (romanticKeywords.any { url.contains(it) || screenText.contains(it) }) {
                shouldBlock = true; isSecurityWarning = true; blockReason = "Restricted romantic content detected!"
            }
            else if (url.contains("youtube.com/shorts")) {
                shouldBlock = true; isSecurityWarning = true; blockReason = "YouTube Shorts are blocked for Focus!"
            } 
            else if (url.contains("facebook.com/reel")) {
                shouldBlock = true; isSecurityWarning = true; blockReason = "Facebook Reels are blocked for Focus!"
            }

            // Custom Adult Keywords
            if (!shouldBlock && DataManager.userCustomAdultKeywords.isNotEmpty()) {
                if (DataManager.userCustomAdultKeywords.any { url.contains(it.lowercase()) || screenText.contains(it.lowercase()) }) {
                    shouldBlock = true; isSecurityWarning = true; blockReason = "Blocked by your custom keywords!"
                }
            }
        }

        // ৩. Simple Blocks Website Checking
        if (DataManager.isFocusActive && !shouldBlock && url.isNotEmpty()) {
            for (web in DataManager.userWebList) {
                val coreName = if (web.contains(".")) web.substringBefore(".") else web
                if (coreName.length > 2 && url.contains(coreName)) {
                    shouldBlock = true
                    blockReason = "This website is in your blocklist."
                    break
                }
            }
        }

        // ৪. Apps Blocking & Deep Study Strict Logic
        if (!shouldBlock) {
            val isSystemCriticalApp = packageName.contains("launcher") || 
                                      packageName.contains("systemui") || 
                                      packageName.contains("dialer") || 
                                      packageName.contains("telecom") || 
                                      packageName.contains("messaging") || 
                                      packageName.contains("mms") || 
                                      packageName == "com.tanimul.android_template_kotlin"

            // Simple Blocks Apps
            if (DataManager.isFocusActive) {
                if (DataManager.simpleBlockMode == 1) { 
                    if (DataManager.userAppList.any { packageName.contains(it) }) {
                        shouldBlock = true; blockReason = "This app is in your blocklist."
                    }
                } else if (DataManager.simpleBlockMode == 0) { 
                    if (!isSystemCriticalApp && !DataManager.userAppList.any { packageName.contains(it) }) {
                        shouldBlock = true; blockReason = "Focus is Active. Only allowed apps can run."
                    }
                }
            }

            // Deep Study Strict Logic
            if (isDeepStudyActive && DataManager.isDeepStudyStrict && !shouldBlock && !isSystemCriticalApp) {
                val pauseBlocking = isDeepStudyBreak && !dsKeepBlockingInBreak // TODO: Add KeepBlockingInBreak to DataManager later if needed
                if (!pauseBlocking) {
                    val appAllowed = dsAllowApps.any { packageName.contains(it) }
                    val webAllowed = url.isNotEmpty() && dsAllowWebs.any { url.contains(it) }
                    
                    if (!appAllowed && !webAllowed) {
                        shouldBlock = true; isSecurityWarning = true
                        blockReason = "Deep Study Strict Mode: Stay Focused!"
                    }
                }
            }
        }

        if (shouldBlock) {
            triggerBlockAction(blockReason, isSecurityWarning)
        }
    }

    private fun triggerBlockAction(reason: String, isSecurityWarning: Boolean) {
        performGlobalAction(GLOBAL_ACTION_HOME)
        
        // Streak Penalty
        if (!isSecurityWarning && DataManager.isAdultFocusActive) { 
            DataManager.totalBlockedCount++
            DataManager.cleanStreakDays = 0 
        }

        val displayMessage = if (isSecurityWarning || !DataManager.showQuotes) reason else getReligiousQuote()
        showWarningPopup(displayMessage, isSecurityWarning)
    }

    private fun getReligiousQuote(): String {
        val quotesList = when (DataManager.adultReligion) {
            0 -> if (DataManager.adultLanguage == 0) muslimQuotesBn else muslimQuotesEn
            1 -> if (DataManager.adultLanguage == 0) hinduQuotesBn else hinduQuotesEn
            2 -> if (DataManager.adultLanguage == 0) christianQuotesBn else christianQuotesEn
            else -> if (DataManager.adultLanguage == 0) motivationalQuotesBn else motivationalQuotesEn 
        }
        return quotesList[Random.nextInt(quotesList.size)]
    }

    // ==========================================
    // Friend Control Validation
    // ==========================================
    fun tryStopFocus(inputPassword: String): Boolean {
        if (DataManager.is24HourLockActive) return false 

        return if (DataManager.controlMode == 1) { 
            if (inputPassword == friendControlPassword) {
                DataManager.isAdultFocusActive = false; true 
            } else false 
        } else { 
            DataManager.isAdultFocusActive = false; true 
        }
    }

    // ==========================================
    // DEEP STUDY ENGINE: Timer, Audio & Overlays
    // ==========================================
    fun startDeepStudySession(focusMinutes: Int, playSound: Boolean) {
        isDeepStudyActive = true
        isDeepStudyBreak = false
        val timeMillis = focusMinutes * 60 * 1000L

        if (playSound) playAmbientSound()
        showFloatingTimer()

        dsTimer?.cancel()
        dsTimer = object : android.os.CountDownTimer(timeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                dsTimeLeftMillis = millisUntilFinished
                updateFloatingTimerText(millisUntilFinished)
                
                if (millisUntilFinished in 59000..60000) {
                    showWarningPopup("⏳ Just 1 Minute Remaining! Keep Going!", false)
                }
            }
            override fun onFinish() {
                stopAmbientSound()
                removeFloatingTimer()
                startDeepStudyBreak(5) // Default 5 mins break
            }
        }.start()
    }

    private fun startDeepStudyBreak(breakMinutes: Int) {
        isDeepStudyBreak = true
        val timeMillis = breakMinutes * 60 * 1000L
        showBreakScreenOverlay()

        dsTimer?.cancel()
        dsTimer = object : android.os.CountDownTimer(timeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                removeBreakScreenOverlay()
                isDeepStudyActive = false
                DataManager.isDeepStudyStrict = false
                showWarningPopup("🎉 Session Completed Successfully!", false)
            }
        }.start()
    }

    private fun playAmbientSound() {}
    private fun stopAmbientSound() { mediaPlayer?.stop(); mediaPlayer?.release(); mediaPlayer = null }

    private fun showFloatingTimer() {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.post {
            if (floatingTimerView != null) return@post
            
            windowManager = getSystemService(Context.WINDOW_SERVICE) as android.view.WindowManager
            val params = android.view.WindowManager.LayoutParams(
                android.view.WindowManager.LayoutParams.WRAP_CONTENT,
                android.view.WindowManager.LayoutParams.WRAP_CONTENT,
                android.view.WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                android.graphics.PixelFormat.TRANSLUCENT
            ).apply {
                gravity = android.view.Gravity.TOP or android.view.Gravity.END
                x = 50; y = 150
            }

            val layout = android.widget.LinearLayout(this).apply {
                setBackgroundColor(android.graphics.Color.parseColor("#0CA8B0")) 
                setPadding(30, 20, 30, 20)
                val shape = android.graphics.drawable.GradientDrawable()
                shape.cornerRadius = 16f
                shape.setColor(android.graphics.Color.parseColor("#0CA8B0"))
                background = shape
            }
            timerTextView = android.widget.TextView(this).apply {
                setTextColor(android.graphics.Color.WHITE)
                textSize = 18f
                setTypeface(null, android.graphics.Typeface.BOLD)
                text = "00:00"
            }
            layout.addView(timerTextView)
            floatingTimerView = layout
            windowManager?.addView(floatingTimerView, params)
        }
    }

    private fun updateFloatingTimerText(millis: Long) {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.post {
            val mins = millis / 60000
            val secs = (millis % 60000) / 1000
            timerTextView?.text = String.format("%02d:%02d", mins, secs)
        }
    }

    private fun removeFloatingTimer() {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.post {
            floatingTimerView?.let { windowManager?.removeView(it) }
            floatingTimerView = null
        }
    }

    private fun showBreakScreenOverlay() {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.post {
            if (breakScreenView != null) return@post

            windowManager = getSystemService(Context.WINDOW_SERVICE) as android.view.WindowManager
            val params = android.view.WindowManager.LayoutParams(
                android.view.WindowManager.LayoutParams.MATCH_PARENT,
                android.view.WindowManager.LayoutParams.MATCH_PARENT,
                android.view.WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                android.graphics.PixelFormat.TRANSLUCENT
            )

            val layout = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setBackgroundColor(android.graphics.Color.parseColor("#1E293B")) 
                gravity = android.view.Gravity.CENTER
            }

            val titleView = android.widget.TextView(this).apply {
                text = "TAKE A BREAK!"
                textSize = 40f
                setTextColor(android.graphics.Color.parseColor("#10B981")) 
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, 0, 0, 30)
            }
            
            val subView = android.widget.TextView(this).apply {
                text = "Breathe deep, rest your eyes, and relax your mind."
                textSize = 16f
                setTextColor(android.graphics.Color.WHITE)
            }

            layout.addView(titleView)
            layout.addView(subView)

            breakScreenView = layout
            windowManager?.addView(breakScreenView, params)
        }
    }

    private fun removeBreakScreenOverlay() {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.post {
            breakScreenView?.let { windowManager?.removeView(it) }
            breakScreenView = null
        }
    }

    // ==========================================
    // STEP 4: Crash-Free Fast Popup Overlay
    // ==========================================
    private fun showWarningPopup(message: String, isSecurityWarning: Boolean) {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        
        handler.post {
            removeWarningPopup()

            windowManager = getSystemService(Context.WINDOW_SERVICE) as android.view.WindowManager

            val layoutParams = android.view.WindowManager.LayoutParams(
                android.view.WindowManager.LayoutParams.MATCH_PARENT,
                android.view.WindowManager.LayoutParams.WRAP_CONTENT,
                android.view.WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, 
                android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                android.graphics.PixelFormat.TRANSLUCENT
            ).apply {
                gravity = android.view.Gravity.TOP or android.view.Gravity.CENTER_HORIZONTAL
                y = 150 
            }

            val linearLayout = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                val bgColor = if (isSecurityWarning) "#E74C3C" else "#0CA8B0" 
                setBackgroundColor(android.graphics.Color.parseColor(bgColor)) 
                setPadding(50, 50, 50, 50)
            }

            val titleView = android.widget.TextView(this).apply {
                text = if (isSecurityWarning) "ACCESS DENIED!" else "FOCUS ACTIVE!"
                textSize = 20f
                setTextColor(android.graphics.Color.WHITE)
                setTypeface(null, android.graphics.Typeface.BOLD)
                gravity = android.view.Gravity.CENTER
            }

            val reasonView = android.widget.TextView(this).apply {
                text = message
                textSize = 15f
                setTextColor(android.graphics.Color.WHITE)
                gravity = android.view.Gravity.CENTER
                setPadding(0, 20, 0, 10)
            }

            linearLayout.addView(titleView)
            linearLayout.addView(reasonView)

            overlayView = linearLayout
            windowManager?.addView(overlayView, layoutParams)

            handler.postDelayed({ removeWarningPopup() }, 5000) 
        }
    }

    private fun removeWarningPopup() {
        if (overlayView != null && windowManager != null) {
            try {
                windowManager?.removeView(overlayView)
                overlayView = null
            } catch (e: Exception) {}
        }
    }

    override fun onInterrupt() {}
}
