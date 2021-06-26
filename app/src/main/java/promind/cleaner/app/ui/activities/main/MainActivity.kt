package promind.cleaner.app.ui.activities.main

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdListener
import kotlinx.android.synthetic.main.activity_main.*
import promind.cleaner.app.core.adsControl.UtilsApp
import promind.cleaner.app.core.app.Application
import promind.cleaner.app.core.utils.Constants
import promind.cleaner.app.R
import promind.cleaner.app.core.service.listener.observerPartener.ObserverInterface
import promind.cleaner.app.core.service.listener.observerPartener.ObserverUtils
import promind.cleaner.app.core.service.listener.observerPartener.eventModel.EvbCheckLoadAds
import promind.cleaner.app.core.service.listener.observerPartener.eventModel.EvbOpenFunc
import promind.cleaner.app.ui.activities.BaseActivity
import promind.cleaner.app.ui.activities.premium.PremiumActivity
import promind.cleaner.app.core.service.service.NotificationUtil
import promind.cleaner.app.core.service.service.ServiceManager
import promind.cleaner.app.core.utils.*
import promind.cleaner.app.core.utils.Config.FUNCTION
import java.util.*

class MainActivity : BaseActivity(), ObserverInterface<Any?> {

    var isLoadAdsNative = true
        private set

    private var mHomeScreen: HomeScreen? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showAds()
        setContentView(R.layout.activity_main)
        ObserverUtils.getInstance().registerObserver(this)
        initView()
        initControl()
        initShowPremiumLogic()
        mInterstitialAd?.adListener = object : AdListener() {
            override fun onAdClosed() {
                val mIntent = Intent(this@MainActivity, ServiceManager::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(this@MainActivity, mIntent)
                } else {
                    startService(mIntent)
                }
                askPermissions()
            }
        }
    }

    private fun showAds() {
        if (Constants.showAds && mInterstitialAd != null && mInterstitialAd!!.isLoaded) mInterstitialAd!!.show()
        else {
            val mIntent = Intent(this@MainActivity, ServiceManager::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(this@MainActivity, mIntent)
            } else {
                startService(mIntent)
            }
            askPermissions()
        }
    }

    private fun tryAndCatch(action: () -> Unit) {
        try {
            action()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun askPermissions() {
        askPermissionStorageWithoutDialog({})
    }

    private fun initShowPremiumLogic() {
        when {
            Application.sharedManager.lastPremiumShowedCount == 1 && dayDiff(
                System.currentTimeMillis(),
                Application.sharedManager.lastPremiumShowedTime
            ) <= 1 -> {
                start()
            }
            Application.sharedManager.lastPremiumShowedCount == 2 && dayDiff(
                System.currentTimeMillis(),
                Application.sharedManager.lastPremiumShowedTime
            ) > 7 -> {
                start()
            }
            Application.sharedManager.lastPremiumShowedCount == 3 && dayDiff(
                System.currentTimeMillis(),
                Application.sharedManager.lastPremiumShowedTime
            ) > 7 -> {
                start()
            }
        }
    }

    val prev = Calendar.getInstance()
    val new = Calendar.getInstance()
    private fun dayDiff(first: Long, second: Long): Int {
        prev.time = Date(first)
        new.time = Date(first)
        return new.get(Calendar.DAY_OF_YEAR) - prev.get(Calendar.DAY_OF_YEAR)
    }

    private fun start() {
        Application.sharedManager.lastPremiumShowedTime = System.currentTimeMillis()
        startActivity(Intent(this, PremiumActivity::class.java))
    }

    override fun refreshDataAfterPurchase() {
        initView()
    }

    private fun initView() {
        mHomeScreen = HomeScreen.instance

        supportFragmentManager.beginTransaction().replace(R.id.viewContainer, mHomeScreen!!)
            .commit()
    }

    private fun initControl() {
        val mFunction = Config.getFunctionById(intent.getIntExtra(Config.DATA_OPEN_RESULT, 0))
        if (mFunction != null) {
            isLoadAdsNative = false
            openScreenResult(mFunction)
        } else {
            val mFunctionService =
                Config.getFunctionById(intent.getIntExtra(Config.DATA_OPEN_FUNCTION, 0))
            if (mFunctionService != null) {
                isLoadAdsNative = false
                openScreenFunction(mFunctionService)
            } else {
                val idFunc = intent.getIntExtra(Config.ALARM_OPEN_FUNTION, 0)
                val mFunctionAlarm = Config.getFunctionById(idFunc)
                if (mFunctionAlarm != null) {
                    isLoadAdsNative = false
                    openScreenFunction(mFunctionAlarm)
                    when (mFunctionAlarm) {
                        FUNCTION.PHONE_BOOST -> {
                            NotificationUtil.getInstance()
                                .cancelNotificationClean(NotificationUtil.ID_NOTIFICATTION_PHONE_BOOST)
                            PreferenceUtils.setTimeAlarmPhoneBoost(AlarmUtils.TIME_PHONE_BOOST_CLICK)
                            AlarmUtils.setAlarm(
                                this,
                                AlarmUtils.ALARM_PHONE_BOOOST,
                                AlarmUtils.TIME_PHONE_BOOST_CLICK
                            )
                        }
                        FUNCTION.CPU_COOLER -> {
                            NotificationUtil.getInstance()
                                .cancelNotificationClean(NotificationUtil.ID_NOTIFICATTION_CPU_COOLER)
                            PreferenceUtils.setTimeAlarmPhoneBoost(AlarmUtils.TIME_CPU_COOLER_CLICK)
                            AlarmUtils.setAlarm(
                                this,
                                AlarmUtils.ALARM_PHONE_CPU_COOLER,
                                AlarmUtils.TIME_CPU_COOLER_CLICK
                            )
                        }
                        FUNCTION.POWER_SAVING -> {
                            NotificationUtil.getInstance()
                                .cancelNotificationClean(NotificationUtil.ID_NOTIFICATTION_BATTERY_SAVE)
                            PreferenceUtils.setTimeAlarmPhoneBoost(AlarmUtils.TIME_BATTERY_SAVE_CLICK)
                            AlarmUtils.setAlarm(
                                this,
                                AlarmUtils.ALARM_PHONE_BATTERY_SAVE,
                                AlarmUtils.TIME_BATTERY_SAVE_CLICK
                            )
                        }
                        FUNCTION.JUNK_FILES -> {
                            NotificationUtil.getInstance()
                                .cancelNotificationClean(NotificationUtil.ID_NOTIFICATTION_JUNK_FILE)
                        }
                    }
                } else {
                    val isResultDeepClean =
                        intent.getBooleanExtra(Config.RESULT_DEEP_CLEAN_DATA, false)
                    if (isResultDeepClean) {
                        isLoadAdsNative = false
                        openScreenResult(FUNCTION.DEEP_CLEAN)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ObserverUtils.getInstance().removeObserver(this)
    }

    override fun notifyAction(action: Any?) {
        if (action is EvbOpenFunc) {
            openScreenFunction(action.mFunction)
        } else if (action is EvbCheckLoadAds) {
            isLoadAdsNative = true
            if (mHomeScreen != null) mHomeScreen!!.loadAds()
        }
    }

    private var doubleBackToExitPressedOnce = false

    override fun onBackPressed() {
        if (!Application.sharedManager.dontShowAgain) {
            showRate()
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                finish()
                return
            }

            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show()

            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 1500)
        }
    }

    private fun showExitAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.do_you_really_want_to_exit))
            .setPositiveButton(getString(R.string.yes)) { _, _ -> finish() }
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .show()
    }

    private var dialogRate: Dialog? = null

    fun showRate() {
        dialogRate = Dialog(this).apply {
            setContentView(R.layout.dialog_rate)
            window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCanceledOnTouchOutside(false)
            setOnCancelListener {
                finish()
            }

            findViewById<RatingBar>(R.id.ratingbar).setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                if (rating > 3) {
                    dismiss()
                    UtilsApp.RateApp(this@MainActivity)
                } else {
                    dismiss()
                    showHelp()
                }
            }
            findViewById<TextView>(R.id.dontshowagain).setOnClickListener {
                Application.sharedManager.dontShowAgain = true
                showExitAlertDialog()
                dismiss()
            }
        }
        dialogRate?.show()
    }

    override fun onStart() {
        super.onStart()
        listener?.invoke()
    }

    override fun onResume() {
        super.onResume()
        listener?.invoke()
    }

    private fun showHelp() {
        Dialog(this).apply {
            setContentView(R.layout.dialog_help)
            window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            findViewById<TextView>(R.id.help).setOnClickListener {
                dismiss()
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.support_link))))
            }
        }.show()
    }
}