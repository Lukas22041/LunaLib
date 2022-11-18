package lunalib.lunaUtil.campaign

import com.fs.starfarer.api.campaign.PlanetAPI
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.StarSystemAPI

/**
A class containing a collection of different Utilities for System specific use.

[SystemUtils on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/CampaignUtils)
 */
object SystemUtils
{
    /**
     * Returns all Planets of a certain type ID (i.e "toxic").
     * @param system the system to look for the planet in
     * @param typeID the id of the type to look for.
     */
    @JvmStatic
    fun getPlanetsWithType(system: StarSystemAPI, typeID: String) : List<PlanetAPI>
    {
        var planets: MutableList<PlanetAPI> = ArrayList()
        system.planets.filter { it.spec.planetType == typeID }
        return planets
    }

    /**
     * Returns all Planets that have the input condition.
     * @param system the system to look for the planet in
     * @param conditionID the id of the condition to look for.
     */
    @JvmStatic
    fun getPlanetsWithCondition(system: StarSystemAPI, conditionID : String): List<PlanetAPI>
    {
        var planets: MutableList<PlanetAPI> = ArrayList()
        system.planets.filter { it.hasCondition(conditionID) }
        return planets
    }

    /**
     * Returns all Custom Entities of a certain type ID (i.e "comm_relay").
     * @param system the system to look for the entity in
     * @param typeID the id of the type to look for.
     */
    @JvmStatic
    fun getCustomEntitiesWithType(system: StarSystemAPI, typeID: String) : List<SectorEntityToken>
    {
        var entities: MutableList<SectorEntityToken> = ArrayList()
        system.customEntities.filter { it.customEntitySpec.id == typeID }
        return entities
    }
}