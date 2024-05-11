package com.example.projectdemo.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.projectdemo.ExoPlayerManager
import com.example.projectdemo.R
import com.example.projectdemo.databinding.MiniPlayBinding
import com.example.projectdemo.event.EventHideMiniPlay
import com.example.projectdemo.event.EventMiniPlay
import com.example.projectdemo.ui.home.view.HomeFragment
import com.example.projectdemo.untils.convertDurationToTimeString
import com.example.projectdemo.untils.eventBusPost
import com.example.projectdemo.untils.eventBusRegister
import com.example.projectdemo.untils.eventBusUnRegister
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class MiniPlay @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,

    ) : ConstraintLayout(context, attrs, defStyleAttr),
    ExoPlayerManager.PlayerEventListener {
    @Inject
    lateinit var exoPlayerManager: ExoPlayerManager
    private var isPlay = true
    private var seconds = 0
    private var minute: String = ""
    private var second: String = ""
    private var oldItem = 0

    private var binding: MiniPlayBinding =
        MiniPlayBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        eventBusRegister()
        exoPlayerManager.setPlayerEventListenerMain(this)
        onFinishInflate()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = MiniPlayBinding.bind(this)


        @Subscribe
        fun onMiniPlay(event: EventMiniPlay) {
            //setupViews sau khi click
            val result = convertDurationToTimeString(event.time)
            minute = result[0]
            second = result[1]
            seconds = 0
            binding.txtTimeMax.text = "$minute:$second"
            binding.txtTitle.text = event.title

            val exoPlayer = exoPlayerManager.getPlayer()

            binding.imgPlay.setOnClickListener {
                isPlay = !isPlay
                if (isPlay) {
                    binding.imgPlay.setImageResource(R.drawable.ic_pause_black)
                    exoPlayer.play()
                } else {
                    exoPlayer.pause()
                    binding.imgPlay.setImageResource(R.drawable.ic_play_black)
                }
            }
            binding.imgClose.setOnClickListener {
                seconds = second.toInt()
                exoPlayer.stop()
                eventBusPost(EventHideMiniPlay())
            }
        }
    }

    override fun onPlaybackEnded() {
        binding.imgPlay.setImageResource(R.drawable.ic_play_black)
        binding.imgPlay.setOnClickListener {
            binding.imgPlay.setImageResource(R.drawable.ic_pause_black)
            seconds = 0
        }
    }

    override fun onReadyPlay() {
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

    override fun onProgress(duration: Long) {
        val result = convertDurationToTimeString(duration.toInt())
        minute = result[0]
        second = result[1]
        binding.txtTimeCurrent.text = "$minute:$second"
    }

    override fun onProgressBar(currentDuration: Long, totalDuration: Long) {
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        eventBusUnRegister()
    }
}