package com.core.gsadmob.banner

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowMetrics
import android.widget.FrameLayout
import com.core.gsadmob.R
import com.core.gsadmob.databinding.AdBannerShimmerBinding
import com.core.gscore.utils.extensions.gone
import com.core.gscore.utils.extensions.invisible
import com.core.gscore.utils.extensions.visible
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

class BannerGsAdView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private var bannerAds: AdView? = null
    private var shimmerView: ShimmerFrameLayout? = null

    init {
        bannerAds = AdView(context)
        bannerAds?.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.BOTTOM
        addView(bannerAds, params)

        val adSize = getAdSize(context)
        bannerAds?.setAdSize(adSize)

        val binding = AdBannerShimmerBinding.inflate(LayoutInflater.from(context))
        shimmerView = binding.adShimmerBanner
        shimmerView?.let {
            addView(shimmerView)
        }
    }

    fun loadAds(
        isVip: Boolean,
        adUnitId: Int = R.string.banner_id,
        show: Boolean = true,
        alwaysShow: Boolean = false,
        isCollapsible: Boolean = false,
        callbackShow: (() -> Unit)? = null,
    ) {
        if (isVip) {
            if (!alwaysShow) {
                stopShimmer()
                gone()
            }
            return
        }

        startShimmer()

        if (TextUtils.isEmpty(bannerAds?.adUnitId)) {
            bannerAds?.adUnitId = context.getString(adUnitId)
        }
        // Create an extra parameter that aligns the bottom of the expanded ad to
        // the bottom of the bannerView.
        val extras = Bundle()
        if (isCollapsible) {
            extras.putString("collapsible", "bottom")
        }
        val adRequest = AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras).setHttpTimeoutMillis(5000).build()
        bannerAds?.loadAd(adRequest)

        bannerAds?.adListener = object : AdListener() {
            override fun onAdLoaded() {
                stopShimmer()
                if (show) {
                    bannerAds?.visible()
                    callbackShow?.invoke()
                } else {
                    if (alwaysShow) {
                        bannerAds?.visible()
                        callbackShow?.invoke()
                    } else {
                        gone()
                    }
                }
                super.onAdLoaded()
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                stopShimmer()
                if (alwaysShow) {
                    bannerAds?.visible()
                    callbackShow?.invoke()
                } else {
                    gone()
                }
            }
        }
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

    fun startShimmer() {
        visible() // quan trọng, nếu không visible() thì sẽ ko hiển thị đc view
        shimmerView?.visible()
        shimmerView?.showShimmer(true)
        bannerAds?.invisible()
    }

    fun stopShimmer() {
        shimmerView?.hideShimmer()
        shimmerView?.gone()
        bannerAds?.visible()
    }
}