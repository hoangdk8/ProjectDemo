package com.example.projectdemo.home

import android.os.CountDownTimer

class TimerCountDown(
    private val durationInMillis: Long,
    private val listener: OnTimerListener
) {

    private var countDownTimer: CountDownTimer? = null
    private var elapsedMillis: Long = 0

    fun startTimer() {
        countDownTimer = object : CountDownTimer(durationInMillis - elapsedMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                elapsedMillis += 1000
                val seconds = (elapsedMillis / 1000) -1
                listener.onTick(seconds.toInt())
            }

            override fun onFinish() {
                listener.onFinish()
            }
        }
        countDownTimer?.start()
    }

    fun stopTimer() {
        countDownTimer?.cancel()
    }

    fun continueTimer() {
        startTimer()
    }

    interface OnTimerListener {
        fun onTick(seconds: Int)
        fun onFinish()
    }
}
