package com.example.projectdemo.ui.explore.adapter

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.audio.ExoPlayerManager
import com.example.projectdemo.R
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.data.dataclass.DataItemType.Companion.ITEM_TYPE_ADVERTISE
import com.example.projectdemo.data.dataclass.DataItemType.Companion.ITEM_TYPE_MUSIC
import com.example.projectdemo.databinding.ItemAdBinding
import com.example.projectdemo.databinding.ItemPlaylistBinding
import com.example.projectdemo.listener.DetailPlayMusic
import com.example.projectdemo.listener.PlayerEventListener
import com.example.projectdemo.listener.eventbus.EventGoneView
import com.example.projectdemo.listener.eventbus.EventHideMiniPlayDetailCategoties
import com.example.projectdemo.untils.convertDurationToTimeString
import com.example.projectdemo.untils.eventBusPost
import com.example.projectdemo.untils.eventBusRegister
import com.example.projectdemo.untils.gone
import com.example.projectdemo.untils.visible
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class DetailCategoriesAdapter @Inject constructor(
    private val itemList: List<Any>,
    private val exoPlayerManager: ExoPlayerManager,
    private val listener: DetailPlayMusic
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
    inner class MusicViewHolder(private val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root), PlayerEventListener {

        fun bindMusicView(ringTone: DataDefaultRings.RingTone, position: Int) {
            val result = convertDurationToTimeString(ringTone.duration!!)
            hour = result[0]
            minute = result[1]
            binding.tvTitle.text = ringTone.name
            binding.tvTime.text = "$hour:$minute"
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
                if (playingPosition!=position){
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
            }
        }
        @SuppressLint("NotifyDataSetChanged")
        override fun onPlaybackEnded() {
            playingPosition = RecyclerView.NO_POSITION
            binding.progressBar.visibility = View.GONE
            notifyDataSetChanged()
            isPlay = false
            isProcess = false
        }

        override fun onReadyPlay(ringTone: DataDefaultRings.RingTone) {
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
    }

    @Subscribe
    fun eventChangeIcon(event: EventHideMiniPlayDetailCategoties) {
        isPlay = false
        playingPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }

    inner class AdversiteViewHolder(private val binding: ItemAdBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_MUSIC -> {
                val binding = ItemPlaylistBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return MusicViewHolder(binding)
            }

            ITEM_TYPE_ADVERTISE -> {
                val binding = ItemAdBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return AdversiteViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MusicViewHolder -> {
                itemList[position].let {
                    holder.bindMusicView(
                        it as DataDefaultRings.RingTone,
                        position
                    )
                }
            }

            is AdversiteViewHolder -> {}

            // is LoadingViewHolder -> {}
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position % 5 == 0 && position != 0 && position < itemList.size -> ITEM_TYPE_ADVERTISE
            else -> ITEM_TYPE_MUSIC
        }
    }
}
