package lunalib.lunaDebug.snippets

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ModSpecAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import kotlinx.coroutines.GlobalScope
import lunalib.backend.ui.settings.LunaSettingsLoader
import lunalib.lunaDebug.LunaDebug
import lunalib.lunaDebug.LunaSnippet
import lunalib.lunaDebug.LunaSnippet.SnippetTags
import lunalib.lunaDebug.SnippetBuilder
import lunalib.lunaSettings.LunaSettings

class LunaLibDataSnippet() : LunaSnippet() {
    override fun getName(): String {
        return "Print LunaLib data"
    }

    override fun getDescription(): String {
        return "Prints miscellaneous points of data from [Lunalib] in to the output window."
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

        var baseColor = Misc.getBasePlayerColor()
        var highlightColor = Misc.getHighlightColor()

        var mod = Global.getSettings().modManager.getModSpec("lunalib")

        var title = output.addPara("${mod.name}: V${mod.version}", 0f, baseColor, highlightColor)
        title.setHighlight("${mod.name}")
        output.addSpacer(20f)

        var data = LunaSettingsLoader.Settings

        output.addPara("LunaSettings", 0f, highlightColor, highlightColor)

        output.addPara("Active Mods with Loaded Settings: ${data.keys}", 0f, baseColor, highlightColor)
        output.addSpacer(5f)
        output.addPara("Total amount of Loaded Settings: ${LunaSettingsLoader.SettingsData.size}", 0f, baseColor, highlightColor)

        output.addSpacer(20f)

        output.addPara("LunaSnippets", 0f, highlightColor, highlightColor)

        var modsWithSnippets = ArrayList<ModSpecAPI>()
        for (snippet in LunaDebug.snippets)
        {
            if (!modsWithSnippets.contains(Global.getSettings().modManager.getModSpec(snippet.modId)))
            modsWithSnippets.add(Global.getSettings().modManager.getModSpec(snippet.modId))
        }

        output.addPara("Active Mods with Snippets: $modsWithSnippets", 0f, baseColor, highlightColor)
        output.addSpacer(5f)
        output.addPara("Total amount of Loaded Snippets: ${LunaDebug.snippets.size}", 0f, baseColor, highlightColor)
    }
}