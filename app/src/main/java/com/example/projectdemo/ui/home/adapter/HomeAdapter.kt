package com.example.projectdemo.ui.home.adapter

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.ExoPlayerManager
import com.example.projectdemo.R
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.data.dataclass.DataItem
import com.example.projectdemo.data.dataclass.DataItemType.Companion.ITEM_TYPE_ADVERTISE
import com.example.projectdemo.data.dataclass.DataItemType.Companion.ITEM_TYPE_LOADING
import com.example.projectdemo.data.dataclass.DataItemType.Companion.ITEM_TYPE_MUSIC
import com.example.projectdemo.data.dataclass.MusicBanner
import com.example.projectdemo.databinding.EachItemBinding
import com.example.projectdemo.databinding.ItemAdBinding
import com.example.projectdemo.databinding.ItemLoadingBinding
import com.example.projectdemo.databinding.ItemPlaylistBinding
import com.example.projectdemo.event.EventHideMiniPlay
import com.example.projectdemo.untils.convertDurationToTimeString
import com.example.projectdemo.untils.eventBusRegister
import com.example.projectdemo.untils.eventBusUnRegister
import com.example.projectdemo.untils.gone
import com.example.projectdemo.untils.logd
import com.example.projectdemo.untils.visible
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject


class HomeAdapter @Inject constructor(
    private var itemList: List<DataItem>,
    private val exoPlayerManager: ExoPlayerManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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


    inner class MusicViewHolder(val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root), ExoPlayerManager.PlayerEventListener {


        @SuppressLint("SetTextI18n")
        fun bindMusicView(homeRingtonesModel: DataDefaultRings.RingTone, position: Int) {
            val result = convertDurationToTimeString(homeRingtonesModel.duration!!)
            hour = result[0]
            minute = result[1]
            binding.tvTitle.text = homeRingtonesModel.name
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
            binding.imgMusic.setOnClickListener {
                currentUrl = homeRingtonesModel.url!!
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
                        exoPlayerManager.playPrepare(homeRingtonesModel)
                        playingPosition = position
                        binding.imgStatus.setImageResource(R.drawable.ic_pause)
                        isPlay = true
                        isProcess = true
                        isReload = previousPlayingPosition != playingPosition
                    }
                }

            }


            binding.ctnMusic.setOnClickListener {
//                eventBusPost(EventGoneView())
//                eventBusPost(EventPlayDetailMusic(homeRingtonesModel))
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

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe
    fun eventChangeIcon(event: EventHideMiniPlay) {
        isPlay = false
        playingPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }

    inner class LoadingViewHolder(private val binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class AdversiteViewHolder(private val binding: ItemAdBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_playlist -> {
                val binding =
                    ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MusicViewHolder(binding)
            }

            R.layout.item_ad -> {
                val binding =
                    ItemAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdversiteViewHolder(binding)
            }

            R.layout.item_loading -> {
                val binding =
                    ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LoadingViewHolder(binding)
            }

            else -> {
                val binding =
                    EachItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HorizontalViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MusicViewHolder -> {
                itemList[position].bannerList.let {
                    holder.bindMusicView(it as DataDefaultRings.RingTone, position)
                }
            }

            is HorizontalViewHolder -> {
                itemList[position].recyclerItemList.let { holder.bind(it as List<MusicBanner>) }
            }

            is AdversiteViewHolder -> {}

            is LoadingViewHolder -> {}
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position].viewType) {
            ITEM_TYPE_MUSIC -> R.layout.item_playlist
            ITEM_TYPE_LOADING -> R.layout.item_loading
            ITEM_TYPE_ADVERTISE -> R.layout.item_ad
            else -> R.layout.each_item
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        eventBusUnRegister()
    }

}
