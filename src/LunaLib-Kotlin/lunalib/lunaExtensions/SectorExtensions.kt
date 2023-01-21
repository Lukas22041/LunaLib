package lunalib.lunaExtensions

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.*
import lunalib.lunaUtil.campaign.SectorUtils
import lunalib.lunaExtensions.EveryFrameScriptLambda

//File for Kotlin Extension Functions of SectorAPI. This is only useable in Kotlin, and not Java.

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun SectorAPI.getFactionsWithMarkets() : List<FactionAPI> =
    SectorUtils.getFactionsWithMarkets()

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun SectorAPI.getPlanetsWithType(typeID: String) : List<PlanetAPI> =
    SectorUtils.getPlanetsWithType(typeID)

/** (LunaLib Extension Function)*/
fun SectorAPI.getPlanetsWithCondition(conditionID : String): List<PlanetAPI> =
    SectorUtils.getPlanetsWithCondition(conditionID)

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun SectorAPI.getCustomEntitiesWithType(typeID: String) : List<SectorEntityToken> =
    SectorUtils.getCustomEntitiesWithType(typeID)

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun SectorAPI.getSystemsWithTag(tag: String) : List<StarSystemAPI> =
    SectorUtils.getSystemsWithTag(tag)

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun SectorAPI.isPlayerInHyperspace() =
    Global.getSector().playerFleet.isInHyperspace

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)
 *
 * Allows adding a script to the sector through an input lambda*/
fun SectorAPI.addScript(runWhilePaused: Boolean = false, function: (amount: Float) -> Unit) : EveryFrameScriptLambda
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
}

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)
 *
 * Allows adding a script to the sector through an input lambda*/
fun SectorAPI.addTransientScript(runWhilePaused: Boolean = false, function: (amount: Float) -> Unit) : EveryFrameScriptLambda
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
}





