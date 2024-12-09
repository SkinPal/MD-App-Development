package com.capstone.skinpal.ui.setting

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.jvm.java
import com.capstone.skinpal.R

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object {
        private val TAG = NotificationWorker::class.java.simpleName
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_01"
        const val CHANNEL_NAME = "dicoding channel"
    }
    private var resultStatus: Result? = null

    override fun doWork(): Result {
        showNotification("Scheduled Notification", "This is your scheduled notification.")
        return Result.success()
    }

    private fun parseDateToMillis(dateString: String): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return try {
            val date = dateFormat.parse(dateString)
            date?.time ?: Long.Companion.MAX_VALUE
        } catch (e: Exception) {
            Long.Companion.MAX_VALUE
        }
    }

    private fun showNotification(title: String, summary: String?) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "scheduled_notification_channel"
        val message = "Don't forget to do your daily routine skincare!"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Scheduled Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notifications)
            .build()

        notificationManager.notify(1, notification)
    }
}
