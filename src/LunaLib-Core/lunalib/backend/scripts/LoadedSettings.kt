package lunalib.backend.scripts

import com.fs.starfarer.api.Global
import lunalib.lunaSettings.LunaSettings
import lunalib.lunaSettings.LunaSettingsListener

class LoadedSettings : LunaSettingsListener
{
    companion object {
        var settingsKeybind = LunaSettings.getInt("lunalib", "luna_SettingsKeybind")
        var debugKeybind = LunaSettings.getInt("lunalib", "luna_DebugKeybind")

        var devmodeKeybind = LunaSettings.getInt("lunalib", "luna_DevmodeKeybind")
        var debugEntryCap = LunaSettings.getInt("lunalib", "luna_DebugEntries")
        var uidebugKeybind = LunaSettings.getInt("lunalib", "luna_UIDebugKeybind")
    }

    override fun settingsChanged(modID: String) {
        if (modID == "lunalib")
        {
            settingsKeybind = LunaSettings.getInt("lunalib", "luna_SettingsKeybind")
            debugKeybind = LunaSettings.getInt("lunalib", "luna_DebugKeybind")
            devmodeKeybind = LunaSettings.getInt("lunalib", "luna_DevmodeKeybind")
            debugEntryCap = LunaSettings.getInt("lunalib", "luna_DebugEntries")
            uidebugKeybind = LunaSettings.getInt("lunalib", "luna_UIDebugKeybind")

        }
    }
}