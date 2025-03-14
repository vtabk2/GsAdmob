package com.core.gsadmob.utils.preferences

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class CMPUtils(applicationContext: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)

    var isCheckGDPR: Boolean
        get() = prefs.getBoolean(CHECK_GDPR, false)
        set(value) = prefs.edit { putBoolean(CHECK_GDPR, value) }

    fun requiredShowCMPDialog(): Boolean {
        val purposeConsent = prefs.getString("IABTCF_PurposeConsents", "") ?: ""
        val vendorConsent = prefs.getString("IABTCF_VendorConsents", "") ?: ""
        val vendorLI = prefs.getString("IABTCF_VendorLegitimateInterests", "") ?: ""
        val purposeLI = prefs.getString("IABTCF_PurposeLegitimateInterests", "") ?: ""

        val googleId = 755
        val hasGoogleVendorConsent = hasAttribute(vendorConsent, index = googleId)
        val hasGoogleVendorLI = hasAttribute(vendorLI, index = googleId)

        // Minimum required for at least non-personalized ads
        return !(hasConsentFor(listOf(1), purposeConsent, hasGoogleVendorConsent) && hasConsentOrLegitimateInterestFor(
            listOf(2, 7, 9, 10),
            purposeConsent,
            purposeLI,
            hasGoogleVendorConsent,
            hasGoogleVendorLI
        ))
    }

    // Check if a binary string has a "1" at position "index" (1-based)
    private fun hasAttribute(input: String, index: Int): Boolean {
        return input.length >= index && input[index - 1] == '1'
    }

    // Check if consent is given for a list of purposes
    private fun hasConsentFor(
        purposes: List<Int>,
        purposeConsent: String,
        hasVendorConsent: Boolean
    ): Boolean {
        return purposes.all { p -> hasAttribute(purposeConsent, p) } && hasVendorConsent
    }

    // Check if a vendor either has consent or legitimate interest for a list of purposes
    private fun hasConsentOrLegitimateInterestFor(
        purposes: List<Int>,
        purposeConsent: String,
        purposeLI: String,
        hasVendorConsent: Boolean,
        hasVendorLI: Boolean
    ): Boolean {
        return purposes.all { p ->
            (hasAttribute(purposeLI, p) && hasVendorLI) || (hasAttribute(purposeConsent, p) && hasVendorConsent)
        }
    }

    companion object {
        const val CHECK_GDPR = "CHECK_GDPR"

        fun newInstance(context: Context) = CMPUtils(context)
    }
}