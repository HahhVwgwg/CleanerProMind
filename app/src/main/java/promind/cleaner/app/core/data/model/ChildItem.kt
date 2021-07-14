package promind.cleaner.app.core.data.model

import android.graphics.drawable.Drawable

class ChildItem(
    val packageName: String, val applicationName: String,
    val applicationIcon: Drawable, val cacheSize: Long, var type: Int,
    var path: String?, var isCheck: Boolean
) {

    companion object {
        const val TYPE_APKS = 0
        const val TYPE_CACHE = 1
        const val TYPE_DOWNLOAD_FILE = 2
        const val TYPE_LARGE_FILES = 3
    }

    override fun toString(): String {
        return "ChildItem(packageName='$packageName', applicationName='$applicationName', applicationIcon=$applicationIcon, cacheSize=$cacheSize, type=$type, path=$path, isCheck=$isCheck)"
    }
}