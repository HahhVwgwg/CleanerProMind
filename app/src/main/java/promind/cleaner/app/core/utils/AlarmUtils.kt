package promind.cleaner.app.core.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import promind.cleaner.app.core.service.broadcasts.AlarmReceiver

object AlarmUtils {
    const val ACTION_AUTOSTART_ALARM = "com.app.action.alarmmanager"
    const val ALARM_PHONE_BOOOST = "alarm phone boost"
    const val ALARM_PHONE_CPU_COOLER = "alarm cpu cooler"
    const val ALARM_PHONE_BATTERY_SAVE = "alarm battery save"
    const val ALARM_PHONE_JUNK_FILE = "alarm junk file"
    const val ACTION_REPEAT_SERVICE = "action_repeat_service"
    const val TIME_REPREAT_SERVICE = 1000
    const val REPREAT_SERVICE_CODE = 1001
    const val ALARM_REQ_PHONE_BOOST = 123
    const val ALARM_REQ_CPU_COOLER = 124
    const val TIME_PHONE_BOOST = (30 * 60 * 1000).toLong()
    const val TIME_PHONE_BOOST_CLICK = (2 * 60 * 60 * 1000).toLong()
    const val TIME_CPU_COOLER = (30 * 60 * 1000).toLong()
    const val TIME_CPU_COOLER_CLICK = (2 * 60 * 60 * 1000).toLong()
    const val TIME_BATTERY_SAVE = (30 * 60 * 1000).toLong()
    const val TIME_BATTERY_SAVE_CLICK = (2 * 60 * 60 * 1000).toLong()
    const val TIME_JUNK_FILE_FIRST_START = 30
    const val BATTERY_HOT = 40
    const val BATTERY_LOW = 20
    const val RAM_USE = 70
    @SuppressLint("UnspecifiedImmutableFlag")
    @JvmStatic
    fun cancel(context: Context, mAction: String?) {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.action = ACTION_AUTOSTART_ALARM
        val sender = PendingIntent.getBroadcast(
            context,
            getRequestCode(mAction),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @JvmStatic
    fun setAlarm(ctx: Context, mAction: String?, mTime: Long) {
        val intent = Intent(ctx, AlarmReceiver::class.java)
        intent.action = ACTION_AUTOSTART_ALARM
        intent.putExtra(mAction, true)
        val sender = PendingIntent.getBroadcast(
            ctx,
            getRequestCode(mAction),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarm = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.cancel(sender)
        val `when` = System.currentTimeMillis() + mTime
        AlarmManagerCompat.setExactAndAllowWhileIdle(alarm, AlarmManager.RTC_WAKEUP, `when`, sender)
    }

    private fun getRequestCode(action: String?): Int {
        when (action) {
            ACTION_REPEAT_SERVICE -> return REPREAT_SERVICE_CODE
            ALARM_PHONE_BOOOST -> return ALARM_REQ_PHONE_BOOST
            ALARM_PHONE_CPU_COOLER -> return ALARM_REQ_CPU_COOLER
        }
        return 0
    }
}