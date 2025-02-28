package com.example.GsAdmob

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.core.gsadmob.natives.AdsMode
import com.core.gsadmob.natives.NativeUtils
import com.core.gsadmob.natives.view.BaseNativeAdView
import com.example.GsAdmob.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var bindingView: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingView = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingView.root)

        val builder = BaseNativeAdView.Builder().apply {
            adLayoutId = R.layout.ad_native_test
            adLayoutShimmerId = R.layout.ad_native_test_shimmer
//            adLayoutShimmerId = 0 // nếu không muốn dùng shimmer
            adHeadlineId = R.id.ad_headline_test
            adStarsId = R.id.ad_stars_test
            adAppIconId = R.id.ad_app_icon_test
            adCallToActionId = R.id.ad_call_to_action_test
            adViewId = R.id.ad_view_test
            adShimmerId = R.id.ad_view_test_shimmer
            adsNativeViewRoot = R.style.ads_NativeTestRoot
            adsMode = AdsMode.CUSTOM
        }
        bindingView.nativeTest1.applyBuilder(builder)

//        bindingView.nativeTest1.applyBuilder(NativeDefaultConfig.BUILDER_ALBUM)
//
//        bindingView.nativeTest2.applyBuilder(NativeDefaultConfig.BUILDER_FONT)
//
//        bindingView.nativeTest3.applyBuilder(NativeDefaultConfig.BUILDER_FRAME)
//
//        bindingView.nativeTest4.applyBuilder(NativeDefaultConfig.BUILDER_LANGUAGE)
//
//        bindingView.nativeTest5.applyBuilder(NativeDefaultConfig.BUILDER_SHARE)
//
//        bindingView.nativeTest6.applyBuilder(NativeDefaultConfig.BUILDER_STICKER)
//
//        bindingView.nativeTest7.applyBuilder(NativeDefaultConfig.BUILDER_TEMPLATE)
//
//        bindingView.nativeTest8.applyBuilder(NativeDefaultConfig.BUILDER_VIP)

        val liveDataNetworkStatus = LiveDataNetworkStatus(this)
        liveDataNetworkStatus.observe(this) { connect ->
            if (connect) {
                loadAds(true)
            }
        }

        bindingView.bannerView.registerDelayTime(10)
        loadAds(false)
    }

    private fun loadAds(isReload: Boolean) {
        bindingView.bannerView.loadAds(isVip = false)

        NativeUtils.loadNativeAds(this, this, isVip = false, callbackStart = {
//            bindingView.nativeTest0.startShimmer()
            bindingView.nativeTest1.startShimmer()
//            bindingView.nativeTest2.startShimmer()
//            bindingView.nativeTest3.startShimmer()
//            bindingView.nativeTest4.startShimmer()
//            bindingView.nativeTest5.startShimmer()
//            bindingView.nativeTest6.startShimmer()
//            bindingView.nativeTest7.startShimmer()
//            bindingView.nativeTest8.startShimmer()
//
//            bindingView.nativeCustom1.startShimmer()
//            bindingView.nativeCustom2.startShimmer()
        }, callback = { nativeAd ->
            Handler(Looper.getMainLooper()).postDelayed({
                //            bindingView.nativeTest0.setNativeAd(nativeAd)
                bindingView.nativeTest1.setNativeAd(nativeAd)
//            bindingView.nativeTest2.setNativeAd(nativeAd)
//            bindingView.nativeTest3.setNativeAd(nativeAd)
//            bindingView.nativeTest4.setNativeAd(nativeAd)
//            bindingView.nativeTest5.setNativeAd(nativeAd)
//            bindingView.nativeTest6.setNativeAd(nativeAd)
//            bindingView.nativeTest7.setNativeAd(nativeAd)
//            bindingView.nativeTest8.setNativeAd(nativeAd)
//
//            bindingView.nativeCustom1.setNativeAd(nativeAd)
//            bindingView.nativeCustom2.setNativeAd(nativeAd)
            }, 2000)
        })
    }
}