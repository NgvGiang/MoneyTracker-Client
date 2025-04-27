package com.example.geminiapi2


import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.geminiapi2.features.navigation.AppNavigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationAccessScreen() {
    val postNotificationPermission = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    var notificationListenerGranted by remember { mutableStateOf(false) }
    var isCheckingPermissions by remember { mutableStateOf(true) }
    // Kiểm tra quyền truy cập thông báo
    val context = LocalContext.current // Lấy context
    fun isNotificationListenerEnabled(): Boolean {
        val enabledListeners = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        val packageName = context.packageName
        return enabledListeners?.contains("$packageName/") == true
    }

    // Cập nhật trạng thái quyền truy cập thông báo
    LaunchedEffect(key1 = context) {
        notificationListenerGranted = isNotificationListenerEnabled()
        isCheckingPermissions = false
    }

    // Launcher để mở cài đặt Notification Listener
    val notificationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        // Kiểm tra lại sau khi quay lại từ cài đặt
        notificationListenerGranted = isNotificationListenerEnabled()
    }
    if (isCheckingPermissions) {
        // Có thể thêm loading indicator nếu cần
    } else if (!notificationListenerGranted) {
        // Sử dụng composable riêng cho phần yêu cầu quyền
        PermissionRequestUI(
            onRequestNotificationListenerPermission = {
                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                notificationSettingsLauncher.launch(intent)
            }
        )
    } else {
        // Permissions granted, show main app content
        AppNavigation()
    }

    // UI
//    if (!notificationListenerGranted) {
//        // Hiển thị UI hướng dẫn người dùng cấp quyền
//        Column(
//            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
//            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(androidx.compose.ui.unit.Dp(16F))
//        )  {
//            Text(
//                text = "Ứng dụng cần quyền truy cập thông báo để hoạt động.",
//                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
//                textAlign = androidx.compose.ui.text.style.TextAlign.Center
//            )
//            Button(onClick = {
//                // Mở màn hình cài đặt Notification Listener
//                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
//                notificationSettingsLauncher.launch(intent) // Sử dụng launcher
//            }) {
//                Text("Cấp quyền nhé :3 ?")
//            }
//            // Yêu cầu quyền POST_NOTIFICATIONS (chỉ khi thực sự cần, và nên có giải thích)
//            if (!postNotificationPermission.status.isGranted) {
//                Button(onClick = { postNotificationPermission.launchPermissionRequest() })
//                {
//                    Text(text = "Cấp quyền thông báo")
//                }
//            }
//        }
//    } else {
//        // Quyền đã được cấp, hiển thị nội dung chính của ứng dụng
//        AppNavigation()
////        GenaiScreen()  // Your main screen content
//    }
}
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequestUI(onRequestNotificationListenerPermission: () -> Unit) {
    val postNotificationPermission =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Ứng dụng cần quyền truy cập thông báo để hoạt động.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onRequestNotificationListenerPermission,
                modifier = Modifier.width(IntrinsicSize.Max)
            ) {
                Text("Cấp quyền")
            }

            // Only show POST_NOTIFICATIONS request if needed
            if (!postNotificationPermission.status.isGranted) {
                Button(
                    onClick = { postNotificationPermission.launchPermissionRequest() },
                    modifier = Modifier.width(IntrinsicSize.Max)
                ) {
                    Text("Cấp quyền thông báo")
                }
            }
        }
    }

}
