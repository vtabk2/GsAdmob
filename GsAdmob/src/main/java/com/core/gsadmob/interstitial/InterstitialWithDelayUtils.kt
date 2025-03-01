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
    private var lastTime = 0L

    fun registerDelayTime(delayTime: Long) {
        this.delayTime = delayTime
    }

    fun loadAd(context: Context, isVip: Boolean, adUnitId: Int = R.string.full_id) {
        if (isVip) return
        if (isLoading) return

        if (delayTime > 0L) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastTime < delayTime * 1000) return
            lastTime = currentTime
        }

        isLoading = true
        mInterstitialAd = null
        val adRequest: AdRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        InterstitialAd.load(context, context.getString(adUnitId), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
                isLoading = false
                if (!isReload) {
                    isReload = true
                    loadAd(context, isVip)
                }
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
                isLoading = false
                mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        mInterstitialAd = null
                        mAdCloseListener?.onAdClose()
                        loadAd(context, isVip)
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        mInterstitialAd = null
                        mAdCloseListener?.onAdCloseIfFailed()
                        loadAd(context, isVip)
                    }
                }
            }
        })
    }

    fun showInterstitialAd(activity: Activity, isVip: Boolean, listener: AdCloseListener? = null) {
        if (isVip) {
            // khi vip xóa quảng cáo đã tải đi
            mInterstitialAd = null
            mAdCloseListener = null
            listener?.onAdCloseIfFailed()
        } else {
            mAdCloseListener = listener
            if (mInterstitialAd != null) {
                isReload = false
                mInterstitialAd?.show(activity)
            } else {
                mAdCloseListener?.onAdCloseIfFailed()
                loadAd(activity, false)
            }
        }
    }

    fun checkReloadInterstitialAdIfNeed(activity: Activity, isVip: Boolean) {
        if (isVip) {
            mInterstitialAd = null
            mAdCloseListener = null
            return
        }
        if (mInterstitialAd == null) {
            loadAd(activity, false)
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