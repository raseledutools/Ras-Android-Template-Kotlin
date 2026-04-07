package com.tanimul.android_template_kotlin.features // বা আপনার ফোল্ডার অনুযায়ী যা ছিল

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.switchmaterial.SwitchMaterial

// এই লাইনটি অত্যন্ত জরুরি, এটি যুক্ত করুন:
import com.tanimul.android_template_kotlin.R

class MainActivity : AppCompatActivity() {

    // ডাটা সেভ করার জন্য SharedPreferences
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // সেটিংস সেভ করার জন্য ফাইল তৈরি
        sharedPreferences = getSharedPreferences("FocusAppSettings", Context.MODE_PRIVATE)

        // ১. সুইচগুলো খুঁজে বের করা (ID অনুযায়ী)
        val switchAdult = findViewById<SwitchMaterial>(R.id.switchAdult)
        val switchImage = findViewById<SwitchMaterial>(R.id.switchImage)
        val switchUninstall = findViewById<SwitchMaterial>(R.id.switchUninstall)

        // ২. বাটনগুলো খুঁজে বের করা
        val btnRemovePartner = findViewById<MaterialButton>(android.R.id.button1) // XML এ ID না থাকলে দিয়ে নিতে হবে
        val cardTakeBreak = findViewById<MaterialCardView>(R.id.cardTakeBreak) // XML এ ID দিয়ে নিতে হবে
        val cardSchedule = findViewById<MaterialCardView>(R.id.cardSchedule) // XML এ ID দিয়ে নিতে হবে

        // ৩. আগের সেভ করা অবস্থা লোড করা (অ্যাপ ওপেন হলে যেন আগের মতো থাকে)
        switchAdult.isChecked = sharedPreferences.getBoolean("adult_content", true)
        switchImage.isChecked = sharedPreferences.getBoolean("image_search", true)
        switchUninstall.isChecked = sharedPreferences.getBoolean("uninstall_protect", true)

        // --- সুইচের লজিক ---

        switchAdult.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("adult_content", isChecked)
            if (isChecked) toast("Adult Content Blocked") else toast("Blocking Disabled")
        }

        switchImage.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("image_search", isChecked)
            if (isChecked) toast("Image Search Blocked")
        }

        switchUninstall.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("uninstall_protect", isChecked)
            if (isChecked) toast("Uninstall Protection Active")
        }

        // --- বাটনের লজিক ---

        // 'Take a Break' কার্ডে ক্লিক করলে
        // আপনার XML এ এই কার্ডটির ID 'cardTakeBreak' দিয়ে নিন
        findViewById<MaterialCardView>(R.id.cardTakeBreak)?.setOnClickListener {
            toast("Timer started for your break!")
        }

        // 'Schedule' কার্ডে ক্লিক করলে
        findViewById<MaterialCardView>(R.id.cardSchedule)?.setOnClickListener {
            toast("Opening Daily Routine Schedule...")
        }
    }

    // সেটিংস মেমোরিতে সেভ করার ফাংশন
    private fun saveSetting(key: String, value: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    // ছোট মেসেজ দেখানোর সহজ ফাংশন
    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
