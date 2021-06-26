package promind.cleaner.app.ui.activities.main

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import promind.cleaner.app.R
import promind.cleaner.app.core.adsControl.AdmobHelp
import promind.cleaner.app.core.adsControl.UtilsApp
import promind.cleaner.app.core.app.Application
import promind.cleaner.app.core.service.listener.observerPartener.ObserverInterface
import promind.cleaner.app.core.service.listener.observerPartener.ObserverUtils
import promind.cleaner.app.core.service.listener.observerPartener.eventModel.EvbOnResumeAct
import promind.cleaner.app.core.utils.Config
import promind.cleaner.app.core.utils.Config.FUNCTION
import promind.cleaner.app.core.utils.Constants.showAds
import promind.cleaner.app.core.utils.listener
import promind.cleaner.app.core.utils.preferences.MyCache
import promind.cleaner.app.ui.activities.BaseActivity
import promind.cleaner.app.ui.activities.premium.PremiumActivity
import promind.cleaner.app.ui.adapter.FunctionAdapter
import promind.cleaner.app.ui.adapter.FunctionAdapter.ClickItemListener
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

@SuppressLint("NonConstantResourceId", "UseRequireInsteadOfGet")
class HomeScreen : Fragment(), ClickItemListener, ObserverInterface<Any?> {
    @JvmField
    @BindView(R.id.rcv_home_horizontal)
    var rcvHorizontal: RecyclerView? = null

    @JvmField
    @BindView(R.id.rcv_home_vertical)
    var rcvVertical: RecyclerView? = null

    @JvmField
    @BindView(R.id.premium)
    var premium: ImageView? = null

    @JvmField
    @BindView(R.id.pager)
    var pager: ViewPager? = null

    @JvmField
    @BindView(R.id.adsControlLayout)
    var adsControlLayout: LinearLayout? = null

    lateinit var drawer: ImageView

    lateinit var drawerLayout: DrawerLayout

    lateinit var leftDrawer: LinearLayout

    lateinit var prImage: ImageView

    lateinit var prName: TextView

    lateinit var lastClean: TextView

    lateinit var accountType: TextView

    lateinit var buyPro: TextView

    lateinit var nightMode: TextView

    lateinit var nightModeSwitch: SwitchCompat

    lateinit var share: TextView

    lateinit var rateUs: TextView

    lateinit var update: TextView

    lateinit var support: TextView

    private var mFunctionAdapterHorizontal: FunctionAdapter? = null
    private var mFunctionAdapterVertical: FunctionAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mView = inflater.inflate(R.layout.home_screen, container, false)
        ButterKnife.bind(this, mView)
        ObserverUtils.getInstance().registerObserver(this)
        loadViews(mView)
        initView()
        initData()
        initControl()
        return mView
    }

    private fun loadViews(mView: View) {
        drawer = mView.findViewById(R.id.drawer)
        drawerLayout = mView.findViewById(R.id.drawerLayout)
        leftDrawer = mView.findViewById(R.id.leftDrawer)
        prImage = mView.findViewById(R.id.pr_image)
        prName = mView.findViewById(R.id.pr_name)
        lastClean = mView.findViewById(R.id.last_clean)
        accountType = mView.findViewById(R.id.account_type)
        buyPro = mView.findViewById(R.id.buy_pro)
        if (Application.sharedManager.premiumAvailable) buyPro.text = getString(R.string.share)
        share = mView.findViewById(R.id.share)
        rateUs = mView.findViewById(R.id.rate)
        update = mView.findViewById(R.id.update)
        support = mView.findViewById(R.id.support)
    }

    private fun toggleDrawer() {
        if (drawerLayout.isDrawerOpen(leftDrawer)) {
            drawerLayout.closeDrawer(leftDrawer)
        } else {
            drawerLayout.openDrawer(leftDrawer)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadAds()
    }

    fun loadAds() {
        if (showAds) {
            if ((Objects.requireNonNull(activity) as MainActivity).isLoadAdsNative) {
                AdmobHelp.getInstance().loadNativeFragment(activity, view)
            }
            adsControlLayout!!.visibility = View.GONE
        } else {
            premium!!.visibility = View.GONE
            adsControlLayout!!.visibility = View.GONE
        }
    }

    @SuppressLint("ResourceType", "SetTextI18n")
    private fun initView() {
        rateUs.setOnClickListener {
            (requireActivity() as MainActivity).showRate()
            drawerLayout.closeDrawer(leftDrawer)
        }

        share.setOnClickListener {
            UtilsApp.shareApp(activity)
            drawerLayout.closeDrawer(leftDrawer)
        }

        update.setOnClickListener {
            UtilsApp.RateApp(activity)
            drawerLayout.closeDrawer(leftDrawer)
        }

        support.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.support_link))))
            drawerLayout.closeDrawer(leftDrawer)
        }

        buyPro.setOnClickListener {
            if (Application.sharedManager.premiumAvailable) {
                drawerLayout.closeDrawer(leftDrawer)
                UtilsApp.shareApp(activity)
            } else {
                startActivity(Intent(activity, PremiumActivity::class.java))
                drawerLayout.closeDrawer(leftDrawer)
            }
        }
        reload()
        premium?.setOnClickListener { startActivity(Intent(activity, PremiumActivity::class.java)) }
    }

    private val sdf = SimpleDateFormat("dd.MM.YY")

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun reload() {
        val millis = MyCache.getCache().get(FUNCTION.JUNK_FILES.id)
        if (millis == 0L) lastClean.text =
            "${getString(R.string._24_12_20)} ${sdf.format(System.currentTimeMillis())}"
        else {
            val lastTime: String =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    val formatter = DateTimeFormatter.ofPattern("HH:mm")
                    val date = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(millis),
                        ZoneId.systemDefault()
                    )
                    formatter.format(date)

                } else {
                    val formatter = SimpleDateFormat("HH:mm")
                    val date = Date(millis)
                    formatter.format(date)
                }

            lastClean.text = "${getString(R.string._24_12_20)} $lastTime"
        }
        val arr = arrayListOf<FUNCTION>()
        for (x in Config.LST_SLIDER) {
            if (MyCache.getCache().get(x.id) < System.currentTimeMillis() - TimeUnit.HOURS.toMillis(3)) {
                arr.add(x)
            }
        }
        if (arr.isEmpty()) {
            arr.add(FUNCTION.PREMIUM)
        } else {
            when {
                arr[0] == FUNCTION.JUNK_FILES -> {
                    mFunctionAdapterHorizontal?.setDot(0)
                }
                arr[0] == FUNCTION.PHONE_BOOST -> {
                    mFunctionAdapterHorizontal?.setDot(1)
                }
                arr[0] == FUNCTION.CPU_COOLER -> {
                    mFunctionAdapterHorizontal?.setDot(2)
                }
                arr[0] == FUNCTION.ANTIVIRUS -> {
                    mFunctionAdapterHorizontal?.setDot(3)
                }
                arr[0] == FUNCTION.POWER_SAVING -> {
                    mFunctionAdapterHorizontal?.setDot(4)
                }
                arr[0] == FUNCTION.DEEP_CLEAN -> {
                    mFunctionAdapterHorizontal?.setDot(5)
                }
                else -> {
                    mFunctionAdapterHorizontal?.resetDot()
                }
            }
        }
//        arr.clear()
        pager?.adapter = PagerAdapter(arr.toTypedArray(), childFragmentManager)
        pager?.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                when {
                    arr[position] == FUNCTION.JUNK_FILES -> {
                        mFunctionAdapterHorizontal?.setDot(0)
                    }
                    arr[position] == FUNCTION.PHONE_BOOST -> {
                        mFunctionAdapterHorizontal?.setDot(1)
                    }
                    arr[position] == FUNCTION.CPU_COOLER -> {
                        mFunctionAdapterHorizontal?.setDot(2)
                    }
                    arr[position] == FUNCTION.ANTIVIRUS -> {
                        mFunctionAdapterHorizontal?.setDot(3)
                    }
                    arr[position] == FUNCTION.POWER_SAVING -> {
                        mFunctionAdapterHorizontal?.setDot(4)
                    }
                    arr[position] == FUNCTION.DEEP_CLEAN -> {
                        mFunctionAdapterHorizontal?.setDot(5)
                    }
                    else -> {
                        mFunctionAdapterHorizontal?.resetDot()
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    private fun initData() {
        mFunctionAdapterHorizontal =
            FunctionAdapter(Config.LST_HOME_HORIZONTAL, Config.TYPE_DISPLAY_ADAPTER.HORIZOLTAL)
        rcvHorizontal!!.adapter = mFunctionAdapterHorizontal
        mFunctionAdapterVertical =
            FunctionAdapter(Config.LST_HOME_VERTICAL, Config.TYPE_DISPLAY_ADAPTER.VERTICAL)
        rcvVertical!!.adapter = mFunctionAdapterVertical
        dataRamMemory
    }


    private val dataRamMemory: Unit
        get() {
        }

    private fun initControl() {

        listener = {
            context?.let {
                reload()
            }
        }

        drawer.setOnClickListener {
            toggleDrawer()
        }

        mFunctionAdapterHorizontal!!.setmClickItemListener(this)
        mFunctionAdapterVertical!!.setmClickItemListener(this)
    }

    override fun itemSelected(mFunction: FUNCTION) {
        if (activity != null) (activity as BaseActivity?)!!.openScreenFunction(mFunction)
    }

    override fun notifyAction(action: Any?) {
        if (action is EvbOnResumeAct) {
            dataRamMemory
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ObserverUtils.getInstance().removeObserver(object : ObserverInterface<Any> {
            override fun notifyAction(action: Any) {
                this@HomeScreen.notifyAction(action)
            }
        })
    }

    companion object {
        val instance: HomeScreen
            get() {
                val mFragmentHome = HomeScreen()
                val mBundle = Bundle()
                mFragmentHome.arguments = mBundle
                return mFragmentHome
            }
    }
}