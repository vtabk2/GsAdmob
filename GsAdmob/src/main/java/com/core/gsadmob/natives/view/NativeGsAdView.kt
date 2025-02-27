package com.core.gsadmob.natives.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.RatingBar
import androidx.appcompat.widget.AppCompatTextView
import com.core.gsadmob.natives.AdsMode
import com.core.gsadmob.utils.extensions.setMarginExtensionFunction
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAdView

class NativeGsAdView(context: Context, attrs: AttributeSet? = null) : BaseNativeAdView(context, attrs) {
    override val titleView: AppCompatTextView? by lazy {
        when (builder.adsMode) {
            AdsMode.NONE -> null
            else -> customView?.findViewById(builder.adHeadlineId)
        }
    }

    override val subTitleView: AppCompatTextView? by lazy {
        when (builder.adsMode) {
            AdsMode.NONE -> null
            else -> customView?.findViewById(builder.adBodyId)
        }
    }

    override val starView: RatingBar? by lazy {
        when (builder.adsMode) {
            AdsMode.NONE -> null
            else -> customView?.findViewById(builder.adStarsId)
        }
    }

    override val iconView: ImageView? by lazy {
        when (builder.adsMode) {
            AdsMode.NONE -> null
            else -> customView?.findViewById(builder.adAppIconId)
        }
    }

    override val callActionButtonView: AppCompatTextView? by lazy {
        when (builder.adsMode) {
            AdsMode.NONE -> null
            else -> customView?.findViewById(builder.adCallToActionId)
        }
    }

    override val adView: NativeAdView? by lazy {
        when (builder.adsMode) {
            AdsMode.NONE -> null
            else -> customView?.findViewById(builder.adViewId)
        }
    }

    override val mediaView: MediaView? by lazy {
        when (builder.adsMode) {
            AdsMode.NONE -> null
            else -> customView?.findViewById(builder.adMediaViewId)
        }
    }

    override val shimmerView: ShimmerFrameLayout? by lazy {
        when (builder.adsMode) {
            AdsMode.NONE -> null
            else -> customView?.findViewById(builder.adShimmerId)
        }
    }

    @SuppressLint("ResourceType")
    override fun initViewWithMode() {
        when (builder.adsMode) {
            AdsMode.NONE -> {
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
                    val typedArrayRoot = context.obtainStyledAttributes(builder.adsNativeViewRoot, attrViewRoot)

                    marginStartRoot = typedArrayRoot.getDimension(2, 0f).toInt()
                    marginTopRoot = typedArrayRoot.getDimension(0, 0f).toInt()
                    marginEndRoot = typedArrayRoot.getDimension(3, 0f).toInt()
                    marginBottomRoot = typedArrayRoot.getDimension(1, 0f).toInt()

                    typedArrayRoot.recycle()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                removeView(customView)
                customView = layoutInflater.inflate(builder.adLayoutId, null)
                addView(customView)
                customView?.setMarginExtensionFunction(marginStartRoot, marginTopRoot, marginEndRoot, marginBottomRoot)
            }
        }
    }
}