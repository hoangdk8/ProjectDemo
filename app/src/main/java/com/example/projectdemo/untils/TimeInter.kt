package com.example.projectdemo.untils

object TimeInter {
    private var currentTime: Long? = null

    fun setCurrentTime(value: Long) {
        currentTime = value
    }

    fun getCurrentTime(): Long? {
        return currentTime
    }
}