package promind.cleaner.app.core.data.database

class AppInfo(t1: String, t2: String, t3: String, t4: Int, t5: String, t6: String, t7: String) {
    var title = ""
    var packageName: String
    var versionName: String
    var versionCode: Int
    var description: String
    var installDir: String
    var installSize: String

    init {
        title = t1
        packageName = t2
        versionName = t3
        versionCode = t4
        installDir = t5
        description = t6
        installSize = t7
    }
}