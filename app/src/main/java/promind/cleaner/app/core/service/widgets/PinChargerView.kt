package promind.cleaner.app.core.service.widgets

import android.content.Context
import android.widget.RelativeLayout
import butterknife.BindView
import android.widget.TextView
import android.view.LayoutInflater
import butterknife.ButterKnife
import android.text.TextUtils
import android.os.Build
import android.text.Html
import android.util.AttributeSet
import com.airbnb.lottie.LottieAnimationView
import promind.cleaner.app.R

class PinChargerView : RelativeLayout {
    @JvmField
    @BindView(R.id.ll_anmation_start_2)
    var llAnimationStart2: LottieAnimationView? = null

    @JvmField
    @BindView(R.id.ll_anmation_start_1)
    var llAnimationStart1: LottieAnimationView? = null

    @JvmField
    @BindView(R.id.ll_anmation_done)
    var llAnimationDone: LottieAnimationView? = null

    @JvmField
    @BindView(R.id.tv_content)
    var tvContent: TextView? = null

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    private fun initView() {
        val mView =
            LayoutInflater.from(context).inflate(R.layout.layout_animation_pin_recharger, this)
        ButterKnife.bind(this, mView)
    }

    fun playAnimationDone() {
        llAnimationStart2!!.pauseAnimation()
        llAnimationStart2!!.visibility = INVISIBLE
        llAnimationStart1!!.pauseAnimation()
        llAnimationStart1!!.visibility = INVISIBLE
        llAnimationDone!!.playAnimation()
    }

    fun setContent(txt: String?) {
        if (!TextUtils.isEmpty(txt)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvContent!!.text = Html.fromHtml(txt, Html.FROM_HTML_MODE_COMPACT)
            } else {
                tvContent!!.text = Html.fromHtml(txt)
            }
        }
    }
}