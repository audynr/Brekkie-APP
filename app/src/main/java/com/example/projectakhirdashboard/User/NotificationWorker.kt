package com.example.projectakhirdashboard.User

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.util.Calendar
import java.util.concurrent.TimeUnit
import android.Manifest
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val message = inputData.getString("MESSAGE") ?: "Time to wake up!"
        showNotification(applicationContext, "Reminder", message)
        return Result.success()
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "default_channel_id"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1, notification)
    }
}

fun scheduleDailyNotification(context: Context) {
    val constraints = Constraints.Builder()
        .build()
    val timeNow = Calendar.getInstance()
    val scheduleTime = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 6)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        if (before(timeNow)) {
            add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    Log.d("NotificationScheduler", "Current time: ${timeNow.time}")
    Log.d("NotificationScheduler", "Scheduled time: ${scheduleTime.time}")

    val delay = scheduleTime.timeInMillis - timeNow.timeInMillis
    Log.d("NotificationScheduler", "Delay: $delay milliseconds")

    val request = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
        .setConstraints(constraints)
        .addTag("DailyNotification")
        .setInputData(workDataOf("MESSAGE" to "Selamat pagi! selamat menjalani hari dan jangan lupa sarapan!"))
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "DailyNotificationWork",
        ExistingPeriodicWorkPolicy.KEEP, // Or REPLACE if updating is not possible
        request
    )
}

fun cancelDailyNotification(context: Context) {
    WorkManager.getInstance(context).cancelUniqueWork("DailyNotificationWork")
}

fun checkAndRequestNotificationPermission(context: Context, activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permission = Manifest.permission.POST_NOTIFICATIONS
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), 101)
        }
    }
}

fun showTestNotification(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = "test_channel" // Use a different channel ID for testing

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, "Test Channel", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle("Test Notification")
        .setContentText("This is a test notification.")
        .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app's icon
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    notificationManager.notify(2, notification) // Use a different notification ID
}







