package promind.cleaner.app.core.service.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import promind.cleaner.app.core.service.service.ServiceManager
import android.content.IntentFilter
import promind.cleaner.app.core.data.model.BatteryInfo
import promind.cleaner.app.core.service.service.NotificationUtil
import promind.cleaner.app.core.utils.PreferenceUtils

class BatteryStatusReceiver : BroadcastReceiver() {
    private val mBatteryInfo = BatteryInfo()
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BATTERY_CHANGED) {
            mBatteryInfo.level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            mBatteryInfo.scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            mBatteryInfo.temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
            mBatteryInfo.voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
            mBatteryInfo.technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
            //charging
            mBatteryInfo.status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            //Day pin
            if (mBatteryInfo.status == BatteryManager.BATTERY_STATUS_FULL && PreferenceUtils.isPowerConnected() && PreferenceUtils.isNotifiBatteryFull()) NotificationUtil.getInstance()
                .fullPower(context)
        } else if (action == Intent.ACTION_POWER_CONNECTED) {
            PreferenceUtils.setPowerConnected(true)
            if (PreferenceUtils.isOnSmartCharger() && ServiceManager.getInstance() != null) ServiceManager.getInstance()
                .startSmartRecharger()
        } else if (action == Intent.ACTION_POWER_DISCONNECTED) {
            PreferenceUtils.setPowerConnected(false)
        }
    }

    fun OnCreate(context: Context) {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        intentFilter.addAction(Intent.ACTION_SCREEN_ON)
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED)
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        intentFilter.addAction(Intent.ACTION_BATTERY_LOW)
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(this, intentFilter)
    }

    fun OnDestroy(context: Context?) {
        context?.unregisterReceiver(this)
    }
}