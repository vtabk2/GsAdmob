package com.core.gsadmob.banner

enum class ShowType {
    /**
     * Tải được thì hiển thị ngược lại thì gone
     */
    SHOW_IF_SUCCESS,

    /**
     * Tải được thì hiển thị ngược lại thì invisible
     */
    ALWAYS_SHOW,

    /**
     * invisible
     */
    HIDE,

    /**
     * gone
     */
    NOT_SHOW
}