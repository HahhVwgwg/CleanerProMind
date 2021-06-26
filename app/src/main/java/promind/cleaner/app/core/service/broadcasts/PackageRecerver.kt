package promind.cleaner.app.core.service.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import promind.cleaner.app.core.service.service.ServiceManager
import promind.cleaner.app.core.utils.PreferenceUtils
import java.io.File

class PackageRecerver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_PACKAGE_ADDED) {
            val pkgName = intent.data!!.schemeSpecificPart
            if (PreferenceUtils.isProtectionRealTime() && ServiceManager.getInstance() != null) {
                ServiceManager.getInstance().startInstall(pkgName)
            }
            if (PreferenceUtils.isScanInstaillApk()) {
                try {
                    val appInfo = context.packageManager.getApplicationInfo(pkgName, 0)
                    val mFile = File(appInfo.sourceDir)
                    mFile.delete()
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            }
        } else if (action == Intent.ACTION_PACKAGE_REMOVED) {
            val pkgName = intent.data!!.schemeSpecificPart
            if (PreferenceUtils.isScanUninstaillApk() && ServiceManager.getInstance() != null) ServiceManager.getInstance()
                .startUninstall(pkgName)
        }
    }

    fun OnCreate(context: Context) {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_PACKAGE_ADDED)
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        filter.addDataScheme("package")
        context.registerReceiver(this, filter)
    }

    fun OnDestroy(context: Context?) {
        context?.unregisterReceiver(this)
    }
}