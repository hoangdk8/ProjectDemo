package com.example.projectdemo.audio

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlaybackException
import androidx.media3.exoplayer.ExoPlayer
import com.example.projectdemo.data.dataclass.BASE_URL_MUSIC
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.listener.PlayerEventListener
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExoPlayerManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
    private var playerEventListener: PlayerEventListener? = null
    private var playerEventListenerMain: PlayerEventListener? = null
    private var currentModel: DataDefaultRings.RingTone? = null
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private var currentPosition: Int = 0
    private var currentRingtone: List<DataDefaultRings.RingTone>? = null


    fun getPlayer(): ExoPlayer {
        return exoPlayer
    }

    fun countTimer() {
        runnable = object : Runnable {
            override fun run() {
                val currentTime = exoPlayer.currentPosition
                playerEventListenerMain?.onProgress(currentTime)
                playerEventListener?.onProgress(currentTime)
                handler.postDelayed(this, 0)
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
        override fun onPlayerError(error: PlaybackException) {
            playerEventListener?.onError()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            if (isPlaying) {
                playerEventListener?.onPlay()
                playerEventListenerMain?.onPlay()
                currentRingtone?.let { playerEventListener?.onNext(it,currentPosition) }
            } else {
                handler.removeCallbacks(runnable!!)
                playerEventListener?.onStopMusic()
                playerEventListenerMain?.onStopMusic()

            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            currentPosition = exoPlayer.currentMediaItemIndex
            currentRingtone?.let { playerEventListener?.onNext(it,currentPosition) }
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

    fun playList(
        listRingTone: List<DataDefaultRings.RingTone>,
        clickedRingTone: DataDefaultRings.RingTone
    ) {
        exoPlayer.clearMediaItems()
        val filteredRingTones = listRingTone.filter { it.id != 0 && it.id != 1 }
        val mediaItems = filteredRingTones.map { ringTone ->
            MediaItem.fromUri(BASE_URL_MUSIC + ringTone.url)
        }
        exoPlayer.addMediaItems(mediaItems)

        val startIndex = filteredRingTones.indexOf(clickedRingTone)
        if (startIndex != -1) {
            exoPlayer.seekTo(startIndex, 0)
            exoPlayer.playWhenReady = true
            exoPlayer.prepare()
            currentModel = clickedRingTone
        }
        currentRingtone = filteredRingTones
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
    @SuppressLint("UnsafeOptInUsageError")
    fun next() {
        exoPlayer.next()
    }
    @SuppressLint("UnsafeOptInUsageError")
    fun previous() {
        exoPlayer.previous()
    }
    fun release() {
        exoPlayer.release()
    }

    fun reload() {
        exoPlayer.seekTo(0)
        exoPlayer.play()
    }
}