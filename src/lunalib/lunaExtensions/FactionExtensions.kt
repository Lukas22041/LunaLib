package lunalib.lunaExtensions

import com.fs.starfarer.api.campaign.*
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.combat.ShipHullSpecAPI
import com.fs.starfarer.api.loading.FighterWingSpecAPI
import com.fs.starfarer.api.loading.HullModSpecAPI
import com.fs.starfarer.api.loading.WeaponSpecAPI
import lunalib.lunaUtil.LunaUtils


//File for Kotlin Extension Functions of FactionAPI. This is only useable in Kotlin, and not Java.

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

