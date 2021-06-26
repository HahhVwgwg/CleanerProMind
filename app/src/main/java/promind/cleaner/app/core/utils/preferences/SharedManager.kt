package promind.cleaner.app.core.utils.preferences

import android.content.SharedPreferences
import promind.cleaner.app.core.utils.preferences.PreferenceHelper.get
import promind.cleaner.app.core.utils.preferences.PreferenceHelper.set

class SharedManager(private val preferences: SharedPreferences) {

    companion object {
        const val IS_PREMIUM = "IS_PREMIUM"
        const val LAST_TIME = "LAST_TIME"
        const val COUNT = "COUNT"
        const val DONTSHOWAGAIN = "DONTSHOWAGAIN"
    }

    var dontShowAgain: Boolean
        get() = preferences[DONTSHOWAGAIN, false]
        set(value) {
            preferences[DONTSHOWAGAIN] = value
        }

    var premiumAvailable: Boolean
        get() = preferences[IS_PREMIUM, false]
        set(value) {
            preferences[IS_PREMIUM] = value
        }

    var lastPremiumShowedTime: Long
        get() = preferences[LAST_TIME, System.currentTimeMillis()]
        set(value) {
            lastPremiumShowedCount++
            preferences[LAST_TIME] = value
        }

    // increment when lastPremiumShowedTime
    var lastPremiumShowedCount: Int
        get() = preferences[COUNT, 0]
        private set(value) {
            preferences[COUNT] = value
        }

    fun deleteAll() {
        preferences.edit().clear().apply()
    }
}
