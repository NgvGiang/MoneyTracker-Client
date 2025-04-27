package com.example.geminiapi2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.geminiapi2.ui.theme.AppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.geminiapi2.features.home.GenaiScreen
import com.example.geminiapi2.features.navigation.AppNavigation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  @OptIn(ExperimentalPermissionsApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {

    super.onCreate(savedInstanceState)
    setContent {
      AppTheme {
        // A surface container using the 'background' color from the theme
//          val postNotificationPermission = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
//          LaunchedEffect(key1 = true) {
//              if(!postNotificationPermission.status.isGranted){
//                  postNotificationPermission.launchPermissionRequest()
//              }
//              val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
//              startActivity(intent)
//          }
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background,
        ) {
//          GenaiScreen()
//            AppNavigation()
            NotificationAccessScreen()
        }
      }
    }

  }
}