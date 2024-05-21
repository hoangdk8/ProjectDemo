package com.example.projectdemo.ui.detailplaymusic

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.exoplayer.ExoPlayer
import com.example.projectdemo.R
import com.example.projectdemo.audio.ExoPlayerManager
import com.example.projectdemo.data.dataclass.BASE_URL_MUSIC
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.databinding.FragmentDetailPlayMusicBinding
import com.example.projectdemo.listener.PlayerEventListener
import com.example.projectdemo.listener.eventbus.EventNotifyDataSetChanged
import com.example.projectdemo.listener.eventbus.EventVisibleView
import com.example.projectdemo.ui.home.view.HomeFragment
import com.example.projectdemo.untils.downloadRingtone
import com.example.projectdemo.untils.eventBusPost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DetailPlayMusicFragment : Fragment(), PlayerEventListener {
    @Inject
    lateinit var exoPlayerManager: ExoPlayerManager
    private lateinit var binding: FragmentDetailPlayMusicBinding
    private lateinit var exoPlayer: ExoPlayer
    private var isPlay = true
    private var isStop = false
    private var isFavorite = false
    private var isDownload = false
    private val viewModel: DetailPlayMusicViewModel by viewModels()
    private val PERMISSION_REQUEST_CODE = 123

    companion object {
        lateinit var animationProgress: ObjectAnimator
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        exoPlayer = ExoPlayer.Builder(requireActivity()).build()
        exoPlayerManager.setPlayerEventListener(this)
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

    @SuppressLint("ResourceAsColor")
    private fun setupViews() {
        binding.imgBack.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            val currentFragment = fragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment != null) {
                fragmentManager.beginTransaction().remove(currentFragment).commit()
            }
            eventBusPost(EventVisibleView())
            eventBusPost(EventNotifyDataSetChanged())
            exoPlayerManager.stop()
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
        val online = arguments?.getBoolean("online")
        binding.txtTitle.text = name
        binding.imgPlay.setOnClickListener {
            isPlay = !isPlay
            if (isPlay) {
                binding.imgPlay.setImageResource(R.drawable.ic_pause_detail)
                exoPlayerManager.play()
            } else {
                binding.imgPlay.setImageResource(R.drawable.ic_detail_play)
                exoPlayerManager.pause()
            }
            if (isStop) {
                exoPlayerManager.reload()
                isStop = false
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
                when (it) {
                    0 -> {
                        isDownload = false
                    }

                    1 -> {
                        isDownload = true
                        binding.buttonDownload.setBackgroundResource(R.drawable.bg_button_undownload)
                        binding.buttonDownload.isClickable = false
                    }
                }
            }
        }
        fun addMusicDb() {
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
                        online,
                        0, 0, null
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    addMusicDb()
                    if (isDownload) {
                        Toast.makeText(requireActivity(), "Đã Download", Toast.LENGTH_SHORT).show()
                    } else {
                        lifecycleScope.launch {
                            viewModel.updateDownloadStatus(id!!)

                        }
                        Toast.makeText(requireActivity(), "Download", Toast.LENGTH_SHORT).show()
                        name?.let { it1 ->
                            downloadRingtone(
                                requireActivity(),
                                BASE_URL_MUSIC + url,
                                it1
                            )
                        }
                        isDownload = true
                    }
                } else {
                    val alertBuilder = AlertDialog.Builder(requireContext())
                    alertBuilder.setCancelable(true)
                    alertBuilder.setTitle("Yêu cầu cấp quyền truy cập")
                    alertBuilder.setMessage("Bạn ần cấp quyền truy cập để có thể tải nhạc chuông.")
                    alertBuilder.setPositiveButton(android.R.string.yes) { _, _ ->
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.data =
                            Uri.parse("package:${requireActivity().applicationContext.packageName}")
                        startActivity(intent)
                    }
                    alertBuilder.setNegativeButton(android.R.string.no) { _, _ ->
                    }
                    val alert = alertBuilder.create()
                    alert.show()
                }
            } else {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    addMusicDb()
                    if (isDownload) {
                        Toast.makeText(requireActivity(), "Đã Download", Toast.LENGTH_SHORT).show()
                    } else {
                        lifecycleScope.launch {
                            viewModel.updateDownloadStatus(id!!)

                        }
                        Toast.makeText(requireActivity(), "Download", Toast.LENGTH_SHORT).show()
                        name?.let { it1 ->
                            downloadRingtone(
                                requireActivity(),
                                BASE_URL_MUSIC + url,
                                it1
                            )
                        }
                        isDownload = true
                    }
                } else {
                    // Quyền chưa được cấp, yêu cầu người dùng cấp quyền
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PERMISSION_REQUEST_CODE
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.stop()
    }

    override fun onPlaybackEnded() {
        isStop = true
        isPlay = false
    }

    override fun onReadyPlay(ringTone: DataDefaultRings.RingTone) {
    }

    override fun onBuffering() {
    }

    override fun onPlay() {
        binding.imgPlay.setImageResource(R.drawable.ic_pause_detail)
    }

    override fun onStopMusic() {
        binding.imgPlay.setImageResource(R.drawable.ic_detail_play)
    }

    override fun onProgress(duration: Long) {
        binding.progressPlay.max = exoPlayerManager.getPlayer().duration.toInt()
        binding.progressPlay.progress = duration.toInt()
    }
}