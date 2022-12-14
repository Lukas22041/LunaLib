import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.DamagingProjectileAPI
import com.fs.starfarer.api.combat.MissileAPI
import com.fs.starfarer.api.loading.MissileSpecAPI
import com.fs.starfarer.api.loading.WeaponSpecAPI
import javafx.application.Application
import lunalib.KeybindsScript
import lunalib.lunaSettings.LunaSettings
import lunalib.lunaSettings.LunaSettingsLoader
import lunalib.lunaUtil.LunaMisc
import lunalib.lunaUtil.campaign.LunaProcgen
import org.lazywizard.lazylib.VectorUtils
import org.lwjgl.input.Keyboard
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
