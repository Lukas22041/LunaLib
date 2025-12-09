package lunalib.backend.scripts

import com.fs.starfarer.api.Global
import lunalib.lunaSettings.LunaSettings
import lunalib.lunaSettings.LunaSettingsListener

class LoadedSettings : LunaSettingsListener
{
    companion object {
        var settingsKeybind = LunaSettings.getInt("lunalib", "luna_SettingsKeybind_new")
        var debugKeybind = LunaSettings.getInt("lunalib", "luna_DebugKeybind_new")

        var devmodeKeybind = LunaSettings.getInt("lunalib", "luna_DevmodeKeybind")
        var debugEntryCap = LunaSettings.getInt("lunalib", "luna_DebugEntries")
        var uidebugKeybind = LunaSettings.getInt("lunalib", "luna_UIDebugKeybind")

        //Only gets updated on game start
        @JvmStatic
        var enableVersionChecker = LunaSettings.getBoolean("lunalib", "luna_enableVC")

    }

    override fun settingsChanged(modID: String) {
        if (modID == "lunalib")
        {
            settingsKeybind = LunaSettings.getInt("lunalib", "luna_SettingsKeybind_new")
            debugKeybind = LunaSettings.getInt("lunalib", "luna_SettingsKeybind_new")
            devmodeKeybind = LunaSettings.getInt("lunalib", "luna_DevmodeKeybind")
            debugEntryCap = LunaSettings.getInt("lunalib", "luna_DebugEntries")
            uidebugKeybind = LunaSettings.getInt("lunalib", "luna_UIDebugKeybind")

        }
    }
}