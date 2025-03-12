package com.core.gsadmob

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.Date

class AppOpenAdManager(private val context: Context) {
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private var isShowingAd = false
    private var loadTime: Long = 0 // Thời gian tải quảng cáo

    // Callback để thông báo khi quảng cáo bắt đầu hiển thị và khi đóng
    interface AppOpenAdShowListener {
        fun onAdShowing() // Quảng cáo bắt đầu hiển thị
        fun onAdDismissed() // Quảng cáo đã đóng
    }

    // Kiểm tra xem quảng cáo có sẵn và chưa hết hạn
    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4) // Quảng cáo hết hạn sau 4 giờ
    }

    // Kiểm tra thời gian tải quảng cáo có trong vòng N giờ hay không
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    // Tải quảng cáo
    fun loadAd() {
        if (isLoadingAd || isAdAvailable()) {
            return
        }
        isLoadingAd = true
        val adRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        AppOpenAd.load(context, context.getString(R.string.app_open_id), adRequest, object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(ad: AppOpenAd) {
                appOpenAd = ad
                isLoadingAd = false
                loadTime = Date().time // Lưu thời gian tải quảng cáo
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                isLoadingAd = false
            }
        }
        )
    }

    // Hiển thị quảng cáo
    fun showAdIfAvailable(activity: Activity, isVip: Boolean, listener: AppOpenAdShowListener) {
        // Kiểm tra nếu Activity hiện tại là AdActivity (quảng cáo full-screen khác đang hiển thị)
        if (activity.javaClass.simpleName == "AdActivity") {
            listener.onAdDismissed()
            return
        }

        if (isVip) {
            listener.onAdDismissed()
            return
        }

        if (isShowingAd) {
            return
        }
        if (!isAdAvailable()) {
            listener.onAdDismissed()
            loadAd()
            return
        }

        // Hiển thị màn hình mờ đợi trước khi hiển thị quảng cáo
        listener.onAdShowing()

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowingAd = false
                listener.onAdDismissed() // Thông báo quảng cáo đã đóng
                loadAd() // Tải quảng cáo mới sau khi quảng cáo hiện tại bị đóng
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                isShowingAd = false
                listener.onAdDismissed() // Thông báo quảng cáo không hiển thị được
                loadAd() // Tải quảng cáo mới nếu quảng cáo hiện tại không hiển thị được
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
            }
        }
        appOpenAd?.show(activity)
    }
}