package com.tanimul.android_template_kotlin.features

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.tanimul.android_template_kotlin.DataManager
import kotlin.random.Random

class BlockerAccessibilityService : AccessibilityService() {

    companion object {
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

    // Religious Quotes (For Full Screen Adult Blocks)
    private val muslimQuotesBn = listOf("মুমিনদের বলুন, তারা যেন তাদের দৃষ্টি নত রাখে...", "লজ্জাশীলতা ঈমানের অঙ্গ।")
    private val muslimQuotesEn = listOf("Tell the believing men to reduce their vision...", "Modesty is a branch of faith.")
    private val hinduQuotesBn = listOf("যে মনকে নিয়ন্ত্রণ করতে পারে গঠন, তার মন তার সবচেয়ে বড় শত্রু।", "কাম, ক্রোধ এবং লোভ—এই তিনটি নরকের দ্বার।")
    private val hinduQuotesEn = listOf("For him who has conquered the mind, the mind is the best of friends.", "Lust, anger, and greed are the three doors to hell.")
    private val christianQuotesBn = listOf("খারাপ সাহচর্য ভালো চরিত্র নষ্ট করে।", "অহংকার পতনের মূল।")
    private val christianQuotesEn = listOf("Bad company ruins good morals.", "Pride goes before destruction.")
    
    // Motivational Quotes (For Deep Study / Pomodoro)
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
    
    // Overlays & Timers
    private var windowManager: android.view.WindowManager? = null
    private var overlayView: android.view.View? = null
    private var dsTimer: android.os.CountDownTimer? = null
    private var dsTimeLeftMillis: Long = 0
    private var floatingTimerView: android.view.View? = null
    private var timerTextView: android.widget.TextView? = null
    private var breakScreenView: android.view.View? = null
    private var sessionCompleteView: android.view.View? = null
    private var fullScreenHadithView: android.view.View? = null // 🟢 Full screen Hadith View

    // Audio & Sound Synthesis Variables
    private var audioTrack: android.media.AudioTrack? = null
    private var isPlayingNoise = false
    private var noiseThread: Thread? = null

    // Drag & Drop Variables
    private var initialX: Int = 0
    private var initialY: Int = 0
    private var initialTouchX: Float = 0f
    private var initialTouchY: Float = 0f

    // Session Recovery
    private lateinit var recoveryPrefs: SharedPreferences

    // ==========================================
    // STEP 1: Initialization
    // ==========================================
    override fun onCreate() {
        super.onCreate()
        DataManager.init(this)
        recoveryPrefs = getSharedPreferences("FocusRecovery", Context.MODE_PRIVATE)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this

        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            notificationTimeout = 100
        }
        this.serviceInfo = info

        // AUTO RECOVERY LOGIC
        val isSavedActive = recoveryPrefs.getBoolean("isTimerActive", false)
        val targetEndTime = recoveryPrefs.getLong("targetEndTime", 0L)
        val sessionType = recoveryPrefs.getInt("sessionType", 0) 
        val soundType = recoveryPrefs.getInt("soundType", 0)
        val playSound = recoveryPrefs.getBoolean("playSound", false)

        if (isSavedActive && targetEndTime > System.currentTimeMillis()) {
            val remainingMillis = targetEndTime - System.currentTimeMillis()
            if (sessionType == 0) {
                resumeDeepStudySession(remainingMillis, playSound, soundType)
            } else {
                startDeepStudyBreak((remainingMillis / 60000).toInt())
            }
        } else {
            recoveryPrefs.edit().clear().apply()
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null; return super.onUnbind(intent)
    }
    override fun onDestroy() {
        super.onDestroy(); instance = null; stopAmbientSound()
    }

    // ==========================================
    // HELPER: SYSTEM APP CHECKER
    // ==========================================
    private fun isSystemApp(packageName: String): Boolean {
        return packageName.contains("launcher") || 
               packageName.contains("systemui") || 
               packageName.contains("dialer") || 
               packageName.contains("telecom") || 
               packageName.contains("messaging") || 
               packageName.contains("mms") || 
               packageName.contains("contacts") || 
               packageName.contains("inputmethod") || 
               packageName.contains("keyboard") || 
               packageName == "com.tanimul.android_template_kotlin"
    }

    // ==========================================
    // STEP 2: Core Brain - Reading Screen & URL
    // ==========================================
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || (!DataManager.isFocusActive && !DataManager.isAdultFocusActive && !isDeepStudyActive)) return

        val packageName = event.packageName?.toString() ?: return

        // 🟢 ১. টাইপিং ফিল্টার এবং ফুল স্ক্রিন হাদিস লজিক
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            val source = event.source
            val typedText = event.text.joinToString(" ").lowercase()

            if (!isSystemApp(packageName) && DataManager.isAdultFocusActive && hardcoreKeywords.any { typedText.contains(it) }) {
                source?.let { node ->
                    val selectArgs = android.os.Bundle()
                    selectArgs.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, 0)
                    selectArgs.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, typedText.length)
                    node.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION, selectArgs)

                    val clearArgs = android.os.Bundle()
                    clearArgs.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "")
                    node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, clearArgs)
                }
                // 🟢 Adult Content Typed -> Trigger Tab Close & Full Screen Hadith
                triggerAdultBlockAction(packageName)
                return
            }
        }

        if (DataManager.is24HourLockActive) {
            if (System.currentTimeMillis() >= DataManager.lock24hEndTime) {
                DataManager.is24HourLockActive = false
                DataManager.isAdultFocusActive = false 
            } else {
                DataManager.isAdultFocusActive = true 
            }
        }

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED || 
            event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            
            val rootNode = rootInActiveWindow ?: return
            var currentUrl = ""
            
            if (packageName.contains("chrome") || packageName.contains("browser") || packageName.contains("edge") || packageName.contains("firefox")) {
                currentUrl = extractUrlFromBrowser(rootNode).lowercase()
            }
            
            val screenText = event.text.joinToString(" ").lowercase()
            
            if (isDeepStudyActive && DataManager.isDeepStudyStrict) {
                checkDeepStudyBlocking(packageName, currentUrl)
            } else {
                checkAndBlockContent(packageName, currentUrl, screenText)
            }
            
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
    // 🟢 DEEP STUDY BLOCKING LOGIC
    // ==========================================
    private fun checkDeepStudyBlocking(packageName: String, url: String) {
        if (isSystemApp(packageName)) return

        val allowedApps = DataManager.dsAllowAppList
        val allowedWebs = DataManager.dsAllowWebList

        val isAppAllowed = allowedApps.any { packageName.contains(it, ignoreCase = true) }
        val isWebAllowed = url.isNotEmpty() && allowedWebs.any { url.contains(it.substringBefore("."), ignoreCase = true) }

        val pauseDuringBreak = isDeepStudyBreak && !DataManager.dsKeepBlockingInBreak

        if (!isAppAllowed && !isWebAllowed && !pauseDuringBreak) {
            val goBackSuccess = performGlobalAction(GLOBAL_ACTION_BACK)
            if (!goBackSuccess) performGlobalAction(GLOBAL_ACTION_HOME)
            
            val quoteList = if (DataManager.adultLanguage == 0) motivationalQuotesBn else motivationalQuotesEn
            val randomQuote = quoteList[Random.nextInt(quoteList.size)]
            showWarningPopup(randomQuote, false, true)
        }
    }

    // ==========================================
    // 🟢 NORMAL BLOCKING LOGIC (Fixed Shorts & Adult Web)
    // ==========================================
    private fun checkAndBlockContent(packageName: String, url: String, screenText: String) {
        var shouldBlockNormal = false
        var isAdultViolation = false
        var blockReason = ""

        if (DataManager.blockSettingsAndUninstall) {
            if (packageName.contains("com.android.settings") || packageName.contains("packageinstaller")) {
                shouldBlockNormal = true; blockReason = "Settings are blocked!"
            }
        }

        if (DataManager.isAdultFocusActive && !shouldBlockNormal) {
            // 🟢 Adult Websites Check (Fixed to check screenText too for hidden URLs)
            if (adultWebsites.any { url.contains(it) || screenText.contains(it.substringBefore(".")) }) {
                isAdultViolation = true
            }
            else if (hardcoreKeywords.any { url.contains(it) || screenText.contains(it) }) {
                isAdultViolation = true
            }
            else if (romanticKeywords.any { url.contains(it) || screenText.contains(it) }) {
                isAdultViolation = true
            }
            // 🟢 Super Logic for Shorts and Reels
            else if ((packageName.contains("youtube") && screenText.contains("shorts")) || url.contains("shorts")) {
                shouldBlockNormal = true; blockReason = "YouTube Shorts are blocked!"
            }
            else if ((packageName.contains("facebook") && screenText.contains("reels")) || url.contains("reel")) {
                shouldBlockNormal = true; blockReason = "Facebook Reels are blocked!"
            }
        }

        if (DataManager.isFocusActive && !shouldBlockNormal && !isAdultViolation && url.isNotEmpty()) {
            for (web in DataManager.userWebList) {
                val coreName = if (web.contains(".")) web.substringBefore(".") else web
                if (coreName.length > 2 && url.contains(coreName)) {
                    shouldBlockNormal = true; blockReason = "Website is in your blocklist."; break
                }
            }
        }

        if (!shouldBlockNormal && !isAdultViolation) {
            if (DataManager.isFocusActive) {
                if (DataManager.simpleBlockMode == 1) { 
                    if (DataManager.userAppList.any { packageName.contains(it) }) {
                        shouldBlockNormal = true; blockReason = "App is in your blocklist."
                    }
                } else if (DataManager.simpleBlockMode == 0) { 
                    if (!isSystemApp(packageName) && !DataManager.userAppList.any { packageName.contains(it) }) {
                        shouldBlockNormal = true; blockReason = "Only allowed apps can run."
                    }
                }
            }
        }

        // Action Executions
        if (isAdultViolation) {
            triggerAdultBlockAction(packageName)
        } else if (shouldBlockNormal) {
            performGlobalAction(GLOBAL_ACTION_HOME) 
            showWarningPopup(if (!DataManager.showQuotes) blockReason else getReligiousQuote(), true, false)
        }
    }

    // ==========================================
    // 🟢 ADULT BLOCK ACTION (Closes Tab + Full Screen Hadith)
    // ==========================================
    private fun triggerAdultBlockAction(packageName: String) {
        val isBrowser = packageName.contains("chrome") || packageName.contains("browser") || 
                        packageName.contains("edge") || packageName.contains("firefox")
        
        // 브াউজার হলে ২ বার ব্যাক চাপবে (ট্যাব ক্লোজ করার জন্য), না হলে ডিরেক্ট হোম
        if (isBrowser) {
            performGlobalAction(GLOBAL_ACTION_BACK)
            Thread.sleep(150) // ছোট্ট গ্যাপ যাতে ডাবল ব্যাক কাজ করে
            performGlobalAction(GLOBAL_ACTION_BACK)
        } else {
            performGlobalAction(GLOBAL_ACTION_HOME)
        }
        
        DataManager.totalBlockedCount++
        DataManager.cleanStreakDays = 0 
        
        showFullScreenHadithPopup(getReligiousQuote())
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
    // 🟢 DYNAMIC PASSWORD FIX
    // ==========================================
    fun tryStopFocus(inputPassword: String): Boolean {
        if (DataManager.is24HourLockActive) return false 
        
        // 🟢 DataManager বা SharedPreferences থেকে ইউজার সেট করা পাসওয়ার্ড পড়া হচ্ছে। ডিফল্ট 1234
        val prefs = getSharedPreferences("RasFocusData", Context.MODE_PRIVATE)
        val savedPassword = prefs.getString("friendPassword", "1234") ?: "1234"

        return if (DataManager.controlMode == 1) { 
            if (inputPassword == savedPassword) { DataManager.isAdultFocusActive = false; true } else false 
        } else { DataManager.isAdultFocusActive = false; true }
    }

    // ==========================================
    // DEEP STUDY ENGINE: Timer, Audio & Auto Save
    // ==========================================
    fun startDeepStudySession(focusMinutes: Int, playSound: Boolean, soundType: Int = 0) {
        val timeMillis = focusMinutes * 60 * 1000L
        resumeDeepStudySession(timeMillis, playSound, soundType)
    }

    private fun resumeDeepStudySession(timeMillis: Long, playSound: Boolean, soundType: Int) {
        isDeepStudyActive = true; isDeepStudyBreak = false

        recoveryPrefs.edit().putBoolean("isTimerActive", true).putLong("targetEndTime", System.currentTimeMillis() + timeMillis)
            .putInt("sessionType", 0).putBoolean("playSound", playSound).putInt("soundType", soundType).apply()

        if (playSound) playAmbientSound(soundType)
        showFloatingTimer()

        dsTimer?.cancel()
        dsTimer = object : android.os.CountDownTimer(timeMillis, 30) {
            override fun onTick(millisUntilFinished: Long) {
                dsTimeLeftMillis = millisUntilFinished; updateFloatingTimerText(millisUntilFinished)
                if (millisUntilFinished in 59000..60030) showWarningPopup("⏳ Just 1 Minute Remaining! Keep Going!", false, true)
            }
            override fun onFinish() {
                stopAmbientSound(); removeFloatingTimer()
                isDeepStudyActive = false; DataManager.isDeepStudyStrict = false
                recoveryPrefs.edit().clear().apply()
                sendBroadcast(Intent("POMODORO_SESSION_UPDATE"))
                showSessionCompletePopup()
            }
        }.start()
    }

    private fun startDeepStudyBreak(breakMinutes: Int) {
        isDeepStudyBreak = true
        val timeMillis = breakMinutes * 60 * 1000L
        showBreakScreenOverlay()

        recoveryPrefs.edit().putBoolean("isTimerActive", true).putLong("targetEndTime", System.currentTimeMillis() + timeMillis).putInt("sessionType", 1).apply()

        dsTimer?.cancel()
        dsTimer = object : android.os.CountDownTimer(timeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                removeBreakScreenOverlay()
                isDeepStudyActive = false; DataManager.isDeepStudyStrict = false
                recoveryPrefs.edit().clear().apply() 
                showWarningPopup("🎉 Break Completed! Ready to focus?", false, true)
                sendBroadcast(Intent("POMODORO_SESSION_UPDATE"))
            }
        }.start()
    }

    // ==========================================
    // SESSION COMPLETE INTERACTIVE POPUP
    // ==========================================
    private fun showSessionCompletePopup() {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.post {
            if (sessionCompleteView != null) return@post

            windowManager = getSystemService(Context.WINDOW_SERVICE) as android.view.WindowManager
            val params = android.view.WindowManager.LayoutParams(
                android.view.WindowManager.LayoutParams.MATCH_PARENT, android.view.WindowManager.LayoutParams.MATCH_PARENT,
                android.view.WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                android.graphics.PixelFormat.TRANSLUCENT
            )

            val layout = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.VERTICAL; gravity = android.view.Gravity.CENTER
                setBackgroundColor(android.graphics.Color.parseColor("#E6000000"))
            }

            val card = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.VERTICAL; gravity = android.view.Gravity.CENTER; setPadding(60, 80, 60, 80)
                val shape = android.graphics.drawable.GradientDrawable(); shape.cornerRadius = 40f; shape.setColor(android.graphics.Color.WHITE)
                background = shape
                layoutParams = android.widget.LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT).apply { setMargins(80, 0, 80, 0) }
            }

            val title = android.widget.TextView(this).apply {
                text = "SESSION COMPLETED! 🎉"; textSize = 22f; setTextColor(android.graphics.Color.parseColor("#0CA8B0"))
                setTypeface(null, android.graphics.Typeface.BOLD); gravity = android.view.Gravity.CENTER; setPadding(0, 0, 0, 60)
            }

            val btnRest = android.widget.Button(this).apply {
                text = "Take a Rest (${DataManager.dsRestMin}m)"; setTextColor(android.graphics.Color.WHITE)
                val btnShape = android.graphics.drawable.GradientDrawable(); btnShape.cornerRadius = 24f; btnShape.setColor(android.graphics.Color.parseColor("#10B981"))
                background = btnShape
                layoutParams = android.widget.LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, 140).apply { setMargins(0, 0, 0, 30) }
                setOnClickListener { removeSessionCompletePopup(); startDeepStudyBreak(DataManager.dsRestMin) }
            }

            val btnStart = android.widget.Button(this).apply {
                text = "Start Again (${DataManager.dsFocusMin}m)"; setTextColor(android.graphics.Color.WHITE)
                val btnShape = android.graphics.drawable.GradientDrawable(); btnShape.cornerRadius = 24f; btnShape.setColor(android.graphics.Color.parseColor("#0CA8B0"))
                background = btnShape
                layoutParams = android.widget.LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, 140).apply { setMargins(0, 0, 0, 30) }
                setOnClickListener {
                    removeSessionCompletePopup()
                    val soundType = recoveryPrefs.getInt("soundType", 0)
                    val playSound = recoveryPrefs.getBoolean("playSound", false)
                    startDeepStudySession(DataManager.dsFocusMin, playSound, soundType)
                }
            }

            val btnClose = android.widget.Button(this).apply {
                text = "Close & Reset"; setTextColor(android.graphics.Color.WHITE)
                val btnShape = android.graphics.drawable.GradientDrawable(); btnShape.cornerRadius = 24f; btnShape.setColor(android.graphics.Color.parseColor("#E74C3C"))
                background = btnShape
                layoutParams = android.widget.LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, 140)
                setOnClickListener { removeSessionCompletePopup() }
            }

            card.addView(title); card.addView(btnRest); card.addView(btnStart); card.addView(btnClose); layout.addView(card)
            sessionCompleteView = layout
            try { windowManager?.addView(sessionCompleteView, params) } catch (e: Exception) {}
        }
    }

    private fun removeSessionCompletePopup() {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.post {
            sessionCompleteView?.let { try { windowManager?.removeView(it) } catch (e: Exception) {} }
            sessionCompleteView = null
        }
    }

    // ==========================================
    // SYNTHESIZED 10 TYPES OF AMBIENT SOUNDS
    // ==========================================
    private fun playAmbientSound(soundType: Int) {
        if (isPlayingNoise) return
        isPlayingNoise = true

        val sampleRate = 44100
        val bufferSize = android.media.AudioTrack.getMinBufferSize(sampleRate, android.media.AudioFormat.CHANNEL_OUT_MONO, android.media.AudioFormat.ENCODING_PCM_16BIT)
        audioTrack = android.media.AudioTrack(android.media.AudioManager.STREAM_MUSIC, sampleRate, android.media.AudioFormat.CHANNEL_OUT_MONO, android.media.AudioFormat.ENCODING_PCM_16BIT, bufferSize, android.media.AudioTrack.MODE_STREAM)
        audioTrack?.play()

        noiseThread = Thread {
            val buffer = ShortArray(bufferSize); val random = java.util.Random(); var lastOut = 0.0; var phase = 0.0
            while (isPlayingNoise) {
                for (i in buffer.indices) {
                    val white = (random.nextDouble() * 2 - 1); var output = 0.0
                    when (soundType) {
                        0 -> output = white * 0.1 
                        1 -> { lastOut = (lastOut + 0.02 * white) / 1.02; output = lastOut * 3.5 } 
                        2 -> { lastOut = (lastOut + 0.01 * white) / 1.01; output = lastOut * 4.5 } 
                        3 -> { lastOut = (lastOut + 0.04 * white) / 1.04; output = lastOut * 2.5 } 
                        4 -> { lastOut = (lastOut + 0.02 * white) / 1.02; output = lastOut * 3.5 + (if (random.nextDouble() > 0.99) white * 0.3 else 0.0) } 
                        5 -> { lastOut = (lastOut + 0.02 * white) / 1.02; output = lastOut * 2.0 + white * 0.05 } 
                        6 -> { lastOut = (lastOut + 0.015 * white) / 1.015; phase += 0.0001; val mod = Math.sin(phase) * 0.5 + 0.5; output = lastOut * 3.0 * (0.4 + 0.6 * mod) } 
                        7 -> { lastOut = (lastOut + 0.005 * white) / 1.005; output = lastOut * 6.0 } 
                        8 -> { lastOut = (lastOut + 0.008 * white) / 1.008; phase += 0.0005; val drone = Math.sin(phase) * 0.15; output = lastOut * 4.0 + drone } 
                        9 -> { lastOut = (lastOut + 0.01 * white) / 1.01; phase += 0.0002; val throb = Math.sin(phase) * 0.3; output = lastOut * 3.5 * (0.7 + throb) } 
                        else -> { lastOut = (lastOut + 0.02 * white) / 1.02; output = lastOut * 3.5 }
                    }
                    if (output > 1.0) output = 1.0; if (output < -1.0) output = -1.0
                    buffer[i] = (output * Short.MAX_VALUE).toInt().toShort()
                }
                audioTrack?.write(buffer, 0, buffer.size)
            }
        }
        noiseThread?.start()
    }

    private fun stopAmbientSound() {
        isPlayingNoise = false; try { noiseThread?.join(500) } catch (e: Exception) {}
        audioTrack?.let { if (it.playState == android.media.AudioTrack.PLAYSTATE_PLAYING) it.stop(); it.release() }
        audioTrack = null
    }

    // ==========================================
    // FLOATING STOPWATCH (Drag & Drop)
    // ==========================================
    private fun showFloatingTimer() {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.post {
            if (floatingTimerView != null) return@post
            windowManager = getSystemService(Context.WINDOW_SERVICE) as android.view.WindowManager
            val params = android.view.WindowManager.LayoutParams(
                android.view.WindowManager.LayoutParams.WRAP_CONTENT, android.view.WindowManager.LayoutParams.WRAP_CONTENT,
                android.view.WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                android.graphics.PixelFormat.TRANSLUCENT
            ).apply { gravity = android.view.Gravity.TOP or android.view.Gravity.START; x = 100; y = 200 }

            val layout = android.widget.LinearLayout(this).apply {
                setPadding(40, 20, 40, 20)
                val shape = android.graphics.drawable.GradientDrawable(); shape.cornerRadius = 30f; shape.setColor(android.graphics.Color.parseColor("#0CA8B0"))
                background = shape
                setOnTouchListener { _, event ->
                    when (event.action) {
                        android.view.MotionEvent.ACTION_DOWN -> { initialX = params.x; initialY = params.y; initialTouchX = event.rawX; initialTouchY = event.rawY; true }
                        android.view.MotionEvent.ACTION_MOVE -> { params.x = initialX + (event.rawX - initialTouchX).toInt(); params.y = initialY + (event.rawY - initialTouchY).toInt(); windowManager?.updateViewLayout(this, params); true }
                        else -> false
                    }
                }
            }

            timerTextView = android.widget.TextView(this).apply {
                setTextColor(android.graphics.Color.WHITE); textSize = 22f; setTypeface(null, android.graphics.Typeface.BOLD); text = "00:00:00"
            }
            layout.addView(timerTextView); floatingTimerView = layout
            try { windowManager?.addView(floatingTimerView, params) } catch (e: Exception) {}
        }
    }

    private fun updateFloatingTimerText(millis: Long) {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.post {
            val mins = (millis / 1000) / 60; val secs = (millis / 1000) % 60; val ms = (millis % 1000) / 10 
            timerTextView?.text = String.format("%02d:%02d:%02d", mins, secs, ms)
        }
    }

    private fun removeFloatingTimer() {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.post { floatingTimerView?.let { try { windowManager?.removeView(it) } catch (e: Exception) {} }; floatingTimerView = null }
    }

    // ==========================================
    // COLORFUL BREAK SCREEN
    // ==========================================
    private fun showBreakScreenOverlay() {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.post {
            if (breakScreenView != null) return@post
            windowManager = getSystemService(Context.WINDOW_SERVICE) as android.view.WindowManager
            val params = android.view.WindowManager.LayoutParams(
                android.view.WindowManager.LayoutParams.MATCH_PARENT, android.view.WindowManager.LayoutParams.MATCH_PARENT,
                android.view.WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                android.graphics.PixelFormat.TRANSLUCENT
            )

            val layout = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.VERTICAL; gravity = android.view.Gravity.CENTER
                val gradient = android.graphics.drawable.GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.TL_BR, intArrayOf(android.graphics.Color.parseColor("#4A00E0"), android.graphics.Color.parseColor("#8E2DE2")))
                background = gradient
            }

            val titleView = android.widget.TextView(this).apply {
                text = "TAKE A BREAK!"; textSize = 45f; setTextColor(android.graphics.Color.WHITE); setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, 0, 0, 30); gravity = android.view.Gravity.CENTER
            }
            val subView = android.widget.TextView(this).apply {
                text = "Breathe deep, rest your eyes, and relax your mind."; textSize = 18f; setTextColor(android.graphics.Color.parseColor("#E2E8F0")); gravity = android.view.Gravity.CENTER
            }
            layout.addView(titleView); layout.addView(subView); breakScreenView = layout
            try { windowManager?.addView(breakScreenView, params) } catch (e: Exception) {}
        }
    }

    private fun removeBreakScreenOverlay() {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.post { breakScreenView?.let { try { windowManager?.removeView(it) } catch (e: Exception) {} }; breakScreenView = null }
    }

    // ==========================================
    // 🟢 NEW: FULL SCREEN HADITH POPUP (For Adult Content)
    // ==========================================
    private fun showFullScreenHadithPopup(message: String) {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.post {
            removeFullScreenHadithPopup() // Remove if already exists
            windowManager = getSystemService(Context.WINDOW_SERVICE) as android.view.WindowManager
            val layoutParams = android.view.WindowManager.LayoutParams(
                android.view.WindowManager.LayoutParams.MATCH_PARENT, android.view.WindowManager.LayoutParams.MATCH_PARENT,
                android.view.WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, 
                android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                android.graphics.PixelFormat.TRANSLUCENT
            )

            val layout = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.VERTICAL; gravity = android.view.Gravity.CENTER
                setBackgroundColor(android.graphics.Color.parseColor("#F12B2C")) // Red Alert Background
                setPadding(60, 60, 60, 60)
            }

            val iconView = android.widget.TextView(this).apply {
                text = "⚠️"
                textSize = 60f
                gravity = android.view.Gravity.CENTER
                setPadding(0, 0, 0, 20)
            }

            val titleView = android.widget.TextView(this).apply {
                text = "ASTAGFIRULLAH!"
                textSize = 35f; setTextColor(android.graphics.Color.WHITE)
                setTypeface(null, android.graphics.Typeface.BOLD); gravity = android.view.Gravity.CENTER
                setPadding(0, 0, 0, 40)
            }

            val reasonView = android.widget.TextView(this).apply {
                text = message; textSize = 22f; setTextColor(android.graphics.Color.WHITE)
                gravity = android.view.Gravity.CENTER; setPadding(0, 0, 0, 80)
            }

            val btnClose = android.widget.Button(this).apply {
                text = "I Understand & Close"; setTextColor(android.graphics.Color.parseColor("#F12B2C"))
                val btnShape = android.graphics.drawable.GradientDrawable(); btnShape.cornerRadius = 24f; btnShape.setColor(android.graphics.Color.WHITE)
                background = btnShape
                layoutParams = android.widget.LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, 150)
                setOnClickListener { removeFullScreenHadithPopup() }
            }

            layout.addView(iconView); layout.addView(titleView); layout.addView(reasonView); layout.addView(btnClose)
            fullScreenHadithView = layout
            try { windowManager?.addView(fullScreenHadithView, layoutParams) } catch (e: Exception) {}
        }
    }

    private fun removeFullScreenHadithPopup() {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.post {
            fullScreenHadithView?.let { try { windowManager?.removeView(it) } catch (e: Exception) {} }
            fullScreenHadithView = null
        }
    }

    // ==========================================
    // Crash-Free Fast Popup Overlay (Standard)
    // ==========================================
    private fun showWarningPopup(message: String, isSecurityWarning: Boolean, isDeepStudyMode: Boolean) {
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.post {
            removeWarningPopup()
            windowManager = getSystemService(Context.WINDOW_SERVICE) as android.view.WindowManager
            val layoutParams = android.view.WindowManager.LayoutParams(
                android.view.WindowManager.LayoutParams.MATCH_PARENT, android.view.WindowManager.LayoutParams.WRAP_CONTENT,
                android.view.WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, 
                android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                android.graphics.PixelFormat.TRANSLUCENT
            ).apply { gravity = android.view.Gravity.TOP or android.view.Gravity.CENTER_HORIZONTAL; y = 150 }

            val linearLayout = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                val bgColor = if (isSecurityWarning) "#E74C3C" else if (isDeepStudyMode) "#4A00E0" else "#0CA8B0" 
                setBackgroundColor(android.graphics.Color.parseColor(bgColor)); setPadding(50, 50, 50, 50)
            }

            val titleView = android.widget.TextView(this).apply {
                text = if (isSecurityWarning) "ACCESS DENIED!" else if (isDeepStudyMode) "STAY FOCUSED!" else "FOCUS ACTIVE!"
                textSize = 20f; setTextColor(android.graphics.Color.WHITE)
                setTypeface(null, android.graphics.Typeface.BOLD); gravity = android.view.Gravity.CENTER
            }

            val reasonView = android.widget.TextView(this).apply {
                text = message; textSize = 15f; setTextColor(android.graphics.Color.WHITE)
                gravity = android.view.Gravity.CENTER; setPadding(0, 20, 0, 10)
            }

            linearLayout.addView(titleView); linearLayout.addView(reasonView); overlayView = linearLayout
            try { windowManager?.addView(overlayView, layoutParams) } catch (e: Exception) {}
            handler.postDelayed({ removeWarningPopup() }, 5000) 
        }
    }

    private fun removeWarningPopup() {
        if (overlayView != null && windowManager != null) {
            try { windowManager?.removeView(overlayView) } catch (e: Exception) {}
            overlayView = null
        }
    }

    override fun onInterrupt() {}
}
