package com.example.projectdemo.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.projectdemo.ExoPlayerManager
import com.example.projectdemo.R
import com.example.projectdemo.databinding.ActivityMainBinding
import com.example.projectdemo.event.EventGoneView
import com.example.projectdemo.event.EventPlayDetailMusic
import com.example.projectdemo.event.EventVisibleView
import com.example.projectdemo.ui.home.adapter.MyPagerAdapter
import com.example.projectdemo.ui.home.view.HomeFragment
import com.example.projectdemo.untils.convertDurationToTimeString
import com.example.projectdemo.untils.eventBusRegister
import com.example.projectdemo.untils.eventBusUnRegister
import com.example.projectdemo.untils.gone
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject


@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), HomeFragment.OnDataPass,
    ExoPlayerManager.PlayerEventListener {
    @Inject
    lateinit var exoPlayerManager: ExoPlayerManager
    private lateinit var binding: ActivityMainBinding
    private var isPlay = true
    private var seconds = 0
    private var minute: String = ""
    private var second: String = ""
    private var oldItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupViews()
        eventBusRegister()
        exoPlayerManager.setPlayerEventListenerMain(this)
    }

    private fun setupViews() {
        setViewPager()
    }

    @Subscribe
    fun playDetailMusic(event: EventPlayDetailMusic) {
        binding.ctnPlayMusic.gone()
    }

    @Subscribe
    fun goneView(event: EventGoneView) {
        binding.bottomNavigation.visibility = View.GONE
    }

    @Subscribe
    fun visibleView(event: EventVisibleView) {
        binding.bottomNavigation.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        eventBusUnRegister()
    }

    private fun setViewPager() {
        binding.viewPager.adapter = MyPagerAdapter(this)
        binding.viewPager.isUserInputEnabled = false
        oldItem = binding.viewPager.currentItem
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {

                R.id.menu_home -> {
                    binding.viewPager.currentItem = 0
                    val fragmentManager = supportFragmentManager
                    val currentFragment =
                        fragmentManager.findFragmentById(R.id.fragment_container_view)
                    if (currentFragment != null) {
                        fragmentManager.beginTransaction().remove(currentFragment).commit()
                    }
                }

                R.id.menu_explore -> binding.viewPager.currentItem = 1
                R.id.menu_search -> {
                    binding.viewPager.currentItem = 2
                    val fragmentManager = supportFragmentManager
                    val currentFragment =
                        fragmentManager.findFragmentById(R.id.fragment_container_view)
                    if (currentFragment != null) {
                        fragmentManager.beginTransaction().remove(currentFragment).commit()
                    }
                }

                R.id.menu_me -> {
                    binding.viewPager.currentItem = 3
                    val fragmentManager = supportFragmentManager
                    val currentFragment =
                        fragmentManager.findFragmentById(R.id.fragment_container_view)
                    if (currentFragment != null) {
                        fragmentManager.beginTransaction().remove(currentFragment).commit()
                    }
                }

                else -> {}
            }
            true
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.bottomNavigation.selectedItemId = when (position) {
                    0 -> R.id.menu_home
                    1 -> R.id.menu_explore
                    2 -> R.id.menu_search
                    3 -> R.id.menu_me
                    else -> R.id.menu_home
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    override fun onDataPass(data: String, title: String, time: Int) {

        binding.ctnPlayMusic.visibility = View.VISIBLE
        //setupViews sau khi click
        val result = convertDurationToTimeString(time)
        minute = result[0]
        second = result[1]
        seconds = 0
        binding.txtTimeMax.text = "$minute:$second"
        binding.txtTitle.text = title

        //setup exoplayer
        //Đếm thời gian
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
            binding.ctnPlayMusic.visibility = View.GONE
        }
    }


    override fun onPlaybackEnded() {
        Log.d("hoang", "onPlaybackEnded: ")
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

    @SuppressLint("SetTextI18n")
    override fun onProgress(duration: Long) {
        val result = convertDurationToTimeString(duration.toInt())
        minute = result[0]
        second = result[1]
        binding.txtTimeCurrent.text = "$minute:$second"
    }

    override fun onProgressBar(currentDuration: Long, totalDuration: Long) {

    }

}