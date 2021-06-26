package promind.cleaner.app.core.utils.preferences

import android.content.Context
import android.content.SharedPreferences

class MyCache private constructor(context: Context) {

    companion object {
        private var cache: MyCache? = null
        private var preferences: SharedPreferences? = null


        fun init(context: Context) {
            if (cache == null)
                cache = MyCache(context)
        }

        fun getCache(): MyCache = cache!!
    }

    init {
        preferences = context.getSharedPreferences("cache", Context.MODE_PRIVATE)
    }

    fun save(id: Int, time: Long) {
        preferences!!.edit().putLong(id.toString(), time).apply()
    }

    fun get(id: Int): Long = preferences!!.getLong(id.toString(), 0)
}

