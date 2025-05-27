package lunalib.lunaUtil.campaign

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.combat.ShipHullSpecAPI
import com.fs.starfarer.api.loading.FighterWingSpecAPI
import com.fs.starfarer.api.loading.HullModSpecAPI
import com.fs.starfarer.api.loading.WeaponSpecAPI

/**
A class containing a collection of different Utilities for Faction specific use.

[SystemUtils on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/CampaignUtils)
 */
object FactionUtils
{
    /**Gets a copy of all markets that a faction holds.
     * @param FactionId the ID of the faction.
     * */
    @JvmStatic
    fun getMarketsCopy(FactionId: String) : List<MarketAPI> =
        Global.getSector().economy.marketsCopy.filter { it.factionId == FactionId }


    /**Checks if the Faction holds any market in the Sector.
     * @param FactionId the ID of the faction.
     * */
    @JvmStatic
    fun existsInSector(FactionId: String) : Boolean
    {
        var found = false
        Global.getSector().economy.marketsCopy.forEach { if (it.factionId == FactionId) {found = true; return@forEach} }
        return found
    }

    /**
     * Returns all ShipHullSpecAPI for ships that are known to the faction.
     * @param FactionId the ID of the faction.
     */
    @JvmStatic
    fun getKnownShipSpecs(FactionId: String) : List<ShipHullSpecAPI> =
        Global.getSettings().allShipHullSpecs.filter { Global.getSector().getFaction(FactionId).knownShips.contains(it.hullId) }

    /**
     * Returns all HullModSpecAPI for ships that are known to the faction.
     * @param FactionId the ID of the faction.
     */
    @JvmStatic
    fun getKnownHullmodSpecs(FactionId: String) : List<HullModSpecAPI> =
        Global.getSettings().allHullModSpecs.filter { Global.getSector().getFaction(FactionId).knownHullMods.contains(it.id) }

    /**
     * Returns all WeaponSpecAPI for ships that are known to the faction.
     * @param FactionId the ID of the faction.
     */
    @JvmStatic
    fun getKnownWeaponSpecs(FactionId: String) : List<WeaponSpecAPI> =
        Global.getSettings().allWeaponSpecs.filter { Global.getSector().getFaction(FactionId).knownWeapons.contains(it.weaponId) }

    /**
     * Returns all FighterWingSpecAPI for ships that are known to the faction.
     * @param FactionId the ID of the faction.
     */
    @JvmStatic
    fun getKnownFighterSpecs(FactionId: String) : List<FighterWingSpecAPI> =
        Global.getSettings().allFighterWingSpecs.filter { Global.getSector().getFaction(FactionId).knownFighters.contains(it.id) }

}