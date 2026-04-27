package com.tanimul.android_template_kotlin

import android.content.Context
import android.content.SharedPreferences

object DataManager {
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        // অ্যাপের ডাটা লোকালি সেভ রাখার জন্য SharedPreferences
        prefs = context.getSharedPreferences("RasFocusData", Context.MODE_PRIVATE)
    }

    // ==========================================
    // 🔴 CRITICAL FIX: সবগুলো ডিফল্ট ভ্যালু false করা হয়েছে
    // ==========================================
    var isFocusActive: Boolean
        get() = prefs.getBoolean("isFocusActive", false) // ডিফল্ট: অফ
        set(value) = prefs.edit().putBoolean("isFocusActive", value).apply()

    var isAdultFocusActive: Boolean
        get() = prefs.getBoolean("isAdultFocusActive", false) // ডিফল্ট: অফ
        set(value) = prefs.edit().putBoolean("isAdultFocusActive", value).apply()

    var blockSettingsAndUninstall: Boolean
        get() = prefs.getBoolean("blockSettingsAndUninstall", false) // ডিফল্ট: অফ
        set(value) = prefs.edit().putBoolean("blockSettingsAndUninstall", value).apply()

    var isDeepStudyStrict: Boolean
        get() = prefs.getBoolean("isDeepStudyStrict", false)
        set(value) = prefs.edit().putBoolean("isDeepStudyStrict", value).apply()

    var is24HourLockActive: Boolean
        get() = prefs.getBoolean("is24HourLockActive", false)
        set(value) = prefs.edit().putBoolean("is24HourLockActive", value).apply()

    var isPeriodicPopupsActive: Boolean
        get() = prefs.getBoolean("isPeriodicPopupsActive", false)
        set(value) = prefs.edit().putBoolean("isPeriodicPopupsActive", value).apply()

    // ==========================================
    // Others Data & Settings
    // ==========================================
    var lock24hEndTime: Long
        get() = prefs.getLong("lock24hEndTime", 0L)
        set(value) = prefs.edit().putLong("lock24hEndTime", value).apply()

    var controlMode: Int
        get() = prefs.getInt("controlMode", 0) // 0 = Self, 1 = Friend
        set(value) = prefs.edit().putInt("controlMode", value).apply()

    var simpleBlockMode: Int
        get() = prefs.getInt("simpleBlockMode", 1) // 1 = Block Mode
        set(value) = prefs.edit().putInt("simpleBlockMode", value).apply()

    var adultReligion: Int
        get() = prefs.getInt("adultReligion", 0) // 0 = Muslim
        set(value) = prefs.edit().putInt("adultReligion", value).apply()

    var adultLanguage: Int
        get() = prefs.getInt("adultLanguage", 0) // 0 = Bangla
        set(value) = prefs.edit().putInt("adultLanguage", value).apply()

    var showQuotes: Boolean
        get() = prefs.getBoolean("showQuotes", true)
        set(value) = prefs.edit().putBoolean("showQuotes", value).apply()

    var totalBlockedCount: Int
        get() = prefs.getInt("totalBlockedCount", 0)
        set(value) = prefs.edit().putInt("totalBlockedCount", value).apply()

    var cleanStreakDays: Int
        get() = prefs.getInt("cleanStreakDays", 12) // ডামি ১২ দিন দেওয়া ছিল
        set(value) = prefs.edit().putInt("cleanStreakDays", value).apply()

    // ==========================================
    // Lists (Custom Keywords, Apps, Websites)
    // ==========================================
    var userCustomAdultKeywords: List<String>
        get() = prefs.getStringSet("userCustomAdultKeywords", emptySet())?.toList() ?: emptyList()
        set(value) = prefs.edit().putStringSet("userCustomAdultKeywords", value.toSet()).apply()

    var userWebList: List<String>
        get() = prefs.getStringSet("userWebList", setOf("facebook.com", "tiktok.com", "instagram.com"))?.toList() ?: listOf("facebook.com", "tiktok.com", "instagram.com")
        set(value) = prefs.edit().putStringSet("userWebList", value.toSet()).apply()

    var userAppList: List<String>
        get() = prefs.getStringSet("userAppList", setOf("com.whatsapp", "com.facebook.katana"))?.toList() ?: listOf("com.whatsapp", "com.facebook.katana")
        set(value) = prefs.edit().putStringSet("userAppList", value.toSet()).apply()
}
