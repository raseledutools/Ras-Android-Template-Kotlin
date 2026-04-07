package com.tanimul.android_template_kotlin.features

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.switchmaterial.SwitchMaterial
import dagger.hilt.android.AndroidEntryPoint
import com.tanimul.android_template_kotlin.R

@AndroidEntryPoint // এটি অত্যন্ত গুরুত্বপূর্ণ, হিল্ট প্রজেক্টে এটি ছাড়া ক্র্যাশ করবে
class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // ডিজাইন সেটআপ
        setContentView(R.layout.activity_main)

        // সেটিংস মেমোরি তৈরি
        sharedPreferences = getSharedPreferences("FocusAppSettings", Context.MODE_PRIVATE)

        // আইডিগুলো খুঁজে বের করা এবং সেফটি চেক (Null safety)
        val switchAdult = findViewById<SwitchMaterial>(R.id.switchAdult)
        val switchImage = findViewById<SwitchMaterial>(R.id.switchImage)
        val switchUninstall = findViewById<SwitchMaterial>(R.id.switchUninstall)
        val cardTakeBreak = findViewById<MaterialCardView>(R.id.cardTakeBreak)
        val cardSchedule = findViewById<MaterialCardView>(R.id.cardSchedule)

        // আগের ডাটা লোড করা
        switchAdult?.isChecked = sharedPreferences.getBoolean("adult_content", true)
        switchImage?.isChecked = sharedPreferences.getBoolean("image_search", true)
        switchUninstall?.isChecked = sharedPreferences.getBoolean("uninstall_protect", true)

        // সুইচের কাজ
        switchAdult?.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("adult_content", isChecked)
            toast(if (isChecked) "Adult Content Blocked" else "Blocking Disabled")
        }

        switchImage?.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("image_search", isChecked)
            if (isChecked) toast("Image Search Blocked")
        }

        switchUninstall?.setOnCheckedChangeListener { _, isChecked ->
            saveSetting("uninstall_protect", isChecked)
            if (isChecked) toast("Uninstall Protection Active")
        }

        // কার্ড বাটনগুলোর কাজ
        cardTakeBreak?.setOnClickListener {
            toast("Timer started for your break!")
        }

        cardSchedule?.setOnClickListener {
            toast("Opening Daily Routine Schedule...")
        }
    }

    private fun saveSetting(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
