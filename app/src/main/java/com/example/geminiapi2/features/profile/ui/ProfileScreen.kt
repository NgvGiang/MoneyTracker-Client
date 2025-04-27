package com.example.geminiapi2.features.profile.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.geminiapi2.R
import com.example.geminiapi2.features.login.LoginViewModel
import com.example.geminiapi2.features.noti.ReadNotificationService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import androidx.compose.foundation.layout.size
@Composable
fun ProfileScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val logoutState by viewModel.logoutState.collectAsState()
    val context = LocalContext.current
    val notificationService = ReadNotificationService(context)
    
    when (logoutState) {
        true -> {
            LaunchedEffect(Unit) {
                onNavigateToLogin()
            }
        }

        false -> {}
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Settings Button
        ElevatedButton(
            onClick = onNavigateToSettings,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.settings_icon),
                contentDescription = "Settings",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Settings")
        }
        
        // Notification Test Button
        Button(
            onClick = {
               notificationService.showNotification()
                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("FCM TOKEN", "Fetching FCM registration token failed", task.exception)
                        return@OnCompleteListener
                    }
                    val token = task.result
                    Log.d("FCM TOKEN", "Manual fetch token: $token")
                })
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Test Notification")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Logout Button
        Button(
            onClick = { viewModel.logout() },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Logout")
        }
    }
}

//FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w("FCM TOKEN", "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//            val token = task.result
//            Log.d("FCM TOKEN", "Manual fetch token: $token")
//        })
