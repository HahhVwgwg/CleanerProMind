package promind.cleaner.app.ui.activities.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd
import promind.cleaner.app.BuildConfig
import promind.cleaner.app.R
import promind.cleaner.app.core.adsControl.AdmobHelp
import promind.cleaner.app.core.utils.Constants.showAds
import promind.cleaner.app.core.utils.mInterstitialAd
import promind.cleaner.app.ui.activities.BaseActivity
import promind.cleaner.app.ui.activities.main.MainActivity
import java.util.*

class SplashActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.tv_content)
    var tvContent: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initAds()
        ButterKnife.bind(this)
        initView()
    }

    private fun initView() {
        YoYo.with(Techniques.SlideInUp).duration(2000).playOn(tvContent)
        Handler().postDelayed({ open() }, 4000)
    }

    private fun open() {
        val mIntent = Intent(this@SplashActivity, MainActivity::class.java)
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(mIntent)
        finish()
    }

    private fun initAds() {
        if (showAds) AdmobHelp.getInstance().init(this)
        mInterstitialAd = PublisherInterstitialAd(this)
        Objects.requireNonNull(mInterstitialAd)!!.adUnitId =
            if (BuildConfig.DEBUG) this.getString(R.string.admob_full_debug) else this.getString(
                R.string.admob_full
            )
        requestNewInterstitial()
    }

    private fun requestNewInterstitial() {
        val adRequest = PublisherAdRequest.Builder().build()
        Objects.requireNonNull(mInterstitialAd)!!.loadAd(adRequest)
    }
}