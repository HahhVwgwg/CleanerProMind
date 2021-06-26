package promind.cleaner.app.core.utils.utilts

import android.os.Environment
import java.io.File

object Config {
    const val DATA = 1000
    const val REPAIR = 2000
    const val UPDATE = 3000
    @JvmField
    var IMAGE_RECOVER_DIRECTORY: String? = null

    init {
        IMAGE_RECOVER_DIRECTORY = Environment.getExternalStorageDirectory().toString() +
                File.separator +
                "RestoredPhotos"
    }
}