import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import lunalib.lunaExtensions.*
import lunalib.lunaSettings.LunaSettingsHotkeyListener
import lunalib.lunaSettings.LunaSettingsLoader
import lunalib.lunaUtil.LunaMemory


class LunaLibPlugin : BaseModPlugin()
{

    override fun onNewGame()
    {
        LunaSettingsLoader.storeSaveSettingsInToMemory()
        LunaSettingsLoader.loadSaveSettingDefaults()
    }

    override fun onNewGameAfterProcGen() {
    }

    override fun onGameLoad(newGame: Boolean)
    {
        Global.getSector().addTransientScript(LunaSettingsHotkeyListener())
    }

    override fun onApplicationLoad()
    {
        LunaSettingsLoader.loadDefault()
        LunaSettingsLoader.saveDefaultsToFile()
        LunaSettingsLoader.loadSettings()
        LunaSettingsLoader.loadSaveSettingDefaults()
    }
}