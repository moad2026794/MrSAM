package com.mistersam.app.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mistersam.app.MainActivity
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object Reminder {
    private const val WORK = "daily_lesson"
    const val CHANNEL = "daily_lesson"

    fun schedule(ctx: Context, hour: Int) {
        val now = LocalDateTime.now()
        var at = now.withHour(hour).withMinute(0).withSecond(0).withNano(0)
        if (!at.isAfter(now)) at = at.plusDays(1)
        val delay = Duration.between(now, at).toMillis()
        val req = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(ctx)
            .enqueueUniquePeriodicWork(WORK, ExistingPeriodicWorkPolicy.UPDATE, req)
    }

    fun cancel(ctx: Context) {
        WorkManager.getInstance(ctx).cancelUniqueWork(WORK)
    }
}

class ReminderWorker(private val ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(Reminder.CHANNEL, "تذكير الدرس اليومي", NotificationManager.IMPORTANCE_DEFAULT)
        )
        val granted = ContextCompat.checkSelfPermission(
            ctx, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            val pi = PendingIntent.getActivity(
                ctx, 0, Intent(ctx, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            val n = NotificationCompat.Builder(ctx, Reminder.CHANNEL)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("مستر سام ينتظرك 👋")
                .setContentText("درس اليوم جاهز — ساعة واحدة تقرّبك من الطلاقة.")
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build()
            try {
                NotificationManagerCompat.from(ctx).notify(1, n)
            } catch (_: SecurityException) {
            }
        }
        return Result.success()
    }
}
