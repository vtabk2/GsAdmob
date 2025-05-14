package com.core.gsadmob.callback

import com.google.android.gms.ads.AdValue

interface AdGsExtendListener {
    /**
     * Khi người dùng ấn vào quảng cáo
     */
    fun onAdClicked() {}

    /**
     * Khi quảng cáo được tính tiền
     */
    fun onPaidListener(adValue: AdValue) {}
}