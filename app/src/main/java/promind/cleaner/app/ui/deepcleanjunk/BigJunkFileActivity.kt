package promind.cleaner.app.ui.deepcleanjunk

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.IPackageStatsObserver
import android.graphics.PorterDuff
import android.os.*
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.airbnb.lottie.LottieAnimationView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_junkfile.*
import promind.cleaner.app.R
import promind.cleaner.app.core.adsControl.AdmobHelp
import promind.cleaner.app.core.data.data.TaskJunkClean
import promind.cleaner.app.core.data.model.ChildItem
import promind.cleaner.app.core.data.model.GroupItem
import promind.cleaner.app.core.service.listener.animation.AnimationListener
import promind.cleaner.app.core.service.widgets.AnimatedExpandableListView
import promind.cleaner.app.core.service.widgets.CleanJunkFileView
import promind.cleaner.app.core.utils.Config
import promind.cleaner.app.core.utils.Constants.showAds
import promind.cleaner.app.core.utils.PreferenceUtils
import promind.cleaner.app.core.utils.SystemUtil
import promind.cleaner.app.core.utils.Toolbox
import promind.cleaner.app.core.utils.preferences.MyCache
import promind.cleaner.app.ui.activities.BaseActivity
import promind.cleaner.app.ui.activities.premium.loge
import promind.cleaner.app.ui.adapter.CleanAdapter
import promind.cleaner.app.ui.adapter.ViewPagerAdapter
import promind.cleaner.app.ui.dialog.DialogConfirmCancel
import java.io.File
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.util.*


@SuppressLint("NonConstantResourceId")
class BigJunkFileActivity : BaseActivity() {

    private lateinit var adapter: ViewPagerAdapter

    @JvmField
    @BindView(R.id.recyclerView)
    var mRecyclerView: AnimatedExpandableListView? = null

    @JvmField
    @BindView(R.id.tvTotalCache)
    var mTvTotalCache: TextView? = null

    @JvmField
    @BindView(R.id.tvType)
    var mTvType: TextView? = null

    @JvmField
    @BindView(R.id.tvNoJunk)
    var mTvNoJunk: TextView? = null

    @JvmField
    @BindView(R.id.tabs)
    var tabLayout: TabLayout? = null

    @JvmField
    @BindView(R.id.viewPager)
    var viewPager: ViewPager? = null

    @JvmField
    @BindView(R.id.btnCleanUp)
    var mBtnCleanUp: Button? = null

    @JvmField
    @BindView(R.id.viewLoading)
    var mViewLoading: View? = null

    @JvmField
    @BindView(R.id.tv_pkg_name)
    var tvPkgName: TextView? = null

    @JvmField
    @BindView(R.id.tv_boost)
    var tvBoost: TextView? = null

    @JvmField
    @BindView(R.id.av_progress)
    var avProgress: LottieAnimationView? = null

    @JvmField
    @BindView(R.id.tv_calculating)
    var tvCalculating: TextView? = null

    @JvmField
    @BindView(R.id.tv_toolbar)
    var tvToolbar: TextView? = null

    @JvmField
    @BindView(R.id.im_back_toolbar)
    var imBack: ImageView? = null

    @JvmField
    @BindView(R.id.cleanJunkFileView)
    var mCleanJunkFileView: CleanJunkFileView? = null
    private var mTotalJunk: Long = 0
    private val mFileListLarge = ArrayList<File>()
    private val mGroupItems = ArrayList<GroupItem>()
    private var mAdapter: CleanAdapter? = null
    private var flagExit = false
    private var alreadyShown: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bigjunkfile)
        ButterKnife.bind(this)
        initView()
        initData()
    }

    override fun onBackPressed() {
        if (flagExit) {
            super.onBackPressed()
        }
    }

    private fun initTimerForAds() {
        tvBoost?.visibility = View.VISIBLE
        object : CountDownTimer(3000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                tvBoost?.text =
                    String.format(applicationContext.getString(R.string.show_ads_format),
                        millisUntilFinished / 1000 + 1)
            }

            override fun onFinish() {
                tvBoost?.visibility = View.GONE
                if (showAds) AdmobHelp.getInstance().showInterstitialAdWithoutWaiting {
                    Log.e("Reklama ", " is turned of")
                    finishAnimationDone()
                }
            }
        }.start()
    }

    private fun finishAnimationDone() {
        mCleanJunkFileView!!.closeAnimation(lstChildItems, object : AnimationListener {
            override fun onStop() {
                for (i in lstChildItems.indices) {
                    if (i == lstChildItems.size - 1) {
                        openScreenResult(Config.FUNCTION.JUNK_FILES)
                        finish()
                        return
                    }
                    val mChildItem = lstChildItems[i]
                    if (mChildItem.type == ChildItem.TYPE_CACHE) {
                        TaskJunkClean(
                            this@BigJunkFileActivity,
                            mChildItem.packageName
                        ) {
                            run {
                                loge("type cache")
                                val mPackageManager = packageManager
                                try {
                                    val mGetfreeStorage = packageManager.javaClass.getMethod(
                                        "freeStorage",
                                        String::class.java,
                                        IPackageStatsObserver::class.java
                                    )
                                    val desiredFreeStorage =
                                        (8 * 1024 * 1024 * 1024).toLong() // Request for 8GB of free space
                                    loge("invoke")
                                    mGetfreeStorage.invoke(
                                        mPackageManager,
                                        mChildItem.packageName,
                                        desiredFreeStorage,
                                        null
                                    )
                                } catch (e: NoSuchMethodException) {
                                    loge("No such method ${e.localizedMessage}")
                                    e.printStackTrace()
                                } catch (e: IllegalAccessException) {
                                    loge("Illegal Access ${e.localizedMessage}")
                                    e.printStackTrace()
                                } catch (e: InvocationTargetException) {
                                    loge("Invocation Target ${e.localizedMessage}")
                                    e.printStackTrace()
                                }
                                PreferenceUtils.setTimePkgCleanCache(mChildItem.packageName)
                            }
                        }.execute()

                    } else {
                        val file = File(mChildItem.path)
                        file.delete()
                        loge("start delete")
                        if (file.exists()) {
                            try {
                                file.canonicalFile.delete()
                                if (file.exists()) {
                                    loge("delete")
                                    loge("____________________________________")
                                    deleteFile(file.name)
                                }
                            } catch (e: IOException) {
                                loge("Error Download: " + e.localizedMessage)
                            }
                        }
                    }
                }
            }
        })

    }


    private fun initView() {
        imBack!!.visibility = View.VISIBLE
        imBack!!.setOnClickListener {
            val dialogConfirmCancel = DialogConfirmCancel(this, Config.FUNCTION.JUNK_FILES.id)
            dialogConfirmCancel.listener = object : DialogConfirmCancel.OnClickListener {
                override fun onDo() {
//                    clean()
                }

                override fun onCancel() {
                    finish()
                }
            }
            dialogConfirmCancel.show()
        }
        tvBoost?.setOnClickListener {
            if (showAds && !alreadyShown) AdmobHelp.getInstance().showInterstitialAdWithoutWaiting {
                Log.e("Reklama ", " is turned of")
                finishAnimationDone()
            }
            alreadyShown = true
        }
        tvToolbar!!.text = "" //getString(Config.FUNCTION.JUNK_FILES.title)
        YoYo.with(Techniques.Flash).duration(2000).repeat(1000).playOn(tvCalculating)
    }

    lateinit var lstChildItems: MutableList<ChildItem>
    private fun clean() {
        initTimerForAds()
        lstChildItems = ArrayList()
        for (mGroupItem in mGroupItems) {
            for (mChildItem in mGroupItem.items) {
                if (mChildItem.isCheck) lstChildItems.add(mChildItem)
            }
        }
        if (lstChildItems.isNotEmpty()) {
            cleanUp(lstChildItems)
            MyCache.getCache().save(1, System.currentTimeMillis())
        } else Toast.makeText(
            this@BigJunkFileActivity,
            getString(R.string.selcet_file_to_clean),
            Toast.LENGTH_LONG
        ).show()
    }

    private val imageResId = intArrayOf(
        R.drawable.ic_one,
        R.drawable.ic_two,
        R.drawable.ic_three)

    private fun initData() {
        tabLayout!!.addTab(tabLayout!!.newTab())
        tabLayout?.addTab(tabLayout!!.newTab())
        tabLayout?.addTab(tabLayout!!.newTab())
        tabLayout?.tabGravity = TabLayout.GRAVITY_FILL
        adapter = ViewPagerAdapter(supportFragmentManager)
        viewPager?.adapter = adapter
        tabLayout?.setupWithViewPager(viewPager)
        for (i in imageResId.indices) {
            tabLayout!!.getTabAt(i)!!.setIcon(imageResId[i])
        }
        val tabIconColor =
            ContextCompat.getColor(applicationContext, R.color.color_b26ff7)
        val tabIconColorGrey =
            ContextCompat.getColor(applicationContext, R.color.grey)
        tabLayout!!.getTabAt(0)?.icon?.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        tabLayout!!.setOnTabSelectedListener(
            object : TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    super.onTabSelected(tab)
                    tab.icon?.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    super.onTabUnselected(tab)
                    tab.icon?.setColorFilter(tabIconColorGrey, PorterDuff.Mode.SRC_IN)
                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                    super.onTabReselected(tab)
                }
            }
        )

        mAdapter = CleanAdapter(this, mGroupItems, object : CleanAdapter.OnGroupClickListener {
            override fun onGroupClick(groupPosition: Int) {
                if (mRecyclerView!!.isGroupExpanded(groupPosition)) {
                    mRecyclerView!!.collapseGroupWithAnimation(groupPosition)
                } else {
                    mRecyclerView!!.expandGroupWithAnimation(groupPosition)
                }
            }

            override fun onSelectItemHeader(position: Int, isCheck: Boolean) {
                changeCleanFileHeader(position, isCheck)
            }

            override fun onSelectItem(groupPosition: Int, childPosition: Int, isCheck: Boolean) {
                changeCleanFileItem(groupPosition, childPosition, isCheck)
            }
        })
        mRecyclerView!!.setAdapter(mAdapter)
        if (mGroupItems.size == 0) {
            Toolbox.setTextFromSize(0, mTvTotalCache, mTvType, btnCleanUp)
            mViewLoading!!.visibility = View.VISIBLE
            startImageLoading()
            largeFile
        } else {
            mAdapter!!.notifyDataSetChanged()
        }
    }

    private fun changeCleanFileHeader(position: Int, isCheck: Boolean) {
        val total = mGroupItems[position].total
        if (isCheck) {
            mTotalJunk += total
        } else {
            mTotalJunk -= total
        }
        mGroupItems[position].setIsCheck(isCheck)
        for (childItem in mGroupItems[position].items) {
            childItem.isCheck = isCheck
        }
        mAdapter!!.notifyDataSetChanged()
        updateSizeTotal()
    }

    private fun changeCleanFileItem(groupPosition: Int, childPosition: Int, isCheck: Boolean) {
        val total = mGroupItems[groupPosition].items[childPosition].cacheSize
        if (isCheck) {
            mTotalJunk += total
        } else {
            mTotalJunk -= total
        }
        mGroupItems[groupPosition].items[childPosition].isCheck = isCheck
        var isCheckItem = false
        for (childItem in mGroupItems[groupPosition].items) {
            isCheckItem = childItem.isCheck
            if (!isCheckItem) {
                break
            }
        }
        mGroupItems[groupPosition].setIsCheck(isCheckItem)
        mAdapter!!.notifyDataSetChanged()
        updateSizeTotal()
    }

    private fun updateSizeTotal() {
        mTotalJunk = 0
        for (mGroupItem in mGroupItems) {
            for (mChildItem in mGroupItem.items) {
                if (mChildItem.isCheck) mTotalJunk += mChildItem.cacheSize
            }
        }
        Toolbox.setTextFromSize(mTotalJunk, mTvTotalCache, mTvType, btnCleanUp)
    }

    private fun startImageLoading() {

    }

    private val largeFile: Unit
        get() {
            ScanLargeFiles(object : OnScanLargeFilesListener {
                override fun onScanCompleted(result: List<File>?) {
                    loge(result?.size.toString() ?: "null")
                    if (result != null && result.isNotEmpty()) {
                        val groupItem = GroupItem()
                        groupItem.title = getString(R.string.large_files)
                        groupItem.setIsCheck(false)
                        groupItem.type = GroupItem.TYPE_FILE
                        val childItems: MutableList<ChildItem> = ArrayList()
                        var size: Long = 0
                        for (currentFile in result) {
                            val childItem = ContextCompat.getDrawable(
                                this@BigJunkFileActivity,
                                R.drawable.ic_android_white_24dp
                            )?.let {
                                ChildItem(
                                    currentFile.name,
                                    currentFile.name, it,
                                    currentFile.length(), ChildItem.TYPE_LARGE_FILES,
                                    currentFile.path, false
                                )
                            }
                            childItem?.let { childItems.add(it) }
                            size += currentFile.length()
                        }
                        groupItem.items = childItems
                        groupItem.total = size
                        mGroupItems.add(groupItem)
                    }

                    updateAdapter()
                }
            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }

    private fun updateAdapter() {
        adapter.setData(mGroupItems)
        updateSizeTotal()
        if (mGroupItems.size != 0) {
            for (i in mGroupItems.indices) {
                if (mRecyclerView!!.isGroupExpanded(i)) {
                    mRecyclerView!!.collapseGroupWithAnimation(i)
                } else {
                    mRecyclerView!!.expandGroupWithAnimation(i)
                }
            }
            YoYo.with(Techniques.FadeInUp).duration(1000).playOn(mRecyclerView)
            mTvNoJunk!!.visibility = View.GONE
            mBtnCleanUp!!.visibility = View.VISIBLE
            mAdapter!!.notifyDataSetChanged()
        } else {
            mRecyclerView!!.visibility = View.GONE
            mBtnCleanUp!!.visibility = View.GONE
            mTvNoJunk!!.visibility = View.VISIBLE
            Toast.makeText(this, getString(R.string.no_junk), Toast.LENGTH_LONG).show()
            openScreenResult(Config.FUNCTION.JUNK_FILES)
            finish()
        }
        tvCalculating!!.visibility = View.GONE
        mViewLoading!!.visibility = View.GONE
        avProgress!!.pauseAnimation()
        avProgress!!.visibility = View.INVISIBLE
        YoYo.with(Techniques.FadeIn).duration(1000).playOn(mTvTotalCache)
        YoYo.with(Techniques.FadeIn).duration(1000).playOn(mTvType)
        YoYo.with(Techniques.FadeOut).duration(1000).playOn(tvPkgName)
        flagExit = true
    }

    interface OnScanLargeFilesListener {
        fun onScanCompleted(result: List<File>?)
    }

    @OnClick(R.id.btnCleanUp)
    fun clickClean() {
        clean()
    }

    fun cleanUp(listItem: List<ChildItem>) {
        mCleanJunkFileView!!.startAnimationCleanHalf(listItem, object : AnimationListener {
            override fun onStop() {
//                finishAnimationDone()
            }
        })
    }

    private inner class ScanLargeFiles(private val mOnScanLargeFilesListener: OnScanLargeFilesListener?) :
        AsyncTask<Void?, String?, List<File>>() {
        override fun onProgressUpdate(vararg values: String?) {
            super.onProgressUpdate(*values)
            if (!TextUtils.isEmpty(values[0])) tvPkgName!!.text = values[0]
        }

        override fun doInBackground(vararg params: Void?): List<File> {
            val root = File(
                Environment.getExternalStorageDirectory()
                    .absolutePath
            )
            return getfile(root, true)
        }

        override fun onPostExecute(result: List<File>) {
            mOnScanLargeFilesListener?.onScanCompleted(result)
        }

        fun getfile(dir: File, isSleep: Boolean): ArrayList<File> {
            val listFile = dir.listFiles()
            if (listFile != null && listFile.isNotEmpty()) {
                for (aListFile in listFile) {
                    publishProgress(aListFile.path)
                    if (aListFile.isDirectory && aListFile.name != Environment.DIRECTORY_DOWNLOADS) {
                        getfile(aListFile, false)
                    } else {
                        val fileSizeInBytes = aListFile.length()
                        val fileSizeInKB = fileSizeInBytes / 1024
                        val fileSizeInMB = fileSizeInKB / 1024
                        if (fileSizeInMB >= 10 && !aListFile.name.endsWith(".apk")) {
                            mTotalJunk += aListFile.length()
                            mFileListLarge.add(aListFile)
                        }
                    }
                    if (isSleep) try {
                        Thread.sleep(200)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
            return mFileListLarge
        }
    }

    companion object {
        fun startActivityWithData(mContext: Context) {
            val mIntent = Intent(mContext, BigJunkFileActivity::class.java)
            mContext.startActivity(mIntent)
        }
    }
}

fun View.gone(): View {
    this.visibility = View.GONE
    return this
}

fun View.show(): View {
    this.visibility = View.VISIBLE
    return this
}


fun View.showGone(show: Boolean): View {
    return if (show) this.show() else this.gone()
}