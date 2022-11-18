package lunalib.lunaUtil.campaign

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.FactionAPI
import com.fs.starfarer.api.campaign.PlanetAPI
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.StarSystemAPI
import lunalib.lunaUtil.LunaMisc

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
    fun getFactionsWithMarkets() : List<FactionAPI>
    {
        var factions: MutableList<FactionAPI> = ArrayList()
        Global.getSector().allFactions.forEach { faction -> Global.getSector().economy.marketsCopy.forEach { market -> if (market.factionId == faction.id) factions.add(faction)} }
        return factions.distinct()
    }

    /**
     * Returns all Planets of a certain type ID (i.e "toxic").
     * @param typeID the id of the type to look for.
     */
    @JvmStatic
    fun getPlanetsWithType(typeID: String) : List<PlanetAPI>
    {
        var planets: MutableList<PlanetAPI> = ArrayList()
        Global.getSector().starSystems.forEach {  planets.addAll(it.planets.filter { it.spec.planetType == typeID })}
        return planets
    }

    /**
     * Returns all Planets that have the input condition.
     * @param conditionID the id of the condition to look for.
     */
    @JvmStatic
    fun getPlanetsWithCondition(conditionID : String): List<PlanetAPI>
    {
        var planets: MutableList<PlanetAPI> = ArrayList()
        Global.getSector().starSystems.forEach {  planets.addAll(it.planets.filter { it.hasCondition(conditionID) })}
        return planets
    }

    /**
     * Returns all Custom Entities of a certain type ID (i.e "comm_relay").
     * @param typeID the id of the type to look for.
     */
    @JvmStatic
    fun getCustomEntitiesWithType(typeID: String) : List<SectorEntityToken>
    {
        var entities: MutableList<SectorEntityToken> = ArrayList()
        Global.getSector().starSystems.forEach {  entities.addAll(it.customEntities.filter { it.customEntitySpec.id == typeID })}
        return entities
    }

    /**
     * Returns all starsystems that include that input tag.
     * @param tag the id of the type to look for.
     */
    @JvmStatic
    fun getSystemsWithTag(tag: String) : List<StarSystemAPI> = Global.getSector().starSystems.filter { it.tags.contains(tag) }

}