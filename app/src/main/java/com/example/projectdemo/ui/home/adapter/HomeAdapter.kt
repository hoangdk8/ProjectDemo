package com.example.projectdemo.ui.home.adapter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectdemo.R
import com.example.projectdemo.audio.ExoPlayerManager
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.data.dataclass.DataItemType.Companion.ITEM_TYPE_ADVERTISE
import com.example.projectdemo.data.dataclass.DataItemType.Companion.ITEM_TYPE_BANNER
import com.example.projectdemo.data.dataclass.DataItemType.Companion.ITEM_TYPE_LOADING
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
import com.example.projectdemo.untils.setSafeOnClickListener
import com.example.projectdemo.untils.visible
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject


class HomeAdapter @Inject constructor(
    private val context: Context,
    private var itemList: List<DataDefaultRings.RingTone>,
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
    private var nextRingTone: DataDefaultRings.RingTone? = null
    private var nativeAd : NativeAd? = null
    private var isAdLoaded = false

    companion object {
        lateinit var animationProgress: ObjectAnimator
    }

    fun filterList(filterlist: ArrayList<DataDefaultRings.RingTone>) {
        itemList = filterlist
        notifyDataSetChanged()
    }

    init {
        eventBusRegister()
        loadAds()
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun loadAds() {
        val adLoader = AdLoader.Builder(
            context,
            "ca-app-pub-3940256099942544/2247696110"
        ).forNativeAd { nativeAD ->
            nativeAd = nativeAD
            isAdLoaded = true
            notifyDataSetChanged()
        }.withAdListener(object : AdListener() {

        }).withNativeAdOptions(NativeAdOptions.Builder().build()).build()

        adLoader.loadAd(AdRequest.Builder().build())
    }
    inner class AdversiteViewHolder(val binding: ItemAdBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindNativeAd(ad: NativeAd) {
            binding.root.visibility = View.GONE
            with(binding) {
                adAppIcon.setImageDrawable(ad.icon?.drawable)
                txtAppName.text = ad.headline
                adStar.rating = ad.starRating?.toFloat() ?: 0f
                adMedia.mediaContent = ad.mediaContent
                adCallToAction.text = ad.callToAction

                root.headlineView = txtAppName
                root.iconView = adAppIcon
                root.starRatingView = adStar
                root.mediaView = adMedia
                root.callToActionView = adCallToAction

                root.setNativeAd(ad)
                root.visibility = View.VISIBLE
            }
        }

    }

    inner class MusicViewHolder(val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root), PlayerEventListener {
        @SuppressLint("SetTextI18n", "DefaultLocale")
        fun bindMusicView(homeRingtonesModel: DataDefaultRings.RingTone, position: Int) {
            val result = convertDurationToTimeString(homeRingtonesModel.duration!!)
            hour = result[0]
            minute = result[1]
            binding.tvTitle.text = homeRingtonesModel.name
            binding.tvTime.text = String.format("%02d:%02d", hour.toInt(), minute.toInt())
            if (playingPosition == position && isPlay) {
                val currentDuration = binding.progressBar.progress
                binding.imgStatus.setImageResource(R.drawable.ic_pause)
                binding.progressBar.visible()
                binding.imgStatus.visible()
                binding.progressBar.progress = currentDuration
            } else if (playingPosition == position && isNext) {
                exoPlayerManager.setPlayerEventListener(this)
                binding.progressBar.visible()
                binding.imgStatus.visible()
                if (isPlay) {
                    animationProgress.resume()
                    binding.imgStatus.setImageResource(R.drawable.ic_pause)
                } else {
                    animationProgress.pause()
                    binding.imgStatus.setImageResource(R.drawable.ic_play)
                }
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
            binding.imgMusic.setSafeOnClickListener {

                currentUrl = homeRingtonesModel.url!!
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
            binding.ctnMusic.setSafeOnClickListener {
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
                        exoPlayerManager.playList(itemList, homeRingtonesModel)
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
            playingPosition += 1
            notifyDataSetChanged()
            if (itemList[playingPosition].id != ITEM_TYPE_ADVERTISE && itemList[playingPosition].id != ITEM_TYPE_BANNER) {
                nextRingTone = itemList[playingPosition]
                exoPlayerManager.playPrepare(nextRingTone!!)
                isPlay = true
                isProcess = true
            } else {
                playingPosition += 1
                nextRingTone = itemList[playingPosition]
                exoPlayerManager.playPrepare(nextRingTone!!)
                isPlay = true
                isProcess = true
            }
            isNext = true
            nextRingTone?.let { EventNextMusic(it) }?.let { eventBusPost(it) }
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
        }

        override fun onProgress(duration: Long) {

        }

        override fun onNext(listRingTone: List<DataDefaultRings.RingTone>, position: Int) {

        }

        override fun onError() {
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe
    fun eventNextMusic(event: EventNextMusic) {
        notifyItemChanged(itemList.indexOf(event.ringTone))
    }

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe
    fun eventChangeIcon(event: EventHideMiniPlay) {
        isPlay = false
        playingPosition = RecyclerView.NO_POSITION
        animationProgress.cancel()
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

            R.layout.each_item -> {
                val binding =
                    EachItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HorizontalViewHolder(binding)
            }

            else -> throw IllegalStateException("Invalid")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MusicViewHolder -> {
                itemList[position].let {
                    holder.bindMusicView(it, position)
                }
            }

            is HorizontalViewHolder -> {
                itemList[0].musicBanners?.let { holder.bind(it) }
            }

            is AdversiteViewHolder -> {
                if (isAdLoaded) {
                    nativeAd?.let { holder.bindNativeAd(it) }
                } else {
                    holder.itemView.visibility = View.GONE
                }
            }

            is LoadingViewHolder -> {}
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position].id) {
            ITEM_TYPE_BANNER -> R.layout.each_item
            ITEM_TYPE_LOADING -> R.layout.item_loading
            ITEM_TYPE_ADVERTISE -> R.layout.item_ad
            else -> R.layout.item_playlist

        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        eventBusUnRegister()
    }

}
