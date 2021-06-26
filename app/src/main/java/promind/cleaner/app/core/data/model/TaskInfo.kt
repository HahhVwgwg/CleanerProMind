package promind.cleaner.app.core.data.model

import android.annotation.SuppressLint
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import promind.cleaner.app.core.utils.Config.PERMISSION_DANGEROUS
import java.io.Serializable
import java.util.*

class TaskInfo : Serializable {
    var appinfo: ApplicationInfo? = null
    var mem: Long = 0
    var pkgInfo: PackagesInfo? = null
    private var pm: PackageManager? = null
    var runinfo: RunningAppProcessInfo? = null
    @JvmField var title: String? = null
    var virusName: String? = null
    var isChceked = false
    var isClickEnable = true
    var lstPermissonDangerous: List<PERMISSION_DANGEROUS> = ArrayList()

    constructor() {}
    constructor(
        context: Context,
        runinfo: RunningAppProcessInfo?
    ) {
        appinfo = null
        pkgInfo = null
        title = null
        this.runinfo = runinfo
        pm = context.applicationContext
            .packageManager
    }

    constructor(context: Context, appinfo: ApplicationInfo?) {
        this.appinfo = null
        pkgInfo = null
        runinfo = null
        title = null
        this.appinfo = appinfo
        pm = context.applicationContext
            .packageManager
    }

    val appInfo: Unit
        @SuppressLint("WrongConstant")
        get() {
            if (appinfo == null) {
                try {
                    val s = runinfo!!.processName
                    appinfo = pm!!.getApplicationInfo(s, 128)
                } catch (e: Exception) {
                }
            }
        }
    val icon: Int
        get() = appinfo!!.icon
    val packageName: String
        get() = appinfo!!.packageName

    fun getTitle(): String? {
        if (title == null) {
            try {
                title = appinfo!!.loadLabel(pm!!).toString()
            } catch (e: Exception) {
            }
        }
        return title
    }

    val isGoodProcess: Boolean
        get() = appinfo != null
}