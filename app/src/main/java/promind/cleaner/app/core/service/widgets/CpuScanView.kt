package promind.cleaner.app.core.service.widgets

import android.animation.Animator
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.airbnb.lottie.LottieAnimationView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import promind.cleaner.app.R
import promind.cleaner.app.core.service.listener.animation.AnimationListener
import promind.cleaner.app.core.utils.Toolbox

class CpuScanView : RelativeLayout {
    @JvmField
    @BindView(R.id.ll_anmation_scan)
    var llAnimationScan: LottieAnimationView? = null

    @JvmField
    @BindView(R.id.tv_content)
    var tvContent: TextView? = null

    @JvmField
    @BindView(R.id.ll_anmation_done)
    var llAnimationDone: LottieAnimationView? = null

    @JvmField
    @BindView(R.id.ll_main)
    var llMain: View? = null
    private var mContext: Context

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mContext = context
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        mContext = context
        initView()
    }

    private fun initView() {
        val mView = LayoutInflater.from(mContext).inflate(R.layout.layout_anmation_cpu, this)
        ButterKnife.bind(this, mView)
    }

    fun playAnimationStart() {
        llAnimationScan!!.visibility = VISIBLE
        llAnimationScan!!.playAnimation()
//        tvContent!!.visibility = VISIBLE
//        YoYo.with(Techniques.Flash).duration(2000).repeat(1000).playOn(tvContent)
    }

    fun stopAnimationStart() {
        YoYo.with(Techniques.FadeOut).duration(1000).playOn(this)
//        llAnimationScan!!.pauseAnimation()
        Handler().postDelayed({ visibility = GONE }, 1000)
    }

    fun playAnimationDone(mAnimationListener: AnimationListener) {
        visibility = VISIBLE
        Toolbox.animationTransColor(
            resources.getColor(R.color.color_ffa800),
            resources.getColor(R.color.color_4254ff),
            3000,
            llMain
        )
        llAnimationDone!!.visibility = VISIBLE
        llAnimationScan!!.visibility = INVISIBLE
        tvContent!!.visibility = INVISIBLE
        llAnimationDone!!.playAnimation()
        llAnimationDone!!.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                mAnimationListener.onStop()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }
}