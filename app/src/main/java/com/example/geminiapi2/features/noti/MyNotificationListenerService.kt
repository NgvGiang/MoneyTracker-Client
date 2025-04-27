package com.example.geminiapi2.features.noti

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import java.util.Timer
import kotlin.concurrent.timerTask

class MyNotificationListenerService : NotificationListenerService() {


    private val targetPackageName = "com.facebook.orca" // Package name của messenger app
    private val notificationService  by lazy{ ReadNotificationService(this) }
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        try {
            if (sbn.packageName == targetPackageName) {
                Log.d("NotificationListener", "Received notification from Messenger")
                Log.d("NotificationListener", "Package: ${sbn.packageName}, Title: ${sbn.notification.extras.getString(Notification.EXTRA_TITLE)}")
                // Lấy thông tin từ notification gốc
                val notification = sbn.notification
                val extras = notification.extras
                val title = extras.getString(Notification.EXTRA_TITLE)
                val text = extras.getString(Notification.EXTRA_TEXT)
                Timer().schedule(timerTask {
                    notificationService.showMessengerNotification(title ?: "New Message", text ?: "You have a new message")
                }, 2000)


            }
        } catch (e: Exception) {
            Log.e("NotificationListener", "Error processing notification: ${e.message}")
        }
    }
    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d("NotificationListener", "Listener Connected")
    }
    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        Log.i("Msg", "Notification Removed")
    }
}