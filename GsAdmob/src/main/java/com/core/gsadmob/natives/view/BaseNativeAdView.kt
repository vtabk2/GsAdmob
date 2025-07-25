package com.core.gsadmob.natives.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import androidx.appcompat.widget.AppCompatTextView
import com.core.gsadmob.R
import com.core.gsadmob.natives.AdsNativeMode
import com.core.gsadmob.natives.NativeDefaultConfig
import com.core.gscore.utils.extensions.gone
import com.core.gscore.utils.extensions.invisible
import com.core.gscore.utils.extensions.visible
import com.core.gscore.utils.extensions.visibleIf
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.annotations.PublicApi
import java.util.Locale

abstract class BaseNativeAdView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    abstract val titleView: AppCompatTextView?
    abstract val subTitleView: AppCompatTextView?
    abstract val starView: RatingBar?
    abstract val iconView: ImageView?
    abstract val callActionButtonView: AppCompatTextView?
    abstract val adView: NativeAdView?
    abstract val mediaView: MediaView?
    abstract val shimmerView: ShimmerFrameLayout?

    var customView: View? = null
    var customShimmerView: View? = null

    var builder = Builder()

    private var nativeAd: NativeAd? = null

    /**
     * Biến này mục đích là để tạm ẩn quảng cáo native đi
     * isHide = true -> tạm ẩn (shimmer sẽ không hiển thị, quảng cáo tải được cũng sẽ không tự động hiển thị)
     */
    private var isHide = false

    /**
     * Biến này mục đích là có muốn gone view luôn không hay chỉ invisible
     * requireGone = true -> cần gone view
     */
    private var requireGone = false

    init {
        attrs?.let {
            setupBuilderWithTypedArray(context.obtainStyledAttributes(it, R.styleable.BaseNativeAdView))
        }
    }

    private fun checkBuilderWithAdsNativeMode() {
        when (builder.adsNativeMode) {
            AdsNativeMode.ALBUM -> {
                if (builder.adsLayoutId == R.layout.ad_native_custom) {
                    builder = NativeDefaultConfig.BUILDER_ALBUM
                }
            }

            AdsNativeMode.FONT -> {
                if (builder.adsLayoutId == R.layout.ad_native_custom) {
                    builder = NativeDefaultConfig.BUILDER_FONT
                }
            }

            AdsNativeMode.FRAME -> {
                if (builder.adsLayoutId == R.layout.ad_native_custom) {
                    builder = NativeDefaultConfig.BUILDER_FRAME
                }
            }

            AdsNativeMode.LANGUAGE -> {
                if (builder.adsLayoutId == R.layout.ad_native_custom) {
                    builder = NativeDefaultConfig.BUILDER_LANGUAGE
                }
            }

            AdsNativeMode.SHARE -> {
                if (builder.adsLayoutId == R.layout.ad_native_custom) {
                    builder = NativeDefaultConfig.BUILDER_SHARE
                }
            }

            AdsNativeMode.STICKER -> {
                if (builder.adsLayoutId == R.layout.ad_native_custom) {
                    builder = NativeDefaultConfig.BUILDER_STICKER
                }
            }

            AdsNativeMode.TEMPLATE -> {
                if (builder.adsLayoutId == R.layout.ad_native_custom) {
                    builder = NativeDefaultConfig.BUILDER_TEMPLATE
                }
            }

            AdsNativeMode.VIP -> {
                if (builder.adsLayoutId == R.layout.ad_native_custom) {
                    builder = NativeDefaultConfig.BUILDER_VIP
                }
            }

            else -> {

            }
        }
    }

    open fun initViewWithMode() {}

    /**
     * @param showShimmerIfFailed = true Nếu không tải được quảng cáo thì sẽ hiển thị shimmer mãi mãi
     */
    open fun setNativeAd(nativeAd: NativeAd?, isStartShimmer: Boolean, showShimmerIfFailed: Boolean = false) {
        this.nativeAd = nativeAd
        if (isStartShimmer && nativeAd == null) {
            startShimmer()
            return
        }
        stopShimmer()
        if (nativeAd == null) {
            if (showShimmerIfFailed) {
                startShimmer()
            } else {
                gone()
            }
            return
        }
        visibleIf(!isHide, !requireGone)

        val icon = nativeAd.icon
        val title = nativeAd.headline
        val callAction = nativeAd.callToAction
        val subTitle = nativeAd.body
        if (icon != null) {
            iconView?.setImageDrawable(icon.drawable)
            iconView?.visible()
        } else {
            iconView?.gone()
        }
        if (subTitle != null) {
            subTitleView?.text = subTitle
            subTitleView?.visible()
        } else {
            subTitleView?.invisible()
        }
        if (title != null) {
            titleView?.text = title
            titleView?.visible()
        } else {
            titleView?.invisible()
        }
        nativeAd.starRating?.let { starRating ->
            starView?.rating = starRating.toFloat()
            starView?.visible()
            when (builder.adsNativeMode) {
                AdsNativeMode.ALBUM -> {
                    subTitleView?.invisible()
                }

                else -> {
                    // nothing
                }
            }
        } ?: run {
            when (builder.adsNativeMode) {
                AdsNativeMode.STICKER -> {
                    if (icon != null) {
                        starView?.invisible()
                    } else {
                        starView?.gone()
                    }
                }

                AdsNativeMode.ALBUM -> {
                    starView?.gone()
                    subTitleView?.visible()
                }
                AdsNativeMode.SHARE -> {
                    if (icon != null) {
                        starView?.invisible()
                    } else {
                        starView?.gone()
                    }
                }

                else -> {
                    starView?.invisible()
                }
            }
        }
        if (callAction != null) {
            callActionButtonView?.text = callAction.lowercase(Locale.getDefault()).replaceFirstChar { it.titlecase() }
            callActionButtonView?.visible()
        } else {
            callActionButtonView?.gone()
        }

        adView?.headlineView = titleView
        adView?.bodyView = subTitleView
        adView?.iconView = iconView
        adView?.callToActionView = callActionButtonView
        adView?.mediaView = mediaView
        adView?.setNativeAd(nativeAd)
    }

    fun updateCallActionButton(resId: Int) {
        if (resId == 0) return
        callActionButtonView?.setBackgroundResource(resId)
    }

    fun destroy() {
        try {
            nativeAd?.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @PublicApi
    fun startShimmer() {
        visibleIf(!isHide, !requireGone) // quan trọng, nếu không visible() thì sẽ ko hiển thị đc view
        shimmerView?.visible()
        shimmerView?.showShimmer(true)
        adView?.invisible()
    }

    @PublicApi
    fun stopShimmer() {
        shimmerView?.hideShimmer()
        shimmerView?.gone()
        adView?.visible()
    }

    fun hide(requireGone: Boolean = false) {
        isHide = true
        this.requireGone = requireGone
        visibleIf(false, !requireGone)
        invalidate()
    }

    fun show(showShimmerIfFailed: Boolean = false) {
        isHide = false
        if (nativeAd != null) {
            visible()
        } else if (showShimmerIfFailed) {
            startShimmer()
        }
        invalidate()
    }

    @PublicApi
    fun applyBuilder(builder: Builder) {
        if (builder.adsNativeMode == AdsNativeMode.NONE) return
        this.builder = builder
        initViewWithMode()
    }

    fun setStyle(styleId: Int) {
        setupBuilderWithTypedArray(context.obtainStyledAttributes(styleId, R.styleable.BaseNativeAdView))
    }

    private fun setupBuilderWithTypedArray(typedArray: TypedArray) {
        if (typedArray.hasValue(R.styleable.BaseNativeAdView_adsNativeMode)) {
            builder.adsNativeMode = AdsNativeMode.entries.toTypedArray()[typedArray.getInt(R.styleable.BaseNativeAdView_adsNativeMode, 0)]
        }
        if (typedArray.hasValue(R.styleable.BaseNativeAdView_adsLayoutId)) {
            builder.adsLayoutId = typedArray.getResourceId(R.styleable.BaseNativeAdView_adsLayoutId, builder.adsLayoutId)
        }
        if (typedArray.hasValue(R.styleable.BaseNativeAdView_adsLayoutShimmerId)) {
            builder.adsLayoutShimmerId = typedArray.getResourceId(R.styleable.BaseNativeAdView_adsLayoutShimmerId, builder.adsLayoutShimmerId)
        }
        if (typedArray.hasValue(R.styleable.BaseNativeAdView_adsHeadlineId)) {
            builder.adsHeadlineId = typedArray.getResourceId(R.styleable.BaseNativeAdView_adsHeadlineId, builder.adsHeadlineId)
        }
        if (typedArray.hasValue(R.styleable.BaseNativeAdView_adsBodyId)) {
            builder.adsBodyId = typedArray.getResourceId(R.styleable.BaseNativeAdView_adsBodyId, builder.adsBodyId)
        }
        if (typedArray.hasValue(R.styleable.BaseNativeAdView_adsStarsId)) {
            builder.adsStarsId = typedArray.getResourceId(R.styleable.BaseNativeAdView_adsStarsId, builder.adsStarsId)
        }
        if (typedArray.hasValue(R.styleable.BaseNativeAdView_adsAppIconId)) {
            builder.adsAppIconId = typedArray.getResourceId(R.styleable.BaseNativeAdView_adsAppIconId, builder.adsAppIconId)
        }
        if (typedArray.hasValue(R.styleable.BaseNativeAdView_adsCallToActionId)) {
            builder.adsCallToActionId = typedArray.getResourceId(R.styleable.BaseNativeAdView_adsCallToActionId, builder.adsCallToActionId)
        }
        if (typedArray.hasValue(R.styleable.BaseNativeAdView_adsViewId)) {
            builder.adsViewId = typedArray.getResourceId(R.styleable.BaseNativeAdView_adsViewId, builder.adsViewId)
        }
        if (typedArray.hasValue(R.styleable.BaseNativeAdView_adsMediaViewId)) {
            builder.adsMediaViewId = typedArray.getResourceId(R.styleable.BaseNativeAdView_adsMediaViewId, builder.adsMediaViewId)
        }
        if (typedArray.hasValue(R.styleable.BaseNativeAdView_adsShimmerId)) {
            builder.adsShimmerId = typedArray.getResourceId(R.styleable.BaseNativeAdView_adsShimmerId, builder.adsShimmerId)
        }
        if (typedArray.hasValue(R.styleable.BaseNativeAdView_adsNativeViewRoot)) {
            builder.adsNativeViewRoot = typedArray.getResourceId(R.styleable.BaseNativeAdView_adsNativeViewRoot, builder.adsNativeViewRoot)
        }
        checkBuilderWithAdsNativeMode()
        typedArray.recycle()

        applyBuilder(builder)
    }

    data class Builder(
        var adsLayoutId: Int = R.layout.ad_native_custom,
        var adsLayoutShimmerId: Int = R.layout.ad_native_custom_shimmer,
        var adsHeadlineId: Int = R.id.ad_headline,
        var adsBodyId: Int = R.id.ad_body,
        var adsStarsId: Int = R.id.ad_stars,
        var adsAppIconId: Int = R.id.ad_app_icon,
        var adsCallToActionId: Int = R.id.ad_call_to_action,
        var adsViewId: Int = R.id.ad_view,
        var adsMediaViewId: Int = R.id.ad_media,
        var adsShimmerId: Int = R.id.ad_shimmer,
        var adsNativeViewRoot: Int = R.style.ads_BaseNativeAdViewRoot,
        var adsNativeMode: AdsNativeMode = AdsNativeMode.NONE
    )
}