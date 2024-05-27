package com.example.projectdemo.ui.detailplaymusic

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.media3.exoplayer.ExoPlayer
import com.example.projectdemo.R
import com.example.projectdemo.audio.ExoPlayerManager
import com.example.projectdemo.data.dataclass.BASE_URL_MUSIC
import com.example.projectdemo.data.dataclass.DataDefaultRings
import com.example.projectdemo.databinding.FragmentDetailPlayMusicBinding
import com.example.projectdemo.listener.PlayerEventListener
import com.example.projectdemo.listener.eventbus.EventNotifyDataSetChanged
import com.example.projectdemo.listener.eventbus.EventVisibleView
import com.example.projectdemo.ui.MyApplication
import com.example.projectdemo.untils.FileUtil
import com.example.projectdemo.untils.TimeInter
import com.example.projectdemo.untils.eventBusPost
import com.example.projectdemo.untils.gone
import com.example.projectdemo.untils.setSafeOnClickListener
import com.example.projectdemo.untils.visible
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private var mRewardedAd: RewardedAd? = null
    var lastAdShownTime: Long = 0
    val interstitialAd: InterstitialAd = MyApplication.interstitialAd


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
        MobileAds.initialize(requireActivity()) {}
        loadRewardedAD()
        setupViews()
    }

    private fun loadRewardedAD() {
        RewardedAd.load(
            requireActivity(), getString(R.string.rewarded_id), AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    super.onAdFailedToLoad(adError)
                    mRewardedAd = null
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    super.onAdLoaded(rewardedAd)
                    mRewardedAd = rewardedAd

                }
            })
    }

    @SuppressLint("ResourceAsColor")
    private fun setupViews() {
        Toast.makeText(requireActivity(), "$interstitialAd", Toast.LENGTH_SHORT).show()
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_download_layout)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnYes: Button = dialog.findViewById(R.id.btn_yes)
        val btnNo: Button = dialog.findViewById(R.id.btn_no)
        loadRewardedAD()
        binding.imgBack.setOnClickListener {
            TimeInter.setCurrentTime(System.currentTimeMillis())
            val timer = TimeInter.getCurrentTime()
            if (System.currentTimeMillis() - timer!! > 30000) {


                if (interstitialAd != null) {
                    Toast.makeText(requireActivity(), "show Ad$interstitialAd", Toast.LENGTH_SHORT)
                        .show()
                    interstitialAd!!.show(requireActivity())

                    interstitialAd!!.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent()
                                requireActivity().onBackPressedDispatcher.onBackPressed()
                                eventBusPost(EventVisibleView())
                                eventBusPost(EventNotifyDataSetChanged())
                            }
                        }
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "adisnull $interstitialAd",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }else{
                requireActivity().onBackPressedDispatcher.onBackPressed()
                eventBusPost(EventVisibleView())
                eventBusPost(EventNotifyDataSetChanged())
            }
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
        reloadViews(id!!)
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
        binding.imgNext.setOnClickListener {
            exoPlayerManager.next()
        }
        binding.imgPrevious.setOnClickListener {
            exoPlayerManager.previous()
        }

        fun addMusicDb() {
            CoroutineScope(Dispatchers.Main).launch {
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
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.updateFavoriteWhenHaveDownload(id!!)
                }
                isFavorite = false

            } else {
                binding.imgHeart.setImageResource(R.drawable.ic_heart_black)
                CoroutineScope(Dispatchers.Main).launch {
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
                    dialog.show()

                } else {
                    val alertBuilder = AlertDialog.Builder(requireContext())
                    alertBuilder.setCancelable(true)
                    alertBuilder.setTitle("Yêu cầu cấp quyền truy cập")
                    alertBuilder.setMessage("Bạn ần cấp quyền truy cập để có thể tải nhạc chuông.")
                    alertBuilder.setPositiveButton(android.R.string.yes) { _, _ ->
                        val intent =
                            Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
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
                    dialog.show()
                } else {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PERMISSION_REQUEST_CODE
                    )
                }
            }


        }

        btnYes.setSafeOnClickListener {
            dialog.dismiss()
            binding.constraintLoading.visible()
            Handler(Looper.getMainLooper()).postDelayed({
                mRewardedAd!!.fullScreenContentCallback =
                    object : FullScreenContentCallback() {

                        override fun onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent()
                            loadRewardedAD()
                            exoPlayerManager.play()
                            reloadViews(id)
                        }
                    }
                mRewardedAd!!.show(requireActivity()) {
                    addMusicDb()
                    if (isDownload) {
                        Toast.makeText(requireActivity(), "Đã Download", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        CoroutineScope(Dispatchers.Main).launch {
                            viewModel.updateDownloadStatus(id!!)

                        }
                        name?.let { it1 -> url?.let { it2 -> downloadMusic(it1, it2) } }
                        isDownload = true
                    }
                }
                binding.constraintLoading.gone()

                exoPlayerManager.pause()
            }, 5000)
        }
        btnNo.setSafeOnClickListener {
            dialog.dismiss()
        }
    }

    private fun reloadViews(id: Int) {
        if (isAdded) {
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.getIsFavoriteById(id!!).observe(requireActivity(), Observer { it ->
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
                })
                viewModel.getIsDownloadById(id!!).observe(requireActivity(), Observer { it ->
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
                })
            }
        }
    }

    private fun downloadMusic(name: String, url: String) {
        val urlDownload = BASE_URL_MUSIC + url
        val nameConvert = name.replace("\\s+".toRegex(), "_")
        FileUtil.downloadAndWriteToCache(
            requireContext(),
            urlDownload,
            "$nameConvert.mp3"
        ) { success ->
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

    override fun onNext(listRingTone: List<DataDefaultRings.RingTone>, position: Int) {
        binding.txtTitle.text = listRingTone[position].name
        binding.progressPlay.progress = 0
    }

    override fun onError() {
        Toast.makeText(requireActivity(), "Bài hát bị lỗi ", Toast.LENGTH_SHORT).show()
        exoPlayerManager.getPlayer()
            .seekTo(exoPlayerManager.getPlayer().currentMediaItemIndex + 1, 0)
        exoPlayerManager.getPlayer().playWhenReady = true
        exoPlayerManager.getPlayer().prepare()
    }
}