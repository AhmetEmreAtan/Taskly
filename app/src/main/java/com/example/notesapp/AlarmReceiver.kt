package com.example.notesapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notesapp.activity.MainActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val todoTitle = intent?.getStringExtra("todoTitle") ?: "No Title"
        val todoMessage = "This is your todo reminder: $todoTitle"

        createNotificationChannel(context)
        showNotification(context, todoTitle, todoMessage)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "your_channel_id"
            val channelName = "Your Channel Name"
            val channelDescription = "Channel description"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(context: Context, todoTitle: String, todoMessage: String) {
        val notificationManager = NotificationManagerCompat.from(context)

        val intent = Intent(context, MainActivity::class.java)

        val uniqueRequestCode = System.currentTimeMillis().toInt()
        val pendingIntent = PendingIntent.getActivity(context, uniqueRequestCode, intent, PendingIntent.FLAG_IMMUTABLE)

        val customView = RemoteViews(context.packageName, R.layout.custom_notification)
        customView.setTextViewText(R.id.notification_title, todoTitle)
        customView.setTextViewText(R.id.notification_message, todoMessage)

        val builder = NotificationCompat.Builder(context, "your_channel_id")
            .setSmallIcon(R.drawable.tasklylogo)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(customView)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        Log.d("AlarmReceiver", "Notification builder initialized.")

        try {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("AlarmReceiver", "Notification permission not granted.")
                return
            }

            val notificationId = System.currentTimeMillis().toInt()
            notificationManager.notify(notificationId, builder.build())
        } catch (e: Exception) {
            Log.e("AlarmReceiver", "Notification error: ${e.message}")
        }
    }
}