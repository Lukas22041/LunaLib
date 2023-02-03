package lunalib.lunaDebug.snippets

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.JumpPointAPI
import com.fs.starfarer.api.campaign.SectorEntityToken
import lunalib.lunaDebug.SnippetBuilder
import lunalib.lunaDebug.LunaSnippet

class ExampleSnippet : LunaSnippet {
    override fun getName(): String {
        return "Example Snippet"
    }

    override fun getDescription(): String {
        return "An example snippet that can be looked at to see how Snippets work! Shows off a few things that can be done with them. This one will teleport you to whatever entity Id you enter"
    }

    override fun getModId(): String {
        return "lunalib"
    }

    override fun getCategories(): List<LunaSnippet.SnippetCategory> {
        return listOf(LunaSnippet.SnippetCategory.Debug, LunaSnippet.SnippetCategory.Cheat)
    }

    override fun addParameters(builder: SnippetBuilder) {
        builder.addStringParameter("Entity Id", "Test")
    }

    override fun execute(parameters: Map<String, Any>) : String{

    var testParam = parameters.get("Test") as String
    if (testParam == "") return "Error: No entity Id has been input."

    var entity: SectorEntityToken? = Global.getSector().getEntityById(testParam) ?: return "Error: Could not find an entity with that id."

    Global.getSector().doHyperspaceTransition(Global.getSector().playerFleet, Global.getSector().playerFleet,
           JumpPointAPI.JumpDestination(entity, ""), 0f)

    return "Successfuly executed!"
    }
}