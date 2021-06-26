package promind.cleaner.app.core.data.data

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.database.SQLException
import android.os.AsyncTask
import android.text.TextUtils
import promind.cleaner.app.core.data.database.SQLiteDatabaseHelper
import promind.cleaner.app.core.data.database.FSEnvironment
import promind.cleaner.app.core.data.model.TaskInfo
import promind.cleaner.app.core.utils.Config.PERMISSION_DANGEROUS
import promind.cleaner.app.core.utils.PreferenceUtils
import java.io.IOException
import java.util.*

class TaskScanVirus : AsyncTask<Void?, String, List<TaskInfo>> {
    private var mOnTaskListListener: OnTaskListListener?
    private var mContext: Context
    private var mSQLiteDatabaseHelper: SQLiteDatabaseHelper? = null
    private var pkgName = ""
    private var timeSleep: Long = 0

    constructor(mContext: Context, timeSleep: Long, mOnTaskListListener: OnTaskListListener?) {
        this.mOnTaskListListener = mOnTaskListListener
        this.mContext = mContext
        this.timeSleep = timeSleep
    }

    constructor(mContext: Context, pkgName: String, timeSleep: Long, mOnTaskListListener: OnTaskListListener?) {
        this.mOnTaskListListener = mOnTaskListListener
        this.mContext = mContext
        this.timeSleep = timeSleep
        this.pkgName = pkgName
    }

    interface OnTaskListListener {
        fun OnResult(lstDangerous: List<TaskInfo>, lstVirus: List<TaskInfo>)
        fun onProgress(appName: String, virusName: String, dangerousSize: String, progress: String)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        mSQLiteDatabaseHelper =
            SQLiteDatabaseHelper(mContext)
        try {
            mSQLiteDatabaseHelper!!.createDataBase()
        } catch (ioe: IOException) {
            throw Error("Unable to create database")
        }
        try {
            mSQLiteDatabaseHelper!!.openDataBase()
        } catch (sqle: SQLException) {
            throw sqle
        }
    }

    override fun doInBackground(vararg voids: Void?): List<TaskInfo> {
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pkgAppsList = mContext.packageManager.queryIntentActivities(mainIntent, 0)
        val lstApp: MutableList<TaskInfo> = ArrayList()
        var rCode: Int
        if (!TextUtils.isEmpty(pkgName)) { /*Quét 1 apk cụ thể*/
            val mTaskInfo: TaskInfo
            try {
                val appInfo = mContext.packageManager.getApplicationInfo(pkgName, 0)
                if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                    mTaskInfo = TaskInfo(mContext, appInfo)
                    mTaskInfo.lstPermissonDangerous = getListPermissionDangerous(mContext, mTaskInfo.packageName)
                    rCode = FSEnvironment.scanFile(appInfo.sourceDir)
                    if (rCode > 0) {
                        mTaskInfo.virusName = mSQLiteDatabaseHelper!!.getVname(rCode)
                    }
                    lstApp.add(mTaskInfo)
                }
                Thread.sleep(timeSleep)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            for (i in pkgAppsList.indices) {
                val mResolveInfo = pkgAppsList[i]
                var mTaskInfo: TaskInfo? = null
                try {
                    val appInfo = mContext.packageManager.getApplicationInfo(mResolveInfo.activityInfo.packageName, 0)
                    if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                        mTaskInfo = TaskInfo(mContext, appInfo)
                        mTaskInfo.lstPermissonDangerous = getListPermissionDangerous(mContext, mTaskInfo.packageName)
                        rCode = FSEnvironment.scanFile(appInfo.sourceDir)
                        if (rCode > 0) {
                            mTaskInfo.virusName = mSQLiteDatabaseHelper!!.getVname(rCode)
                        }
                        lstApp.add(mTaskInfo)
                    }
                    Thread.sleep(timeSleep)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val progress = i.toFloat() / pkgAppsList.size.toFloat()
                publishProgress(mResolveInfo.activityInfo.packageName,
                        if (mTaskInfo == null) "" else mTaskInfo.virusName ?: "",
                        mTaskInfo?.lstPermissonDangerous?.size?.toString() ?: "",
                        if (i == pkgAppsList.size - 1) 100.toString() else (progress * 100).toString())
            }
        }
        return lstApp
    }

    override fun onProgressUpdate(vararg values: String) {
        super.onProgressUpdate(*values)
        if (mOnTaskListListener != null) if (values.size >= 4) mOnTaskListListener!!.onProgress(values[0], values[1], values[2], values[3])
    }

    override fun onPostExecute(taskInfos: List<TaskInfo>) {
        super.onPostExecute(taskInfos)
        if (mOnTaskListListener != null) {
            val lstDangerous: MutableList<TaskInfo> = ArrayList()
            val lstVisrus: MutableList<TaskInfo> = ArrayList()
            for (mTaskInfo in taskInfos) {
                if (!TextUtils.isEmpty(mTaskInfo.virusName) && !PreferenceUtils.getListAppWhileVirus().contains(mTaskInfo.packageName)) {
                    lstVisrus.add(mTaskInfo)
                } else if (!mTaskInfo.lstPermissonDangerous.isEmpty()) {
                    lstDangerous.add(mTaskInfo)
                }
            }
            mOnTaskListListener!!.OnResult(lstDangerous, lstVisrus)
        }
        mSQLiteDatabaseHelper!!.close()
    }

    fun getListPermissionDangerous(mContext: Context, appPackage: String?): List<PERMISSION_DANGEROUS> {
        val lstPer = getGrantedPermissions(mContext, appPackage)
        val lstDangerous: MutableList<PERMISSION_DANGEROUS> = ArrayList()
        for (permission in lstPer) {
            for (permissionDangerous in PERMISSION_DANGEROUS.values()) {
                if (permission == permissionDangerous.name) {
                    lstDangerous.add(permissionDangerous)
                    break
                }
            }
        }
        return lstDangerous
    }

    fun getGrantedPermissions(mContext: Context, appPackage: String?): List<String> {
        val granted: MutableList<String> = ArrayList()
        try {
            val pi = mContext.packageManager.getPackageInfo(appPackage!!, PackageManager.GET_PERMISSIONS)
            for (i in pi.requestedPermissions.indices) {
                if (pi.requestedPermissionsFlags[i] and PackageInfo.REQUESTED_PERMISSION_GRANTED != 0) {
                    granted.add(pi.requestedPermissions[i])
                }
            }
        } catch (e: Exception) {
        }
        return granted
    }
}