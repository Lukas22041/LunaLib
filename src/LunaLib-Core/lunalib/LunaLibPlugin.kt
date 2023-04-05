package lunalib

import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags
import lunalib.backend.scripts.KeybindsScript
import lunalib.backend.scripts.LoadedSettings
import lunalib.backend.ui.settings.LunaSettingsLoader
import lunalib.backend.ui.versionchecker.VCModPlugin
import lunalib.lunaDebug.LunaDebug
import lunalib.lunaDebug.snippets.LunaLibDataSnippet
import lunalib.lunaDebug.snippets.ModListSnippet
import lunalib.lunaDebug.snippets.SnippetsListSnippet
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
        var pc = Global.getSettings().modManager.getModSpec("parallel_construction")
        if (pc != null)
        {
            throw Exception("Parallel Construction is not compatible with this version of Lunalib, PC itself is not being updated anymore, but an updated version of PC is included in the mod \"Random Assortment of Things\"")
        }

        if (!LunaSettingsLoader.hasLoaded)
        {
            LunaSettingsLoader.load()
        }

        /*LunaSettings.SettingsCreator.addColor("lunalib", "test", "Test Color", "Test Description", Color(255, 255, 255))
        LunaSettings.SettingsCreator.refresh("lunalib")*/

        LunaDebug.addSnippet(ModListSnippet())
        LunaDebug.addSnippet(LunaLibDataSnippet())
        LunaDebug.addSnippet(SnippetsListSnippet())

        VCModPlugin().onApplicationLoad()
    }
}

