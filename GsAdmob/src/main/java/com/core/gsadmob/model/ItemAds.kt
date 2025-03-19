package com.core.gsadmob.model

import com.google.android.gms.ads.nativead.NativeAd

data class ItemAds(
    var nativeAd: NativeAd? = null,
    var isLoading: Boolean = false
)