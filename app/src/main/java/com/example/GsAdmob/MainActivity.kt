package com.example.GsAdmob

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.core.gsadmob.natives.AdsMode
import com.core.gsadmob.natives.NativeUtils
import com.core.gsadmob.natives.view.BaseNativeAdView
import com.example.GsAdmob.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bindingView = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingView.root)

        //        val builder = BaseNativeAdView.Builder().apply {
//            adLayoutId = R.layout.ad_native_test
//            adHeadlineId = R.id.ad_headline_test
//            adStarsId = R.id.ad_stars_test
//            adAppIconId = R.id.ad_app_icon_test
//            adCallToActionId = R.id.ad_call_to_action_test
//            adViewId = R.id.ad_view_test
//            adShimmerId = R.id.ad_view_test_shimmer
//        }
//        bindingView.nativeTest1.applyBuilder(builder)

        bindingView.nativeTest1.applyBuilder(BaseNativeAdView.Builder().apply {
            adsMode = AdsMode.ALBUM
        })

        bindingView.nativeTest2.applyBuilder(BaseNativeAdView.Builder().apply {
            adsMode = AdsMode.FONT
        })

        bindingView.nativeTest3.applyBuilder(BaseNativeAdView.Builder().apply {
            adsMode = AdsMode.FRAME
        })

        bindingView.nativeTest4.applyBuilder(BaseNativeAdView.Builder().apply {
            adsMode = AdsMode.LANGUAGE
        })

        bindingView.nativeTest5.applyBuilder(BaseNativeAdView.Builder().apply {
            adsMode = AdsMode.SHARE
        })

        bindingView.nativeTest6.applyBuilder(BaseNativeAdView.Builder().apply {
            adsMode = AdsMode.STICKER
        })

        bindingView.nativeTest7.applyBuilder(BaseNativeAdView.Builder().apply {
            adsMode = AdsMode.TEMPLATE
        })

        bindingView.nativeTest8.applyBuilder(BaseNativeAdView.Builder().apply {
            adsMode = AdsMode.VIP
        })

        NativeUtils.loadNativeAds(this, this, isVip = false, callbackStart = {
            bindingView.nativeTest1.startShimmer()
            bindingView.nativeTest2.startShimmer()
            bindingView.nativeTest3.startShimmer()
            bindingView.nativeTest4.startShimmer()
            bindingView.nativeTest5.startShimmer()
            bindingView.nativeTest6.startShimmer()
            bindingView.nativeTest7.startShimmer()
            bindingView.nativeTest8.startShimmer()

            bindingView.nativeCustom.startShimmer()
        }, callback = { nativeAd ->
            bindingView.nativeTest1.setNativeAd(nativeAd)
            bindingView.nativeTest2.setNativeAd(nativeAd)
            bindingView.nativeTest3.setNativeAd(nativeAd)
            bindingView.nativeTest4.setNativeAd(nativeAd)
            bindingView.nativeTest5.setNativeAd(nativeAd)
            bindingView.nativeTest6.setNativeAd(nativeAd)
            bindingView.nativeTest7.setNativeAd(nativeAd)
            bindingView.nativeTest8.setNativeAd(nativeAd)

            bindingView.nativeCustom.setNativeAd(nativeAd)
        })
    }
}