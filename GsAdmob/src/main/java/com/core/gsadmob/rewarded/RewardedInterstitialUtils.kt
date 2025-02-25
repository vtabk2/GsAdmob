package com.core.gsadmob.rewarded

import android.app.Activity
import android.content.Context
import com.core.gsadmob.R
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

class RewardedInterstitialUtils {
    private var mRewardedInterstitialAd: RewardedInterstitialAd? = null
    private var isReload = false
    private var isLoading = false
    private var isShowing = false
    private var listener: RewardedAdCloseListener? = null

    var isCancel: Boolean = false

    fun load(context: Context) {
        if (isLoading) return
        mRewardedInterstitialAd = null
        val adRequest = AdRequest.Builder()
            .setHttpTimeoutMillis(5000)
            .build()
        RewardedInterstitialAd.load(
            context,
            context.getString(R.string.rewarded_interstitial_id),
            adRequest,
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    mRewardedInterstitialAd = null
                    isLoading = false
                    isShowing = false
                    listener?.onAdCloseIfFailed()
                    if (!isReload) {
                        isReload = true
                        load(context)
                    }
                }

                override fun onAdLoaded(rewardedInterstitialAd: RewardedInterstitialAd) {
                    mRewardedInterstitialAd = rewardedInterstitialAd
                    isLoading = false
                    listener?.let {
                        if (context is Activity) {
                            show(context)
                        }
                    }
                    mRewardedInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                mRewardedInterstitialAd = null
                                isShowing = false
                                listener?.onAdClose()
                                load(context)
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                mRewardedInterstitialAd = null
                                isShowing = false
                                listener?.onAdClose()
                                load(context)
                            }
                        }
                }
            })
    }

    fun showAd(activity: Activity, callbackShow: () -> Unit) {
        if (mRewardedInterstitialAd != null) {
            isReload = false
            show(activity)
            callbackShow()
        } else {
            load(activity)
        }
    }

    private fun show(activity: Activity) {
        if (isCancel) {
            mRewardedInterstitialAd = null
            isLoading = false
            isShowing = false
            isReload = false
            return
        }
        if (!isShowing) {
            isShowing = true
            mRewardedInterstitialAd?.show(activity) {
                listener?.onShowFinishSuccess()
            }
        }
    }

    fun registerAdsListener(listener: RewardedAdCloseListener?) {
        this.listener = listener
    }

    fun removeAdsListener() {
        listener = null
    }

    interface RewardedAdCloseListener {
        fun onAdCloseIfFailed() {}
        fun onShowFinishSuccess() {}
        fun onAdClose() {}
    }

    companion object {
        private var singleton: RewardedInterstitialUtils? = null

        /***
         * returns an instance of this class. if singleton is null create an instance
         * else return  the current instance
         * @return
         */
        val instance: RewardedInterstitialUtils
            get() {
                if (singleton == null) {
                    singleton = RewardedInterstitialUtils()
                }
                return singleton as RewardedInterstitialUtils
            }
    }
}