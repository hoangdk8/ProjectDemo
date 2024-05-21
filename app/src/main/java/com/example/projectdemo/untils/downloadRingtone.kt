package com.example.projectdemo.untils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment

fun downloadRingtone(context: Context, url: String, title: String) {
    val request = DownloadManager.Request(Uri.parse(url))
    request.setTitle(title)
    request.setDescription("Downloading ringtone")
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_RINGTONES, title)

    val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    manager.enqueue(request)
}