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
            adsMode = AdsMode.VIP
        })

//        bindingView.nativeTest1.gone()
//        bindingView.nativeAlbum.gone()
//        bindingView.nativeFont.gone()
//        bindingView.nativeFrame1.gone()
//        bindingView.nativeFrame2.gone()
//        bindingView.nativeLanguage.gone()
//        bindingView.nativeShare.gone()
//        bindingView.nativeSticker.gone()
//        bindingView.nativeTemplate.gone()
//        bindingView.nativeCustom.gone()

        NativeUtils.loadNativeAds(this, this, isVip = false, callbackStart = {
            bindingView.nativeTest1.startShimmer()
//            bindingView.nativeAlbum.startShimmer()
//            bindingView.nativeFont.startShimmer()
//            bindingView.nativeFrame1.startShimmer()
//            bindingView.nativeFrame2.startShimmer()
//            bindingView.nativeLanguage.startShimmer()
//            bindingView.nativeShare.startShimmer()
//            bindingView.nativeSticker.startShimmer()
//            bindingView.nativeTemplate.startShimmer()
            bindingView.nativeCustom.startShimmer()
        }, callback = { nativeAd ->
            bindingView.nativeTest1.setNativeAd(nativeAd)
//            bindingView.nativeAlbum.setNativeAd(nativeAd)
//            bindingView.nativeFont.setNativeAd(nativeAd)
//            bindingView.nativeFrame1.setNativeAd(nativeAd)
//            bindingView.nativeFrame2.setNativeAd(nativeAd)
//            bindingView.nativeLanguage.setNativeAd(nativeAd)
//            bindingView.nativeShare.setNativeAd(nativeAd)
//            bindingView.nativeSticker.setNativeAd(nativeAd)
//            bindingView.nativeTemplate.setNativeAd(nativeAd)
            bindingView.nativeCustom.setNativeAd(nativeAd)
        })
    }
}