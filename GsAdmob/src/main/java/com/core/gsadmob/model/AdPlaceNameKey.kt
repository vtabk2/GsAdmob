package com.core.gsadmob.model

data class AdPlaceNameKey(
    /**
     * Tên của quảng cáo = loại quảng cáo + tên màn hình (Ví dụ : banner_home)
     */
    var name: String = "",

    /**
     * id của quảng cáo
     */
    var adUnitId: String = "",
)