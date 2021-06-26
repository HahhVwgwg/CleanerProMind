package promind.cleaner.app.ui.activities.cleanNotification

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import com.jpardogo.android.googleprogressbar.library.ChromeFloatingCirclesDrawable
import kotlinx.android.synthetic.main.activity_clean_notification_setting.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import promind.cleaner.app.core.app.Application
import promind.cleaner.app.R
import promind.cleaner.app.ui.adapter.AppSelectAdapter
import promind.cleaner.app.core.data.data.TaskCollectApps
import promind.cleaner.app.ui.dialog.DialogConfirmCancel
import promind.cleaner.app.core.data.model.TaskInfo
import promind.cleaner.app.ui.activities.BaseActivity
import promind.cleaner.app.core.service.service.NotificationListener
import promind.cleaner.app.core.service.service.NotificationUtil
import promind.cleaner.app.core.utils.Config
import promind.cleaner.app.core.utils.PreferenceUtils
import promind.cleaner.app.core.utils.Toolbox
import promind.cleaner.app.core.utils.preferences.MyCache.Companion.getCache
import java.util.*

class NotificationCleanSettingActivity : BaseActivity() {

    private var mAppSelectAdapterNetwork: AppSelectAdapter? = null
    private var mAppSelectAdapterThirdParty: AppSelectAdapter? = null
    private val lstNetWork: MutableList<TaskInfo> = ArrayList()
    private val lstThirdParty: MutableList<TaskInfo> = ArrayList()
    private var lstAppSave: MutableList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clean_notification_setting)
        PreferenceUtils.setFirstUsedFunction(Config.FUNCTION.NOTIFICATION_MANAGER)
        initView()
        initData()
        initListener()
        click()
    }

    private fun initView() {
        imBack!!.visibility = View.VISIBLE
        imBack!!.setOnClickListener { v: View? ->
            val dialogConfirmCancel = DialogConfirmCancel(this, Config.FUNCTION.NOTIFICATION_MANAGER.id)
            dialogConfirmCancel.listener = object : DialogConfirmCancel.OnClickListener {
                override fun onDo() {}
                override fun onCancel() {
                    finish()
                }
            }
            dialogConfirmCancel.show()
        }
        tvToolbar!!.text = getString(R.string.notification_manager)
        menuToolbar!!.handleViewAndClick()
        /**/
        val drawable = ChromeFloatingCirclesDrawable.Builder(this)
                .colors(Toolbox.getProgressDrawableColors(this))
                .build()
        val bounds = google_progress!!.indeterminateDrawable.bounds
        google_progress!!.indeterminateDrawable = drawable
        google_progress!!.indeterminateDrawable.bounds = bounds
    }

    private fun initData() {
        lstAppSave = PreferenceUtils.getListAppCleanNotifi()
        mAppSelectAdapterNetwork = AppSelectAdapter(this, AppSelectAdapter.TYPE_SELECT.SWITCH, lstNetWork)
        rcv_social_network!!.adapter = mAppSelectAdapterNetwork
        mAppSelectAdapterThirdParty = AppSelectAdapter(this, AppSelectAdapter.TYPE_SELECT.SWITCH, lstThirdParty)
        rcv_third_party!!.adapter = mAppSelectAdapterThirdParty
        TaskCollectApps(this) { lstApp: List<TaskInfo> ->
            lstNetWork.clear()
            lstThirdParty.clear()
            if (!lstApp.isEmpty()) {
                for (mTaskInfo in lstApp) {
                    if (isAppNetwork(mTaskInfo.packageName)) lstNetWork.add(mTaskInfo) else lstThirdParty.add(
                        mTaskInfo
                    )
                }
                if (!lstNetWork.isEmpty()) ll_network_app!!.visibility = View.VISIBLE
                if (!lstThirdParty.isEmpty()) ll_third_app!!.visibility = View.VISIBLE
                fillterAppSelect()
                setStateListApp(PreferenceUtils.isTurnOnCleanNotification())
                mAppSelectAdapterNetwork!!.notifyDataSetChanged()
                mAppSelectAdapterThirdParty!!.notifyDataSetChanged()
            }
            google_progress!!.visibility = View.GONE
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        sw_clean_notification!!.isChecked = PreferenceUtils.isTurnOnCleanNotification()
    }

    private fun initListener() {
        mAppSelectAdapterNetwork!!.setmItemClickListener { position: Int ->
            val state = lstNetWork[position].isChceked
            if (!state) {
                sw_network_all!!.isChecked = false
            } else {
                for (mTaskInfo in lstNetWork) {
                    if (!mTaskInfo.isChceked) return@setmItemClickListener
                }
                sw_network_all!!.isChecked = true
            }
        }
        mAppSelectAdapterThirdParty!!.setmItemClickListener { position: Int ->
            val state = lstThirdParty[position].isChceked
            if (!state) {
                sw_third_app_all!!.isChecked = false
            } else {
                for (mTaskInfo in lstThirdParty) {
                    if (!mTaskInfo.isChceked) return@setmItemClickListener
                }
                sw_third_app_all!!.isChecked = true
            }
        }
    }

    private fun setStateListApp(state: Boolean) {
        ll_list_app!!.isEnabled = state
        ll_list_app!!.alpha = if (state) 1.0f else 0.6f
        for (mTaskInfo in lstNetWork) {
            mTaskInfo.isClickEnable = state
        }
        for (mTaskInfo in lstThirdParty) {
            mTaskInfo.isClickEnable = state
        }
    }

    private fun fillterAppSelect() {
        var coutNetWork = 0
        var coutThirdApp = 0
        for (pkgNameSave in lstAppSave) {
            var check = false
            for (mTaskInfo in lstNetWork) {
                if (mTaskInfo.packageName.equals(pkgNameSave, ignoreCase = true)) {
                    mTaskInfo.isChceked = true
                    check = true
                    coutNetWork++
                    break
                }
            }
            if (!check) {
                for (mTaskInfo in lstThirdParty) {
                    if (mTaskInfo.packageName.equals(pkgNameSave, ignoreCase = true)) {
                        mTaskInfo.isChceked = true
                        coutThirdApp++
                        break
                    }
                }
            }
        }
        sw_network_all!!.isChecked = coutNetWork == lstNetWork.size
        sw_third_app_all!!.isChecked = coutThirdApp == lstThirdParty.size
    }

    private fun isAppNetwork(pkgName: String): Boolean {
        for (pkgNetwork in Config.LIST_APP_NETWORK) {
            if (pkgNetwork.equals(pkgName, ignoreCase = true)) return true
        }
        return false
    }

    private fun click() {
        done.setOnClickListener { click(it) }
        sw_third_app_all.setOnClickListener { click(it) }
        sw_network_all.setOnClickListener { click(it) }
        sw_clean_notification.setOnClickListener { click(it) }
    }

    fun click(mView: View) {
        when (mView.id) {
            R.id.done -> {
                PreferenceUtils.setCleanNotification(sw_clean_notification!!.isChecked)
                lstAppSave.clear()
                for (mTaskInfo in lstNetWork) {
                    if (mTaskInfo.isChceked) lstAppSave.add(mTaskInfo.packageName)
                }
                for (mTaskInfo in lstThirdParty) {
                    if (mTaskInfo.isChceked) lstAppSave.add(mTaskInfo.packageName)
                }
                PreferenceUtils.setListAppCleanNotifi(lstAppSave)
                if (sw_clean_notification!!.isChecked && NotificationListener.getInstance() != null) {
                    NotificationListener.getInstance().loadCurrentNotifi()
                } else {
                    NotificationUtil.getInstance().cancelNotificationClean(NotificationUtil.ID_NOTIFI_CLEAN)
                }
                onBackPressed()
            }
            R.id.sw_network_all -> if (sw_clean_notification!!.isChecked) {
                for (mTaskInfo in lstNetWork) {
                    mTaskInfo.isChceked = sw_network_all!!.isChecked
                }
                mAppSelectAdapterNetwork!!.notifyDataSetChanged()
            } else {
                sw_network_all!!.isChecked = !sw_network_all!!.isChecked
            }
            R.id.sw_third_app_all -> if (sw_clean_notification!!.isChecked) {
                for (mTaskInfo in lstThirdParty) {
                    mTaskInfo.isChceked = sw_third_app_all!!.isChecked
                }
                mAppSelectAdapterThirdParty!!.notifyDataSetChanged()
            } else {
                sw_third_app_all!!.isChecked = !sw_third_app_all!!.isChecked
            }
            R.id.sw_clean_notification -> {
                setStateListApp(sw_clean_notification!!.isChecked)
                getCache().save(Config.FUNCTION.NOTIFICATION_MANAGER.id, System.currentTimeMillis())
            }
        }
    }

    override fun onBackPressed() {
        if (!PreferenceUtils.isTurnOnCleanNotification()) {
            Application.getInstance().clearAllActivityUnlessMain()
        } else {
            super.onBackPressed()
        }
    }
}