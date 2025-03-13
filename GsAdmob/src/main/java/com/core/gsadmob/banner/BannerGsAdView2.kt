package com.core.gsadmob.banner

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.core.gsadmob.databinding.AdBannerGsAdViewBinding
import com.core.gscore.utils.extensions.gone
import com.core.gscore.utils.extensions.invisible
import com.core.gscore.utils.extensions.visible
import com.core.gscore.utils.extensions.visibleIf
import com.google.android.gms.ads.AdView

class BannerGsAdView2 @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    private var binding = AdBannerGsAdViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun setAdView(bannerView: AdView?, bannerConfig: BannerConfig) {
        binding.apply {
            adsBannerView.removeAllViews()
            stopShimmer(bannerConfig)

            if (bannerView == null) {
                if (bannerConfig.alwaysShow) {
                    invisible()
                } else {
                    gone()
                }
                return
            }

            visibleIf(bannerConfig.show, bannerConfig.alwaysShow)

            val params = adsBannerView.layoutParams as LayoutParams
            params.gravity = Gravity.BOTTOM
            adsBannerView.addView(bannerView, params)
        }
    }

    fun startShimmer(bannerConfig: BannerConfig) {
        if (!bannerConfig.show) return
        visible() // quan trọng, nếu không visible() thì sẽ ko hiển thị đc view
        binding.adsShimmerBanner.visible()
        binding.adsShimmerBanner.showShimmer(true)
        binding.adsBannerView.invisible()
    }

    fun stopShimmer(bannerConfig: BannerConfig) {
        binding.adsShimmerBanner.hideShimmer()
        binding.adsShimmerBanner.gone()
        binding.adsBannerView.visibleIf(bannerConfig.show, bannerConfig.alwaysShow)
    }
}