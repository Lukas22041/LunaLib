import lunalib.lunaSettings.LunaSettingsHotkeyListener
import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import lunalib.lunaSettings.LunaSettings
import lunalib.lunaSettings.LunaSettingsLoader
import lunalib.lunaWrappers.LunaMemory

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

        var test = IntelMessage()
        test.setIntelMessage()
        test.sendIntelMessage()
    }

    override fun onApplicationLoad()
    {
        LunaSettingsLoader.loadDefault()
        LunaSettingsLoader.saveDefaultsToFile()
        LunaSettingsLoader.loadSettings()
        LunaSettingsLoader.loadSaveSettingDefaults()
    }
}