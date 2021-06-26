package promind.cleaner.app.ui.activities.premium

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_premium.*
import promind.cleaner.app.R
import promind.cleaner.app.ui.adapter.FunctionAdapter
import promind.cleaner.app.ui.activities.BaseActivity
import promind.cleaner.app.core.utils.Config

class PremiumActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium)

        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        window.statusBarColor = Color.TRANSPARENT

        rcv_home_vertical.adapter = FunctionAdapter(Config.LST_PREMIUM, Config.TYPE_DISPLAY_ADAPTER.VERTICAL, false)

        close.setOnClickListener { finish() }

        premium.setOnClickListener {
            // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
            processBilling()
        }
    }

    override fun refreshDataAfterPurchase() {
        finish()
    }

    override fun onBackPressed() {
        finish()
    }
}

fun loge(tag: String = "RRR", message: String) {
    Log.e(tag, message)
}

fun FragmentActivity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun FragmentActivity.toast(message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun loge(message: String) {
    Log.e("RRR", message)
}