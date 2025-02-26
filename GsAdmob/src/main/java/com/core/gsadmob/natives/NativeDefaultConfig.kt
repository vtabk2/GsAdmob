package com.core.gsadmob.natives

import com.core.gsadmob.R
import com.core.gsadmob.natives.view.BaseNativeAdView

object NativeDefaultConfig {

    val BUILDER_ALBUM = BaseNativeAdView.Builder().apply {
        adLayoutId = R.layout.ad_native_album
        adHeadlineId = R.id.ad_headline_album
        adStarsId = R.id.ad_stars_album
        adAppIconId = R.id.ad_app_icon_album
        adCallToActionId = R.id.ad_call_to_action_album
        adViewId = R.id.ad_view_album
        adShimmerId = R.id.ad_shimmer_album
        adsMode = AdsMode.ALBUM
    }

    val BUILDER_FONT = BaseNativeAdView.Builder().apply {
        adLayoutId = R.layout.ad_native_font
        adHeadlineId = R.id.ad_headline_font
        adBodyId = R.id.ad_body_font
        adStarsId = R.id.ad_stars_font
        adAppIconId = R.id.ad_app_icon_font
        adCallToActionId = R.id.ad_call_to_action_font
        adViewId = R.id.ad_view_font
        adShimmerId = R.id.ad_shimmer_font
        adsMode = AdsMode.FONT
    }

    val BUILDER_FRAME = BaseNativeAdView.Builder().apply {
        adLayoutId = R.layout.ad_native_frame
        adHeadlineId = R.id.ad_headline_frame
        adBodyId = R.id.ad_body_frame
        adStarsId = R.id.ad_stars_frame
        adAppIconId = R.id.ad_app_icon_frame
        adCallToActionId = R.id.ad_call_to_action_frame
        adViewId = R.id.ad_view_frame
        adMediaViewId = R.id.ad_media_frame
        adShimmerId = R.id.ad_shimmer_frame
        adsMode = AdsMode.FRAME
    }

    val BUILDER_LANGUAGE = BaseNativeAdView.Builder().apply {
        adLayoutId = R.layout.ad_native_language
        adHeadlineId = R.id.ad_headline_language
        adBodyId = R.id.ad_body_language
        adStarsId = R.id.ad_stars_language
        adAppIconId = R.id.ad_app_icon_language
        adCallToActionId = R.id.ad_call_to_action_language
        adViewId = R.id.ad_view_language
        adMediaViewId = R.id.ad_media_language
        adShimmerId = R.id.ad_shimmer_language
        adsMode = AdsMode.LANGUAGE
    }

    val BUILDER_SHARE = BaseNativeAdView.Builder().apply {
        adLayoutId = R.layout.ad_native_share
        adHeadlineId = R.id.ad_headline_share
        adBodyId = R.id.ad_body_share
        adStarsId = R.id.ad_stars_share
        adAppIconId = R.id.ad_app_icon_share
        adCallToActionId = R.id.ad_call_to_action_share
        adViewId = R.id.ad_view_share
        adMediaViewId = R.id.ad_media_share
        adShimmerId = R.id.ad_shimmer_share
        adsMode = AdsMode.SHARE
    }

    val BUILDER_STICKER = BaseNativeAdView.Builder().apply {
        adLayoutId = R.layout.ad_native_sticker
        adHeadlineId = R.id.ad_headline_sticker
        adBodyId = R.id.ad_body_sticker
        adStarsId = R.id.ad_stars_sticker
        adAppIconId = R.id.ad_app_icon_sticker
        adCallToActionId = R.id.ad_call_to_action_sticker
        adViewId = R.id.ad_view_sticker
        adShimmerId = R.id.ad_shimmer_sticker
        adsMode = AdsMode.STICKER
    }

    val BUILDER_TEMPLATE = BaseNativeAdView.Builder().apply {
        adLayoutId = R.layout.ad_native_template
        adHeadlineId = R.id.ad_headline_template
        adBodyId = R.id.ad_body_template
        adStarsId = R.id.ad_stars_template
        adAppIconId = R.id.ad_app_icon_template
        adCallToActionId = R.id.ad_call_to_action_template
        adViewId = R.id.ad_view_template
        adShimmerId = R.id.ad_shimmer_template
        adsMode = AdsMode.TEMPLATE
    }

    val BUILDER_VIP = BaseNativeAdView.Builder().apply {
        adLayoutId = R.layout.ad_native_vip
        adHeadlineId = R.id.ad_headline_vip
        adBodyId = R.id.ad_body_vip
        adStarsId = R.id.ad_stars_vip
        adCallToActionId = R.id.ad_call_to_action_vip
        adViewId = R.id.ad_view_vip
        adMediaViewId = R.id.ad_media_vip
        adShimmerId = R.id.ad_shimmer_vip
        adsMode = AdsMode.VIP
    }
}