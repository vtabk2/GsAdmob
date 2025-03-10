package com.core.gsadmob.rewarded

import android.app.Activity
import android.content.Context
import com.core.gsadmob.R
import com.core.gsadmob.callback.AdGsListener
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class RewardedUtils {
    private var mRewardedAd: RewardedAd? = null
    private var isReload = false
    private var isLoading = false
    private var isShowing = false
    private var listener: AdGsListener? = null

    var isCancel: Boolean = false

    fun load(context: Context) {
        if (isLoading) return
        mRewardedAd = null
        val adRequest = AdRequest.Builder()
            .setHttpTimeoutMillis(5000)
            .build()
        RewardedAd.load(
            context,
            context.getString(R.string.rewarded_id),
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    mRewardedAd = null
                    isLoading = false
                    isShowing = false
                    listener?.onAdCloseIfFailed()
                    if (!isReload) {
                        isReload = true
                        load(context)
                    }
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    mRewardedAd = rewardedAd
                    isLoading = false
                    listener?.let {
                        if (context is Activity) {
                            show(context)
                        }
                    }
                    mRewardedAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                mRewardedAd = null
                                isShowing = false
                                listener?.onAdClose()
                                load(context)
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                mRewardedAd = null
                                isShowing = false
                                listener?.onAdClose()
                                load(context)
                            }
                        }
                }
            })
    }

    fun showAd(activity: Activity, callbackShow: () -> Unit) {
        if (mRewardedAd != null) {
            isReload = false
            show(activity)
            callbackShow()
        } else {
            load(activity)
        }
    }

    private fun show(activity: Activity) {
        if (isCancel) {
            mRewardedAd = null
            isLoading = false
            isShowing = false
            isReload = false
            return
        }
        if (!isShowing) {
            isShowing = true
            mRewardedAd?.show(activity) {
                listener?.onShowFinishSuccess()
            }
        }
    }

    fun registerAdsListener(listener: AdGsListener?) {
        this.listener = listener
    }

    fun removeAdsListener() {
        listener = null
    }

    companion object {
        private var singleton: RewardedUtils? = null

        /***
         * returns an instance of this class. if singleton is null create an instance
         * else return  the current instance
         * @return
         */
        val instance: RewardedUtils
            get() {
                if (singleton == null) {
                    singleton = RewardedUtils()
                }
                return singleton as RewardedUtils
            }
    }
}