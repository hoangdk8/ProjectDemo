package com.example.projectdemo.ui

import android.annotation.SuppressLint
import com.example.projectdemo.audio.ExoPlayerManager
import com.example.projectdemo.R
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.databinding.MiniPlayBinding
import com.example.projectdemo.listener.PlayerEventListener
import com.example.projectdemo.listener.eventbus.EventHideMiniPlay
import com.example.projectdemo.listener.eventbus.EventHideMiniPlayDetailCategoties
import com.example.projectdemo.listener.eventbus.EventHideMiniPlaySeeAll
import com.example.projectdemo.untils.convertDurationToTimeString
import com.example.projectdemo.untils.eventBusPost
import com.example.projectdemo.untils.gone

class MiniPlay(
    var binding: MiniPlayBinding, private val exoPlayerManager: ExoPlayerManager
) :
    PlayerEventListener {
    private var isPlay = true
    private var seconds = 0
    private var minute: String = ""
    private var second: String = ""
    private var isReload = false

    init {
        exoPlayerManager.setPlayerEventListenerMain(this)
        actionClick()
    }

    private fun actionClick() {
        binding.imgPlay.setOnClickListener {
            isPlay = !isPlay
            if (isPlay) {
                binding.imgPlay.setImageResource(R.drawable.ic_pause_black)
                exoPlayerManager.play()
            } else {
                exoPlayerManager.pause()
                binding.imgPlay.setImageResource(R.drawable.ic_play_black)
            }

        }

        binding.imgClose.setOnClickListener {
            seconds = second.toInt()
            binding.root.gone()
            exoPlayerManager.stop()
            eventBusPost(EventHideMiniPlay())
            eventBusPost(EventHideMiniPlaySeeAll())
            eventBusPost(EventHideMiniPlayDetailCategoties())
        }
    }

    override fun onPlaybackEnded() {
        binding.imgPlay.setImageResource(R.drawable.ic_play_black)
    }

    override fun onReadyPlay(ringTone: DataDefaultRings.RingTone) {
        binding.txtTitle.text = ringTone.name
        val result = convertDurationToTimeString(ringTone.duration!!)
        minute = result[0]
        second = result[1]
        seconds = 0
        binding.txtTimeMax.text = "$minute:$second"
    }

    override fun onBuffering() {
    }

    override fun onPlay() {
        binding.imgPlay.setImageResource(R.drawable.ic_pause_black)
        exoPlayerManager.countTimer()
        isPlay = true
    }

    override fun onStopMusic() {
        binding.imgPlay.setImageResource(R.drawable.ic_play_black)
        isPlay = false
    }

    @SuppressLint("SetTextI18n")
    override fun onProgress(duration: Long) {
        val result = convertDurationToTimeString(duration.toInt())
        minute = result[0]
        second = result[1]
        binding.txtTimeCurrent.text = "$minute:$second"
    }

    override fun onNext(listRingTone: List<DataDefaultRings.RingTone>, position: Int) {
    }

    override fun onError() {
        exoPlayerManager.next()
    }


}