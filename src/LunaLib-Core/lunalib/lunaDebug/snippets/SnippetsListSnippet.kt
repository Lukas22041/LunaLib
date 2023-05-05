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

class SnippetsListSnippet() : LunaSnippet() {
    override fun getName(): String {
        return "Display List of Snippets"
    }

    override fun getDescription(): String {
        return "Prints all loaded [Snippets] sorted by the mod adding them."
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


        var title = output.addPara("Loaded Snippets", 0f, baseColor, highlightColor, "Loaded Snippets")
        output.addSpacer(10f)

        var modsWithSnippets = ArrayList<ModSpecAPI>()
        for (snippet in LunaDebug.snippets)
        {
            if (!modsWithSnippets.contains(Global.getSettings().modManager.getModSpec(snippet.modId)))
            modsWithSnippets.add(Global.getSettings().modManager.getModSpec(snippet.modId))
        }

        for (mod in modsWithSnippets)
        {
            var snippets = LunaDebug.snippets.filter { it.modId == mod.id }
            output.addSpacer(10f)

            output.addPara(mod.name, 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor(), mod.name)
            output.addSpacer(2f)

            for (snippet in snippets)
            {
                output.addPara(snippet.name, 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor())
            }
        }
    }
}