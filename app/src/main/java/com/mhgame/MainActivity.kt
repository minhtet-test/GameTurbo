package com.mhgame

import android.app.ActivityManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var isTurboOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnToggleTurbo = findViewById<Button>(R.id.btnToggleTurbo)

        btnToggleTurbo.setOnClickListener {
            if (!isTurboOn) {
                // On လုပ်မည်
                if (checkDndPermission()) {
                    enableGameTurbo()
                    btnToggleTurbo.text = "TURN OFF TURBO"
                    btnToggleTurbo.setBackgroundColor(Color.parseColor("#03DAC5")) // Teal Color
                    isTurboOn = true
                }
            } else {
                // Off လုပ်မည်
                disableGameTurbo()
                btnToggleTurbo.text = "TURN ON TURBO"
                btnToggleTurbo.setBackgroundColor(Color.parseColor("#6200EE")) // Purple Color
                isTurboOn = false
            }
        }
    }

    private fun checkDndPermission(): Boolean {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!notificationManager.isNotificationPolicyAccessGranted) {
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                startActivity(intent)
                Toast.makeText(this, "Please allow Do Not Disturb access for MH Game Turbo", Toast.LENGTH_LONG).show()
                return false
            }
        }
        return true
    }

    private fun enableGameTurbo() {
        // 1. Block Notifications
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
        }

        // 2. Kill Background Processes for Network Optimization
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val packages = packageManager.getInstalledPackages(0)
        var killedCount = 0
        for (pkg in packages) {
            if (pkg.packageName != packageName) {
                activityManager.killBackgroundProcesses(pkg.packageName)
                killedCount++
            }
        }
        
        Toast.makeText(this, "Turbo Enabled! $killedCount apps cleared. Noti blocked.", Toast.LENGTH_SHORT).show()
    }

    private fun disableGameTurbo() {
        // Allow Notifications Again
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (notificationManager.isNotificationPolicyAccessGranted) {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            }
        }
        Toast.makeText(this, "Turbo Disabled! Notifications allowed.", Toast.LENGTH_SHORT).show()
    }
}
