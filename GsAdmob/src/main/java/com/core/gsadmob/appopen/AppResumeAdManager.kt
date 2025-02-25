package com.core.gsadmob.appopen

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.core.gsadmob.R
import com.google.android.gms.ads.AdActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.Date

class AppResumeAdManager {
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isShowingAd = false

    /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
    private var loadTime: Long = 0

    /**
     * Load an ad.
     *
     * @param context the context of the activity that loads the ad
     */
    fun loadAd(context: Context) {
        // Do not load ad if there is an unused ad or one is already loading.
        if (isLoadingAd || isAdAvailable()) {
            return
        }

        isLoadingAd = true
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            context.getString(R.string.app_open_id_resume),
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                /**
                 * Called when an app open ad has loaded.
                 *
                 * @param ad the loaded app open ad.
                 */
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                }

                /**
                 * Called when an app open ad has failed to load.
                 *
                 * @param loadAdError the error.
                 */
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                }
            }
        )
    }

    /** Check if ad was loaded more than n hours ago. */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    /** Check if ad exists and can be shown. */
    private fun isAdAvailable(): Boolean {
        // Ad references in the app open beta will time out after four hours, but this time limit
        // may change in future beta versions. For details, see:
        // https://support.google.com/admob/answer/9341964?hl=en
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    fun checkHasAds(): Boolean {
        return appOpenAd != null
    }

    fun showAdIfAvailable(activity: Activity, tag: String, isVip: Boolean, callbackFailed: ((error: Boolean) -> Unit)? = null) {
        if (activity is AdActivity || isVip) return
        // If the app open ad is already showing, do not show the ad again.
        if (isShowingAd) {
            return
        }
        // If the app open ad is not available yet, invoke the callback then load the ad.
        if (!isAdAvailable()) {
            callbackFailed?.invoke(true)
            loadAd(activity)
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            /** Called when full screen content is dismissed. */
            override fun onAdDismissedFullScreenContent() {
                // Set the reference to null so isAdAvailable() returns false.
                appOpenAd = null
                isShowingAd = false

                callbackFailed?.invoke(false)
                loadAd(activity)
            }

            /** Called when fullscreen content failed to show. */
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                isShowingAd = false

                callbackFailed?.invoke(true)
                loadAd(activity)
            }

            /** Called when fullscreen content is shown. */
            override fun onAdShowedFullScreenContent() {
            }
        }
        isShowingAd = true

        (activity as? AppCompatActivity)?.supportFragmentManager?.let { fragmentManager ->
            val bottomDialogFragment = fragmentManager.findFragmentByTag(tag)
            if (bottomDialogFragment != null && bottomDialogFragment.isVisible) {
                // BottomDialogFragment đang hiển thị
                appOpenAd?.show(activity)
            } else {
                // BottomDialogFragment không hiển thị
                appOpenAd = null
                isShowingAd = false
            }
        }
    }
}