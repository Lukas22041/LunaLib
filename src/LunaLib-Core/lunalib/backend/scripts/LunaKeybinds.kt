package lunalib.backend.scripts

import com.fs.starfarer.api.Global
import lunalib.lunaSettings.LunaSettings
import lunalib.lunaSettings.LunaSettingsListener

class LunaKeybinds : LunaSettingsListener
{
    companion object {
        var settingsKeybind = LunaSettings.getInt("lunalib", "luna_SettingsKeybind")
    }

    init {
        Global.getSector().listenerManager.addListener(this, true)
    }

    override fun settingsChanged(modID: String) {
        settingsKeybind = LunaSettings.getInt("lunalib", "luna_SettingsKeybind")
    }
}