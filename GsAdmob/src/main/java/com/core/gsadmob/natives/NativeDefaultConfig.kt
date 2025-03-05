package com.core.gsadmob.natives

import com.core.gsadmob.R
import com.core.gsadmob.natives.view.BaseNativeAdView

object NativeDefaultConfig {

    val BUILDER_ALBUM = BaseNativeAdView.Builder().apply {
        adsLayoutId = R.layout.ad_native_album
        adsLayoutShimmerId = R.layout.ad_native_album_shimmer
        adsHeadlineId = R.id.ad_headline_album
        adsBodyId = R.id.ad_body_album
        adsStarsId = R.id.ad_stars_album
        adsAppIconId = R.id.ad_app_icon_album
        adsCallToActionId = R.id.ad_call_to_action_album
        adsViewId = R.id.ad_view_album
        adsShimmerId = R.id.ad_shimmer_album
        adsNativeViewRoot = R.style.ads_NativeAlbumRoot
        adsMode = AdsMode.ALBUM
    }

    val BUILDER_FONT = BaseNativeAdView.Builder().apply {
        adsLayoutId = R.layout.ad_native_font
        adsLayoutShimmerId = R.layout.ad_native_font_shimmer
        adsHeadlineId = R.id.ad_headline_font
        adsBodyId = R.id.ad_body_font
        adsStarsId = R.id.ad_stars_font
        adsAppIconId = R.id.ad_app_icon_font
        adsCallToActionId = R.id.ad_call_to_action_font
        adsViewId = R.id.ad_view_font
        adsShimmerId = R.id.ad_shimmer_font
        adsNativeViewRoot = R.style.ads_NativeFontRoot
        adsMode = AdsMode.FONT
    }

    val BUILDER_FRAME = BaseNativeAdView.Builder().apply {
        adsLayoutId = R.layout.ad_native_frame
        adsLayoutShimmerId = R.layout.ad_native_frame_shimmer
        adsHeadlineId = R.id.ad_headline_frame
        adsBodyId = R.id.ad_body_frame
        adsStarsId = R.id.ad_stars_frame
        adsAppIconId = R.id.ad_app_icon_frame
        adsCallToActionId = R.id.ad_call_to_action_frame
        adsViewId = R.id.ad_view_frame
        adsMediaViewId = R.id.ad_media_frame
        adsShimmerId = R.id.ad_shimmer_frame
        adsNativeViewRoot = R.style.ads_NativeFrameRoot
        adsMode = AdsMode.FRAME
    }

    val BUILDER_LANGUAGE = BaseNativeAdView.Builder().apply {
        adsLayoutId = R.layout.ad_native_language
        adsLayoutShimmerId = R.layout.ad_native_language_shimmer
        adsHeadlineId = R.id.ad_headline_language
        adsBodyId = R.id.ad_body_language
        adsStarsId = R.id.ad_stars_language
        adsAppIconId = R.id.ad_app_icon_language
        adsCallToActionId = R.id.ad_call_to_action_language
        adsViewId = R.id.ad_view_language
        adsMediaViewId = R.id.ad_media_language
        adsShimmerId = R.id.ad_shimmer_language
        adsNativeViewRoot = R.style.ads_NativeLanguageRoot
        adsMode = AdsMode.LANGUAGE
    }

    val BUILDER_SHARE = BaseNativeAdView.Builder().apply {
        adsLayoutId = R.layout.ad_native_share
        adsLayoutShimmerId = R.layout.ad_native_share_shimmer
        adsHeadlineId = R.id.ad_headline_share
        adsBodyId = R.id.ad_body_share
        adsStarsId = R.id.ad_stars_share
        adsAppIconId = R.id.ad_app_icon_share
        adsCallToActionId = R.id.ad_call_to_action_share
        adsViewId = R.id.ad_view_share
        adsMediaViewId = R.id.ad_media_share
        adsShimmerId = R.id.ad_shimmer_share
        adsNativeViewRoot = R.style.ads_NativeShareRoot
        adsMode = AdsMode.SHARE
    }

    val BUILDER_STICKER = BaseNativeAdView.Builder().apply {
        adsLayoutId = R.layout.ad_native_sticker
        adsLayoutShimmerId = R.layout.ad_native_sticker_shimmer
        adsHeadlineId = R.id.ad_headline_sticker
        adsBodyId = R.id.ad_body_sticker
        adsStarsId = R.id.ad_stars_sticker
        adsAppIconId = R.id.ad_app_icon_sticker
        adsCallToActionId = R.id.ad_call_to_action_sticker
        adsViewId = R.id.ad_view_sticker
        adsShimmerId = R.id.ad_shimmer_sticker
        adsNativeViewRoot = R.style.ads_NativeStickerRoot
        adsMode = AdsMode.STICKER
    }

    val BUILDER_TEMPLATE = BaseNativeAdView.Builder().apply {
        adsLayoutId = R.layout.ad_native_template
        adsLayoutShimmerId = R.layout.ad_native_template_shimmer
        adsHeadlineId = R.id.ad_headline_template
        adsBodyId = R.id.ad_body_template
        adsStarsId = R.id.ad_stars_template
        adsAppIconId = R.id.ad_app_icon_template
        adsCallToActionId = R.id.ad_call_to_action_template
        adsViewId = R.id.ad_view_template
        adsShimmerId = R.id.ad_shimmer_template
        adsNativeViewRoot = R.style.ads_NativeTemplateRoot
        adsMode = AdsMode.TEMPLATE
    }

    val BUILDER_VIP = BaseNativeAdView.Builder().apply {
        adsLayoutId = R.layout.ad_native_vip
        adsLayoutShimmerId = R.layout.ad_native_vip_shimmer
        adsHeadlineId = R.id.ad_headline_vip
        adsBodyId = R.id.ad_body_vip
        adsStarsId = R.id.ad_stars_vip
        adsCallToActionId = R.id.ad_call_to_action_vip
        adsViewId = R.id.ad_view_vip
        adsMediaViewId = R.id.ad_media_vip
        adsShimmerId = R.id.ad_shimmer_vip
        adsNativeViewRoot = R.style.ads_NativeVipRoot
        adsMode = AdsMode.VIP
    }
}