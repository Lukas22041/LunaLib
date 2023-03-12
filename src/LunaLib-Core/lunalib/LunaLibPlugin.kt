package lunalib

import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import lunalib.backend.scripts.KeybindsScript
import lunalib.backend.scripts.LoadedSettings
import lunalib.backend.ui.settings.LunaSettingsLoader
import lunalib.lunaDebug.LunaDebug
import lunalib.lunaDebug.snippets.LunaLibDataSnippet
import lunalib.lunaDebug.snippets.ModListSnippet
import me.xdrop.fuzzywuzzy.FuzzySearch
import java.lang.Exception


class LunaLibPlugin : BaseModPlugin()
{
    override fun onGameLoad(newGame: Boolean)
    {
        Global.getSector().addTransientScript(KeybindsScript())
        LoadedSettings()
    }

    override fun onApplicationLoad()
    {
        var rat = Global.getSettings().modManager.getModSpec("assortment_of_things")
        if (rat != null)
        {
            if (rat.version == "1.0.0" || rat.version == "0.3.2")
            {
                throw Exception("Your version of Random-Assortment-of-Things is not compatible with this version of Lunalib, make sure to update it")
            }
        }

        var pc = Global.getSettings().modManager.getModSpec("parallel_construction")
        if (pc != null)
        {
            throw Exception("Parallel Construction is not compatible with this version of Lunalib, PC itself is not being updated anymore, but an updated version of PC is included in the mod \"Random Assortment of Things\"")
        }

        LunaSettingsLoader.loadDefault()
        LunaSettingsLoader.saveDefaultsToFile()
        LunaSettingsLoader.loadSettings()

        /*LunaSettings.SettingsCreator.addColor("lunalib", "test", "Test Color", "Test Description", Color(255, 255, 255))
        LunaSettings.SettingsCreator.refresh("lunalib")*/

        LunaDebug.addSnippet(ModListSnippet())
        LunaDebug.addSnippet(LunaLibDataSnippet())
    }
}

