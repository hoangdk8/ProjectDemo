package com.example.projectdemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.example.projectdemo.databinding.ActivityMainBinding
import com.example.projectdemo.home.TimerCountDown
import com.example.projectdemo.home.adapter.MyPagerAdapter
import com.example.projectdemo.home.fragment.HomeFragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import dagger.hilt.android.AndroidEntryPoint


@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), HomeFragment.OnDataPass {
    private lateinit var binding: ActivityMainBinding
    private lateinit var exoPlayer: ExoPlayer
    private var baseMusicUrl = "https://pub-a59f0b5c0b134cdb808fe708183c7d0e.r2.dev/ringstorage/"
    private var isPlay = true
    private var seconds = 0
    private var hour: String = "00"
    private var minute: String = "00"
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

                R.id.menu_home -> binding.viewPager.currentItem = 0
                R.id.menu_explore -> binding.viewPager.currentItem = 1
                R.id.menu_search -> binding.viewPager.currentItem = 2
                R.id.menu_me -> binding.viewPager.currentItem = 3
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

    private fun convertDuration(duration: Int) {
        when {
            duration < 10000 -> {
                hour = "0"
                minute = "0${duration / 1000}"
            }

            duration in 10000..59999 -> {
                hour = "0"
                minute = "${duration / 1000}"
            }

            duration in 60000..60999 -> {
                hour = "1"
                minute = "00"
            }

            duration in 61000..119999 -> {
                hour = "1"
                minute = "${(duration - 60000) / 1000}"
            }

            else -> {
                Toast.makeText(this, "$duration", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onDataPass(data: String, title: String, time: Int) {
        convertDuration(time)
        isPlay = true
        binding.txtTimeMax.text = "$hour:$minute"
        exoPlayer.stop()
        binding.txtTitle.text = title
        binding.ctnPlayMusic.visibility = View.VISIBLE
        val mediaItem = MediaItem.fromUri(baseMusicUrl + data)
        when {
            binding.ctnPlayMusic.isVisible -> exoPlayer.setMediaItem(mediaItem)
        }
        seconds = 0
        val timerCountDown = TimerCountDown(time.toLong(), object : TimerCountDown.OnTimerListener {

            override fun onTick(seconds: Int) {
                when {
                    seconds < 10 -> binding.txtTimeCurrent.text = "$hour:0$seconds"
                    seconds >= 10 -> binding.txtTimeCurrent.text = "$hour:$seconds"
                }

            }

            override fun onFinish() {
                seconds = 0
            }
        })
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
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (isPlaying) {
                    Log.d("hoang", "onItemClick: ")
                    timerCountDown.startTimer()
                    binding.imgPlay.setImageResource(R.drawable.ic_pause_black)

                } else {
                    Log.d("Hoang", "onDataPass: ")
                    timerCountDown.stopTimer()
                    binding.imgPlay.setImageResource(R.drawable.ic_play_black)
                }
            }
        })

        exoPlayer.prepare()
        exoPlayer.play()

        binding.imgPlay.setOnClickListener {
            if (!isPlay) {
                exoPlayer.play()
                isPlay = true
            } else {
                exoPlayer.pause()
                isPlay = false

            }
        }
        binding.imgClose.setOnClickListener {
            exoPlayer.stop()
            binding.ctnPlayMusic.visibility = View.GONE
        }
    }
}