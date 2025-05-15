package com.core.gsadmob.model

enum class AdShowStatus {
    /**
     * Có thể hiển thị quảng cáo
     */
    CAN_SHOW,

    /**
     *  Lỗi không thể hiển thị quảng cáo do chưa bật webview vào cài đặt -> ứng dụng -> Android System WebView bật nó lên
     */
    ERROR_WEB_VIEW,

    /**
     * Có vip rồi không hiển thị quảng cáo nữa
     */
    ERROR_VIP,

    /**
     * Cần tải quảng cáo
     */
    REQUIRE_LOAD,

    /**
     * Quảng cáo đang bị hủy không thể hiển thị
     */
    CANCEL,

    /**
     * Quảng cáo đang được hiển thị
     */
    SHOWING,

    /**
     * Lỗi do cấu hình chỉ được hiển thị quảng cáo nhưng quảng cáo lại không có sẵn
     */
    ONLY_SHOW,

    /**
     * Lỗi do không có activiy nào để hiển thị vào
     */
    NO_ACTIVITY,

    /**
     * Lỗi quảng cáo chưa đươc kích hoạt sử dụng
     */
    ADS_DISABLE,

    /**
     * Chưa đủ thời gian giữa các lần hiển thị
     */
    REQUIRE_DELAY_SHOW_TIME
}