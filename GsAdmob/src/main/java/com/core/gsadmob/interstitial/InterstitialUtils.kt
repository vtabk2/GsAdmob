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

class InterstitialUtils {
    private var mInterstitialAd: InterstitialAd? = null
    private var mAdCloseListener: AdCloseListener? = null
    private var isReload = false
    private var isLoading = false

    private var mInterstitialAdNoVideo: InterstitialAd? = null
    private var mAdCloseNoVideoListener: AdCloseListener? = null
    private var isReloadNoVideo = false
    private var isLoadingNoVideo = false

    fun loadAd(context: Context, isVip: Boolean) {
        if (isVip) return
        if (isLoading) return
        isLoading = true
        mInterstitialAd = null
        val adRequest: AdRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        InterstitialAd.load(
            context,
            context.getString(R.string.full_id),
            adRequest,
            object : InterstitialAdLoadCallback() {
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
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
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

    fun loadAdNoVideo(context: Context, isVip: Boolean) {
        if (isVip) return
        if (isLoadingNoVideo) return
        mInterstitialAdNoVideo = null
        val adRequest: AdRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        InterstitialAd.load(
            context,
            context.getString(R.string.full_without_video),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAdNoVideo = null
                    isLoadingNoVideo = false
                    if (!isReloadNoVideo) {
                        isReloadNoVideo = true
                        loadAdNoVideo(context, isVip)
                    }
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    mInterstitialAdNoVideo = ad
                    isLoadingNoVideo = false
                    mInterstitialAdNoVideo?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                mInterstitialAdNoVideo = null
                                mAdCloseNoVideoListener?.onAdClose()
                                loadAdNoVideo(context, isVip)
                            }

                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                mInterstitialAdNoVideo = null
                                mAdCloseNoVideoListener?.onAdCloseIfFailed()
                                loadAdNoVideo(context, isVip)
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

    fun showInterstitialAdNoVideo(activity: Activity, isVip: Boolean, listener: AdCloseListener? = null) {
        if (isVip) {
            // khi vip xóa quảng cáo đã tải đi
            mInterstitialAdNoVideo = null
            mAdCloseNoVideoListener = null
            listener?.onAdCloseIfFailed()
        } else {
            mAdCloseNoVideoListener = listener
            if (mInterstitialAdNoVideo != null) {
                isReloadNoVideo = false
                mInterstitialAdNoVideo?.show(activity)
            } else {
                mAdCloseNoVideoListener?.onAdCloseIfFailed()
                loadAdNoVideo(activity, false)
            }
        }
    }

    interface AdCloseListener {
        fun onAdClose() {}
        fun onAdCloseIfFailed() {}
    }

    companion object {
        private var singleton: InterstitialUtils? = null

        /***
         * returns an instance of this class. if singleton is null create an instance
         * else return  the current instance
         * @return
         */
        val instance: InterstitialUtils
            get() {
                if (singleton == null) {
                    singleton = InterstitialUtils()
                }
                return singleton as InterstitialUtils
            }
    }
}