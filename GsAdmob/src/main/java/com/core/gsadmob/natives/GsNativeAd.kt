package com.core.gsadmob.natives

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.core.gsadmob.natives.view.NativeGsAdView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import java.lang.ref.WeakReference
import java.util.WeakHashMap

class GsNativeAd private constructor(
    private val contextRef: WeakReference<Context>,
    private val _lifecycleOwner: LifecycleOwner? = null
) : DefaultLifecycleObserver {

    private val lifecycle: Lifecycle?
        get() = _lifecycleOwner?.lifecycle ?: (contextRef.get() as? LifecycleOwner)?.lifecycle


    companion object {
        private val instances = WeakHashMap<LifecycleOwner, GsNativeAd>()

        fun with(context: Context): GsNativeAd {
            return GsNativeAd(WeakReference(context))
        }

        fun withLifecycleOwner(owner: LifecycleOwner): GsNativeAd {
            return instances.getOrPut(owner) {
                GsNativeAd(WeakReference(owner.getContextSafe()), owner).apply {
                    lifecycle?.addObserver(this)
                }
            }
        }
    }

    private var adUnitId: String? = null
    private var adLoader: AdLoader? = null
    private var currentAdView: NativeGsAdView? = null

    fun load(adUnitId: String): GsNativeAd {
        this.adUnitId = adUnitId
        return this
    }

    fun into(adView: NativeGsAdView) {
        Log.d("GsNativeAd_datnd", "into_54: ")
        currentAdView?.destroy() // Hủy quảng cáo cũ nếu có
        currentAdView = adView
        // Tải quảng cáo mới
        loadNativeAd(contextRef.get() ?: return)
    }

    private fun loadNativeAd(context: Context) {
        Log.d("GsNativeAd_datnd", "loadNativeAd_61: ")
        currentAdView?.startShimmer()
        adUnitId?.let { unitId ->
            adLoader = AdLoader.Builder(context, unitId)
                .forNativeAd { nativeAd ->
                    Log.d("GsNativeAd_datnd", "loadNativeAd_64: loaded = $nativeAd")
                    currentAdView?.setNativeAd(nativeAd, false)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.d("GsNativeAd_datnd", "onAdFailedToLoad_71: ")
                        // Handle ad load failure
                        // TODO: datnd xử lý lỗi / gone ads if need
                        currentAdView?.stopShimmer()
                    }
                })
                .build()

            adLoader?.loadAd(AdRequest.Builder().build())
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        cleanup()
        instances.remove(owner)
        owner.lifecycle.removeObserver(this)
    }

    private fun cleanup() {
        currentAdView?.destroy()
        adLoader = null
        currentAdView = null
    }
}

fun LifecycleOwner.getContextSafe(): Context {
    return when (this) {
        is Activity -> this
        is Fragment -> requireContext()
        else -> throw IllegalArgumentException("Unsupported LifecycleOwner type")
    }
}