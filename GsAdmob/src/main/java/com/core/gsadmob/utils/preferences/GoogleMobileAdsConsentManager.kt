package com.core.gsadmob.utils.preferences

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.core.gsadmob.utils.extensions.getAndroidId
import com.core.gsadmob.utils.extensions.log
import com.core.gsadmob.utils.extensions.md5
import com.core.gscore.hourglass.Hourglass
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

    private var isRequestLoaded = false

    /**
     * Helper method to call the UMP SDK methods to request consent information and load/show a
     * consent form if necessary.
     */
    fun gatherConsent(
        activity: Activity,
        onCanShowAds: (() -> Unit),
        onDisableAds: (() -> Unit),
        isDebug: Boolean,
        timeout: Long = 3500,
        delayTime: Long = 1500
    ) {

        fun setupCallback() {
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

        val requireTimeout = if (timeout < MIN_TIME_DELAY) {
            MIN_TIME_DELAY
        } else {
            timeout
        }

        log("GoogleMobileAdsConsentManager_gatherConsent_requireTimeout", requireTimeout)

        val timeoutHourglass = object : Hourglass(requireTimeout, 500) {
            override fun onTimerTick(timeRemaining: Long) {}

            override fun onTimerFinish() {
                if (isRequestLoaded) return

                setupCallback()
            }
        }
        timeoutHourglass.startTimer()

        isRequestLoaded = false

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                isRequestLoaded = true
                timeoutHourglass.stopTimer()
                if (isPrivacyOptionsRequired) {
                    if (cmpUtils.requiredShowCMPDialog()) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (activity.isFinishing || activity.isDestroyed) return@postDelayed
                            cmpUtils.isCheckGDPR = true
                            UserMessagingPlatform.showPrivacyOptionsForm(activity) {

                                setupCallback()
                            }
                        }, delayTime)
                    } else {
                        onCanShowAds.invoke()
                    }
                } else {
                    onCanShowAds.invoke()
                }
            }, {
                isRequestLoaded = true
                timeoutHourglass.stopTimer()

                setupCallback()
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

        const val MIN_TIME_DELAY = 1000L

        fun getInstance(context: Context) =
            instance
                ?: synchronized(this) {
                    instance ?: GoogleMobileAdsConsentManager(context).also { instance = it }
                }
    }
}