package lunalib.lunaExtensions

import com.fs.starfarer.api.campaign.LocationAPI
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.impl.campaign.procgen.StarAge
import com.fs.starfarer.api.util.Misc
import lunalib.lunaUtil.campaign.SystemUtils

//File for Kotlin Extension Functions of StarSystemAPI and LocationAPI. This is only useable in Kotlin, and not Java.

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun StarSystemAPI.getFleetsInOrNearSystem() =
    Misc.getFleetsInOrNearSystem(this)

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun StarSystemAPI.hasAnySurveyData() =
    Misc.hasAnySurveyDataFor(this)

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun StarSystemAPI.setAllPlanetsKnown() =
    Misc.setAllPlanetsKnown(this)

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun StarSystemAPI.setAllPlanetsSurveyed(setRuinsExplored: Boolean) =
    Misc.setAllPlanetsSurveyed(this, setRuinsExplored)

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun StarSystemAPI.generatePlanetConditions(starAge: StarAge) =
    Misc.generatePlanetConditions(this, starAge)

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun StarSystemAPI.getCustomEntitiesWithType(typeID: String) =
    SystemUtils.getCustomEntitiesWithType(this, typeID)

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun StarSystemAPI.getPlanetsWithType(typeID: String) =
    SystemUtils.getPlanetsWithType(this, typeID)

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun StarSystemAPI.getPlanetsWithCondition(typeID: String) =
    SystemUtils.getPlanetsWithCondition(this, typeID)

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun StarSystemAPI.hasPlanets() =
    Misc.systemHasPlanets(this)

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun LocationAPI.getMarkets(factionId: String? = null) =
    if (factionId == null)
        Misc.getMarketsInLocation(this)
    else
        Misc.getMarketsInLocation(this, factionId)

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun LocationAPI.getBiggestMarket() =
    Misc.getBiggestMarketInLocation(this)