package com.core.gsadmob.banner

import android.content.Context
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

class BannerGsAdView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    private var binding = AdBannerGsAdViewBinding.inflate(LayoutInflater.from(context), this, true)
    private var bannerView: AdView? = null

    private var adsAlwaysShow: Boolean = false

    var adsShow: Boolean = true
        set(value) {
            field = value

            if (bannerView == null) {
                if (adsAlwaysShow) {
                    invisible()
                } else {
                    gone()
                }
                return
            }

            visibleIf(adsShow, adsAlwaysShow)
        }

    private var adsAutoStartShimmer: Boolean = true
        set(value) {
            field = value
            if (field) {
                startShimmer()
            } else {
                stopShimmer()
            }
        }

    init {
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.BannerGsAdView) {
                adsAlwaysShow = getBoolean(R.styleable.BannerGsAdView_adsAlwaysShow, adsAlwaysShow)
                adsShow = getBoolean(R.styleable.BannerGsAdView_adsShow, adsShow)
                adsAutoStartShimmer = getBoolean(R.styleable.BannerGsAdView_adsAutoStartShimmer, adsAutoStartShimmer)
            }
        }
    }

    fun setBannerAdView(adView: AdView?) {
        this.bannerView = adView

        binding.apply {
            adsBannerView.removeAllViews()
            stopShimmer()

            if (bannerView == null) {
                if (adsAlwaysShow) {
                    invisible()
                } else {
                    gone()
                }
                return
            }

            visibleIf(adsShow, adsAlwaysShow)

            val params = adsBannerView.layoutParams as LayoutParams
            params.gravity = Gravity.BOTTOM

            // Kiểm tra nếu view đã có parent
            if (bannerView?.parent != null) {
                (bannerView?.parent as? ViewGroup)?.removeView(bannerView) // Xóa view khỏi parent hiện tại
            }
            adsBannerView.addView(bannerView, params)
        }
    }

    fun startShimmer() {
        if (!adsShow) return
        visible() // quan trọng, nếu không visible() thì sẽ ko hiển thị đc view
        binding.adsShimmerBanner.visible()
        binding.adsShimmerBanner.showShimmer(true)
        binding.adsBannerView.invisible()
    }

    fun stopShimmer() {
        binding.adsShimmerBanner.hideShimmer()
        binding.adsShimmerBanner.gone()
        binding.adsBannerView.visibleIf(adsShow, adsAlwaysShow)
    }

    fun pause() {
        bannerView?.pause()
    }

    fun resume() {
        bannerView?.resume()
    }

    fun destroy() {
        try {
            bannerView?.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}