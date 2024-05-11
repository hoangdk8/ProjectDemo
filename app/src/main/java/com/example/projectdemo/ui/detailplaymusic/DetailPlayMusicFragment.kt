package com.example.projectdemo.ui.detailplaymusic

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.projectdemo.R
import com.example.projectdemo.data.dataclass.BASE_URL_MUSIC
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.databinding.FragmentDetailPlayMusicBinding
import com.example.projectdemo.event.EventVisibleView
import com.example.projectdemo.untils.eventBusPost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailPlayMusicFragment : Fragment() {
    private lateinit var binding: FragmentDetailPlayMusicBinding
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mediaItem: MediaItem
    private var isPlay = true
    private var isFavorite = false
    private var isDownload = false
    private val viewModel: DetailPlayMusicViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        exoPlayer = ExoPlayer.Builder(requireActivity()).build()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailPlayMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding.imgBack.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val currentFragment = fragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment != null) {
                fragmentManager.beginTransaction().remove(currentFragment).commit()
            }
            eventBusPost(EventVisibleView())
        }

        val arguments = arguments
        val id = arguments?.getInt("id")
        val name = arguments?.getString("name")
        val categories = arguments?.getString("categories")
        val duration = arguments?.getInt("duration")
        val count = arguments?.getInt("count")
        val url = arguments?.getString("url")
        val hometype = arguments?.getString("hometype")
        val isVip = arguments?.getInt("isVip")
        val datatype = arguments?.getString("datatype")

        binding.txtTitle.text = name
        mediaItem = MediaItem.fromUri(BASE_URL_MUSIC + url)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        exoPlayer.play()
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                if (state == Player.STATE_ENDED) {
                    binding.imgPlay.setOnClickListener {
                        exoPlayer.setMediaItem(mediaItem)
                        exoPlayer.play()
                    }
                } else if (state == Player.STATE_BUFFERING)
                    if (state == Player.STATE_ENDED || state == Player.STATE_IDLE) {
                        binding.imgPlay.setOnClickListener {
                            exoPlayer.setMediaItem(mediaItem)
                        }
                    }
            }

        })
        binding.imgPlay.setOnClickListener {
            isPlay = !isPlay
            if (isPlay) {
                binding.imgPlay.setImageResource(R.drawable.ic_pause_detail)
                exoPlayer.play()
            } else {
                binding.imgPlay.setImageResource(R.drawable.ic_detail_play)
                exoPlayer.stop()
            }
        }

        lifecycleScope.launch {
            viewModel.getIsFavoriteById(id!!).observe(viewLifecycleOwner) {
                when (it) {
                    0 -> {
                        isFavorite = false
                        binding.imgHeart.setImageResource(R.drawable.ic_heart)
                    }

                    1 -> {
                        isFavorite = true
                        binding.imgHeart.setImageResource(R.drawable.ic_heart_black)
                    }
                }
            }
            viewModel.getIsDownloadById(id!!).observe(viewLifecycleOwner) {
                Toast.makeText(requireActivity(), "$it", Toast.LENGTH_SHORT).show()
                when (it) {
                    0 -> {
                        isDownload = false
                    }

                    1 -> {
                        isDownload = true
                    }
                }
            }
        }
        fun addMusicDb(){
            lifecycleScope.launch {
                viewModel.addMusic(
                    DataDefaultRings.RingTone(
                        categories,
                        count,
                        datatype,
                        duration,
                        hometype,
                        id,
                        isVip,
                        name,
                        url,
                        0, 0
                    )
                )
            }
        }
        binding.imgHeart.setOnClickListener {
            addMusicDb()
            if (isFavorite) {
                binding.imgHeart.setImageResource(R.drawable.ic_heart)
                lifecycleScope.launch {
                    viewModel.updateFavoriteWhenHaveDownload(id!!)
                }
                isFavorite = false

            } else {
                binding.imgHeart.setImageResource(R.drawable.ic_heart_black)
                lifecycleScope.launch {
                    viewModel.updateFavoriteStatus(id!!)
                }
                isFavorite = true
                Toast.makeText(requireActivity(), "Thêm vào yêu thích", Toast.LENGTH_SHORT)
                    .show()

            }
        }
        binding.buttonDownload.setOnClickListener {
            addMusicDb()
            if (isDownload) {
                Toast.makeText(requireActivity(), "Đã Download", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
                    viewModel.updateDownloadStatus(id!!)

                }
                Toast.makeText(requireActivity(), "Download", Toast.LENGTH_SHORT).show()
                isDownload = true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.stop()
    }
}