package lunalib

import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import lunalib.backend.scripts.KeybindsScript
import lunalib.backend.ui.settings.LunaSettingsLoader


class LunaLibPlugin : BaseModPlugin()
{
    override fun onGameLoad(newGame: Boolean)
    {
        Global.getSector().addTransientScript(KeybindsScript())
    }

    override fun onApplicationLoad()
    {
        LunaSettingsLoader.loadDefault()
        LunaSettingsLoader.saveDefaultsToFile()
        LunaSettingsLoader.loadSettings()
    }
}


