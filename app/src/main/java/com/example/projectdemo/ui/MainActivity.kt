package com.example.projectdemo.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.projectdemo.R
import com.example.projectdemo.audio.ExoPlayerManager
import com.example.projectdemo.databinding.ActivityMainBinding
import com.example.projectdemo.listener.eventbus.EventGoneView
import com.example.projectdemo.listener.eventbus.EventShowMiniPlay
import com.example.projectdemo.listener.eventbus.EventVisibleView
import com.example.projectdemo.ui.home.adapter.MyPagerAdapter
import com.example.projectdemo.untils.NotifyWorker
import com.example.projectdemo.untils.eventBusRegister
import com.example.projectdemo.untils.eventBusUnRegister
import com.example.projectdemo.untils.gone
import com.example.projectdemo.untils.showPermissionDialog
import com.example.projectdemo.untils.visible
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject


@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var exoPlayerManager: ExoPlayerManager
    private lateinit var binding: ActivityMainBinding
    private lateinit var miniPlay: MiniPlay
    private var oldItem = 0
    private val PERMISSION_REQUEST_CODE = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupViews()
        eventBusRegister()
        miniPlay = MiniPlay(binding.ctnPlayMusic, exoPlayerManager)
        checkPermission()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_CANCELED -> {
                    checkPermission()
                }
            }
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                PERMISSION_REQUEST_CODE
            )
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                showPermissionDialog(this)
            }
        }
    }

    private fun setupViews() {
        setViewPager()
    }

    @Subscribe
    fun goneView(event: EventGoneView) {
        binding.bottomNavigation.visibility = View.GONE
        binding.ctnPlayMusic.root.gone()
    }

    @Subscribe
    fun showView(event: EventShowMiniPlay) {
        binding.ctnPlayMusic.root.visible()
    }

    @Subscribe
    fun visibleView(event: EventVisibleView) {
        binding.bottomNavigation.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        eventBusUnRegister()
    }

    override fun onResume() {
        super.onResume()
        NotifyWorker.cancelWork(this)
    }

    override fun onPause() {
        super.onPause()
        NotifyWorker.notifyWork(this)
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

}