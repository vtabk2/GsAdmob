package com.core.gsadmob.banner

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import com.core.gsadmob.R
import com.core.gsadmob.databinding.AdBannerGsAdViewBinding
import com.core.gscore.utils.extensions.gone
import com.core.gscore.utils.extensions.invisible
import com.core.gscore.utils.extensions.visible
import com.core.gscore.utils.extensions.visibleIf
import com.google.android.gms.ads.AdView

class BannerGsAdView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    private var binding = AdBannerGsAdViewBinding.inflate(LayoutInflater.from(context), this, true)
    private var bannerView: AdView? = null

    var showType = ShowType.SHOW_IF_SUCCESS
        set(value) {
            field = value

            setupVisible()
        }

    var adsBannerGsBackgroundColor = Color.WHITE
        set(value) {
            field = value

            setBackgroundColor(value)
        }

    init {
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.BannerGsAdView) {
                showType = ShowType.entries.toTypedArray()[getInt(R.styleable.BannerGsAdView_adsShowType, 0)]
                adsBannerGsBackgroundColor = getColor(R.styleable.BannerGsAdView_adsBannerGsBackgroundColor, adsBannerGsBackgroundColor)

                setupVisible()
            }
        }
    }

    fun setBannerAdView(adView: AdView?) {
        this.bannerView = adView

        binding.apply {
            adsBannerView.removeAllViews()
            stopShimmer()

            setupVisible()

            bannerView?.let {
                // Xóa view khỏi parent hiện tại
                (it.parent as? ViewGroup)?.removeView(it)

                adsBannerView.addView(it)
            }
        }
    }

    fun startShimmer() {
        setupVisible(start = true)

        binding.adsShimmerBanner.visible()
        binding.adsShimmerBanner.showShimmer(true)
        binding.adsBannerView.invisible()
    }

    fun stopShimmer() {
        setupVisible()

        binding.adsShimmerBanner.gone()
        binding.adsShimmerBanner.hideShimmer()
        binding.adsBannerView.visible()
    }

    private fun setupVisible(start: Boolean = false) {
        when (showType) {
            ShowType.SHOW_IF_SUCCESS -> {
                if (start) {
                    visible()
                } else {
                    visibleIf(bannerView != null)
                }
            }

            ShowType.ALWAYS_SHOW -> {
                if (start) {
                    visible()
                } else {
                    visibleIf(bannerView != null, true)
                }
            }
            ShowType.HIDE -> invisible()
            ShowType.NOT_SHOW -> gone()
        }
    }

    fun pause() {
        try {
            bannerView?.pause()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resume() {
        try {
            bannerView?.resume()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun destroy() {
        try {
            bannerView?.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}