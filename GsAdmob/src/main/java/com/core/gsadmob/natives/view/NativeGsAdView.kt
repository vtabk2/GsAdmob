package com.core.gsadmob.natives.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.RatingBar
import androidx.appcompat.widget.AppCompatTextView
import com.core.gsadmob.natives.AdsNativeMode
import com.core.gsadmob.utils.extensions.setMarginExtensionFunction
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAdView
import androidx.core.content.withStyledAttributes

class NativeGsAdView(context: Context, attrs: AttributeSet? = null) : BaseNativeAdView(context, attrs) {
    override val titleView: AppCompatTextView? by lazy {
        when (builder.adsNativeMode) {
            AdsNativeMode.NONE -> null
            else -> customView?.findViewById(builder.adsHeadlineId)
        }
    }

    override val subTitleView: AppCompatTextView? by lazy {
        when (builder.adsNativeMode) {
            AdsNativeMode.NONE -> null
            else -> customView?.findViewById(builder.adsBodyId)
        }
    }

    override val starView: RatingBar? by lazy {
        when (builder.adsNativeMode) {
            AdsNativeMode.NONE -> null
            else -> customView?.findViewById(builder.adsStarsId)
        }
    }

    override val iconView: ImageView? by lazy {
        when (builder.adsNativeMode) {
            AdsNativeMode.NONE -> null
            else -> customView?.findViewById(builder.adsAppIconId)
        }
    }

    override val callActionButtonView: AppCompatTextView? by lazy {
        when (builder.adsNativeMode) {
            AdsNativeMode.NONE -> null
            else -> customView?.findViewById(builder.adsCallToActionId)
        }
    }

    override val adView: NativeAdView? by lazy {
        when (builder.adsNativeMode) {
            AdsNativeMode.NONE -> null
            else -> customView?.findViewById(builder.adsViewId)
        }
    }

    override val mediaView: MediaView? by lazy {
        when (builder.adsNativeMode) {
            AdsNativeMode.NONE -> null
            else -> customView?.findViewById(builder.adsMediaViewId)
        }
    }

    override val shimmerView: ShimmerFrameLayout? by lazy {
        when (builder.adsNativeMode) {
            AdsNativeMode.NONE -> null
            else -> customShimmerView?.findViewById(builder.adsShimmerId)
        }
    }

    @SuppressLint("ResourceType")
    override fun initViewWithMode() {
        when (builder.adsNativeMode) {
            AdsNativeMode.NONE -> {
                // nothing
            }

            else -> {
                var marginStartRoot = 0
                var marginTopRoot = 0
                var marginEndRoot = 0
                var marginBottomRoot = 0

                try {
                    val attrViewRoot = intArrayOf(
                        android.R.attr.layout_marginTop,
                        android.R.attr.layout_marginBottom,
                        android.R.attr.layout_marginStart,
                        android.R.attr.layout_marginEnd,
                    )
                    context.withStyledAttributes(builder.adsNativeViewRoot, attrViewRoot) {

                        marginStartRoot = getDimension(2, 0f).toInt()
                        marginTopRoot = getDimension(0, 0f).toInt()
                        marginEndRoot = getDimension(3, 0f).toInt()
                        marginBottomRoot = getDimension(1, 0f).toInt()

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                removeAllViews()
                // adView
                if (builder.adsLayoutId != 0) {
                    customView = layoutInflater.inflate(builder.adsLayoutId, null)
                    customView?.let {
                        addView(it)
                        it.setMarginExtensionFunction(marginStartRoot, marginTopRoot, marginEndRoot, marginBottomRoot)
                    }
                }
                // shimmer view
                if (builder.adsLayoutShimmerId != 0) {
                    customShimmerView = layoutInflater.inflate(builder.adsLayoutShimmerId, null)
                    customShimmerView?.let {
                        addView(it)
                        it.setMarginExtensionFunction(marginStartRoot, marginTopRoot, marginEndRoot, marginBottomRoot)
                    }
                }
            }
        }
    }
}