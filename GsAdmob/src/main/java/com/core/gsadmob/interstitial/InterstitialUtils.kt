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

    private var interstitialAdNoVideo: InterstitialAd? = null
    private var adCloseListenerNoVideo: AdCloseListener? = null
    private var isReloadNoVideo = false
    private var isLoadingNoVideo = false

    fun loadAd(context: Context) {
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
                        loadAd(context)
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

    fun loadAdNoVideo(context: Context) {
        if (isLoadingNoVideo) return
        interstitialAdNoVideo = null
        val adRequest: AdRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        InterstitialAd.load(
            context,
            context.getString(R.string.full_without_video),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAdNoVideo = null
                    isLoadingNoVideo = false
                    if (!isReloadNoVideo) {
                        isReloadNoVideo = true
                        loadAdNoVideo(context)
                    }
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAdNoVideo = ad
                    isLoadingNoVideo = false
                    interstitialAdNoVideo?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                interstitialAdNoVideo = null
                                adCloseListenerNoVideo?.onAdClose()
                                loadAdNoVideo(context)
                            }

                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                interstitialAdNoVideo = null
                                adCloseListenerNoVideo?.onAdCloseIfFailed()
                                loadAdNoVideo(context)
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

    fun showInterstitialAdNoVideo(activity: Activity, isVip: Boolean) {
        showInterstitialAdNoVideo(activity, isVip, null)
    }

    fun showInterstitialAdNoVideo(
        activity: Activity,
        isVip: Boolean,
        adCloseListenerNoVideo: AdCloseListener?
    ) {
        this.adCloseListenerNoVideo = adCloseListenerNoVideo
        if (isVip) {
            this.adCloseListenerNoVideo?.onAdCloseIfFailed()
        } else {
            if (this.interstitialAdNoVideo != null) {
                isReloadNoVideo = false
                interstitialAdNoVideo?.show(activity)
            } else {
                this.adCloseListenerNoVideo?.onAdCloseIfFailed()
                loadAdNoVideo(activity)
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