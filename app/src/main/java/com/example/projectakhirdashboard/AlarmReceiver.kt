package com.example.projectakhirdashboard

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val isSoundEnabled = intent?.getBooleanExtra("SOUND_ENABLED", false) ?: false
        val isVibrationEnabled = intent?.getBooleanExtra("VIBRATION_ENABLED", false) ?: false

        // Create the notification
        showAlarmNotification(context, isSoundEnabled, isVibrationEnabled)

        // Start a foreground service to handle sound/vibration and dismissal
        val foregroundIntent = Intent(context, AlarmForegroundService::class.java).apply {
            putExtra("SOUND_ENABLED", isSoundEnabled)
            putExtra("VIBRATION_ENABLED", isVibrationEnabled)
        }
        ContextCompat.startForegroundService(context, foregroundIntent)
    }

    // Helper function to create the notification
    private fun showAlarmNotification(context: Context, isSoundEnabled: Boolean, isVibrationEnabled: Boolean) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "alarm_channel"

        // Create the notification channel (for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Alarm Notifications", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // Create a PendingIntent to stop the alarm when the notification is clicked
        val stopAlarmIntent = Intent(context, AlarmForegroundService::class.java).apply {
            action = AlarmForegroundService.ACTION_STOP_ALARM
        }
        val stopAlarmPendingIntent = PendingIntent.getService(
            context,
            0, // Use a consistent request code (0 is fine)
            stopAlarmIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT // IMPORTANT! Use UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentIntent(stopAlarmPendingIntent)
            .setSmallIcon(R.drawable.ic_bell)
            .setContentTitle("Alarm!")
            .setContentText("Tap to dismiss")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(stopAlarmPendingIntent, true)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)


    }
}