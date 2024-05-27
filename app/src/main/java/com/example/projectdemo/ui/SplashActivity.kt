package com.example.projectdemo.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import com.example.projectdemo.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    companion object {
        private const val COUNTER_TIME: Long = 5
    }

    private var secondsRemaining: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Handler(Looper.getMainLooper()).postDelayed({

            createTimer(COUNTER_TIME)
        }, 3000)
        binding.motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                startActivity()
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        })
    }

    private fun createTimer(seconds: Long) {
        val countDownTimer: CountDownTimer = object : CountDownTimer(seconds * 1000, 1000) {
            override fun onTick(p0: Long) {
                secondsRemaining = p0 / 1000 + 1
            }

            override fun onFinish() {
                secondsRemaining = 0
                val application = application
                if (application !is MyApplication) {
                    return
                }

                application.showAdIfAvailable(
                    this@SplashActivity, object : MyApplication.OnShowAdCompleteListener {
                        override fun onShowAdComplete() {
                            binding.motionLayout.transitionToEnd()
                        }
                    }
                )
            }

        }
        countDownTimer.start()
    }

    private fun startActivity() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }

}