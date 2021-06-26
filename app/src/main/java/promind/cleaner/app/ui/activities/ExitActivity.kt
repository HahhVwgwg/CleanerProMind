package promind.cleaner.app.ui.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

class ExitActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finishAndRemoveTask()
    }

    companion object {
        @JvmStatic
        fun exitApplicationAndRemoveFromRecent(context: Context) {
            val intent = Intent(context, ExitActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_NO_ANIMATION)
            context.startActivity(intent)
        }
    }
}