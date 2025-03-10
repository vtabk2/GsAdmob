package com.example.gsadmob.utils.preferences

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.core.gsadmob.utils.extensions.getAndroidId
import com.core.gsadmob.utils.extensions.md5
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

class GoogleMobileAdsConsentManager private constructor(context: Context) {
    private val consentInformation: ConsentInformation = UserMessagingPlatform.getConsentInformation(context)
    private val cmpUtils: CMPUtils = CMPUtils(context)

    /** Helper variable to determine if the app can request ads. */
    val canRequestAds: Boolean get() = consentInformation.canRequestAds()

    /** Helper variable to determine if the privacy options form is required. */
    private val isPrivacyOptionsRequired: Boolean get() = consentInformation.privacyOptionsRequirementStatus == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    val privacyOptionsRequirementStatus: ConsentInformation.PrivacyOptionsRequirementStatus get() = consentInformation.privacyOptionsRequirementStatus

    fun reset() {
        consentInformation.reset()
    }

    /**
     * Helper method to call the UMP SDK methods to request consent information and load/show a
     * consent form if necessary.
     */
    fun gatherConsent(
        activity: Activity,
        onCanShowAds: (() -> Unit),
        onDisableAds: (() -> Unit),
        isDebug: Boolean,
        timeout: Long = 1500
    ) {
        val params = if (isDebug) {
            /*Debug*/
            val debugSettings = ConsentDebugSettings.Builder(activity)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId(md5(getAndroidId(activity)))
                .build()
            ConsentRequestParameters.Builder().setConsentDebugSettings(debugSettings).build()
        } else {
            /*Release*/
            ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(false).build()
        }
        Log.d("TAG5", "gatherConsent: isDebug = $isDebug")
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                Log.d("TAG5", "gatherConsent: isPrivacyOptionsRequired1 = $isPrivacyOptionsRequired")
                if (isPrivacyOptionsRequired) {
                    if (cmpUtils.requiredShowCMPDialog()) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (activity.isFinishing) return@postDelayed
                            cmpUtils.isCheckGDPR = true
                            UserMessagingPlatform.showPrivacyOptionsForm(activity) {
                                if (canRequestAds) {
                                    onCanShowAds.invoke()
                                } else {
                                    onDisableAds.invoke()
                                }
                            }
                        }, timeout)
                    } else {
                        onCanShowAds.invoke()
                    }
                } else {
                    onCanShowAds.invoke()
                }
            }, {
                Log.d("TAG5", "gatherConsent: isPrivacyOptionsRequired2 = $isPrivacyOptionsRequired")
                if (isPrivacyOptionsRequired) {
                    if (canRequestAds) {
                        onCanShowAds.invoke()
                    } else {
                        onDisableAds.invoke()
                    }
                } else {
                    onCanShowAds.invoke()
                }
            }
        )
    }

    fun requestPrivacyOptionsRequirementStatus(
        activity: Activity,
        isDebug: Boolean,
        callback: (ConsentInformation.PrivacyOptionsRequirementStatus) -> Unit
    ) {
        val params = if (isDebug) {
            /*Debug*/
            val debugSettings = ConsentDebugSettings.Builder(activity)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId(md5(getAndroidId(activity)))
                .build()
            ConsentRequestParameters.Builder().setConsentDebugSettings(debugSettings).build()
        } else {
            /*Release*/
            ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(false).build()
        }
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                callback.invoke(privacyOptionsRequirementStatus)
            }, {
                callback.invoke(privacyOptionsRequirementStatus)
            })
    }

    /** Helper method to call the UMP SDK method to show the privacy options form. */
    fun showPrivacyOptionsForm(activity: Activity, onConsentFormDismissedListener: ConsentForm.OnConsentFormDismissedListener) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity, onConsentFormDismissedListener)
    }

    companion object {
        @Volatile
        private var instance: GoogleMobileAdsConsentManager? = null

        fun getInstance(context: Context) =
            instance
                ?: synchronized(this) {
                    instance ?: GoogleMobileAdsConsentManager(context).also { instance = it }
                }
    }
}