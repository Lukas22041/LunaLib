package lunalib.lunaExtensions

import com.fs.starfarer.api.campaign.LocationAPI
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.impl.campaign.procgen.StarAge
import com.fs.starfarer.api.util.Misc
import lunalib.lunaUtil.LunaMisc
import lunalib.lunaUtil.campaign.SystemUtils

//File for Kotlin Extension Functions of StarSystemAPI and LocationAPI. This is only useable in Kotlin, and not Java.

/** (**LunaLib Extension Function**)*/
fun StarSystemAPI.getFleetsInOrNearSystem() =
    Misc.getFleetsInOrNearSystem(this)

/** (**LunaLib Extension Function**)*/
fun StarSystemAPI.hasAnySurveyData() =
    Misc.hasAnySurveyDataFor(this)

/** (**LunaLib Extension Function**)*/
fun StarSystemAPI.setAllPlanetsKnown() =
    Misc.setAllPlanetsKnown(this)

/** (**LunaLib Extension Function**)*/
fun StarSystemAPI.setAllPlanetsSurveyed(setRuinsExplored: Boolean) =
    Misc.setAllPlanetsSurveyed(this, setRuinsExplored)

/** (**LunaLib Extension Function**)*/
fun StarSystemAPI.generatePlanetConditions(starAge: StarAge) =
    Misc.generatePlanetConditions(this, starAge)

/** (**LunaLib Extension Function**)*/
fun StarSystemAPI.getCustomEntitiesWithType(typeID: String) =
    SystemUtils.getCustomEntitiesWithType(this, typeID)

/** (**LunaLib Extension Function**)*/
fun StarSystemAPI.getPlanetsWithType(typeID: String) =
    SystemUtils.getPlanetsWithType(this, typeID)

/** (**LunaLib Extension Function**)*/
fun StarSystemAPI.getPlanetsWithCondition(typeID: String) =
    SystemUtils.getPlanetsWithCondition(this, typeID)

/** (**LunaLib Extension Function**)*/
fun StarSystemAPI.hasPlanets() =
    Misc.systemHasPlanets(this)

/** (**LunaLib Extension Function**)*/
fun LocationAPI.getMarkets(factionId: String? = null) =
    if (factionId == null)
        Misc.getMarketsInLocation(this)
    else
        Misc.getMarketsInLocation(this, factionId)

/** (**LunaLib Extension Function**)*/
fun LocationAPI.getBiggestMarket() =
    Misc.getBiggestMarketInLocation(this)