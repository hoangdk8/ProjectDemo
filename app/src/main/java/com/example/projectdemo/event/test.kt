//package com.example.project_figma.ui.home.adapter
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.example.project_figma.R
//import com.example.project_figma.constants.DataItemType.Companion.ITEM_TYPE_ADVERTISE
//import com.example.project_figma.constants.DataItemType.Companion.ITEM_TYPE_LOADING
//import com.example.project_figma.constants.DataItemType.Companion.ITEM_TYPE_MUSIC
//import com.example.project_figma.data.model.DataItemHomeRingtones
//import com.example.project_figma.data.model.HomeModel
//import com.example.project_figma.data.model.ItemModel
//import com.example.project_figma.databinding.EachItemBinding
//import com.example.project_figma.databinding.ItemAdversiteBinding
//import com.example.project_figma.databinding.ItemLoadingBinding
//import com.example.project_figma.databinding.ItemPlayMusicBinding
//import com.example.project_figma.ui.home.ExoPlayerManager
//import com.example.project_figma.utils.PlayPauseEvent
//import com.example.project_figma.utils.convertDurationToTimeString
//import org.greenrobot.eventbus.EventBus
//import org.greenrobot.eventbus.Subscribe
//import javax.inject.Inject
//
//class HomeAdapter @Inject constructor(
//    private var itemList: List<DataItemHomeRingtones>,
//    private val exoPlayerManager: ExoPlayerManager
//) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ExoPlayerManager.PlayerEventListener {
//    private var hour: String = "00"
//    private var minute: String = "00"
//    private var playingPosition = RecyclerView.NO_POSITION
//    private lateinit var onClickItem: (ringtoneModel: HomeModel) -> Unit
//    private lateinit var onClickItemDetailMusic: (ringtoneModel: HomeModel) -> Unit
//    private lateinit var recyclerView: RecyclerView
//
//    @Subscribe
//    fun onPlayPauseEvent(event: PlayPauseEvent) {
//        if (playingPosition != RecyclerView.NO_POSITION) {
//            val holder = recyclerView.findViewHolderForAdapterPosition(playingPosition) as MusicViewHolder
//            if (event.isPlaying) {
//                holder.binding.imgStatus.setImageResource(R.drawable.ic_pause_white)
//            } else {
//                holder.binding.imgStatus.setImageResource(R.drawable.ic_play_white)
//            }
//        }
//    }
//
//    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
//        super.onAttachedToRecyclerView(recyclerView)
//        this.recyclerView = recyclerView
//        EventBus.getDefault().register(this)
//    }
//
//    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
//        super.onDetachedFromRecyclerView(recyclerView)
//        EventBus.getDefault().unregister(this)
//    }
//
//    fun onClickItemListener(onClickItem: (homeRingtonesModel: HomeModel) -> Unit) {
//        this.onClickItem = onClickItem
//    }
//
//    fun onClickItemListenerDetailMusic(onClickItemDetailMusic: (homeRingtonesModel: HomeModel) -> Unit) {
//        this.onClickItemDetailMusic = onClickItemDetailMusic
//    }
//
//    inner class MusicViewHolder(val binding: ItemPlayMusicBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bindMusicView(homeRingtonesModel: HomeModel, position: Int) {
//            val result = convertDurationToTimeString(homeRingtonesModel.duration!!)
//            hour = result[0]
//            minute = result[1]
//            binding.tvTitle.text = homeRingtonesModel.name
//            binding.tvTime.text = "$hour:$minute"
//
//            if (position == playingPosition) {
//                binding.imgStatus.setImageResource(R.drawable.ic_pause_white)
//            } else {
//                binding.imgStatus.setImageResource(R.drawable.ic_play_white)
//                binding.progressBarInitPlay.visibility = View.GONE
//            }
//
//            binding.playMusicDetailHome.setOnClickListener {
//                onClickItemDetailMusic.invoke(homeRingtonesModel)
//                if (playingPosition != RecyclerView.NO_POSITION && playingPosition != position) {
//                    val previousPlayingPosition = playingPosition
//                    playingPosition = position
//                    notifyItemChanged(previousPlayingPosition)
//                }
//                playingPosition = position
//                notifyItemChanged(playingPosition)
//            }
//
//            binding.imgStatus.setOnClickListener {
//                onClickItem.invoke(homeRingtonesModel)
//                exoPlayerManager.setPlayerEventListener(this@HomeAdapter)
//                binding.progressBarInitPlay.visibility = View.VISIBLE
//
//                if (playingPosition == position) {
//                    if (exoPlayerManager.isPlaying) {
//                        exoPlayerManager.pause()
//
//                        if (position == playingPosition) {
//                            binding.imgStatus.setImageResource(R.drawable.ic_pause_white)
//                        } else {
//                            binding.imgStatus.setImageResource(R.drawable.ic_play_white)
//                        }
//                        binding.progressBarInitPlay.visibility = View.GONE
//                    } else {
//                        exoPlayerManager.playPrepare(homeRingtonesModel.url!!)
//                        binding.progressBarInitPlay.visibility = View.GONE
//                    }
//                } else {
//                    exoPlayerManager.playPrepare(homeRingtonesModel.url!!)
//                    binding.progressBarInitPlay.visibility = View.VISIBLE
//                }
//
//                //check position
//                if (playingPosition != RecyclerView.NO_POSITION && playingPosition != position) {
//                    val previousPlayingPosition = playingPosition
//                    playingPosition = position
//                    notifyItemChanged(previousPlayingPosition)
//                }
//                playingPosition = position
//                notifyItemChanged(playingPosition)
//            }
//        }
//    }
//
//    inner class LoadingViewHolder(private val binding: ItemLoadingBinding) :
//        RecyclerView.ViewHolder(binding.root)
//
//    inner class AdversiteViewHolder(private val binding: ItemAdversiteBinding) :
//        RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return when (viewType) {
//            R.layout.item_play_music -> {
//                val binding =
//                    ItemPlayMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//                MusicViewHolder(binding)
//            }
//
//            R.layout.item_adversite -> {
//                val binding =
//                    ItemAdversiteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//                AdversiteViewHolder(binding)
//            }
//
//            R.layout.item_loading -> {
//                val binding =
//                    ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//                LoadingViewHolder(binding)
//            }
//
//            else -> {
//                val binding =
//                    EachItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//                HorizontalViewHolder(binding)
//            }
//        }
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        when (holder) {
//            is MusicViewHolder -> {
//                itemList[position].bannerList.let {
//                    holder.bindMusicView(it as HomeModel, position)
//                }
//            }
//
//            is HorizontalViewHolder -> {
//                itemList[position].recyclerItemList.let { holder.bind(it as List<ItemModel>) }
//            }
//
//            is AdversiteViewHolder -> {}
//
//            is LoadingViewHolder -> {}
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return itemList.size
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return when (itemList[position].viewType) {
//            ITEM_TYPE_MUSIC -> R.layout.item_play_music
//            ITEM_TYPE_LOADING -> R.layout.item_loading
//            ITEM_TYPE_ADVERTISE -> R.layout.item_adversite
//            else -> R.layout.each_item
//        }
//    }
//
//    override fun onPlaybackEnded() {
//        playingPosition = RecyclerView.NO_POSITION
//        notifyDataSetChanged()
//    }
//}