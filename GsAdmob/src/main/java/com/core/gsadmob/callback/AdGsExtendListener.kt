package com.core.gsadmob.callback

import com.google.android.gms.ads.AdValue

interface AdGsExtendListener {

    /**
     * Khi người dùng ấn vào quảng cáo
     */
    fun onAdClicked() {}

    /**
     * Xác nhận quảng cáo thực sự xuất hiện trên giao diện (đủ điều kiện visibility theo tiêu chuẩn IAB)
     */
    fun onAdImpression() {}

    fun onPaidEvent(adValue: AdValue) {}
}