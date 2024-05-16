package com.example.projectdemo.ui.home.adapter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.audio.ExoPlayerManager
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
import com.example.projectdemo.listener.DetailPlayMusic
import com.example.projectdemo.listener.PlayerEventListener
import com.example.projectdemo.listener.eventbus.EventHideMiniPlay
import com.example.projectdemo.listener.eventbus.EventNextMusic
import com.example.projectdemo.listener.eventbus.EventNotifyDataSetChanged
import com.example.projectdemo.listener.eventbus.EventReload
import com.example.projectdemo.listener.eventbus.EventShowMiniPlay
import com.example.projectdemo.untils.convertDurationToTimeString
import com.example.projectdemo.untils.eventBusPost
import com.example.projectdemo.untils.eventBusRegister
import com.example.projectdemo.untils.eventBusUnRegister
import com.example.projectdemo.untils.gone
import com.example.projectdemo.untils.logd
import com.example.projectdemo.untils.visible
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject


class HomeAdapter @Inject constructor(
    private var itemList: List<DataItem>,
    private val exoPlayerManager: ExoPlayerManager,
    private val listener: DetailPlayMusic
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var hour: String = "00"
    private var minute: String = "00"
    private var playingPosition = RecyclerView.NO_POSITION
    private var previousPlayingPosition = RecyclerView.NO_POSITION
    private var isPlay = false
    private var isNext = false
    private var isLoading = false
    private var isReload = false
    private var isProcess = false
    private var currentUrl = ""
    private var nextUrl: DataDefaultRings.RingTone? = null
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        lateinit var animationProgress: ObjectAnimator
    }

    init {
        eventBusRegister()
    }


    inner class MusicViewHolder(val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root), PlayerEventListener {


        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n")
        fun bindMusicView(homeRingtonesModel: DataDefaultRings.RingTone, position: Int) {
            val result = convertDurationToTimeString(homeRingtonesModel.duration!!)
            hour = result[0]
            minute = result[1]
            binding.tvTitle.text = homeRingtonesModel.name
            binding.tvTime.text = "$hour:$minute"
            if (playingPosition == position && isPlay) {
                val currentDuration = binding.progressBar.progress
                binding.imgStatus.setImageResource(R.drawable.ic_pause)
                binding.progressBar.visible()
                binding.imgStatus.visible()
                binding.progressBar.progress = currentDuration
            } else if (playingPosition == position && isNext) {
                val currentDuration = binding.progressBar.progress
                binding.imgStatus.setImageResource(R.drawable.ic_pause)
                binding.progressBar.visible()
                binding.imgStatus.visible()
                animationProgress.start()
                binding.progressBar.progress = currentDuration
            } else {
                binding.imgStatus.setImageResource(R.drawable.ic_play)
                binding.imgStatus.visible()
                binding.loading.gone()
                if (playingPosition == position) {
                    binding.progressBar.visible()
                    val currentDuration = exoPlayerManager.getPlayer().currentPosition.toInt()
                    binding.progressBar.progress = currentDuration

                } else {
                    binding.progressBar.gone()
                }
            }
            binding.imgMusic.setOnClickListener {

                if (itemList[position].viewType == ITEM_TYPE_MUSIC) {
                    currentUrl = homeRingtonesModel.url!!
                }
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
                exoPlayerManager.stop()
                notifyItemChanged(playingPosition)
                playingPosition = RecyclerView.NO_POSITION
                isPlay = false
                listener.onShowDetailsMusic(homeRingtonesModel)
                when {
                    exoPlayerManager.isPlaying && isPlay && position == playingPosition -> {
                        isPlay = false
                        isProcess = true
                    }

                    else -> {
                        exoPlayerManager.playPrepare(homeRingtonesModel)
                        playingPosition = position
                        isPlay = true
                        isProcess = true
                        isReload = previousPlayingPosition != playingPosition
                    }
                }
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onPlaybackEnded() {
            animationProgress.cancel()
            eventBusPost(EventNextMusic())
        }

        override fun onReadyPlay(ringTone: DataDefaultRings.RingTone) {
            eventBusPost(EventShowMiniPlay())
            isLoading = false
            isPlay = true
            binding.loading.visibility = View.INVISIBLE
            binding.imgStatus.visibility = View.VISIBLE
            binding.progressBar.max = ringTone.duration!!.toInt()
            animationProgress = ObjectAnimator.ofInt(
                binding.progressBar,
                "progress",
                exoPlayerManager.getPlayer().currentPosition.toInt(),
                ringTone.duration!!.toInt()
            )
            val remainTime =
                ringTone.duration!!.toInt() - exoPlayerManager.getPlayer().currentPosition.toInt() // Tinh time con lai theo don vi milisecond
            animationProgress.duration = if (remainTime.toLong() > 0) remainTime.toLong() else 0
            animationProgress.start()
        }

        override fun onBuffering() {
            isLoading = true
            binding.imgStatus.visibility = View.INVISIBLE
            binding.loading.visibility = View.VISIBLE
        }

        override fun onPlay() {
            animationProgress.resume()
            binding.imgStatus.setImageResource(R.drawable.ic_pause)
            isPlay = true
            binding.progressBar.visibility = View.VISIBLE
            isProcess = true

        }

        override fun onStopMusic() {
            animationProgress.pause()
            binding.imgStatus.setImageResource(R.drawable.ic_play)
            isPlay = false
            isProcess = true
            animationProgress.pause()
        }

        override fun onProgress(duration: Long) {

        }

    }

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe
    fun eventNextMusic(event: EventNextMusic) {
        playingPosition += 1
        if (itemList[playingPosition].viewType == ITEM_TYPE_MUSIC) {
            nextUrl = itemList[playingPosition].bannerList!!
            exoPlayerManager.playPrepare(nextUrl!!)
            isPlay = true
            isProcess = true
        } else {
            playingPosition += 1
            nextUrl = itemList[playingPosition].bannerList!!
            exoPlayerManager.playPrepare(nextUrl!!)
            isPlay = true
            isProcess = true
        }
        isNext = true
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe
    fun eventChangeIcon(event: EventHideMiniPlay) {
        isPlay = false
        playingPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }

    @Subscribe
    fun updateView(event: EventNotifyDataSetChanged) {
        notifyItemChanged(playingPosition)
        playingPosition = RecyclerView.NO_POSITION
        isPlay = false
    }

    @Subscribe
    fun reloadMusic(event: EventReload) {
        isPlay = true
        notifyItemChanged(playingPosition)
        "$previousPlayingPosition".logd()
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

    @RequiresApi(Build.VERSION_CODES.O)
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
