package promind.cleaner.app.core.utils

import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd
import java.io.File

var mInterstitialAd: PublisherInterstitialAd? = null
var listener: (() -> Unit)? = null

fun deleteRecursively(file: File?) {
    file?.let {
        it.deleteRecursively()
    }
}