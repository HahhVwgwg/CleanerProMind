package promind.cleaner.app.core.utils.preferences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object PreferenceHelper {

    fun customPrefs(context: Context, name: String): SharedPreferences =
            context.getSharedPreferences(name, Context.MODE_PRIVATE)

    fun defaultPrefs(context: Context): SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }

    operator fun SharedPreferences.set(key: String, value: Any?) {
        when (value) {
            is Int -> edit { it.putInt(key, value) }
            is Long -> edit { it.putLong(key, value) }
            is Float -> edit { it.putFloat(key, value) }
            is String -> edit { it.putString(key, value) }
            is Boolean -> edit { it.putBoolean(key, value) }
            else -> throw UnsupportedOperationException("Not implemented type")
        }
    }

    inline operator fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T): T {
        return when (T::class) {
            Int::class -> getInt(key, defaultValue as Int) as T
            Long::class -> getLong(key, defaultValue as Long) as T
            Float::class -> getFloat(key, defaultValue as Float) as T
            String::class -> getString(key, defaultValue as String) as T
            Boolean::class -> getBoolean(key, defaultValue as Boolean) as T
            else -> throw UnsupportedOperationException("Not implemented type")
        }
    }
}