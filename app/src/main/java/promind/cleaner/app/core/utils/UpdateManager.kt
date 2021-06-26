package promind.cleaner.app.core.utils

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class UpdateManager(private val activity: Activity) {

    private val updateManagerRequestCode = 19901
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var listener: InstallStateUpdatedListener

    fun checkUpdate() {

        initManager()

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        loge("Checking for updates")

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            when (appUpdateInfo.updateAvailability()) {
                UpdateAvailability.UPDATE_AVAILABLE -> {
                    appUpdateManager.registerListener(listener)
                    requestUpdate(appUpdateInfo)
                    loge("Update available")
                }
                UpdateAvailability.UPDATE_NOT_AVAILABLE -> {
                    loge("Update not available")
                }
                UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                    loge("Update in progress")
                }
                UpdateAvailability.UNKNOWN -> {
                    loge("Update unknown")
                }
            }
        }
        appUpdateInfoTask.addOnFailureListener {
            loge(it.localizedMessage!!)
            it.printStackTrace()
        }
    }

    private fun requestUpdate(appUpdateInfo: AppUpdateInfo) {

        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE, // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                    activity,
                    updateManagerRequestCode
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initManager() {

        appUpdateManager = AppUpdateManagerFactory.create(activity)

        listener = InstallStateUpdatedListener { installState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                loge("An update has been downloaded")
                toast("An update has been downloaded, Please restart an app")
                appUpdateManager.completeUpdate()
            }
        }
    }

    fun onResume() {

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        activity,
                        updateManagerRequestCode
                )
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int) {
        if (requestCode == updateManagerRequestCode) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    appUpdateManager.unregisterListener(listener)
                }
                Activity.RESULT_CANCELED -> {
                    checkUpdate()
                    toast("")
                }
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    checkUpdate()
                    toast("")
                }
            }
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }

    private fun loge(msg: String, tag: String = "RRR") {
        Log.e(tag, msg)
    }
}
