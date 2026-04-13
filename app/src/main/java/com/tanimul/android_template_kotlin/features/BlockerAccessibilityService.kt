package com.tanimul.android_template_kotlin.features

import android.accessibilityservice.AccessibilityService
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.*

class BlockerAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())

    // ================= ডাটাবেস লিস্ট (আপাতত ডামি, পরে Room DB থেকে আসবে) =================
    
    // ১. হার্ডকোর, মিডিয়াম এবং বাংলা অ্যাডাল্ট কি-ওয়ার্ড
    private val allBadWords = listOf(
        "pornhub", "xvideos", "xnxx", "redtube", "brazzers", "xhamster", 
        "chaturbate", "spankbang", "eporner", "youporn", "tube8", "hqporner",
        "porn", "sex", "xxx", "nude", "naked", "adult video", "18+", 
        "choti", "mia khalifa", "johnny sins",
        "hot video", "hot scene", "desi", "boudi", "bhabhi", "devar", 
        "item song", "item dance", "mujra", "belly dance", "bikini", 
        "romance", "kissing", "ullu", "web series", "ullongo", 
        "kapor chara", "tiktok dance", "dj dance", "hot dance", "nongra dance",
        // বাংলা কি-ওয়ার্ড
        "চটি", "চটি গল্প", "সেক্স", "পর্ণ", "পর্ন", "খারাপ ভিডিও", 
        "নগ্ন", "বৌদি", "উলঙ্গ", "হট ভিডিও", "হট সিন", "রোমান্স", 
        "চুমু", "নীল ছবি", "এডাল্ট", "খারাপ ছবি", "নোংরা ভিডিও", 
        "কাপড় ছাড়া", "খুল্লাম খুল্লা", "বাসর রাত", "গোপন ভিডিও"
    )

    // ২. ব্লক করা অ্যাপের প্যাকেজ নেম (উদাহরণস্বরূপ টিকটক সম্পূর্ণ ব্লক, ফেসবুক আংশিক)
    private val blockedApps = listOf("com.zhiliaoapp.musically")

    // ৩. ব্লক করা ওয়েবসাইটের লিস্ট
    private val blockedSites = listOf("tiktok.com", "instagram.com")

    // ৪. হাদিস ও সতর্কবার্তা
    private val hadiths = listOf(
        "হাদিস: অশ্লীলতা ইসলামের অংশ নয়।",
        "হাদিস: লজ্জা ঈমানের একটি বিশেষ অঙ্গ।",
        "বার্তা: আল্লাহ তোমার সব কাজ দেখছেন। পড়াশোনায় মন দাও।",
        "হাদিস: প্রকৃত মুসলিম সে-ই, যার ভাষা ও হাত থেকে অন্যরা নিরাপদ।",
        "বার্তা: জীবন খুব ছোট, এই অমূল্য সময় নষ্ট কোরো না।"
    )

    override fun onServiceConnected() {
        super.onServiceConnected()
        // সার্ভিস চালু হওয়ার সাথে সাথেই Usage Stats-এর পাহারাদার ব্যাকগ্রাউন্ডে চালু হয়ে যাবে
        startUsageStatsMonitor()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString() ?: return
        val rootNode = rootInActiveWindow

        // ================= ১. ইনস্ট্যান্ট অ্যাপ ব্লকিং (সবচেয়ে ফাস্ট) =================
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (blockedApps.contains(packageName)) {
                triggerInstantBlock("এই অ্যাপটি আপনার ফোকাস লিস্টে ব্লক করা আছে!")
                return
            }
        }

        // ================= ২. কি-ওয়ার্ড ব্লকিং (যেকোনো জায়গায় টাইপ করলে) =================
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            val typedText = event.text.toString().lowercase()
            if (typedText.isNotEmpty()) {
                for (word in allBadWords) {
                    if (typedText.contains(word)) {
                        triggerInstantBlock("খারাপ শব্দ টাইপ করা নিষেধ! (ADULT)")
                        return
                    }
                }
            }
        }

        // ================= ৩. ওয়েবসাইট ব্লকিং (ব্রাউজারের URL বার স্ক্যান) =================
        if (rootNode != null && event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            // ক্রোম বা অন্য ব্রাউজারের URL বার খোঁজা
            val urlNodes = rootNode.findAccessibilityNodeInfosByViewId("com.android.chrome:id/url_bar")
            if (urlNodes.isNotEmpty()) {
                val urlText = urlNodes[0].text?.toString()?.lowercase() ?: ""
                for (site in blockedSites) {
                    if (urlText.contains(site)) {
                        triggerInstantBlock("এই ওয়েবসাইটটি আপনার ফোকাস লিস্টে ব্লক করা আছে!")
                        urlNodes[0].recycle()
                        return
                    }
                }
                urlNodes[0].recycle()
            }
        }

        // ================= ৪. ইউটিউব শর্টস এবং ফেসবুক রিলস ১০০% ব্লকিং =================
        if (rootNode != null && event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            
            // ইউটিউব শর্টস লজিক
            if (packageName == "com.google.android.youtube") {
                val shortsNodes = rootNode.findAccessibilityNodeInfosByText("Shorts")
                val selectedShorts = rootNode.findAccessibilityNodeInfosByText("Selected, Shorts") // যখন বটম নেভিগেশন বারে সিলেক্ট থাকে

                // যদি "Shorts" বা "Selected, Shorts" খুঁজে পায়
                if (shortsNodes.isNotEmpty() || selectedShorts.isNotEmpty()) {
                    for (node in shortsNodes + selectedShorts) {
                        val desc = node.contentDescription?.toString()?.trim()?.lowercase() ?: ""
                        val text = node.text?.toString()?.trim()?.lowercase() ?: ""
                        
                        // নিশ্চিত করা যে এটি আসলেই শর্টস ট্যাব বা ভিডিও
                        if (desc == "shorts" || text == "shorts" || desc.contains("selected, shorts") || text.contains("selected, shorts")) {
                            triggerInstantBlock("YOUTUBE SHORTS BLOCKED!") // আগের BlockActivity তে লাল স্ক্রিন আনবে
                            node.recycle()
                            return
                        }
                        node.recycle()
                    }
                }
            }

            // ফেসবুক রিলস লজিক (Lite এবং Main App উভয়ের জন্য)
            if (packageName == "com.facebook.katana" || packageName == "com.facebook.lite") {
                val reelsNodes = rootNode.findAccessibilityNodeInfosByText("Reels")
                val reelNodes = rootNode.findAccessibilityNodeInfosByText("Reel")
                val selectedReels = rootNode.findAccessibilityNodeInfosByText("Selected, Reels")
                val allReelsNodes = reelsNodes + reelNodes + selectedReels

                if (allReelsNodes.isNotEmpty()) {
                    for (node in allReelsNodes) {
                        val desc = node.contentDescription?.toString()?.trim()?.lowercase() ?: ""
                        val text = node.text?.toString()?.trim()?.lowercase() ?: ""
                        
                        // নিশ্চিত করা যে এটি রিলস সেকশন
                        if (desc == "reels" || desc == "reel" || text == "reels" || text == "reel" || 
                            desc.contains("selected, reels") || text.contains("reels video")) {
                            triggerInstantBlock("FACEBOOK REELS BLOCKED!") // আগের BlockActivity তে নীল স্ক্রিন আনবে
                            node.recycle()
                            return
                        }
                        node.recycle()
                    }
                }
            }
            // মেমরি লিক রোধে rootNode রিসাইকেল করা
            rootNode.recycle()
        }
    }

    // ================= ৫. Usage Stats মনিটর (The Hybrid Smart Logic) =================
    // যদি কোনো অ্যাপ Accessibility-কে ফাঁকি দিয়ে ওপেন হয়েও যায়, এই লজিক তাকে ধরে ফেলবে
    private fun startUsageStatsMonitor() {
        serviceScope.launch {
            val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            while (isActive) {
                val endTime = System.currentTimeMillis()
                val startTime = endTime - 2000 // গত ২ সেকেন্ডের ডাটা চেক করবে

                val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
                
                if (stats != null) {
                    for (usageStats in stats) {
                        // যদি অ্যাপটি বর্তমানে ফোরগ্রাউন্ডে থাকে এবং আমাদের ব্লকলিস্টে থাকে
                        if (usageStats.lastTimeUsed > startTime && blockedApps.contains(usageStats.packageName)) {
                            
                            // মেইন থ্রেডে ব্লক স্ক্রিন কল করতে হবে
                            withContext(Dispatchers.Main) {
                                triggerInstantBlock("Usage Access: ব্লক করা অ্যাপ চালানোর চেষ্টা করা হয়েছে!")
                            }
                        }
                    }
                }
                delay(1500) // প্রতি ১.৫ সেকেন্ড পর পর চেক করবে (ব্যাটারি অপ্টিমাইজড)
            }
        }
    }

    // অ্যাপ ক্লোজ করে একদম ইনস্ট্যান্ট ব্লক স্ক্রিনে পাঠানোর ফাংশন
    private fun triggerInstantBlock(reason: String) {
        // ১. আগে ব্যাকগ্রাউন্ডে পাঠিয়ে দিবে (যাতে ইউজার ভিডিও আর দেখতে না পারে)
        performGlobalAction(GLOBAL_ACTION_HOME)

        val randomHadith = hadiths.random()

        // ২. সাথে সাথে স্পেশাল ব্লক স্ক্রিনটা ওপেন করে দিবে
        val intent = Intent(this, BlockActivity::class.java).apply {
            putExtra("HADITH_TEXT", randomHadith)
            putExtra("BLOCK_REASON", reason)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel() // সার্ভিস বন্ধ হলে ব্যাকগ্রাউন্ড চেকিং বন্ধ হবে
    }

    override fun onInterrupt() {}
}
