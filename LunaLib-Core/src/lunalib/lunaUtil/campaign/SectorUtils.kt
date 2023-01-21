package lunalib.lunaUtil.campaign

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.FactionAPI
import com.fs.starfarer.api.campaign.PlanetAPI
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.StarSystemAPI

/**
A class containing a collection of different Utilities for Sector related stuff.

[SystemUtils on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/CampaignUtils)
 */
object SectorUtils
{

    /**
     * Returns all Factions that own a market in the sector.
     */
    @JvmStatic
    fun getFactionsWithMarkets() : List<FactionAPI> =
        Global.getSector().allFactions
            .filter { faction -> Global.getSector().economy.marketsCopy
            .map { market -> market.factionId }.contains(faction.id) }

    /**
     * Returns all Planets of a certain type ID (i.e "toxic").
     * @param typeID the id of the type to look for.
     */
    @JvmStatic
    fun getPlanetsWithType(typeID: String) : List<PlanetAPI> =
        Global.getSector().starSystems.flatMap { system -> system.planets.filter { planet -> planet.spec.planetType == typeID }}


    /**
     * Returns all Planets that have the input condition.
     * @param conditionID the id of the condition to look for.
     */
    @JvmStatic
    fun getPlanetsWithCondition(conditionID : String): List<PlanetAPI> =
        Global.getSector().starSystems.flatMap { system -> system.planets.filter { planet-> planet.hasCondition(conditionID) } }

    /**
     * Returns all Custom Entities of a certain type ID (i.e "comm_relay").
     * @param typeID the id of the type to look for.
     */
    @JvmStatic
    fun getCustomEntitiesWithType(typeID: String) : List<SectorEntityToken> =
        Global.getSector().starSystems.flatMap { system -> system.customEntities.filter { entity -> entity.customEntitySpec.id == typeID }}

    /**
     * Returns all starsystems that include the input tag.
     * @param tag the id of the type to look for.
     */
    @JvmStatic
    fun getSystemsWithTag(tag: String) : List<StarSystemAPI> = Global.getSector().starSystems.filter { it.tags.contains(tag) }

}