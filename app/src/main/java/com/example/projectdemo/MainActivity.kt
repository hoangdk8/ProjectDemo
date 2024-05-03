package com.example.projectdemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.example.projectdemo.databinding.ActivityMainBinding
import com.example.projectdemo.data.dataclass.BASE_URL_MUSIC
import com.example.projectdemo.explore.fragment.ExploreFragment
import com.example.projectdemo.untils.TimerCountDown
import com.example.projectdemo.home.adapter.MyPagerAdapter
import com.example.projectdemo.home.fragment.HomeFragment
import com.example.projectdemo.untils.convertDurationToTimeString
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import dagger.hilt.android.AndroidEntryPoint


@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), HomeFragment.OnDataPass,
    ExploreFragment.OnDataCategories {
    private lateinit var binding: ActivityMainBinding
    private lateinit var exoPlayer: ExoPlayer
    private var isPlay = true
    private var seconds = 0
    private var minute: String = "00"
    private var second: String = "00"
    private var timer : TimerCountDown?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupViews()
        exoPlayer = ExoPlayer.Builder(this).build()
    }

    private fun setupViews() {
        setViewPager()
    }

    private fun setViewPager() {
        binding.viewPager.adapter = MyPagerAdapter(this)
        binding.viewPager.isUserInputEnabled = false
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
        binding.txtTimeCurrent.text = "0:00"
        minute = result[0]
        second = result[1]
        seconds = 0
        binding.txtTimeMax.text = "$minute:$second"
        binding.txtTitle.text = title

        //setup exoplayer
        isPlay = true
        exoPlayer.stop()
        timer?.stopTimer()
        val mediaItem = MediaItem.fromUri(BASE_URL_MUSIC + data)
        when {
            binding.ctnPlayMusic.isVisible -> {
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
            }
        }
        //Đếm thời gian
        timer = TimerCountDown(time.toLong(), object : TimerCountDown.OnTimerListener {
            override fun onTick(seconds: Int) {


                    when {
                        seconds < 10 -> binding.txtTimeCurrent.text = "$minute:0$seconds"
                        seconds >= 10 -> binding.txtTimeCurrent.text = "$minute:$seconds"
                    }
            }

            override fun onFinish() {
                seconds = second.toInt()
            }
        })

        timer?.startTimer()
        exoPlayer.prepare()
        exoPlayer.play()
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                if (state == Player.STATE_ENDED || state == Player.STATE_IDLE) {
                    binding.imgPlay.setImageResource(R.drawable.ic_play_black)
                    binding.imgPlay.setOnClickListener {
                        exoPlayer.setMediaItem(mediaItem)
                        binding.imgPlay.setImageResource(R.drawable.ic_pause_black)
                        isPlay = true
                        seconds = 0
                        timer!!.resetTimer()
                        timer!!.startTimer()
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (isPlaying) {
                    Log.d("hoang", "onItemClick1: ")
                    binding.imgPlay.setImageResource(R.drawable.ic_pause_black)

                }
                else {
                    Log.d("hoang", "onItemClick2: ")
                    binding.imgPlay.setImageResource(R.drawable.ic_play_black)
                }
            }
        })


        binding.imgPlay.setOnClickListener {
            if (!isPlay) {
                binding.imgPlay.setImageResource(R.drawable.ic_pause_black)
                exoPlayer.play()
                isPlay = true
                timer!!.startTimer()
            } else {
                exoPlayer.pause()
                isPlay = false
                timer!!.stopTimer()
                binding.imgPlay.setImageResource(R.drawable.ic_play_black)
            }
        }
        binding.imgClose.setOnClickListener {
            seconds = second.toInt()
            timer?.resetTimer()
            exoPlayer.stop()
            binding.ctnPlayMusic.visibility = View.GONE
        }
    }

    override fun onDataCategories(id: Int, title: String, count: Int, url: String) {
    }
}