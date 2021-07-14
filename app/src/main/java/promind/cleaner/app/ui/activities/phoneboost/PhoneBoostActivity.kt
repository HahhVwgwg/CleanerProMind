package promind.cleaner.app.ui.activities.phoneboost

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_phone_boost.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import promind.cleaner.app.R
import promind.cleaner.app.core.adsControl.AdmobHelp
import promind.cleaner.app.core.data.data.TaskBoostApps
import promind.cleaner.app.core.data.data.TaskForceStop
import promind.cleaner.app.core.data.data.TaskKillProgress
import promind.cleaner.app.core.data.model.TaskInfo
import promind.cleaner.app.core.service.listener.animation.AnimationListener
import promind.cleaner.app.core.service.service.ForceStopAccessibility
import promind.cleaner.app.core.utils.Config
import promind.cleaner.app.core.utils.Config.FUNCTION
import promind.cleaner.app.core.utils.Constants.showAds
import promind.cleaner.app.core.utils.preferences.MyCache.Companion.getCache
import promind.cleaner.app.ui.activities.BaseActivity
import promind.cleaner.app.ui.adapter.AppSelectAdapter
import promind.cleaner.app.ui.dialog.DialogConfirmCancel
import java.util.*

class PhoneBoostActivity : BaseActivity() {
    private var alreadyShown: Boolean = false

    /**
     * This activity used for function:
     * - Phone boost
     * - CPU Cooler
     * - power saver
     */
    private var activity: PhoneBoostActivity? = null
    private var anmationRunning = false
    private var mAppSelectAdapter: AppSelectAdapter? = null
    private var mFunction: FUNCTION? = null
    private val lstApp: MutableList<TaskInfo> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_boost)
        mFunction = intent.getSerializableExtra(Config.DATA_OPEN_BOOST) as FUNCTION?
        initView()
        initData()
        activity = this
        initClick()
        initTimerForAds()
    }

    private fun initTimerForAds() {
        object : CountDownTimer(3000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                tv_boost.text =
                    String.format(applicationContext.getString(R.string.show_ads_format),
                        millisUntilFinished / 1000 + 1)
            }

            override fun onFinish() {
                tv_boost.visibility = View.GONE
                println("MineMine 71")
                if (showAds) AdmobHelp.getInstance().showInterstitialAdWithoutWaiting {
                    println("MineMine 73")
                    finishAnimationDone()
                } else {
                    println("MineMine 77")
                }
            }
        }.start()
    }

    private fun initView() {
        im_back_toolbar!!.visibility = View.VISIBLE
        im_back_toolbar!!.setOnClickListener { v: View? ->
            val dialogConfirmCancel = DialogConfirmCancel(this, which)
            dialogConfirmCancel.listener = object : DialogConfirmCancel.OnClickListener {
                override fun onDo() {}
                override fun onCancel() {
                    finish()
                }
            }
            dialogConfirmCancel.show()
        }
        if (mFunction != null) tv_toolbar!!.text = getString(mFunction!!.title)
        when (mFunction) {
            FUNCTION.PHONE_BOOST -> {
//                tv_content_header!!.setText(R.string.memory_kill_found)
//                tv_boost!!.setText(R.string.boost)
                rocketScanView!!.visibility = View.VISIBLE
                rocketScanView!!.playAnimationStart()
            }
            FUNCTION.CPU_COOLER -> {
//                tv_content_header!!.setText(R.string.further_optimize_cpu)
//                tv_boost!!.setText(R.string.cool_down)
                cpuScanView!!.visibility = View.VISIBLE
                cpuScanView!!.playAnimationStart()
            }
            FUNCTION.POWER_SAVING -> {
//                tv_content_header!!.setText(R.string.secretly_consuming_battery)
                updateText()
                powerScanView!!.visibility = View.VISIBLE
                powerScanView!!.playAnimationStart()
            }
        }
    }

    private fun initData() {
        mAppSelectAdapter = AppSelectAdapter(this, AppSelectAdapter.TYPE_SELECT.CHECK_BOX, lstApp)
        mAppSelectAdapter!!.setmItemClickListener {
            tv_num_appskill!!.text = mAppSelectAdapter!!.checkedItemCount.toString()
            updateText()
        }
        rcv_app!!.adapter = mAppSelectAdapter
        listAppRunning
        progress_bar.max = lstApp.size
    }

    @SuppressLint("StringFormatInvalid")
    private fun updateText() {
//        if (mFunction == FUNCTION.POWER_SAVING)
//            tv_boost!!.text = getString(R.string.extend_battery_life, tv_num_appskill!!.text.toString())
    }

    var progressIndicator = 0

    val listAppRunning: Unit
        get() {
            anmationRunning = true
            TaskBoostApps(object :
                TaskBoostApps.OnTaskListListener {
                override fun OnResult(arrList: List<TaskInfo>) {
                    anmationRunning = false
                    if (arrList != null) {
                        Collections.sort(arrList) { o1: TaskInfo, o2: TaskInfo ->
                            o1.title!!.compareTo(
                                o2.title!!,
                                ignoreCase = true
                            )
                        }
                        lstApp.clear()
                        lstApp.addAll(arrList)
                        setListStatusItem(true)
                        mAppSelectAdapter!!.notifyDataSetChanged()
                        tv_num_appskill!!.text = arrList.size.toString()
                        updateText()
//                        setViewAffterScan()
                        boost()
                        progress_bar.visibility = View.GONE
                        tv_description.visibility = View.GONE
                        tv_content.visibility = View.GONE
                        tv_boost.visibility = View.GONE
                    }
                }

                override fun onProgress(appName: String) {
                    rocketScanView!!.setContent("")
                    progress_bar.progress = progressIndicator++
                    tv_description.text = appName
                    val currentProgress = progressIndicator * 100 / progress_bar.max
                    progress_tv.text = currentProgress.toString()
                }

                override fun size(size: Int) {
                    progress_bar.max = size
                }
            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }

    private fun setViewAffterScan() {
        id_menu_toolbar.handleViewAndClick()
        when (mFunction) {
            FUNCTION.PHONE_BOOST -> rocketScanView!!.stopAnimationStart()
            FUNCTION.CPU_COOLER -> cpuScanView!!.stopAnimationStart()
            FUNCTION.POWER_SAVING -> powerScanView!!.stopAnimationStart()
        }
    }

    private fun playAnimationDone() {
        when (mFunction) {
            FUNCTION.PHONE_BOOST -> {
//                YoYo.with(Techniques.FadeIn).duration(1000).playOn(rocketScanView)
                rocketScanView!!.playAnimationDone(object : AnimationListener {
                    override fun onStop() {
                        finishAnimationDone()
                    }
                })
            }
            FUNCTION.CPU_COOLER -> {
//                YoYo.with(Techniques.FadeIn).duration(1000).playOn(cpuScanView)
                cpuScanView!!.playAnimationDone(object : AnimationListener {
                    override fun onStop() {
                        finishAnimationDone()
                    }
                })
            }
            FUNCTION.POWER_SAVING -> {
//                YoYo.with(Techniques.FadeIn).duration(1000).playOn(powerScanView)
                val lstSelect: MutableList<TaskInfo> = ArrayList()
                for (mTaskInfo in lstApp) {
                    if (mTaskInfo.isChceked) lstSelect.add(mTaskInfo)
                }
                powerScanView!!.playAnimationDone(lstSelect, 300, object : AnimationListener {
                    override fun onStop() {
                        finishAnimationDone()
                    }
                })
            }
        }
    }

    private fun finishAnimationDone() {
        anmationRunning = false
        openScreenResult(mFunction)
        finish()
    }

    private fun runkillApp() {
        anmationRunning = true
        TaskKillProgress(this, lstApp).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
//        playAnimationDone()
    }

    private fun setListStatusItem(status: Boolean) {
        for (mTaskInfo in lstApp) {
            mTaskInfo.isChceked = status
        }
    }

    override fun onBackPressed() {
        if (!anmationRunning) super.onBackPressed()
    }

    private fun initClick() {
        tv_boost.setOnClickListener { click(it) }
    }

    fun click(mView: View) {
        when (mView.id) {
            R.id.tv_boost -> if (showAds && !alreadyShown) AdmobHelp.getInstance()
                .showInterstitialAd {
                    finishAnimationDone()
                }
        }
        alreadyShown = true
    }

    private fun boost() {
        if (checkEmptyItemSelect()) {
            if (mFunction == FUNCTION.POWER_SAVING) {
                /*if (ForceStopAccessibility.getInstance() != null) {
                    validateDeepClean()
                } else {
                    DialogSelection.Builder()
                            .setTitle(getString(R.string.deep_boost))
                            .setContent(getString(R.string.content_permission_1))
                            .setAction1(getString(R.string.deep_boost)) { dialog: DialogSelection ->
                                validateDeepClean()
                                dialog.dismiss()
                            }
                            .setAction2(getString(R.string.normal_boost)) { dialog: DialogSelection ->
                                runkillApp()
                                dialog.dismiss()
                            }
                            .build()
                            .show(supportFragmentManager, DialogSelection::class.java.name)
                }*/
                runkillApp()
            } else {
                runkillApp()
            }
            getCache().save(mFunction!!.id, System.currentTimeMillis())
        }
    }

    private fun validateDeepClean() {
        try {
            checkdrawPermission {
                if (ForceStopAccessibility.getInstance() != null) {
                    startDeepClean()
                } else {
                    requestDetectHome()
                }
                getCache().save(FUNCTION.DEEP_CLEAN.id, System.currentTimeMillis())
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startDeepClean() {
        val lstSelect: MutableList<TaskInfo> = ArrayList()
        for (mTaskInfo in lstApp) {
            if (mTaskInfo.isChceked) lstSelect.add(mTaskInfo)
        }
        TaskForceStop(this, lstSelect).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    private fun checkEmptyItemSelect(): Boolean {
        for (mTaskInfo in lstApp) {
            if (mTaskInfo.isChceked) return true
        }
        Toast.makeText(this, getString(R.string.empty_item_select), Toast.LENGTH_LONG).show()
        return false
    }

    companion object {
        private var which = 0
        fun startActivityWithData(mContext: Context, mFunction: FUNCTION) {
            val mIntent = Intent(mContext, PhoneBoostActivity::class.java)
            mIntent.putExtra(Config.DATA_OPEN_BOOST, mFunction)
            which = mFunction.id
            mContext.startActivity(mIntent)
        }
    }
}