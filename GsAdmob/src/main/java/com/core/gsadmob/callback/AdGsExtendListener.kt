package com.core.gsadmob.callback

interface AdGsExtendListener {

    /**
     * Khi người dùng ấn vào quảng cáo
     */
    fun onAdClicked() {}

    /**
     * Xác nhận quảng cáo thực sự xuất hiện trên giao diện (đủ điều kiện visibility theo tiêu chuẩn IAB)
     */
    fun onAdImpression() {}
}