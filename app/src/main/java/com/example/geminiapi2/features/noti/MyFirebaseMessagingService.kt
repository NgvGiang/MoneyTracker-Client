package com.example.geminiapi2.features.noti

import android.util.Log
import com.example.geminiapi2.data.ApiService
import com.example.geminiapi2.data.dto.DeviceTokenRequest
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject


class MyFirebaseMessagingService @Inject constructor(
    private val apiService: ApiService
): FirebaseMessagingService(

)  {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())
    override fun onNewToken(token: String) {
        Log.d("DEVICE TOKEN", "New token: $token")
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
//        sendRegistrationToServer(token)
        coroutineScope.launch {
            try {
                apiService.pushDeviceToken(DeviceTokenRequest(token))
                Log.d("DEVICE TOKEN", "Token pushed successfully")
            } catch (e: Exception) {
                Log.e("DEVICE TOKEN", "Failed to push token: ${e.message}")
            }
        }


    }
    // Hủy scope khi service bị hủy để tránh memory leak
    override fun onDestroy() {
        coroutineScope.cancel()
        super.onDestroy()
    }

}