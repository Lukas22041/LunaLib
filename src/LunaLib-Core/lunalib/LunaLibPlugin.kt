package lunalib

import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator
import lunalib.backend.scripts.KeybindsScript
import lunalib.backend.scripts.LoadedSettings
import lunalib.backend.ui.settings.LunaSettingsConfigLoader
import lunalib.backend.ui.settings.LunaSettingsLoader
import lunalib.backend.ui.testpanel.TestPanel
import lunalib.backend.ui.versionchecker.VCModPlugin
import lunalib.lunaDebug.LunaDebug
import lunalib.lunaDebug.snippets.LunaLibDataSnippet
import lunalib.lunaDebug.snippets.ModListSnippet
import lunalib.lunaDebug.snippets.ReloadSettingsSnippet
import lunalib.lunaDebug.snippets.SnippetsListSnippet
import lunalib.lunaSettings.LunaSettings
import java.lang.Exception


class LunaLibPlugin : BaseModPlugin()
{
    override fun onGameLoad(newGame: Boolean)
    {
        Global.getSector().addTransientScript(KeybindsScript())
    }

    override fun onApplicationLoad()
    {
        LunaSettings.addSettingsListener(LoadedSettings())

        if (!LunaSettingsLoader.hasLoaded)
        {
            LunaSettingsLoader.load()
        }

        /*LunaSettings.SettingsCreator.addColor("lunalib", "test", "Test Color", "Test Description", Color(255, 255, 255))
        LunaSettings.SettingsCreator.refresh("lunalib")*/

        LunaDebug.addSnippet(ModListSnippet())
        LunaDebug.addSnippet(LunaLibDataSnippet())
        LunaDebug.addSnippet(SnippetsListSnippet())
        LunaDebug.addSnippet(ReloadSettingsSnippet())

        VCModPlugin().onApplicationLoad()
    }

    override fun onDevModeF8Reload() {
        super.onDevModeF8Reload()

        LunaSettingsConfigLoader.reload()
        LunaSettingsLoader.loadDefault()
        LunaSettingsLoader.saveDefaultsToFile()
        LunaSettingsLoader.loadSettings()
    }
}

