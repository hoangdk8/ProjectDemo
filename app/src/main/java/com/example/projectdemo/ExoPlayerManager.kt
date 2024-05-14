package com.example.projectdemo

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.projectdemo.data.dataclass.BASE_URL_MUSIC
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.untils.TimerCountDown
import com.example.projectdemo.untils.logd
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExoPlayerManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
    private var playerEventListener: PlayerEventListener? = null
    private var playerEventListenerMain: PlayerEventListener? = null
    private var currentModel : DataDefaultRings.RingTone? = null
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null

    interface PlayerEventListener {
        fun onPlaybackEnded()
        fun onReadyPlay(ringTone: DataDefaultRings.RingTone)
        fun onBuffering()
        fun onPlay()
        fun onStopMusic()
        fun onProgress(duration: Long)
    }

    fun getPlayer(): ExoPlayer {
        return exoPlayer
    }

    fun countTimer() {
        runnable = object : Runnable {
            override fun run() {
                val currentTime = exoPlayer.currentPosition
                playerEventListenerMain?.onProgress(currentTime)
                handler.postDelayed(this, 500)

            }
        }
        handler.post(runnable!!)
    }

    private val eventListener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            if (state == ExoPlayer.STATE_ENDED) {
                playerEventListener?.onPlaybackEnded()
                playerEventListenerMain?.onPlaybackEnded()
            } else if (state == ExoPlayer.STATE_BUFFERING) {
                playerEventListener?.onBuffering()
                playerEventListenerMain?.onBuffering()
            } else if (state == ExoPlayer.STATE_READY) {
                playerEventListener?.onReadyPlay(currentModel!!)
                playerEventListenerMain?.onReadyPlay(currentModel!!)
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (isPlaying) {
                playerEventListener?.onPlay()
                playerEventListenerMain?.onPlay()
            } else {
                handler.removeCallbacks(runnable!!)
                playerEventListener?.onStopMusic()
                playerEventListenerMain?.onStopMusic()

            }


        }
    }

    init {
        exoPlayer.addListener(eventListener)
    }

    fun setPlayerEventListener(listener: PlayerEventListener) {
        playerEventListener = listener
    }

    fun setPlayerEventListenerMain(listener: PlayerEventListener) {
        playerEventListenerMain = listener
    }


    fun playPrepare(ringTone: DataDefaultRings.RingTone) {
        val mediaItem = MediaItem.fromUri(BASE_URL_MUSIC + ringTone.url)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        currentModel = ringTone
    }

    fun play() {
        exoPlayer.play()
    }

    val isPlaying: Boolean
        get() = exoPlayer.isPlaying

    fun pause() {
        exoPlayer.pause()
    }

    fun stop() {
        exoPlayer.stop()
    }

    fun release() {
        exoPlayer.release()
    }
    fun reload() {
        exoPlayer.seekTo(0)
        exoPlayer.play()
    }
}