package promind.cleaner.app.core.data.data

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import promind.cleaner.app.core.utils.WeakIdentityHashMap
import java.util.*
import kotlin.reflect.KProperty

class FieldProperty<R, T : Any>(
        val initializer: (R) -> T = { throw IllegalStateException("Not initialized.") }
) {
    private val map = WeakIdentityHashMap<R, T>()

    operator fun getValue(thisRef: R, property: KProperty<*>): T =
            map[thisRef] ?: setValue(thisRef, property, initializer(thisRef))

    operator fun setValue(thisRef: R, property: KProperty<*>, value: T): T {
        map[thisRef] = value
        return value
    }
}

class ManagerConnect {
    private var mDatas: List<ApplicationInfo> = ArrayList()

    fun getListManager(context: Context, listener: OnManagerConnectListener?) {
        mDatas = checkForLaunchIntent(context, context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA))
        listener?.OnResultManager(mDatas)
    }

    private fun checkForLaunchIntent(context: Context, list: List<ApplicationInfo>): List<ApplicationInfo> {
        val applist = ArrayList<ApplicationInfo>()
        for (info in list) {
            try {
                if (null != context.packageManager.getLaunchIntentForPackage(info.packageName)) {
                    applist.add(info)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return applist
    }

    interface OnManagerConnectListener {
        fun OnResultManager(result: List<ApplicationInfo>)
    }
}