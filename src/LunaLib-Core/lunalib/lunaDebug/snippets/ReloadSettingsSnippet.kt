package lunalib.lunaDebug.snippets

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ModSpecAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.backend.ui.settings.LunaSettingsConfigLoader
import lunalib.backend.ui.settings.LunaSettingsLoader
import lunalib.lunaDebug.LunaDebug
import lunalib.lunaDebug.LunaSnippet
import lunalib.lunaDebug.SnippetBuilder
import kotlin.system.measureTimeMillis

class ReloadSettingsSnippet() : LunaSnippet() {
    override fun getName(): String {
        return "Reload LunaSettings.csv"
    }

    override fun getDescription(): String {
        return "Reloads the LunaSettings.csv file, which causes modification towards it to be reloaded while the game is still running. This does not reset any configs."
    }

    override fun getModId(): String {
        return "lunalib"
    }

    override fun getTags(): MutableList<String> {
        return mutableListOf(SnippetTags.Debug.toString())
    }

    override fun addParameters(builder: SnippetBuilder?) {

    }

    override fun execute(parameters: MutableMap<String, Any>?, output: TooltipMakerAPI) {

        var time = measureTimeMillis {
            LunaSettingsConfigLoader.reload()
            LunaSettingsLoader.loadDefault()
            LunaSettingsLoader.saveDefaultsToFile()
            LunaSettingsLoader.loadSettings()
        }

        output.addPara("Sucesfully reloaded LunaSettings.csv in ${time}ms", 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor())

    }
}