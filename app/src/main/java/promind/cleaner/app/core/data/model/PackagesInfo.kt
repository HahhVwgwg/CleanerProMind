package promind.cleaner.app.core.data.model

import android.content.Context
import android.content.pm.ApplicationInfo

class PackagesInfo {
    private var appList: List<*>

    constructor(context: Context) {
        appList = context.applicationContext.packageManager
            .getInstalledApplications(0)
    }

    constructor(context: Context, s: String?) {
        appList = context.applicationContext.packageManager
            .getInstalledApplications(128)
    }

    fun getInfo(s: String?): ApplicationInfo? {
        var applicationInfo: ApplicationInfo? = null
        if (s != null) {
            for (anAppList in appList) {
                applicationInfo = anAppList as ApplicationInfo?
                val s1 = applicationInfo!!.processName
                if (s == s1) {
                    break
                }
            }
        }
        return applicationInfo
    }
}