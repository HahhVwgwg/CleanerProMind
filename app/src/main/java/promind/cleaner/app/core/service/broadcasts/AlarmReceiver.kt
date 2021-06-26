package promind.cleaner.app.core.service.broadcasts

import android.annotation.SuppressLint
import android.content.Intent
import promind.cleaner.app.core.utils.AlarmUtils
import android.os.PowerManager
import promind.cleaner.app.core.service.service.ServiceManager
import android.os.Build
import promind.cleaner.app.core.data.data.TaskTotalRAM
import promind.cleaner.app.core.utils.SystemUtil
import android.os.AsyncTask
import promind.cleaner.app.core.data.data.TaskBattery
import android.os.BatteryManager
import promind.cleaner.app.core.utils.Toolbox
import android.content.IntentFilter
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import androidx.core.content.ContextCompat
import promind.cleaner.app.core.service.service.NotificationUtil
import promind.cleaner.app.core.utils.PreferenceUtils
import java.lang.Exception

class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("InvalidWakeLockTag")
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AlarmUtils.ACTION_AUTOSTART_ALARM) {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "NAG")
            wl.acquire()
            val extras = intent.extras
            if (extras != null) {
                if (!isMyServiceRunning(ServiceManager::class.java, context) && extras.getBoolean(
                        AlarmUtils.ACTION_REPEAT_SERVICE,
                        java.lang.Boolean.FALSE
                    )
                ) {
                    try {
                        if (ServiceManager.getInstance() != null) ServiceManager.getInstance()
                            .onDestroy()
                    } catch (e: Exception) {
                    }
                    val mIntent = Intent(context, ServiceManager::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ContextCompat.startForegroundService(context, mIntent)
                    } else {
                        context.startService(Intent(context, ServiceManager::class.java))
                    }
                }
                if (extras.getBoolean(AlarmUtils.ALARM_PHONE_BOOOST)) {
                    PreferenceUtils.setTimeAlarmPhoneBoost(AlarmUtils.TIME_PHONE_BOOST)
                    AlarmUtils.setAlarm(
                        context,
                        AlarmUtils.ALARM_PHONE_BOOOST,
                        AlarmUtils.TIME_PHONE_BOOST
                    )
                    TaskTotalRAM { useRam: Long, totalRam: Long ->
                        val progress = useRam.toFloat() / totalRam.toFloat()
                        if (progress * 100 > AlarmUtils.RAM_USE && SystemUtil.checkDNDDoing()) {
                            NotificationUtil.getInstance().showNotificationAlarm(
                                context,
                                NotificationUtil.ID_NOTIFICATTION_PHONE_BOOST
                            )
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                } else if (extras.getBoolean(AlarmUtils.ALARM_PHONE_CPU_COOLER)) {
                    PreferenceUtils.setTimeAlarmPhoneBoost(AlarmUtils.TIME_CPU_COOLER)
                    AlarmUtils.setAlarm(
                        context,
                        AlarmUtils.ALARM_PHONE_CPU_COOLER,
                        AlarmUtils.TIME_CPU_COOLER
                    )
                    TaskBattery(
                        context,
                        BatteryManager.EXTRA_TEMPERATURE
                    ) { pin: Int ->
                        if (pin > AlarmUtils.BATTERY_HOT && SystemUtil.checkDNDDoing()) NotificationUtil.getInstance()
                            .showNotificationAlarm(
                                context,
                                NotificationUtil.ID_NOTIFICATTION_CPU_COOLER
                            )
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                } else if (extras.getBoolean(AlarmUtils.ALARM_PHONE_BATTERY_SAVE)) {
                    PreferenceUtils.setTimeAlarmPhoneBoost(AlarmUtils.TIME_BATTERY_SAVE)
                    AlarmUtils.setAlarm(
                        context,
                        AlarmUtils.ALARM_PHONE_BATTERY_SAVE,
                        AlarmUtils.TIME_BATTERY_SAVE
                    )
                    TaskBattery(
                        context,
                        BatteryManager.EXTRA_LEVEL
                    ) { pin: Int ->
                        if (pin < AlarmUtils.BATTERY_LOW && SystemUtil.checkDNDDoing()) NotificationUtil.getInstance()
                            .showNotificationAlarm(
                                context,
                                NotificationUtil.ID_NOTIFICATTION_BATTERY_SAVE
                            )
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                } else if (extras.getBoolean(AlarmUtils.ALARM_PHONE_JUNK_FILE)) {
                    AlarmUtils.setAlarm(
                        context,
                        AlarmUtils.ALARM_PHONE_JUNK_FILE,
                        Toolbox.getTimeAlarmJunkFile(false)
                    )
                    if (SystemUtil.checkDNDDoing()) NotificationUtil.getInstance()
                        .showNotificationAlarm(context, NotificationUtil.ID_NOTIFICATTION_JUNK_FILE)
                }
            }
        }
    }

    fun OnCreate(context: Context) {
        val intentFilter = IntentFilter()
        intentFilter.addAction(AlarmUtils.ACTION_AUTOSTART_ALARM)
        context.registerReceiver(this, intentFilter)
    }

    fun OnDestroy(context: Context?) {
        context?.unregisterReceiver(this)
    }

    private fun isMyServiceRunning(serviceClass: Class<*>, mContext: Context): Boolean {
        val manager = mContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}