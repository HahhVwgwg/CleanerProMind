package promind.cleaner.app.ui.activities.antivirus

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import kotlinx.android.synthetic.main.activity_antivirus.*
import kotlinx.android.synthetic.main.layout_animation_antivirus.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import promind.cleaner.app.R
import promind.cleaner.app.core.adsControl.AdmobHelp
import promind.cleaner.app.core.data.data.TaskScanVirus
import promind.cleaner.app.core.data.model.TaskInfo
import promind.cleaner.app.core.utils.Config
import promind.cleaner.app.core.utils.Constants
import promind.cleaner.app.core.utils.listener
import promind.cleaner.app.core.utils.preferences.MyCache.Companion.getCache
import promind.cleaner.app.ui.activities.BaseActivity
import promind.cleaner.app.ui.dialog.DialogConfirmCancel
import java.util.*

class AntivirusActivity : BaseActivity() {
    private var alreadyShown: Boolean = false
    private var isCanback = true
    private val lstAppDangerous: MutableList<TaskInfo> = ArrayList()
    private val lstAppVirus: MutableList<TaskInfo> = ArrayList()
    private var startResolve = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_antivirus)
        initView()
        initData()
        initTimerForAds()
        click()
    }

    private fun initTimerForAds() {
        object : CountDownTimer(3000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                tv_resolve_all.text =
                    String.format(applicationContext.getString(R.string.show_ads_format),
                        millisUntilFinished / 1000 + 1)
            }

            override fun onFinish() {
                tv_resolve_all.visibility = View.GONE
                if (Constants.showAds) AdmobHelp.getInstance().showInterstitialAdWithoutWaiting {
                    finishAnimationDone()
                }
            }
        }.start()
    }

    private fun finishAnimationDone() {
        if (!lstAppVirus.isEmpty()) {
            startResolve = lstAppVirus.size - 1
            uninstall(startResolve)
        } else {
            Objects.requireNonNull(listener)!!.invoke()
            getCache().save(Config.FUNCTION.ANTIVIRUS.id, System.currentTimeMillis())
            openScreenResult(Config.FUNCTION.ANTIVIRUS)
            finish()
        }
    }

    fun getLstAppVirus(): List<TaskInfo> {
        return lstAppVirus
    }

    fun getLstAppDangerous(): List<TaskInfo> {
        return lstAppDangerous
    }

    private fun initView() {
        im_back_toolbar.setVisibility(View.VISIBLE)
        im_back_toolbar.setOnClickListener { v ->
            val dialogConfirmCancel = DialogConfirmCancel(this, Config.FUNCTION.ANTIVIRUS.id)
            dialogConfirmCancel.listener = object : DialogConfirmCancel.OnClickListener {
                override fun onDo() {}
                override fun onCancel() {
                    finish()
                }
            }
            dialogConfirmCancel.show()
        }
        tv_toolbar.setText(getString(R.string.security))
    }

    private fun initData() {
        antivirusScanView.setVisibility(View.VISIBLE)
        antivirusScanView.startAnimationScan()
        isCanback = false
        TaskScanVirus(this, 200, object : TaskScanVirus.OnTaskListListener {
            override fun OnResult(lstDangerous: List<TaskInfo>, lstVirus: List<TaskInfo>) {
                YoYo.with(Techniques.FadeOut).duration(1000).playOn(antivirusScanView)
                lstAppDangerous.clear()
                lstAppDangerous.addAll(lstDangerous)
                lstAppVirus.clear()
                lstAppVirus.addAll(lstVirus)
                updateData()
                tv_toolbar.setVisibility(View.INVISIBLE)
                isCanback = true
                antivirusScanView.stopAnimationScan()
                id_menu_toolbar.setVisibility(View.VISIBLE)
            }

            override fun onProgress(
                appName: String,
                virusName: String,
                dangerousSize: String,
                progress: String,
            ) {
                antivirusScanView.setContent(appName)
                antivirusScanView.setProgress(progress.toFloat().toInt())
                if (virusName != null && !virusName.isEmpty()) {
                    antivirusScanView.showBgVirus()
                } else if (dangerousSize != null && !dangerousSize.isEmpty() && dangerousSize.toInt() != 0) {
                    antivirusScanView.showBgDangerous()
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    private fun click() {
        tv_resolve_all.setOnClickListener { click(it) }
        id_menu_toolbar.setOnClickListener { click(it) }
    }

    fun click(mView: View) {
        when (mView.id) {
            R.id.id_menu_toolbar -> {
                val popupMenu = PopupMenu(this, id_menu_toolbar)
                popupMenu.menuInflater.inflate(R.menu.scan_virus_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item: MenuItem? ->
                    openWhileListVirusSceen()
                    true
                }
                popupMenu.show()
            }
            R.id.tv_resolve_all -> if (Constants.showAds) AdmobHelp.getInstance()
                .showInterstitialAd {
                    finishAnimationDone()
                }
        }
        alreadyShown = true
    }

    @SuppressLint("StringFormatInvalid")
    fun updateData() {
        ll_virus.setVisibility(if (lstAppVirus.isEmpty()) View.GONE else View.VISIBLE)
        ll_dangerous.setVisibility(if (lstAppDangerous.isEmpty()) View.GONE else View.VISIBLE)
        val total = lstAppVirus.size + lstAppDangerous.size
        tv_total_issues.setText(total.toString())
        issuesCount.text = getString(R.string.issues_found, total.toString())
        if (total == 0) {
            openScreenResult(Config.FUNCTION.ANTIVIRUS)
            finish()
        }
    }

    override fun onBackPressed() {
        if (isCanback) super.onBackPressed()
    }

    fun uninstall(position: Int) {
        val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE)
        intent.data = Uri.parse("package:" + lstAppVirus[position].packageName)
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
        startActivityForResult(intent, Config.UNINSTALL_REQUEST_CODE_ACTIVITY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Config.UNINSTALL_REQUEST_CODE_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                lstAppVirus.removeAt(startResolve)
                updateData()
                startResolve--
                if (startResolve >= 0) uninstall(startResolve)
            }
        }
    }
}