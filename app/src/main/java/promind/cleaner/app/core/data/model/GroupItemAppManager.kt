package promind.cleaner.app.core.data.model

import android.content.pm.ApplicationInfo
import java.util.ArrayList

class GroupItemAppManager {
    var title: String? = null
    var total = 0
    var type = 0
    var items: List<ApplicationInfo> = ArrayList()

    companion object {
        const val TYPE_USER_APPS = 0
        const val TYPE_SYSTEM_APPS = 1
    }
}