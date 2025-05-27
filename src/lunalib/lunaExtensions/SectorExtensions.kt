package lunalib.lunaExtensions

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.*
import lunalib.backend.ui.OpenCustomPanelFromDialog
import lunalib.lunaUtil.campaign.SectorUtils
import lunalib.lunaExtensions.scripts.EveryFrameScriptLambda
import lunalib.lunaUI.panel.LunaBaseCustomPanelPlugin

//File for Kotlin Extension Functions of SectorAPI. This is only useable in Kotlin, and not Java.

fun SectorAPI.openLunaCustomPanel(plugin: LunaBaseCustomPanelPlugin) {
    this.campaignUI.showInteractionDialog(OpenCustomPanelFromDialog(plugin), Global.getSector().playerFleet)
}

fun SectorAPI.getFactionsWithMarkets() : List<FactionAPI> =
    SectorUtils.getFactionsWithMarkets()

fun SectorAPI.getPlanetsWithType(typeID: String) : List<PlanetAPI> =
    SectorUtils.getPlanetsWithType(typeID)

fun SectorAPI.getPlanetsWithCondition(conditionID : String): List<PlanetAPI> =
    SectorUtils.getPlanetsWithCondition(conditionID)

fun SectorAPI.getCustomEntitiesWithType(typeID: String) : List<SectorEntityToken> =
    SectorUtils.getCustomEntitiesWithType(typeID)

fun SectorAPI.getSystemsWithTag(tag: String) : List<StarSystemAPI> =
    SectorUtils.getSystemsWithTag(tag)

fun SectorAPI.isPlayerInHyperspace() =
    Global.getSector().playerFleet.isInHyperspace

/**
 *
 * Allows adding a script to the sector through an input lambda*/
/*fun SectorAPI.addScript(runWhilePaused: Boolean = false, function: (amount: Float) -> Unit) : EveryFrameScriptLambda
{
    var script = object : EveryFrameScriptLambda(runWhilePaused) {

        init {
            this.runWhilePaused = runWhilePaused
            this.isScriptDone = false
        }

        override fun advance(amount: Float) {
            function(amount)
        }
    }
    Global.getSector().addScript(script)
    return script
}*/

/**
 *
 * Allows adding a script to the sector through an input lambda*/
/*fun SectorAPI.addTransientScript(runWhilePaused: Boolean = false, function: (amount: Float) -> Unit) : EveryFrameScriptLambda
{
    var script = object : EveryFrameScriptLambda(runWhilePaused) {

        init {
            this.runWhilePaused = runWhilePaused
            this.isScriptDone = false
        }

        override fun advance(amount: Float) {
            function(amount)
        }
    }
    Global.getSector().addTransientScript(script)
    return script
}*/





