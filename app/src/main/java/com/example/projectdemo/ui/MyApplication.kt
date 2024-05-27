package com.example.projectdemo.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.projectdemo.R
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.HiltAndroidApp
import java.util.Date

@HiltAndroidApp
class MyApplication : Application(), Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private var appOpenManager: AppOpenManager? = null
    private var currentActivity: Activity? = null
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: MyApplication
            private set
        val interstitialAd: InterstitialAd
            get() = instance.mInterstitialAd ?: throw IllegalStateException("mInterstitialAd is not initialized")
    }
    private var mInterstitialAd: InterstitialAd? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        registerActivityLifecycleCallbacks(this)
        MobileAds.initialize(this) {}

        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("PLACE YOUR TEST DEVICE ID", "PLACE YOUR TEST DEVICE ID"))
                .build()
        )

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        appOpenManager = AppOpenManager()

        loadInterstitialAd()
    }
    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            applicationContext,
            getString(R.string.inter_id),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    mInterstitialAd = null
                }
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    mInterstitialAd = interstitialAd
                }
            })
    }
    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onMoveToForeGround() {

        appOpenManager!!.showAdIfAvailable(currentActivity!!)
    }

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
        if (!appOpenManager!!.isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
        appOpenManager!!.showAdIfAvailable(activity, onShowAdCompleteListener)
    }
        private inner class AppOpenManager {
            private var appOpenAd: AppOpenAd? = null
            private var isLoadingAd = false
            var isShowingAd = false

            private var loadTime: Long = 0

            private fun loadAd(context: Context) {
                if (isLoadingAd || isAdAvailable()) {
                    return
                }
                isLoadingAd = true
                val request = AdRequest.Builder().build()
                AppOpenAd.load(
                    context,
                    getString(R.string.open_app_id),
                    request,
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                    object : AppOpenAd.AppOpenAdLoadCallback() {
                        override fun onAdLoaded(ad: AppOpenAd) {
                            super.onAdLoaded(ad)
                            appOpenAd = ad
                            isLoadingAd = false
                            loadTime = Date().time
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            super.onAdFailedToLoad(loadAdError)
                            isLoadingAd = false
                            Toast.makeText(context, "Loi ad", Toast.LENGTH_SHORT).show()
                        }
                    })
            }

            private fun wasLoadTimeLessThanHoursAgo(numHours: Long): Boolean {
                val dateDifference = Date().time - loadTime
                val numMilliSecondsInHour: Long = 3600000
                return dateDifference < numMilliSecondsInHour * numHours
            }

            private fun isAdAvailable(): Boolean {
                return appOpenAd != null && wasLoadTimeLessThanHoursAgo(4)
            }

            fun showAdIfAvailable(activity: Activity) {
                showAdIfAvailable(activity, object : OnShowAdCompleteListener {
                    override fun onShowAdComplete() {
                    }

                })

            }

            fun showAdIfAvailable(
                activity: Activity,
                onShowAdCompleteListener: OnShowAdCompleteListener
            ) {
                if (isShowingAd) {
                    return
                }
                if (!isAdAvailable()) {
                    onShowAdCompleteListener.onShowAdComplete()
                    loadAd(activity)
                    return
                }

                appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdClicked() {
                        super.onAdClicked()
                    }

                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        appOpenAd = null
                        isShowingAd = false
                        onShowAdCompleteListener.onShowAdComplete()
                        loadAd(activity)
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                        appOpenAd = null
                        isShowingAd = false
                        onShowAdCompleteListener.onShowAdComplete()
                        loadAd(activity)
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                    }

                    override fun onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent()
                    }
                }
                isShowingAd = true
                appOpenAd!!.show(activity)
            }

        }
    }
