package com.example.projectdemo.ui.me.adapter

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.audio.ExoPlayerManager
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.databinding.ItemDownloadBinding
import com.example.projectdemo.listener.DetailPlayMusic
import com.example.projectdemo.listener.PlayerEventListener
import com.example.projectdemo.listener.eventbus.EventGoneView
import com.example.projectdemo.listener.eventbus.EventHideMiniPlayDetailCategoties
import com.example.projectdemo.listener.eventbus.EventShowMiniPlay
import com.example.projectdemo.listener.eventbus.EventUnDownload
import com.example.projectdemo.untils.convertDurationToTimeString
import com.example.projectdemo.untils.eventBusPost
import com.example.projectdemo.untils.eventBusRegister
import com.example.projectdemo.untils.gone
import com.example.projectdemo.untils.visible
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class DownloadAdapter @Inject constructor(
    private val download: List<DataDefaultRings.RingTone>,
    private val exoPlayerManager: ExoPlayerManager,
    private val listener: DetailPlayMusic
) :
    RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder>() {
    private var hour: String = "00"
    private var minute: String = "00"
    private var playingPosition = RecyclerView.NO_POSITION
    private var previousPlayingPosition = RecyclerView.NO_POSITION
    private var isPlay = false
    private var isLoading = false
    private var isReload = false
    private var isProcess = false
    private var currentUrl = ""
    private val handler = Handler(Looper.getMainLooper())

    init {
        eventBusRegister()
    }
    inner class DownloadViewHolder(private val binding: ItemDownloadBinding) :
        RecyclerView.ViewHolder(binding.root), PlayerEventListener {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bindDownoad(ringTone: DataDefaultRings.RingTone, position: Int) {
            val result = convertDurationToTimeString(ringTone.duration!!)
            hour = result[0]
            minute = result[1]
            binding.txtTime.text = "$hour:$minute"
            binding.txtTitle.text = ringTone.name
            if (playingPosition == position && isPlay) {
                binding.imgStatus.setImageResource(R.drawable.ic_pause)
                binding.progressBar.visible()
                binding.imgStatus.visible()
            } else {
                binding.imgStatus.setImageResource(R.drawable.ic_play)
                binding.imgStatus.visible()

                binding.loading.gone()
                if (playingPosition == position) {
                    binding.progressBar.visible()
                    val currentDuration = binding.progressBar.progress
                    binding.progressBar.progress = currentDuration
                } else {
                    binding.progressBar.gone()
                }
            }
            binding.ctn.setOnClickListener {
                currentUrl = ringTone.url!!
                exoPlayerManager.setPlayerEventListener(this)
                if (playingPosition != position) {
                    previousPlayingPosition = playingPosition
                    notifyItemChanged(previousPlayingPosition)
                    notifyItemChanged(playingPosition)
                }
                when {
                    exoPlayerManager.isPlaying && isPlay && position == playingPosition -> {
                        exoPlayerManager.pause()
                        binding.imgStatus.setImageResource(R.drawable.ic_play)
                        isPlay = false
                        isProcess = true
                    }

                    !exoPlayerManager.isPlaying && !isPlay && position == playingPosition -> {
                        exoPlayerManager.play()
                        binding.imgStatus.setImageResource(R.drawable.ic_pause)
                        isPlay = true
                        isReload = false
                        isProcess = true
                    }

                    else -> {
                        exoPlayerManager.playPrepare(ringTone)
                        playingPosition = position
                        binding.imgStatus.setImageResource(R.drawable.ic_pause)
                        isPlay = true
                        isProcess = true
                        isReload = previousPlayingPosition != playingPosition
                    }
                }

            }
            binding.ctnMusic.setOnClickListener {
                eventBusPost(EventGoneView())
                listener.onShowDetailsMusic(ringTone)
                exoPlayerManager.playList(download, ringTone)
            }
            binding.imgDelete.setOnClickListener {
                eventBusPost(EventUnDownload(ringTone.id!!))
                notifyDataSetChanged()
            }
        }

        override fun onPlaybackEnded() {
            playingPosition = RecyclerView.NO_POSITION
            binding.progressBar.visibility = View.GONE
            notifyDataSetChanged()
            isPlay = false
            isProcess = false
        }

        override fun onReadyPlay(ringTone: DataDefaultRings.RingTone) {
            eventBusPost(EventShowMiniPlay())
            isLoading = false
            isPlay = true
            binding.loading.visibility = View.INVISIBLE
            binding.imgStatus.visibility = View.VISIBLE
            binding.progressBar.max = exoPlayerManager.getPlayer().duration.toInt()

            handler.postDelayed(object : Runnable {
                override fun run() {
                    binding.progressBar.progress =
                        exoPlayerManager.getPlayer().currentPosition.toInt()
                    handler.postDelayed(this, 0) // Update progress every second
                }
            }, 0)
        }

        override fun onBuffering() {
            isLoading = true
            binding.imgStatus.visibility = View.INVISIBLE
            binding.loading.visibility = View.VISIBLE
        }

        override fun onPlay() {
            binding.imgStatus.setImageResource(R.drawable.ic_pause)
            isPlay = true
            binding.progressBar.visibility = View.VISIBLE
            isProcess = true
        }

        override fun onStopMusic() {
            binding.imgStatus.setImageResource(R.drawable.ic_play)
            isPlay = false
            isProcess = true
        }

        override fun onProgress(duration: Long) {

        }

        override fun onNext(listRingTone: List<DataDefaultRings.RingTone>, position: Int) {
        }

        override fun onError() {
        }
    }

    @Subscribe
    fun eventChangeIcon(event: EventHideMiniPlayDetailCategoties) {
        isPlay = false
        playingPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        val binding = ItemDownloadBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DownloadViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return download.size
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        holder.bindDownoad(download[position], position)
    }
}