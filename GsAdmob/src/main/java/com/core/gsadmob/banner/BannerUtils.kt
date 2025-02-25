package com.core.gsadmob.banner

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowMetrics
import android.widget.FrameLayout
import com.core.gsadmob.R
import com.core.gscore.utils.extensions.gone
import com.core.gscore.utils.extensions.visible
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

object BannerUtils {

    fun initBannerAds(
        activity: Activity,
        flBannerAds: FrameLayout,
        isVip: Boolean,
        adUnitId: Int = R.string.banner_id,
        show: Boolean = true,
        alwaysShow: Boolean = false,
        isCollapsible: Boolean = false,
        callbackShow: (() -> Unit)? = null,
        callbackAdMob: (AdView) -> Unit
    ) {
        flBannerAds.removeAllViews()
        if (isVip) {
            if (!alwaysShow) {
                flBannerAds.gone()
            }
            return
        }
        val bannerAds = AdView(activity)
        bannerAds.adUnitId = activity.getString(adUnitId)
        bannerAds.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        flBannerAds.removeAllViews()
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.BOTTOM
        flBannerAds.addView(bannerAds, params)
        val adSize = getAdSize(activity)
        bannerAds.setAdSize(adSize)

        // Create an extra parameter that aligns the bottom of the expanded ad to
        // the bottom of the bannerView.
        val extras = Bundle()
        if (isCollapsible) {
            extras.putString("collapsible", "bottom")
        }
        val adRequest = AdRequest.Builder()
            .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
            .setHttpTimeoutMillis(5000)
            .build()
        bannerAds.loadAd(adRequest)
        bannerAds.adListener = object : AdListener() {
            override fun onAdLoaded() {
                if (show) {
                    flBannerAds.visible()
                    callbackShow?.invoke()
                } else {
                    if (alwaysShow) {
                        flBannerAds.visible()
                        callbackShow?.invoke()
                    } else {
                        flBannerAds.gone()
                    }
                }
                super.onAdLoaded()
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                if (alwaysShow) {
                    flBannerAds.visible()
                } else {
                    flBannerAds.gone()
                }
            }
        }
        callbackAdMob.invoke(bannerAds)
    }

    private fun getAdSize(context: Context): AdSize {
        val displayMetrics = context.resources.displayMetrics
        val adWidthPixels = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics: WindowMetrics = (context as Activity).windowManager.currentWindowMetrics
                windowMetrics.bounds.width()
            } else {
                displayMetrics.widthPixels
            }
        } catch (e: Exception) {
            e.printStackTrace()
            displayMetrics.widthPixels
        }
        val density = displayMetrics.density
        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }
}