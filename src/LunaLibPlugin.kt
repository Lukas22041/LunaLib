import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import lunalib.KeybindsScript
import lunalib.lunaSettings.LunaSettingsLoader
import lunalib.lunaUtil.campaign.LunaProcgen
import java.util.*

class LunaLibPlugin : BaseModPlugin()
{
    override fun onNewGame()
    {
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
    }

    override fun onApplicationLoad()
    {
        LunaSettingsLoader.loadDefault()
        LunaSettingsLoader.saveDefaultsToFile()
        LunaSettingsLoader.loadSettings()
    }
}
