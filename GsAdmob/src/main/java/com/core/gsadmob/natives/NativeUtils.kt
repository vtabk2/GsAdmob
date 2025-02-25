package com.core.gsadmob.natives

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import com.core.gsadmob.R
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd

object NativeUtils {
    fun loadNativeAds(lifecycleOwner: LifecycleOwner, activity: Activity, nativeId: Int = R.string.native_id, isVip: Boolean, callbackStart: () -> Unit, callback: (nativeAd: NativeAd?) -> Unit) {
        lifecycleOwner.launchWhenResumed {
            if (!isVip) {
                try {
                    callbackStart.invoke()
                    val adLoader = AdLoader.Builder(activity, activity.getString(nativeId)).forNativeAd { nativeAd ->
                        // If this callback occurs after the activity is destroyed, you
                        // must call destroy and return or you may get a memory leak.
                        // Note `isDestroyed` is a method on Activity.
                        if (activity.isDestroyed) {
                            nativeAd.destroy()
                            return@forNativeAd
                        }
                        callback.invoke(nativeAd)
                    }.build()
                    adLoader.loadAd(AdRequest.Builder().setHttpTimeoutMillis(5000).build())
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback.invoke(null)
                }
            } else {
                callback.invoke(null)
            }
        }
    }
}