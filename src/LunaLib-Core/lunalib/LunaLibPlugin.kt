import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.FactionAPI
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry
import com.fs.starfarer.api.impl.campaign.submarkets.BaseSubmarketPlugin
import lunalib.KeybindsScript
import lunalib.OpenDebugWindowDelegate
import lunalib.lunaDebug.DebugWindowUI
import lunalib.lunaExtensions.addTransientScript
import lunalib.lunaExtensions.getMarkets
import lunalib.lunaSettings.LunaSettingsLoader
import lunalib.lunaUtil.campaign.LunaProcgen
import java.lang.invoke.MethodHandle
import java.util.*


class LunaLibPlugin : BaseModPlugin()
{
    override fun onNewGame()
    {
        LunaProcgen.random = Random(Global.getSector().seedString.hashCode().toLong())
    }

    override fun onNewGameAfterEconomyLoad() {
    }

    override fun onGameLoad(newGame: Boolean)
    {
        LunaProcgen.random = Random(Global.getSector().seedString.hashCode().toLong())
        Global.getSector().addTransientScript(KeybindsScript())

        Global.getSector().addTransientScript {
            playerFleet.cargo.credits.add(1f)
        }
    }

    override fun onApplicationLoad()
    {
        LunaSettingsLoader.loadDefault()
        LunaSettingsLoader.saveDefaultsToFile()
        LunaSettingsLoader.loadSettings()
    }
}

