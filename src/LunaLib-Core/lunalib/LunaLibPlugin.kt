package lunalib

import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import lunalib.backend.scripts.LoadedSettings
import lunalib.backend.scripts.KeybindsScript
import lunalib.backend.ui.settings.LunaSettingsLoader
import lunalib.lunaDebug.LunaDebug
import lunalib.lunaDebug.snippets.ExampleSnippet
import lunalib.lunaDebug.snippets.ModsListSnippet
import lunalib.lunaSettings.LunaSettings
import java.awt.Color


class LunaLibPlugin : BaseModPlugin()
{
    override fun onGameLoad(newGame: Boolean)
    {
        Global.getSector().addTransientScript(KeybindsScript())
        LoadedSettings()
    }

    override fun onApplicationLoad()
    {
        LunaSettingsLoader.loadDefault()
        LunaSettingsLoader.saveDefaultsToFile()
        LunaSettingsLoader.loadSettings()

        /*LunaSettings.SettingsCreator.addColor("lunalib", "test", "Test Color", "Test Description", Color(255, 255, 255))
        LunaSettings.SettingsCreator.refresh("lunalib")*/

        LunaDebug.addSnippet(ExampleSnippet())
        LunaDebug.addSnippet(ModsListSnippet())
    }
}


