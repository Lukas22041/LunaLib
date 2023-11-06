package lunalib

import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import lunalib.backend.scripts.KeybindsScript
import lunalib.backend.scripts.LoadedSettings
import lunalib.backend.ui.refit.RefitButtonAdder
import lunalib.backend.ui.settings.LunaSettingsConfigLoader
import lunalib.backend.ui.settings.LunaSettingsLoader
import lunalib.backend.ui.versionchecker.VCModPlugin
import lunalib.lunaDebug.LunaDebug
import lunalib.lunaDebug.snippets.LunaLibDataSnippet
import lunalib.lunaDebug.snippets.ModListSnippet
import lunalib.lunaDebug.snippets.ReloadSettingsSnippet
import lunalib.lunaDebug.snippets.SnippetsListSnippet
import lunalib.lunaRefit.RefitEffectButtonExample
import lunalib.lunaRefit.LunaRefitManager
import lunalib.lunaRefit.RefitPanelButtonExample
import lunalib.lunaSettings.LunaSettings
import lunalib.lunaUtil.LunaCommons
import org.lazywizard.lazylib.MathUtils


class LunaLibPlugin : BaseModPlugin()
{
    override fun onGameLoad(newGame: Boolean)
    {
        Global.getSector().addTransientScript(RefitButtonAdder())
        Global.getSector().addTransientScript(KeybindsScript())
    }

    override fun onApplicationLoad()
    {

        LunaRefitManager.addRefitButton(RefitEffectButtonExample())
        LunaRefitManager.addRefitButton(RefitPanelButtonExample())

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

        LunaCommons.init()
    }

    override fun onDevModeF8Reload() {
        super.onDevModeF8Reload()

        LunaSettingsConfigLoader.reload()
        LunaSettingsLoader.loadDefault()
        LunaSettingsLoader.saveDefaultsToFile()
        LunaSettingsLoader.loadSettings()
    }
}

