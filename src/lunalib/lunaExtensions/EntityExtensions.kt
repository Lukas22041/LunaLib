package lunalib.lunaExtensions

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.InteractionDialogPlugin
import com.fs.starfarer.api.campaign.JumpPointAPI
import com.fs.starfarer.api.campaign.PlanetAPI
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.impl.campaign.procgen.PlanetConditionGenerator
import com.fs.starfarer.api.impl.campaign.procgen.StarAge
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.api.util.Misc.FleetFilter

//File for Kotlin Extension Functions of SectorEntityToken and PlanetAPI. This is only useable in Kotlin, and not Java.

/** (**LunaLib Extension Function**)*/
fun SectorEntityToken.getDistance(to: SectorEntityToken) =
    Misc.getDistance(this, to)

/** (**LunaLib Extension Function**)*/
fun SectorEntityToken.getDistanceLY(to: SectorEntityToken) =
    Misc.getDistanceLY(this, to)

/** (**LunaLib Extension Function**)*/
fun SectorEntityToken.getDistanceToPlayerLY() =
    Misc.getDistanceToPlayerLY(this)

/** (**LunaLib Extension Function**)*/
fun SectorEntityToken.findNearbyFleets(maxRange: Float, filter: FleetFilter) =
    Misc.findNearbyFleets(this, maxRange, filter)

/** (**LunaLib Extension Function**)*/
fun SectorEntityToken.getNearbyStarSystems(maxRangeLY: Float) =
    Misc.getNearbyStarSystems(this, maxRangeLY)

/** (**LunaLib Extension Function**)*/
fun SectorEntityToken.getNearbyStarSystem(maxRangeLY: Float) =
    Misc.getNearbyStarSystem(this, maxRangeLY)

/** (**LunaLib Extension Function**)*/
fun SectorEntityToken.getNearestStarSystem() =
    Misc.getNearestStarSystem(this)

/** (**LunaLib Extension Function**)*/
fun SectorEntityToken.getNearbyStarSystem() =
    Misc.getNearbyStarSystem(this)

/** (**LunaLib Extension Function**)*/
fun SectorEntityToken.showRuleDialog(initialTrigger: String) =
    Misc.showRuleDialog(this, initialTrigger)

/** (**LunaLib Extension Function**)*/
fun SectorEntityToken.showInteractionDialog(plugin: InteractionDialogPlugin) =
    Global.getSector().campaignUI.showInteractionDialog(plugin, this)

/** (**LunaLib Extension Function**)*/
fun SectorEntityToken.findNearestJumppint() =
    Misc.findNearestJumpPoint(this)

/** (**LunaLib Extension Function**)*/
fun SectorEntityToken.findNearestPlanet(allowStars: Boolean) =
    Misc.findNearestPlanetTo(this, false, allowStars)


fun SectorEntityToken.getSalvageSeed() =
    Misc.getSalvageSeed(this)

/** (**LunaLib Extension Function**)*/
fun PlanetAPI.generatePlanetCondition(starAge: StarAge) =
    PlanetConditionGenerator.generateConditionsForPlanet(this, starAge)


