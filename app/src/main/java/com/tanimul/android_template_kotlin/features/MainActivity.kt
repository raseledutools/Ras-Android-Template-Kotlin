fun enableUninstallProtection(days: Int) {
    val lockDurationMillis = TimeUnit.DAYS.toMillis(days.toLong())
    // যদি কেউ ২৪ ঘণ্টার কম দিতে চায়, ফোর্স করে ২৪ ঘণ্টা (১ দিন) করে দিন
    val actualDuration = if (lockDurationMillis < TimeUnit.DAYS.toMillis(1)) TimeUnit.DAYS.toMillis(1) else lockDurationMillis
    
    val unlockTime = System.currentTimeMillis() + actualDuration

    prefs.edit()
        .putBoolean("uninstallProtection", true)
        .putLong("uninstall_unlock_time", unlockTime) // আনলক হওয়ার সময় সেভ করে রাখলাম
        .commit()
    
    _uiState.update { it.copy(uninstallProtection = true) }
}

// ইউজার যখন টগল অফ করতে যাবে, তখন এই ফাংশন কল করবেন
fun disableUninstallProtection(onTimeNotFinished: (String) -> Unit) {
    val unlockTime = prefs.getLong("uninstall_unlock_time", 0L)
    val currentTime = System.currentTimeMillis()

    if (currentTime < unlockTime) {
        // এখনো সময় শেষ হয়নি!
        val remainingMillis = unlockTime - currentTime
        val hoursLeft = TimeUnit.MILLISECONDS.toHours(remainingMillis)
        onTimeNotFinished("You cannot disable protection yet! $hoursLeft hours remaining.")
        return // এখান থেকে রিটার্ন করে দিবে, অফ হবে না
    }

    // সময় শেষ হলে অফ করার পারমিশন পাবে
    prefs.edit().putBoolean("uninstallProtection", false).putLong("uninstall_unlock_time", 0L).commit()
    _uiState.update { it.copy(uninstallProtection = false) }
}
