package lunalib.lunaExtensions

import com.fs.starfarer.api.campaign.*
import lunalib.lunaUtil.LunaMisc
import lunalib.lunaUtil.campaign.SectorUtils

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



