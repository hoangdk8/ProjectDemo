package com.example.projectdemo.untils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.projectdemo.R
import com.example.projectdemo.ui.MainActivity
import java.util.concurrent.TimeUnit

class NotifyWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        sendNotification()
        return Result.success()
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification() {

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = System.currentTimeMillis().toInt()
        val notificationTitle = "Thông báo $DAY_STEP"
        val notificationContent = "Đây là một thông báo từ ứng dụng của bạn"
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(notificationTitle)
            .setContentText(notificationContent)
            .setSmallIcon(R.drawable.ic_app)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(notificationId, notification)

        when (DAY_STEP.toInt()) {
            10 -> {
                DAY_STEP = 20
                val notifyDay = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
                    .setInitialDelay(DAY_STEP, TimeUnit.SECONDS)
                    .build()
                WorkManager.getInstance(applicationContext).enqueue(notifyDay)
            }
            20 -> {
                DAY_STEP = 40
                val notifyDay = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
                    .setInitialDelay(DAY_STEP, TimeUnit.SECONDS)
                    .build()
                WorkManager.getInstance(applicationContext).enqueue(notifyDay)
            }
            40 -> {
                DAY_STEP = 70
                val notifyDay = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
                    .setInitialDelay(DAY_STEP, TimeUnit.SECONDS)
                    .build()
                WorkManager.getInstance(applicationContext).enqueue(notifyDay)
            }
            70 -> {
                WorkManager.getInstance(applicationContext).cancelAllWork()
            }
        }


    }

    companion object {
        const val CHANNEL_ID = "channel_id"
        const val CHANNEL_NAME = "Channel Name"
        var DAY_STEP: Long = 10

        fun notifyWork(context: Context) {
            val notifyDay = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
                .setInitialDelay(DAY_STEP, TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(context).enqueue(notifyDay)
        }

        fun cancelWork(context: Context) {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWork()
            DAY_STEP = 10
        }
    }
}