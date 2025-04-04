package com.core.gsadmob

import android.os.Build
import android.text.TextUtils
import android.webkit.WebView
import androidx.multidex.MultiDexApplication
import com.core.gsadmob.utils.extensions.getAndroidId
import com.core.gsadmob.utils.extensions.md5
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.ConsentType
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.EnumMap

abstract class GsAdmobApplication : MultiDexApplication() {
    var canShowAppOpenResume: Boolean = true

    private val deviceTestList = mutableListOf<String>()

    override fun onCreate() {
        super.onCreate()

        fixWebView("")

        setupDeviceTest(isDebug = false)

        setupConsentMode()

        registerAdGsManager()

        initOtherConfig()
    }

    open fun fixWebView(packageName: String) {
        if (TextUtils.isEmpty(packageName)) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val process = getProcessName()
            if (packageName != process) WebView.setDataDirectorySuffix(process)
        }
    }

    /**
     * Cho thiết bị hiện tại thành thiết bị test
     * @param isDebug = true cho thiết bị thành thành thiết bị test -> hiển thị quảng cáo test
     */
    open fun setupDeviceTest(isDebug: Boolean) {
        if (isDebug) {
            deviceTestList.add(md5(getAndroidId(this)).uppercase())

            val requestConfiguration = RequestConfiguration.Builder()
                .setTestDeviceIds(deviceTestList)
                .build()
            MobileAds.setRequestConfiguration(requestConfiguration)
        }
    }

    /**
     * Đăng ký lắng nghe quảng cáo
     */
    open fun registerAdGsManager() {}

    /**
     * Khởi tạo các cấu hình khác
     */
    open fun initOtherConfig() {}

    /**
     * Khởi tạo quảng cáo
     */
    fun initMobileAds() {
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@GsAdmobApplication) {}
        }
    }

    /**
     * Cấu hình analytics cho người dùng châu âu khi có thông báo
     * (End users in the European Economic Area (EEA) must provide consent for their personal data to be shared with Google for advertising purposes.
     * When data is not marked as consented, it may impact ads personalization and measurement. Verify your Firebase consent settings)
     */
    private fun setupConsentMode() {
        // Set consent types.
        val consentMap: MutableMap<ConsentType, FirebaseAnalytics.ConsentStatus> = EnumMap(ConsentType::class.java)
        consentMap[ConsentType.ANALYTICS_STORAGE] = FirebaseAnalytics.ConsentStatus.GRANTED
        consentMap[ConsentType.AD_STORAGE] = FirebaseAnalytics.ConsentStatus.GRANTED
        consentMap[ConsentType.AD_USER_DATA] = FirebaseAnalytics.ConsentStatus.GRANTED
        consentMap[ConsentType.AD_PERSONALIZATION] = FirebaseAnalytics.ConsentStatus.GRANTED

        Firebase.analytics.setConsent(consentMap)
    }
}