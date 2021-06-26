package promind.cleaner.app.core.service.winManager

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import promind.cleaner.app.R
import promind.cleaner.app.core.data.model.TaskInfo
import promind.cleaner.app.core.service.listener.animation.AnimationListener
import promind.cleaner.app.core.service.widgets.PowerScanView
import promind.cleaner.app.core.utils.Toolbox

class DeepboostWindowmanager(private val mContext: Context) {
    @JvmField
    @BindView(R.id.powerScanView)
    var mPowerScanView: PowerScanView? = null

    @JvmField
    @BindView(R.id.ll_toolbar)
    var llToolbar: LinearLayout? = null
    private val mWindowManager: WindowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var mView: View? = null
    private fun setupLayout(): WindowManager.LayoutParams {
        val mLayoutParams: WindowManager.LayoutParams
        val flag = (WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        val type =
            if (Toolbox.isOreo()) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
        val height = Toolbox.getHeightScreen(mContext) + Toolbox.getHeightStatusBar(
            mContext,
            true
        ) + Toolbox.getHeightNavigationbar(
            mContext
        ) + 100
        mLayoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            height,
            type,
            flag,
            PixelFormat.TRANSLUCENT
        )
        mLayoutParams.gravity = Gravity.TOP
        return mLayoutParams
    }

    @SuppressLint("InflateParams")
    private fun initView() {
        val layoutInflater = LayoutInflater.from(mContext)
        mView = layoutInflater.inflate(R.layout.layout_window_deepboost, null)
        ButterKnife.bind(this, mView!!)
        llToolbar!!.setPadding(0, Toolbox.getHeightStatusBar(mContext, true), 0, 0)
    }

    fun startAnimation(lstApp: List<TaskInfo?>?) {
        mWindowManager.addView(mView, setupLayout())
        mPowerScanView!!.alpha = 1.0f
        mPowerScanView!!.visibility = View.VISIBLE
        mPowerScanView!!.playAnimationDone(lstApp, TIME_ONE_DEEP_CLEAN,object :AnimationListener{
            override fun onStop() {
                YoYo.with(Techniques.FadeOut).duration(1000).playOn(mPowerScanView)
                Handler().postDelayed({
                    if (mPowerScanView != null) mPowerScanView!!.visibility = View.GONE
                    try {
                        mWindowManager.removeView(mView)
                    } catch (e: Exception) {
                    }
                }, 1000)
            }
        })
    }

    companion object {
        @JvmField
        var TIME_ONE_DEEP_CLEAN: Long = 1000
    }

    init {
        initView()
    }
}