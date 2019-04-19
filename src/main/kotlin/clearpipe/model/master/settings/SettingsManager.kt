package clearpipe.model.master.settings

import kotlin.reflect.KProperty


interface ISettingsManager {
    var openPath: String
    var savePath: String
}

private var defaultPath = "C:\\"

class SettingsManager(private val preferences: IPreferences)  : ISettingsManager
{
    override var openPath: String by PreferenceStringDelegate("OpenPath", defaultPath)
    override var savePath: String by PreferenceStringDelegate( "SavePath", defaultPath)

    // =============
    // ==== Delegates
    // region Delegates

    private inner class PreferenceStringDelegate( val key: String, val default: String) {
        var field : String? = null

        operator fun getValue(thisRef: SettingsManager, prop: KProperty<*>): String {
            val ret = field ?: preferences.getString(key) ?: default
            field = ret
            return ret
        }

        operator fun setValue(thisRef:SettingsManager, prop: KProperty<*>, value: String) {
            field = value
            preferences.putString(key, value)
        }
    }
    private inner class PreferenceIntDelegate( val key: String, val default: Int) {
        var field : Int? = null

        operator fun getValue(thisRef: SettingsManager, prop: KProperty<*>): Int {
            val ret = field ?: preferences.getInt(key, default)
            field = ret
            return ret
        }

        operator fun setValue(thisRef:SettingsManager, prop: KProperty<*>, value: Int) {
            field = value
            preferences.putInt(key, value)
        }
    }
    private inner class PreferenceBooleanDelegate( val key: String, val default: Boolean) {
        var field : Boolean? = null

        operator fun getValue(thisRef: SettingsManager, prop: KProperty<*>): Boolean {
            val ret = field ?: preferences.getBoolean(key, default)
            field = ret
            return ret
        }

        operator fun setValue(thisRef:SettingsManager, prop: KProperty<*>, value: Boolean) {
            field = value
            preferences.putBoolean(key, value)
        }
    }

    // endregion
}