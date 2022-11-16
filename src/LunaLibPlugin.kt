import lunalib.lunaSettings.LunaSettingsHotkeyListener
import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import lunalib.lunaSettings.LunaSettingsLoader

class LunaLibPlugin : BaseModPlugin()
{
    override fun onNewGame()
    {
        LunaSettingsLoader.storeSaveSettingsInToMemory()
        LunaSettingsLoader.loadSaveSettingDefaults()
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