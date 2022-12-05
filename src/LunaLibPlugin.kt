import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import javafx.application.Application
import lunalib.KeybindsScript
import lunalib.lunaSettings.LunaSettings
import lunalib.lunaSettings.LunaSettingsLoader
import lunalib.lunaUtil.LunaMisc
import lunalib.lunaUtil.campaign.LunaProcgen
import org.lwjgl.input.Keyboard
import java.util.*

class LunaLibPlugin : BaseModPlugin()
{
    override fun onNewGame()
    {
        LunaSettingsLoader.storeSaveSettingsInToMemory()
        LunaSettingsLoader.loadSaveSettingDefaults()

        LunaProcgen.random = Random(Global.getSector().seedString.hashCode().toLong())
    }

    override fun onNewGameAfterEconomyLoad() {
        
    }

    override fun onNewGameAfterTimePass() {

    }

    override fun onGameLoad(newGame: Boolean)
    {
        LunaProcgen.random = Random(Global.getSector().seedString.hashCode().toLong())
        Global.getSector().addTransientScript(KeybindsScript())

        var keybind = Keyboard.getKeyName(LunaSettings.getInt("lunalib", "luna_SettingsKeybind", false)!!)
        LunaMisc.createIntelMessage("LunaLib",
            "Press $keybind to open the Mod Settings.\n" +
                    "Use Shift + F2 in case the Keybind is inaccesible.")
    }


    override fun onApplicationLoad()
    {
        LunaSettingsLoader.loadDefault()
        LunaSettingsLoader.saveDefaultsToFile()
        LunaSettingsLoader.loadSettings()
        LunaSettingsLoader.loadSaveSettingDefaults()
    }
}
