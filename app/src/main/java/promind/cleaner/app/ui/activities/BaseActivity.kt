package promind.cleaner.app.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.Constants
import com.anjlab.android.iab.v3.TransactionDetails
import promind.cleaner.app.BuildConfig
import promind.cleaner.app.R
import promind.cleaner.app.core.app.Application
import promind.cleaner.app.core.service.service.NotificationListener
import promind.cleaner.app.core.utils.Config
import promind.cleaner.app.core.utils.Config.FUNCTION
import promind.cleaner.app.core.utils.PreferenceUtils
import promind.cleaner.app.core.utils.SystemUtil
import promind.cleaner.app.core.utils.Toolbox
import promind.cleaner.app.core.utils.UpdateManager
import promind.cleaner.app.core.utils.preferences.MyCache.Companion.getCache
import promind.cleaner.app.ui.activities.antivirus.AntivirusActivity
import promind.cleaner.app.ui.activities.appManager.AppManagerActivity
import promind.cleaner.app.ui.activities.cleanNotification.NotificationCleanActivity
import promind.cleaner.app.ui.activities.cleanNotification.NotificationCleanGuildActivity
import promind.cleaner.app.ui.activities.cleanNotification.NotificationCleanSettingActivity
import promind.cleaner.app.ui.activities.deepClean.DeepScanActivity
import promind.cleaner.app.ui.activities.guildPermission.GuildPermissionActivity
import promind.cleaner.app.ui.activities.junkfile.JunkFileActivity
import promind.cleaner.app.ui.activities.listAppSelect.AppSelectActivity
import promind.cleaner.app.ui.activities.phoneboost.PhoneBoostActivity
import promind.cleaner.app.ui.activities.premium.PremiumActivity
import promind.cleaner.app.ui.activities.premium.loge
import promind.cleaner.app.ui.activities.premium.toast
import promind.cleaner.app.ui.activities.result.ResultActivity
import promind.cleaner.app.ui.activities.smartCharger.SmartChargerActivity
import promind.cleaner.app.ui.dialog.DialogAskPermission
import java.util.*
import java.util.concurrent.Callable

open class BaseActivity : AppCompatActivity(), BillingProcessor.IBillingHandler {
    private var imBackToolbar: ImageView? = null
    private var loPanel: View? = null
    private val callables: MutableList<Callable<Unit>> = ArrayList()
    private var bp: BillingProcessor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Application.getInstance().doForCreate(this)

//        initBilling() // todo implement IAP v3

        initUpdateManager()
    }

    protected fun openPremium() {
        startActivity(Intent(this, PremiumActivity::class.java))
    }

    fun ImageView.handleViewAndClick() {
        this.visibility =
            if (promind.cleaner.app.core.utils.Constants.isPremium) View.GONE else View.VISIBLE
        setOnClickListener {
            openPremium()
        }
    }

    private var updateManager: UpdateManager? = null
    private fun initUpdateManager() {
        if (!BuildConfig.DEBUG) updateManager = UpdateManager(this).apply {
            checkUpdate()
        }
    }

    protected fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    private fun initBilling() {
        bp = BillingProcessor(this, BuildConfig.BILLING_KEY, this).apply {
            initialize()

            loadOwnedPurchasesFromGoogle()

            getSubscriptionListingDetails("adfree")?.let {
                loge("getSubscriptionListingDetails: $it")
            }
            getSubscriptionTransactionDetails("adfree")?.let {
                Application.sharedManager.premiumAvailable =
                    (it.purchaseInfo.purchaseData.productId).contains("adfree")
                loge("premiumAvailable: ${Application.sharedManager.premiumAvailable} ")
            }
        }
    }

    fun processBilling() {
        val isAvailable = BillingProcessor.isIabServiceAvailable(this)
        val isSubsUpdateSupported = bp?.isSubscriptionUpdateSupported
        println("isAvailable $isAvailable isSubsUpdateSupported $isSubsUpdateSupported")
        if (isAvailable && isSubsUpdateSupported ?: false) {
            bp?.subscribe(this, "adfree")
        } else toast(R.string.billing_or_subscription_not_supported)

    }

    override fun onBillingInitialized() {
    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        toast(R.string.product_purchased)
        loge("productId : $productId")
        loge("details : $details")

        Application.sharedManager.premiumAvailable = true
        refreshDataAfterPurchase()
    }

    open fun refreshDataAfterPurchase() {
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        when (errorCode) {
            Constants.BILLING_RESPONSE_RESULT_USER_CANCELED -> loge("BILLING_RESPONSE_RESULT_USER_CANCELED")
            Constants.BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE -> loge("BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE")
            Constants.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE -> loge("BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE")
            Constants.BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE -> loge("BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE")
            Constants.BILLING_RESPONSE_RESULT_DEVELOPER_ERROR -> loge("BILLING_RESPONSE_RESULT_DEVELOPER_ERROR")
            Constants.BILLING_RESPONSE_RESULT_ERROR -> loge("BILLING_RESPONSE_RESULT_ERROR")
            Constants.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED -> loge("BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED")
            Constants.BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED -> loge("BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED")
        }
    }

    override fun onPurchaseHistoryRestored() {
    }

    override fun onDestroy() {
        super.onDestroy()
        Application.getInstance().doForFinish(this)
        bp?.apply { release() }

    }

    fun clear() {
        super.finish()
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        imBackToolbar = findViewById(R.id.im_back_toolbar)
        loPanel = findViewById(R.id.layout_padding)
        if (loPanel != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Toolbox.getHeightStatusBar(
                    this
                ) > 0
            ) {
                loPanel!!.setPadding(0, Toolbox.getHeightStatusBar(this), 0, 0)
            }
            Toolbox.setStatusBarHomeTransparent(this)
        }
        initControl()
    }

    private fun initControl() {
        if (imBackToolbar != null) imBackToolbar!!.setOnClickListener { v: View? -> onBackPressed() }
    }

    private val baseHandler = Handler()
    private var baseRunnable = Runnable {
    }

    fun removePreviousCallback(action: () -> Unit, millis: Long = 500) {
        baseHandler.removeCallbacks(baseRunnable)
        baseRunnable = Runnable { action() }
        baseHandler.postDelayed(baseRunnable, millis)
    }

    fun openScreenFunction(mFunction: FUNCTION?) {
        removePreviousCallback({
            when (mFunction) {
                FUNCTION.JUNK_FILES -> try {
                    JunkFileActivity.startActivityWithData(this@BaseActivity)
                } catch (e: Exception) {
                }
                FUNCTION.CPU_COOLER, FUNCTION.PHONE_BOOST, FUNCTION.POWER_SAVING -> try {
                    if (PreferenceUtils.checkLastTimeUseFunction(mFunction)) PhoneBoostActivity.startActivityWithData(
                        this@BaseActivity,
                        mFunction
                    ) else openScreenResult(mFunction)
                } catch (e: Exception) {
                }
                FUNCTION.ANTIVIRUS -> try {
                    getCache().save(FUNCTION.ANTIVIRUS.id, System.currentTimeMillis())
                    askPermissionStorage {
                        startActivity(Intent(this, AntivirusActivity::class.java))
                        null
                    }
                } catch (e: Exception) {
                }
                FUNCTION.SMART_CHARGE -> if (!SystemUtil.checkCanWriteSettings(this)) {
                    startActivity(Intent(this, SmartChargerActivity::class.java))
                }
                FUNCTION.DEEP_CLEAN -> {
                    getCache().save(FUNCTION.DEEP_CLEAN.id, System.currentTimeMillis())
                    startActivity(
                        Intent(
                            this,
                            DeepScanActivity::class.java
                        )
                    )
                }
                FUNCTION.DEEP_CLEAN_JUNK -> try {
                    JunkFileActivity.startActivityWithData(this@BaseActivity)
                } catch (e: Exception) {
                }
                FUNCTION.PREMIUM -> startActivity(
                    Intent(
                        this, PremiumActivity::class.java
                    )
                )
                FUNCTION.APP_UNINSTALL -> startActivity(
                    Intent(
                        this,
                        AppManagerActivity::class.java
                    )
                )
                FUNCTION.NOTIFICATION_MANAGER -> if (PreferenceUtils.isFirstUsedFunction(mFunction)) {
                    startActivity(Intent(this, NotificationCleanGuildActivity::class.java))
                } else {
                    try {
                        askPermissionNotificaitonSetting {
                            if (NotificationListener.getInstance() != null) {
                                if (NotificationListener.getInstance().lstSave.isEmpty()) {
                                    startActivity(
                                        Intent(
                                            this@BaseActivity,
                                            NotificationCleanSettingActivity::class.java
                                        )
                                    )
                                } else {
                                    startActivity(
                                        Intent(
                                            this@BaseActivity,
                                            NotificationCleanActivity::class.java
                                        )
                                    )
                                }
                            } else {
                                startActivity(
                                    Intent(
                                        this@BaseActivity,
                                        NotificationCleanSettingActivity::class.java
                                    )
                                )
                            }
                            null
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })

    }

    fun openWhileListVirusSceen() {
        AppSelectActivity.openSelectAppScreen(this, AppSelectActivity.TYPE_SCREEN.WHILE_LIST_VIRUS)
    }

    fun openScreenResult(mFunction: FUNCTION?, needInterstitial: Boolean = false) {
        val mIntent = Intent(this, ResultActivity::class.java)
        if (mFunction != null) {
            mIntent.putExtra(Config.DATA_OPEN_RESULT, mFunction.id)
            mIntent.putExtra(Config.DATA_OPEN_RESULT_WITHOUT_INTERSTITIAL, true)
        }
        startActivity(mIntent)
    }

    @Throws(Exception::class)
    fun askPermissionStorageWithoutDialog(callable: Callable<Unit>) {
        callables.clear()
        callables.add(callable)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    Config.MY_PERMISSIONS_REQUEST_STORAGE
                )
            } else {
                callable.call()
            }
        } else {
            callable.call()
        }
    }

    @Throws(Exception::class)
    fun askPermissionStorage(callable: Callable<Unit>) {
        callables.clear()
        callables.add(callable)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                DialogAskPermission.getInstance(Manifest.permission.READ_EXTERNAL_STORAGE) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        Config.MY_PERMISSIONS_REQUEST_STORAGE
                    )
                }.show(supportFragmentManager, DialogAskPermission::class.java.name)
            } else {
                callable.call()
            }
        } else {
            callable.call()
        }
    }

    @Throws(Exception::class)
    fun askPermissionUsageSetting(callable: Callable<Unit>) {
        callables.clear()
        callables.add(callable)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !SystemUtil.isUsageAccessAllowed(
                this
            )
        ) {
            DialogAskPermission.getInstance(Settings.ACTION_USAGE_ACCESS_SETTINGS) {
                startActivityForResult(
                    Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                    Config.PERMISSIONS_USAGE
                )
                GuildPermissionActivity.openActivityGuildPermission(
                    this,
                    Settings.ACTION_USAGE_ACCESS_SETTINGS
                )
            }.show(supportFragmentManager, DialogAskPermission::class.java.name)
        } else {
            callable.call()
        }
    }

    @Throws(Exception::class)
    fun askPermissionWriteSetting(callable: Callable<Unit>) {
        callables.clear()
        callables.add(callable)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(this)) {
            DialogAskPermission.getInstance(Settings.ACTION_MANAGE_WRITE_SETTINGS) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, Config.PERMISSIONS_WRITE_SETTINGS)
                GuildPermissionActivity.openActivityGuildPermission(
                    this,
                    Settings.ACTION_MANAGE_WRITE_SETTINGS
                )
            }.show(supportFragmentManager, DialogAskPermission::class.java.name)
        } else {
            callable.call()
        }
    }

    @Throws(Exception::class)
    fun askPermissionNotificaitonSetting(callable: Callable<Unit>) {
        callables.clear()
        callables.add(callable)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && !SystemUtil.isNotificationListenerEnabled(
                this
            )
        ) {
            DialogAskPermission.getInstance(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS) {
                startActivityForResult(
                    Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS),
                    Config.PERMISSIONS_NOTIFICATION_LISTENER
                )
                GuildPermissionActivity.openActivityGuildPermission(
                    this,
                    Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
                )
            }.show(supportFragmentManager, DialogAskPermission::class.java.name)
        } else {
            callable.call()
        }
    }

    @Throws(Exception::class)
    fun checkdrawPermission(callable: Callable<Unit>) {
        callables.clear()
        callables.add(callable)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            DialogAskPermission.getInstance(Settings.ACTION_MANAGE_OVERLAY_PERMISSION) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, Config.PERMISSIONS_DRAW_APPICATION)
                GuildPermissionActivity.openActivityGuildPermission(
                    this,
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION
                )
            }.show(supportFragmentManager, DialogAskPermission::class.java.name)
        } else {
            callable.call()
        }
    }

    fun requestDetectHome() {
        DialogAskPermission.getInstance(Settings.ACTION_ACCESSIBILITY_SETTINGS) {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            GuildPermissionActivity.openActivityGuildPermission(
                this,
                Settings.ACTION_ACCESSIBILITY_SETTINGS
            )
        }.show(supportFragmentManager, DialogAskPermission::class.java.name)
    }

    fun openSettingApplication(packageName: String) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        updateManager?.onActivityResult(requestCode, resultCode)
        if (bp?.handleActivityResult(requestCode, resultCode, data) == true) return
        when (requestCode) {
            Config.PERMISSIONS_DRAW_APPICATION -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(
                    this
                )
            ) callListener()
            Config.PERMISSIONS_USAGE -> if (SystemUtil.isUsageAccessAllowed(this)) {
                callListener()
            }
            Config.PERMISSIONS_NOTIFICATION_LISTENER -> if (SystemUtil.isNotificationListenerEnabled(
                    this
                )
            ) callListener()
            Config.PERMISSIONS_WRITE_SETTINGS -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(
                    this
                )
            ) callListener()
        }
    }

    fun callListener() {
        for (callable in callables) {
            try {
                callable.call()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        when (requestCode) {
            Config.MY_PERMISSIONS_REQUEST_CLEAN_CACHE, Config.MY_PERMISSIONS_REQUEST_STORAGE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                for (callable in callables) {
                    try {
                        callable.call()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                callables.clear()
            }
        }
    }

    fun addFragmentWithTag(targetFragment: Fragment?, tag: String?) {
        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(
            R.anim.pull_in_right,
            R.anim.push_out_left,
            R.anim.pull_in_left,
            R.anim.push_out_right
        )
        if (targetFragment != null) {
            ft.addToBackStack(null)
            ft.add(R.id.layout_fragment, targetFragment, tag).commitAllowingStateLoss()
        }
    }
}