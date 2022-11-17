package lunalib.lunaExtensions

import com.fs.starfarer.api.campaign.*
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.combat.ShipHullSpecAPI
import com.fs.starfarer.api.loading.FighterWingSpecAPI
import com.fs.starfarer.api.loading.HullModSpecAPI
import com.fs.starfarer.api.loading.WeaponSpecAPI
import lunalib.lunaUtil.LunaUtils

//File for Kotlin Extension Functions of SectorAPI. This is only useable in Kotlin, and not Java.

/** (LunaLib Extension Function)
 *
 * Returns a list of all Factions that own a market in the sector.
 */
fun SectorAPI.getFactionsWithMarkets() : List<FactionAPI> = LunaUtils.SectorUtils.getFactionsWithMarkets()


/** (LunaLib Extension Function)
 *
 * Returns all Planets of a certain type ID (i.e "toxic").
 * @param typeID the id of the type to look for.
 */
fun SectorAPI.getPlanetsWithType(typeID: String) : List<PlanetAPI> = LunaUtils.SectorUtils.getPlanetsWithType(typeID)

/**(LunaLib Extension Function)
 *
 * Returns all Planets that have the input condition.
 * @param conditionID the id of the condition to look for.
 */
fun SectorAPI.getPlanetsWithCondition(conditionID : String): List<PlanetAPI> = LunaUtils.SectorUtils.getPlanetsWithCondition(conditionID)


/** (LunaLib Extension Function)
 *
 * Returns all Custom Entities of a certain type ID (i.e "comm_relay").
 * @param typeID the id of the type to look for.
 */
fun SectorAPI.getCustomEntitiesWithType(typeID: String) : List<SectorEntityToken> = LunaUtils.SectorUtils.getCustomEntitiesWithType(typeID)


/** (LunaLib Extension Function)
 *
 * Returns all starsystems that include the input tag.
 * @param tag the tag of the system to look for.
 */
fun SectorAPI.getSystemsWithTag(tag: String) : List<StarSystemAPI> =  LunaUtils.SectorUtils.getSystemsWithTag(tag)



