package lunalib.lunaDebug.snippets

import com.fs.starfarer.api.Global
import lunalib.lunaDebug.SnippetBuilder
import lunalib.lunaDebug.LunaSnippet
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection

class ModsListSnippet : LunaSnippet {
    override fun getName(): String {
        return "Copy loaded mods to Clipboard"
    }

    override fun getDescription(): String {
        return "Copies a list of all loaded mods on to the Computers Clipboard"
    }

    override fun getModId(): String {
        return "lunalib"
    }

    override fun getCategories(): List<LunaSnippet.SnippetCategory> {
        return listOf(LunaSnippet.SnippetCategory.Debug)
    }

    override fun addParameters(builder: SnippetBuilder) {
        builder.addBooleanParameter("Discord Format", "Discord", false)
    }

    override fun execute(parameters: Map<String, Any>) : String{
        try {
            var testParam = parameters.get("Discord") as Boolean ?: false


            var mods = Global.getSettings().modManager.enabledModsCopy
            var list = ""
            for (mod in mods)
            {

                if (testParam)
                {
                    list += "**${mod.name}:** ``${mod.version}``\n"
                }
                else
                {
                    list += "${mod.name}: ${mod.version}\n"
                }
            }

            val stringSelection = StringSelection(list)
            val clipboard: Clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
            clipboard.setContents(stringSelection, null)
        }
        catch (e : Throwable) { return "Error: Something went wrong during execution"}
        return "Copied to Clipboard!"
    }
}