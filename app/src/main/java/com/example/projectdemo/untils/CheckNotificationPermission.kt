package com.example.projectdemo.untils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat

fun checkNotificationPermission(context: Context) {
    if (!isNotificationPermissionGranted(context)) {
        showPermissionDialog(context)
    }
}

fun isNotificationPermissionGranted(context: Context): Boolean {
    val notificationManager = NotificationManagerCompat.from(context)

    return notificationManager.areNotificationsEnabled()
}

fun showPermissionDialog(context: Context) {
    AlertDialog.Builder(context)
        .setTitle("Cấp quyền thông báo")
        .setMessage("Ứng dụng cần quyền thông báo để hoạt động đúng cách. Vui lòng cấp quyền thông báo trong cài đặt.")
        .setPositiveButton("Đi đến cài đặt") { dialog, _ ->
            openNotificationSettings(context)
            dialog.dismiss()
        }
        .setNegativeButton("Hủy") { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}

private fun openNotificationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", context.packageName, null)
    intent.data = uri
    context.startActivity(intent)
}
