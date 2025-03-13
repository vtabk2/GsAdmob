package com.core.gsadmob.banner

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.core.gsadmob.databinding.AdBannerGsAdViewBinding
import com.core.gscore.utils.extensions.gone
import com.core.gscore.utils.extensions.invisible
import com.core.gscore.utils.extensions.visible
import com.core.gscore.utils.extensions.visibleIf
import com.google.android.gms.ads.AdView

class BannerGsAdView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    private var binding = AdBannerGsAdViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun setBannerAdView(bannerView: AdView?, bannerConfig: BannerConfig = BannerConfig()) {
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

            // Kiểm tra nếu view đã có parent
            if (bannerView.parent != null) {
                (bannerView.parent as? ViewGroup)?.removeView(bannerView) // Xóa view khỏi parent hiện tại
            }
            adsBannerView.addView(bannerView, params)
        }
    }

    fun startShimmer(bannerConfig: BannerConfig = BannerConfig()) {
        if (!bannerConfig.show) return
        visible() // quan trọng, nếu không visible() thì sẽ ko hiển thị đc view
        binding.adsShimmerBanner.visible()
        binding.adsShimmerBanner.showShimmer(true)
        binding.adsBannerView.invisible()
    }

    fun stopShimmer(bannerConfig: BannerConfig = BannerConfig()) {
        binding.adsShimmerBanner.hideShimmer()
        binding.adsShimmerBanner.gone()
        binding.adsBannerView.visibleIf(bannerConfig.show, bannerConfig.alwaysShow)
    }
}