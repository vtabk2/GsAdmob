package com.core.gsadmob.interstitial

import android.app.Activity
import android.content.Context
import com.core.gsadmob.R
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialWithDelayUtils {
    private var mInterstitialAd: InterstitialAd? = null
    private var mAdCloseListener: AdCloseListener? = null
    private var isReload = false
    private var isLoading = false

    private var delayTime = 0L
    private var lastTime = System.currentTimeMillis()

    fun registerDelayTime(time: Long) {
        delayTime = time
    }

    fun loadAd(context: Context, adUnitId: Int = R.string.full_id) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTime < delayTime * 1000) return
        if (isLoading) return
        lastTime = currentTime
        isLoading = true
        mInterstitialAd = null
        val adRequest: AdRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        InterstitialAd.load(context, context.getString(adUnitId), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
                isLoading = false
                if (!isReload) {
                    isReload = true
                    loadAd(context)
                }
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
                isLoading = false
                mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        mInterstitialAd = null
                        mAdCloseListener?.onAdClose()
                        loadAd(context)
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        mInterstitialAd = null
                        mAdCloseListener?.onAdCloseIfFailed()
                        loadAd(context)
                    }
                }
            }
        })
    }

    fun showInterstitialAd(activity: Activity, isVip: Boolean) {
        showInterstitialAd(activity, isVip, null)
    }

    fun showInterstitialAd(activity: Activity, isVip: Boolean, adCloseListener: AdCloseListener?) {
        if (isVip) {
            adCloseListener?.onAdCloseIfFailed()
        } else {
            mAdCloseListener = adCloseListener
            if (mInterstitialAd != null) {
                isReload = false
                mInterstitialAd?.show(activity)
            } else {
                adCloseListener?.onAdCloseIfFailed()
                loadAd(activity)
            }
        }
    }

    fun checkInterstitialAd(activity: Activity) {
        if (mInterstitialAd == null) {
            loadAd(activity)
        }
    }

    interface AdCloseListener {
        fun onAdClose() {}
        fun onAdCloseIfFailed() {}
    }

    companion object {
        private var singleton: InterstitialWithDelayUtils? = null

        /***
         * returns an instance of this class. if singleton is null create an instance
         * else return  the current instance
         * @return
         */
        val instance: InterstitialWithDelayUtils
            get() {
                if (singleton == null) {
                    singleton = InterstitialWithDelayUtils()
                }
                return singleton as InterstitialWithDelayUtils
            }
    }
}