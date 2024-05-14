package com.example.projectdemo.untils

import android.util.Log

fun String.logd() {
    Log.d("hoang", this)
}


fun String.loge(TAG: String) {
    Log.e(TAG, this)
}

fun Throwable.loge(TAG: String) {
    Log.e(TAG, this.toString())
}
operator fun String.times(x: Int): String {
    val stringBuilder = StringBuilder()
    for (i in 1..x) {
        stringBuilder.append(this)
    }
    return stringBuilder.toString()
}