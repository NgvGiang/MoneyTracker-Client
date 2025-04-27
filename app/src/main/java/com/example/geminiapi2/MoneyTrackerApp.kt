package com.example.geminiapi2

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import kotlin.system.measureNanoTime

@HiltAndroidApp
class MoneyTrackerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val notificationChannel = NotificationChannel(
            "money_tracker",
            "Money Tracker Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        FirebaseApp.initializeApp(this)
//
        notificationChannel.description = "You have a new notification?"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }



}