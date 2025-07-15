package com.core.gsadmob.banner

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
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
import com.google.firebase.annotations.PublicApi

class BannerGsAdView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    private var binding = AdBannerGsAdViewBinding.inflate(LayoutInflater.from(context), this, true)
    private var bannerView: AdView? = null

    @PublicApi
    var showType = ShowType.SHOW_IF_SUCCESS
        set(value) {
            field = value

            setupVisible()
        }

    @PublicApi
    var adsBannerGsBackgroundColor = Color.WHITE
        set(value) {
            field = value

            setBackgroundColor(value)
        }

    init {
        // Set gravity for the FrameLayout (this)
        (binding.root.layoutParams as? LayoutParams)?.gravity = Gravity.BOTTOM

        attrs?.let {
            context.withStyledAttributes(it, R.styleable.BannerGsAdView) {
                showType = ShowType.entries.toTypedArray()[getInt(R.styleable.BannerGsAdView_adsShowType, 0)]
                adsBannerGsBackgroundColor = getColor(R.styleable.BannerGsAdView_adsBannerGsBackgroundColor, adsBannerGsBackgroundColor)

                setupVisible()
            }
        }
    }

    fun setBannerAdView(adView: AdView?, isStartShimmer: Boolean) {
        if (isStartShimmer && adView == null) {
            startShimmer()
            return
        }

        // Nếu cùng một instance thì bỏ qua
        if (bannerView == adView) {
            stopShimmer()
            return
        }

        // Hủy banner cũ nếu có
        bannerView?.let { oldAdView ->
            (oldAdView.parent as? ViewGroup)?.removeView(oldAdView)
            oldAdView.destroy()
        }

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

    @PublicApi
    fun startShimmer() {
        setupVisible(start = true)

        binding.adsShimmerBanner.visible()
        binding.adsShimmerBanner.showShimmer(true)
        binding.adsBannerView.invisible()
    }

    @PublicApi
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
            bannerView?.let { adView ->
                // Xóa view khỏi parent trước khi hủy
                (adView.parent as? ViewGroup)?.removeView(adView)
                adView.destroy()
            }
            // Đặt lại thành null để tránh sử dụng lại
            bannerView = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}