package promind.cleaner.app.core.utils

import promind.cleaner.app.core.app.Application

object Constants {

    val isPremium = Application.sharedManager.premiumAvailable
    val showAds = !isPremium
}