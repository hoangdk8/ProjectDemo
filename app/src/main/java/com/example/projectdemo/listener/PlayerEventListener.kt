package com.example.projectdemo.listener

import com.example.projectdemo.data.dataclass.DataDefaultRings

interface PlayerEventListener {
    fun onPlaybackEnded()
    fun onReadyPlay(ringTone: DataDefaultRings.RingTone)
    fun onBuffering()
    fun onPlay()
    fun onStopMusic()
    fun onProgress(duration: Long)
}