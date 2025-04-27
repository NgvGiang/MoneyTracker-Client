package com.example.geminiapi2.features.noti

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.geminiapi2.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

class ReadNotificationService(private val context: Context) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val _notificationCount = MutableStateFlow(0)
    val notificationCount: StateFlow<Int> = _notificationCount

    // Phương thức để cập nhật và lấy số lượng thông báo
    fun incrementNotificationCount() {
        _notificationCount.value++
    }

    fun resetNotificationCount() {
        _notificationCount.value = 0
    }
    fun showNotification(){

        val notification = NotificationCompat.Builder(context, "money_tracker")
            .setContentTitle("Test Title")
            .setContentText("Test Message")
            .setSmallIcon(R.drawable.chatboticon)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)  // Tăng mức độ ưu tiên
            .setAutoCancel(true)
            .build()
        notificationManager.notify(
            Random.nextInt(),
            notification
        )
        incrementNotificationCount()
        Log.d("NotificationService", "Notification shown successfully. Count: ${_notificationCount.value}")
    }
    fun showMessengerNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(context, "money_tracker")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.chatboticon)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)  // Tăng mức độ ưu tiên
            .setAutoCancel(true)
            .build()
        notificationManager.notify(
            Random.nextInt(),
            notification
        )
        incrementNotificationCount()
        Log.d("NotificationService", "Notification shown successfully. Count: ${_notificationCount.value}")
    }
}