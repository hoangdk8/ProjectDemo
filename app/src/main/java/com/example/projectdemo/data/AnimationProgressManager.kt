package com.example.projectdemo.data

import android.animation.ObjectAnimator
import android.widget.ProgressBar

object AnimationProgressManager {
    private var animationProgress: ObjectAnimator? = null

    fun startAnimation(progressBar: ProgressBar, currentDuration: Int, totalDuration: Int) {
        animationProgress?.cancel() // Cancel animation if it's already running

        animationProgress = ObjectAnimator.ofInt(
            progressBar,
            "progress",
            currentDuration,
            totalDuration
        ).apply {
            start() // Start the animation
        }
    }

    fun cancelAnimation() {
        animationProgress?.cancel() // Cancel animation if it's running
    }
    fun durationAnimation(duration:Long){
        animationProgress!!.duration = duration
    }
    fun start() {
        animationProgress?.start()
    }
    fun pause() {
        animationProgress?.pause()
    }
}
