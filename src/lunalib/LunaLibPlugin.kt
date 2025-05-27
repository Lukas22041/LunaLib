package lunalib

import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.impl.codex.CodexDataV2
import com.fs.starfarer.api.impl.codex.CodexEntryV2
import lunalib.backend.scripts.KeybindsScript
import lunalib.backend.scripts.LoadedSettings
import lunalib.backend.ui.refit.RefitButtonAdder
import lunalib.backend.ui.settings.LunaSettingsConfigLoader
import lunalib.backend.ui.settings.LunaSettingsLoader
import lunalib.backend.ui.versionchecker.VCModPlugin
import lunalib.lunaCodex.LunaCodex
import lunalib.lunaDebug.LunaDebug
import lunalib.lunaDebug.snippets.LunaLibDataSnippet
import lunalib.lunaDebug.snippets.ModListSnippet
import lunalib.lunaDebug.snippets.ReloadSettingsSnippet
import lunalib.lunaDebug.snippets.SnippetsListSnippet
import lunalib.lunaRefit.LunaRefitManager
import lunalib.lunaRefit.RefitEffectButtonExample
import lunalib.lunaRefit.RefitPanelButtonExample
import lunalib.lunaSettings.LunaSettings
import lunalib.lunaTitle.LunaTitleRecordingScipt
import lunalib.lunaTitle.TitleSpecLoader
import lunalib.lunaUtil.LunaCommons
import lunalib.lunaUtil.campaign.LunaCampaignRenderer


class LunaLibPlugin : BaseModPlugin()
{

    override fun onAboutToStartGeneratingCodex() {
        super.onAboutToStartGeneratingCodex()

        var path = "graphics/icons/lunalib_ModsCategory.png"
        Global.getSettings().loadTexture(path)
        val cat = CodexEntryV2(LunaCodex.CODEX_CAT_MODS, "Mods", path)

        CodexDataV2.ROOT.addChild(cat)
    }

    override fun onGameLoad(newGame: Boolean)
    {
        Global.getSector().addTransientScript(LunaTitleRecordingScipt())
        Global.getSector().addTransientScript(RefitButtonAdder())
        Global.getSector().addTransientScript(KeybindsScript())

        var script = LunaCampaignRenderer.getScript()
        if (!Global.getSector().hasScript(script::class.java)) {
            Global.getSector().addScript(script)
        }
    }

    override fun onApplicationLoad()
    {

        TitleSpecLoader.loadTitlesFromCSV()

        LunaRefitManager.addRefitButton(lunalib.lunaRefit.RefitEffectButtonExample())
        LunaRefitManager.addRefitButton(lunalib.lunaRefit.RefitPanelButtonExample())

        LunaSettings.addSettingsListener(LoadedSettings())

        if (!LunaSettingsLoader.hasLoaded)
        {
            LunaSettingsLoader.load()
        }

        /*LunaSettings.SettingsCreator.addColor("lunalib", "test", "Test Color", "Test Description", Color(255, 255, 255))
        LunaSettings.SettingsCreator.refresh("lunalib")*/

        LunaDebug.addSnippet(lunalib.lunaDebug.snippets.ModListSnippet())
        LunaDebug.addSnippet(LunaLibDataSnippet())
        LunaDebug.addSnippet(SnippetsListSnippet())
        LunaDebug.addSnippet(ReloadSettingsSnippet())

        lunalib.backend.ui.versionchecker.VCModPlugin().onApplicationLoad()

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

