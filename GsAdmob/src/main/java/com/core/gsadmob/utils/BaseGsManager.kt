package com.core.gsadmob.utils

import com.core.gsadmob.model.AdPlaceName

abstract class BaseGsManager {
    var backupDelayTimeMap = HashMap<Int, Long>()

    fun registerDelayTime(delayTime: Long, adPlaceName: AdPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_FULL) {
        backupDelayTimeMap[adPlaceName.adUnitId] = delayTime
    }

    abstract fun clearWithAdPlaceName(adPlaceName: AdPlaceName = AdPlaceNameConfig.AD_PLACE_NAME_FULL)

    abstract fun clearAll()
}