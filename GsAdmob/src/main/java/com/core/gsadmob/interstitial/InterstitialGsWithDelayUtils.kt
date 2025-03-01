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

class InterstitialGsWithDelayUtils {
    private val interstitialMap = HashMap<Int, SubInterstitial>()

    fun registerDelayTime(delayTime: Long, adUnitId: Int = R.string.full_id) {
        if (interstitialMap.containsKey(adUnitId)) {
            interstitialMap[adUnitId]?.delayTime = delayTime
        } else {
            interstitialMap[adUnitId] = SubInterstitial(delayTime = delayTime)
        }
    }

    fun loadAd(context: Context, isVip: Boolean, adUnitId: Int = R.string.full_id) {
        if (isVip) return
        val subInterstitial = interstitialMap[adUnitId] ?: SubInterstitial()
        if (subInterstitial.isLoading) return

        if (subInterstitial.delayTime > 0L) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - subInterstitial.lastTime < subInterstitial.delayTime * 1000) return
            subInterstitial.lastTime = currentTime
        }

        subInterstitial.isLoading = true
        subInterstitial.interstitialAd = null

        val adRequest: AdRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        InterstitialAd.load(context, context.getString(adUnitId), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                subInterstitial.interstitialAd = null
                subInterstitial.isLoading = false
                if (!subInterstitial.isReload) {
                    subInterstitial.isReload = true
                    loadAd(context, isVip)
                }
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                subInterstitial.interstitialAd = interstitialAd
                subInterstitial.isLoading = false
                subInterstitial.interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        subInterstitial.interstitialAd = null
                        subInterstitial.adCloseListener?.onAdClose()
                        loadAd(context, isVip)
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        subInterstitial.interstitialAd = null
                        subInterstitial.adCloseListener?.onAdCloseIfFailed()
                        loadAd(context, isVip)
                    }
                }
            }
        })
    }

    fun showInterstitialAd(activity: Activity, isVip: Boolean, adUnitId: Int = R.string.full_id, listener: AdCloseListener? = null) {
        interstitialMap[adUnitId]?.let { subInterstitial ->
            if (isVip) {
                // khi vip xóa quảng cáo đã tải đi
                subInterstitial.interstitialAd = null
                subInterstitial.adCloseListener = null
                listener?.onAdCloseIfFailed()
            } else {
                subInterstitial.adCloseListener = listener
                if (subInterstitial.interstitialAd != null) {
                    subInterstitial.isReload = false
                    subInterstitial.interstitialAd?.show(activity)
                } else {
                    subInterstitial.adCloseListener?.onAdCloseIfFailed()
                    loadAd(activity, false)
                }
            }
        }
    }

    fun checkReloadInterstitialAdIfNeed(activity: Activity, isVip: Boolean, adUnitId: Int = R.string.full_id) {
        interstitialMap[adUnitId]?.let { subInterstitial ->
            if (isVip) {
                subInterstitial.interstitialAd = null
                subInterstitial.adCloseListener = null
                return
            }
            if (subInterstitial.interstitialAd == null) {
                loadAd(activity, false)
            }
        }
    }

    interface AdCloseListener {
        fun onAdClose() {}
        fun onAdCloseIfFailed() {}
    }

    data class SubInterstitial(
        var interstitialAd: InterstitialAd? = null,
        var adCloseListener: AdCloseListener? = null,
        var isReload: Boolean = false,
        var isLoading: Boolean = false,
        var delayTime: Long = 0L,
        var lastTime: Long = 0L
    )

    companion object {
        private var singleton: InterstitialGsWithDelayUtils? = null

        /***
         * returns an instance of this class. if singleton is null create an instance
         * else return  the current instance
         * @return
         */
        val instance: InterstitialGsWithDelayUtils
            get() {
                if (singleton == null) {
                    singleton = InterstitialGsWithDelayUtils()
                }
                return singleton as InterstitialGsWithDelayUtils
            }
    }
}