package com.example.projectakhirdashboard

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.core.app.ServiceCompat.stopForeground
import androidx.core.content.ContextCompat.getSystemService
import java.security.Provider

class AlarmForegroundService : Service() {
    companion object {
        const val ACTION_STOP_ALARM = "com.example.projectakhirdashboard.STOP_ALARM"
    }

    private var ringtone: Ringtone? = null
    private var vibrator: Vibrator? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val isSoundEnabled = intent?.getBooleanExtra("SOUND_ENABLED", false) ?: false
        val isVibrationEnabled = intent?.getBooleanExtra("VIBRATION_ENABLED", false) ?: false


        if (isVibrationEnabled) {
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 500), 0))
        }

        if (isSoundEnabled) {
            val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ringtone = RingtoneManager.getRingtone(this, notificationUri)
            ringtone?.play()
        }


        // Handle stop alarm action
        if (intent?.action == ACTION_STOP_ALARM) {
            stopAlarm()
        }


        return START_STICKY  // or START_NOT_STICKY depending on your requirements
    }

    override fun onBind(intent: Intent): IBinder? {
        return null // Not a bound service
    }

    private fun stopAlarm() {
        ringtone?.stop()
        vibrator?.cancel()
        stopForeground(true) // Remove notification and stop service
        stopSelf()
    }



}