package promind.cleaner.app.core.service.widgets

import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RelativeLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.airbnb.lottie.LottieAnimationView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.makeramen.roundedimageview.RoundedImageView
import promind.cleaner.app.R
import promind.cleaner.app.core.data.model.ChildItem
import promind.cleaner.app.core.service.listener.animation.AnimationListener

class CleanJunkFileView : RelativeLayout {
    @JvmField
    @BindView(R.id.av_clean_file)
    var avCleanJunk: LottieAnimationView? = null

    @JvmField
    @BindView(R.id.ll_main)
    var llMain: View? = null

    @JvmField
    @BindView(R.id.im_iconApp)
    var imIconApp: RoundedImageView? = null

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    private fun initView() {
        val mView = LayoutInflater.from(context).inflate(R.layout.layout_animation_junk_clean, this)
        ButterKnife.bind(this, mView)
    }

    fun startAnimationClean(
        lstItem: List<ChildItem>,
        timeOneItem: Int,
        mAnimationListener: AnimationListener
    ) {
        visibility = VISIBLE
        YoYo.with(Techniques.FadeIn).duration(1000).playOn(this)
        //        avCleanJunk.setMaxFrame(40);
//        avCleanJunk.setMinFrame(30);
        avCleanJunk!!.playAnimation()
        startProgress(lstItem, timeOneItem.toLong(), mAnimationListener)
    }

    private var isFinished = false
    fun startProgress(
        lstItem: List<ChildItem>,
        timeOneItem: Long,
        mAnimationListener: AnimationListener
    ) {
        isFinished = false
        val ofInt = ValueAnimator.ofInt(0, lstItem.size - 1)
        ofInt.duration = lstItem.size * timeOneItem
        ofInt.interpolator = AccelerateDecelerateInterpolator()
        ofInt.addUpdateListener { animation: ValueAnimator ->
            val position = animation.animatedValue.toString().toInt()
            val mChildItem = lstItem[position]
            when (mChildItem.type) {
                ChildItem.TYPE_APKS -> {
                    imIconApp!!.setImageResource(R.drawable.ic_android_white_24dp)
                }
                ChildItem.TYPE_CACHE -> {
                    imIconApp!!.setImageDrawable(mChildItem.applicationIcon)
                }
                ChildItem.TYPE_DOWNLOAD_FILE -> {
                    imIconApp!!.setImageResource(R.drawable.ic_file_download_white_24dp)
                }
                ChildItem.TYPE_LARGE_FILES -> {
                    imIconApp!!.setImageResource(R.drawable.ic_description_white_24dp)
                }
            }
            if (position == lstItem.size - 1 && !isFinished) {
                isFinished = true
                Handler().postDelayed({
                    ofInt.cancel()
                    YoYo.with(Techniques.FadeOut).duration(1000).playOn(this)
                    mAnimationListener.onStop()
                }, 3000)
            }
        }
        ofInt.start()
    }
}