package com.example.projectdemo

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.example.projectdemo.databinding.ActivityMainBinding
import com.example.projectdemo.home.adapter.MyPagerAdapter
import com.example.projectdemo.home.fragment.HomeFragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), HomeFragment.OnDataPass {
    private lateinit var binding: ActivityMainBinding
    private lateinit var exoPlayer: ExoPlayer
    private var baseMusicUrl = "https://pub-a59f0b5c0b134cdb808fe708183c7d0e.r2.dev/ringstorage/"
    private var isPlay = true
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupViews()
        exoPlayer = ExoPlayer.Builder(this).build()
    }

    private fun setupViews() {
        setBottomNavigation()
        setViewPager()


    }

    private fun setViewPager() {
        binding.viewPager.adapter = MyPagerAdapter(this)
        binding.bottomNavigation.setOnTabSelectedListener(AHBottomNavigation.OnTabSelectedListener { position, _ ->
            binding.viewPager.currentItem = position
            true
        })
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.bottomNavigation.setCurrentItem(position, false)
            }
        })
        binding.viewPager.isUserInputEnabled = false
    }

    private fun setBottomNavigation() {
        val item1 =
            AHBottomNavigationItem("Home", R.drawable.ic_home, R.color.black)
        val item2 =
            AHBottomNavigationItem("Explore", R.drawable.ic_explore, R.color.black)
        val item3 =
            AHBottomNavigationItem("Search", R.drawable.ic_search, R.color.black)
        val item4 =
            AHBottomNavigationItem("Me", R.drawable.ic_me, R.color.black)


        binding.bottomNavigation.addItem(item1)
        binding.bottomNavigation.addItem(item2)
        binding.bottomNavigation.addItem(item3)
        binding.bottomNavigation.addItem(item4)
        binding.bottomNavigation.defaultBackgroundColor = Color.parseColor("#FFFFFF")
        binding.bottomNavigation.accentColor = Color.parseColor("#131313")
        binding.bottomNavigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
        binding.bottomNavigation.inactiveColor = Color.parseColor("#B3B3B3")

    }
    suspend fun countDownToMinute(hour:Int,minute: Int, text: CharSequence) {
        var seconds = 0
        while (seconds <= minute * 60) {
            val formattedTime = String.format("%02d:%02d", seconds / 60, seconds % 60)
            withContext(Dispatchers.Main) {
                binding.txtTime.text = "$formattedTime/0$hour:$minute"
            }
            delay(1000)
            if (seconds == minute){

            }else {
                seconds++
            }
        }
    }
    fun startCountDownToMinute(hour:Int,minute: Int, txtTime: TextView, ) {
        CoroutineScope(Dispatchers.Default).launch {
            countDownToMinute(hour,minute, binding.txtTime.text)
        }
    }
    @SuppressLint("SetTextI18n")
    override fun onDataPass(data: String, title: String, time:Int) {
        var hour : Int
        var minute : Int
        if (time >= 60000 && time <= 11999){
            hour = 1
            minute = (time - 60000) /1000
        }else{
            hour = 0
            minute = time /1000
        }
        exoPlayer.stop()
        binding.txtTitle.text = title
        startCountDownToMinute(hour,minute, binding.txtTime)
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                if (state == Player.STATE_ENDED || state == Player.STATE_IDLE) {
                    binding.imgPlay.setImageResource(R.drawable.ic_play_black)
                }
            }
        })
        val mediaItem = MediaItem.fromUri(baseMusicUrl+data)
        exoPlayer.setMediaItem(mediaItem)
        Log.d("hoang", "onItemClick:$data ")
        exoPlayer.prepare()
        exoPlayer.play()
        binding.ctnPlayMusic.visibility = View.VISIBLE
        binding.imgPlay.setOnClickListener {
            if (!isPlay){
                exoPlayer.play()
                isPlay = true
                binding.imgPlay.setImageResource(R.drawable.ic_pause_black)
            }else{
                exoPlayer.pause()
                isPlay = false
                binding.imgPlay.setImageResource(R.drawable.ic_play_black)
            }
        }
        binding.imgClose.setOnClickListener {
            exoPlayer.stop()
            binding.ctnPlayMusic.visibility = View.GONE
        }
    }
}