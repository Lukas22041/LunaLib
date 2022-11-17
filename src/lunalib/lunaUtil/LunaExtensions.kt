package lunalib.lunaUtil

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.*
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.combat.ShipHullSpecAPI
import com.fs.starfarer.api.loading.FighterWingSpecAPI
import com.fs.starfarer.api.loading.HullModSpecAPI
import com.fs.starfarer.api.loading.WeaponSpecAPI


//File for all kinds of Kotlin Extension Functions. This is only useable in Kotlin, and not Java.
//Most of those functions just return methods from LunaUtils.

//The extensions can simply be used on the objects they are extending.
//i.e FactionAPI.getMarketsCopy()


//Faction Extensions

/** (LunaLib Extension Function)
 *
 * Gets a copy of all markets that a faction holds. */
fun FactionAPI.getMarketsCopy() : List<MarketAPI> = LunaUtils.FactionUtils.getMarketsCopy(this.id)

/** (LunaLib Extension Function)
 *
 * Returns all ShipHullSpecAPI for ships that are known to the faction.*/
fun FactionAPI.getKnownShipSpecs() : List<ShipHullSpecAPI> = LunaUtils.FactionUtils.getKnownShipSpecs(this.id)

/** (LunaLib Extension Function)
 *
 * Returns all HullModSpecAPI for ships that are known to the faction.*/
fun FactionAPI.getKnownHullmodSpecs() : List<HullModSpecAPI> = LunaUtils.FactionUtils.getKnownHullmodSpecs(this.id)

/** (LunaLib Extension Function)
 *
 * Returns all WeaponSpecAPI for ships that are known to the faction. */
fun FactionAPI.getKnownWeaponSpecs() : List<WeaponSpecAPI> = LunaUtils.FactionUtils.getKnownWeaponSpecs(this.id)

/** (LunaLib Extension Function)
 *
 * Returns all FighterWingSpecAPI for ships that are known to the faction. */
fun FactionAPI.getKnownFightersSpecs() : List<FighterWingSpecAPI> = LunaUtils.FactionUtils.getKnownFighterSpecs(this.id)






//SectorAPI Extensions


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
 * Returns all starsystems that include that input tag.
 * @param tag the id of the type to look for.
 */
fun SectorAPI.getSystemsWithTag(tag: String) : List<StarSystemAPI> =  LunaUtils.SectorUtils.getSystemsWithTag(tag)



