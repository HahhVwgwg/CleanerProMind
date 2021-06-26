package promind.cleaner.app.ui.activities.appManager

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.jpardogo.android.googleprogressbar.library.ChromeFloatingCirclesDrawable
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar
import promind.cleaner.app.R
import promind.cleaner.app.ui.adapter.AppManagerAdapter
import promind.cleaner.app.core.data.data.ManagerConnect
import promind.cleaner.app.core.data.data.ManagerConnect.OnManagerConnectListener
import promind.cleaner.app.ui.dialog.DialogConfirmCancel
import promind.cleaner.app.ui.activities.BaseActivity
import promind.cleaner.app.core.utils.Config
import promind.cleaner.app.core.utils.Toolbox
import kotlin.collections.ArrayList
import kotlin.math.min

class AppManagerActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.recyclerView)
    var mRecyclerView: RecyclerView? = null

    @JvmField
    @BindView(R.id.im_back_toolbar)
    var imBackToolbar: ImageView? = null

    @JvmField
    @BindView(R.id.tv_toolbar)
    var tvToolbar: TextView? = null

    @JvmField
    @BindView(R.id.google_progress)
    var mGoogleProgressBar: GoogleProgressBar? = null

    @JvmField
    @BindView(R.id.delete)
    var delete: Button? = null

    @JvmField
    @BindView(R.id.checkAll)
    var checkAll: CheckBox? = null
    private val mHandlerLocal: Handler? = Handler(Looper.getMainLooper())
    private var mGroupItems: MutableList<ApplicationInfo> = ArrayList()
    private var selectedApp: ApplicationInfo? = null
    var runnableLocal: Runnable? = null
    private var mAdapter: AppManagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_app_manager)
        ButterKnife.bind(this)
        initView()
        initData()
        loadData()
    }

    private fun initView() {
        checkAll!!.setOnCheckedChangeListener { button: CompoundButton?, isChecked: Boolean -> mAdapter!!.setChecked(isChecked) }
        imBackToolbar!!.visibility = View.VISIBLE
        imBackToolbar!!.setOnClickListener { v: View? ->
            val dialogConfirmCancel = DialogConfirmCancel(this, Config.FUNCTION.APP_UNINSTALL.id)
            dialogConfirmCancel.listener = object : DialogConfirmCancel.OnClickListener {
                override fun onDo() {}
                override fun onCancel() {
                    finish()
                }
            }
            dialogConfirmCancel.show()
        }
        delete!!.setOnClickListener { v: View? ->
            for (checkedItem in mAdapter!!.getCheckedItems()) {
                deleteApp(checkedItem)
            }
        }
        tvToolbar!!.text = getString(R.string.app_uninstall)
        val drawable = ChromeFloatingCirclesDrawable.Builder(this)
                .colors(Toolbox.getProgressDrawableColors(this))
                .build()
        val bounds = mGoogleProgressBar!!.indeterminateDrawable.bounds
        mGoogleProgressBar!!.indeterminateDrawable = drawable
        mGoogleProgressBar!!.indeterminateDrawable.bounds = bounds
    }

    private fun deleteApp(app: ApplicationInfo) {
        selectedApp = app
        val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE)
        intent.data = Uri.parse("package:" + app.packageName)
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
        startActivityForResult(intent, UNINSTALL_REQUEST_CODE)
    }

    private fun initData() {
        mAdapter = AppManagerAdapter(this, object : AppManagerAdapter.OnClickItemListener {
            override fun onChecked() {
                delete!!.text = getString(R.string.delete_selected, mAdapter!!.checkedCount().toString())
            }

            override fun onUninstallApp(app: ApplicationInfo) {
                deleteApp(app)
            }

            override fun onClickItem(data: ApplicationInfo) {
                if (data.packageName != null) {
                    val intent = Intent()
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    intent.data = Uri.parse("package:" + data.packageName)
                    startActivity(intent)
                }
            }
        })

        loadFilteredApps()
        mRecyclerView!!.adapter = mAdapter
        delete!!.text = getString(R.string.delete_selected, "0")
    }

    private val appsToFilter = arrayListOf(
            "com.xiaomi.calendar",
            "com.google.android.googlequicksearchbox",
            "com.miui.securitycenter",
            "com.miui.gallery",
            "com.miui.videoplayer",
            "com.mi.globalbrowser",
            "com.google.ar.lens",
            "com.android.providers.downloads.ui",
            "com.android.vending",
            "com.miui.huanji",
            "com.android.stk",
            "com.miui.player",
            "com.miui.miservice"
    )

    private fun loadFilteredApps() {
        appsToFilter.add(packageName)
        appsToFilter.distinct()
        print(mGroupItems.map {
            it.loadLabel(packageManager) to it.packageName
        }.toString())
        mGroupItems = ArrayList(mGroupItems.filter {
            it.packageName !in appsToFilter
                    && !it.packageName.startsWith("com.mi")
                    && !it.packageName.startsWith("com.google")
                    && !it.packageName.startsWith("com.android")
        })
        mAdapter!!.setData(mGroupItems)
    }

    private fun print(s: String) {
        val chunkSize = 2048
        var i = 0
        while (i < s.length) {
            Log.d("RRR", s.substring(i, min(s.length, i + chunkSize)))
            i += chunkSize
        }
    }

    private fun loadData() {
        mGoogleProgressBar!!.visibility = View.VISIBLE
        val managerConnect = ManagerConnect()
        managerConnect.getListManager(this, object : OnManagerConnectListener {
            override fun OnResultManager(result: List<ApplicationInfo>) {
                if (result.isNotEmpty()) {
                    /*//set app user
                        GroupItemAppManager groupItemAppManagerUser = new GroupItemAppManager();
                        groupItemAppManagerUser.setTitle(getString(R.string.user_app));
                        groupItemAppManagerUser.setType(GroupItemAppManager.TYPE_USER_APPS);
                        int countUser = 0;
                        List<ApplicationInfo> applicationInfosUser = new ArrayList<>();
                        for (ApplicationInfo applicationInfo : result) {
                            if (!applicationInfo.packageName.equals(getPackageName())) {
                                countUser++;
                                applicationInfosUser.add(applicationInfo);
                            }
                        }
                        groupItemAppManagerUser.setItems(applicationInfosUser);
                        groupItemAppManagerUser.setTotal(countUser);*/
                    mGroupItems.addAll(result)
                    /*//set app system
                        GroupItemAppManager groupItemAppManagerSystem = new GroupItemAppManager();
                        groupItemAppManagerSystem.setTitle(getString(R.string.system_app));
                        groupItemAppManagerSystem.setType(GroupItemAppManager.TYPE_SYSTEM_APPS);
                        int countSystem = 0;
                        List<ApplicationInfo> applicationInfosSystem = new ArrayList<>();
                        for (ApplicationInfo applicationInfo : result) {
                            if (!Toolbox.isUserApp(applicationInfo)) {
                                countSystem++;
                                applicationInfosSystem.add(applicationInfo);
                            }
                        }
                        groupItemAppManagerSystem.setItems(applicationInfosSystem);
                        groupItemAppManagerSystem.setTotal(countSystem);
    //                    mGroupItems.add(groupItemAppManagerSystem);*/
                    loadFilteredApps()
                    mAdapter!!.notifyDataSetChanged()
                }
                mGoogleProgressBar!!.visibility = View.GONE
            }
        })
    }

    public override fun onDestroy() {
        super.onDestroy()
        runnableLocal?.let { mHandlerLocal?.removeCallbacks(it) }
    }

    private fun deleteResult() {
        mGroupItems.remove(selectedApp)
        loadFilteredApps()
        delete!!.text = getString(R.string.delete_selected, mAdapter!!.checkedCount().toString())
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UNINSTALL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                deleteResult()
            }
        }
    }

    companion object {
        private const val UNINSTALL_REQUEST_CODE = 1
    }
}