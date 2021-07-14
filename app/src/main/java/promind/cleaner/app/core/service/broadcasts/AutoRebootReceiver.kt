package promind.cleaner.app.core.service.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import promind.cleaner.app.core.service.service.ServiceManager

class AutoRebootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED || action == "android.intent.action.QUICKBOOT_POWERON" || action == "com.htc.intent.action.QUICKBOOT_POWERON" || action.equals(
                "android.intent.action.SCREEN_ON",
                ignoreCase = true
            )
            || action.equals("android.intent.action.USER_PRESENT", ignoreCase = true)
            || action.equals("android.intent.action.ACTION_POWER_CONNECTED", ignoreCase = true)
            || action.equals("android.net.wifi.WIFI_STATE_CHANGED", ignoreCase = true)
            || action.equals("android.net.conn.CONNECTIVITY_CHANGE", ignoreCase = true)
            || action.equals("android.net.wifi.STATE_CHANGE", ignoreCase = true)
        ) {
            val intent2 = Intent(context, ServiceManager::class.java)

            try {
                ContextCompat.startForegroundService(context, intent2)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
}