package promind.cleaner.app.core.data.model

import android.graphics.drawable.Drawable
import android.service.notification.StatusBarNotification
import java.io.Serializable

class NotifiModel : Serializable {
    @JvmField
    var appName: String? = null
    @JvmField
    var iconApp: Drawable? = null
    @JvmField
    var barNotification: StatusBarNotification

    constructor(appName: String?, iconApp: Drawable?, barNotification: StatusBarNotification) {
        this.appName = appName
        this.iconApp = iconApp
        this.barNotification = barNotification
    }

    constructor(barNotification: StatusBarNotification) {
        this.barNotification = barNotification
    }
}