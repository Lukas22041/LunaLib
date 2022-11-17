package lunalib.lunaUtil

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.FactionAPI
import com.fs.starfarer.api.campaign.PlanetAPI
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.combat.ShipHullSpecAPI
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel
import com.fs.starfarer.api.loading.FighterWingSpecAPI
import com.fs.starfarer.api.loading.HullModSpecAPI
import com.fs.starfarer.api.loading.IndustrySpecAPI
import com.fs.starfarer.api.loading.WeaponSpecAPI
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.combat.entities.terrain.Planet
import java.awt.Color
import kotlin.random.Random

object LunaUtils
{



    /**
     * Generates and returns a random color.
     * @param saturation Saturation of the color.
     */
    @JvmStatic
    fun randomColor(saturation: Float): Color {
        val hue = Random.nextFloat()
        val luminance = 0.9f
        return Color.getHSBColor(hue, saturation, luminance)
    }

    /**
     * Generates and returns a random color.
     * @param saturation Saturation of the color.
     * @param alpha Alpha of the generated color.
     */
    @JvmStatic
    fun randomColor(saturation: Float, alpha: Int) : Color {
        val hue = Random.nextFloat()
        val luminance = 0.9f
        val color = Color.getHSBColor(hue, saturation, luminance)
        return Color(color.red, color.green, color.blue, alpha)
    }

    /**
     *Creates an intel Message (The pop-ups in the bottom left of the UI.
     * @param Title
     * @param Text
     */
    @JvmStatic
    fun createIntelMessage(Title: String, Text: String)
    {
        val intel = MessageIntel(Title, Misc.getHighlightColor())
        intel.addLine(Text)
        Global.getSector().campaignUI.addMessage(intel)
    }

    /**
     *Creates an intel Message (The pop-ups in the bottom left of the UI.
     * @param Title
     * @param Text
     * @param Icon the Spritename of an icon that should be used.
     */
    @JvmStatic
    fun createIntelMessage(Title: String, Text: String, Icon: String)
    {
        val intel = MessageIntel(Title, Misc.getHighlightColor())
        intel.icon = Icon
        intel.addLine(Text)
        Global.getSector().campaignUI.addMessage(intel)
    }

    /**Class providing utilities for faction data.*/
    object FactionUtils
    {

         /**Gets a copy of all markets that a faction holds.
          * @param FactionId the ID of the faction.
          * */
        @JvmStatic
        fun getMarketsCopy(FactionId: String) : List<MarketAPI> = Global.getSector().economy.marketsCopy.filter { it.factionId == FactionId }

        /**
        * Returns all ShipHullSpecAPI for ships that are known to the faction.
         * @param FactionId the ID of the faction.
        */
        @JvmStatic
        fun getKnownShipSpecs(FactionId: String) : List<ShipHullSpecAPI> = Global.getSettings().allShipHullSpecs.filter { Global.getSector().getFaction(FactionId).knownShips.contains(it.hullId) }

        /**
         * Returns all HullModSpecAPI for ships that are known to the faction.
         * @param FactionId the ID of the faction.
         */
        @JvmStatic
        fun getKnownHullmodSpecs(FactionId: String) : List<HullModSpecAPI> = Global.getSettings().allHullModSpecs.filter { Global.getSector().getFaction(FactionId).knownHullMods.contains(it.id) }

        /**
         * Returns all WeaponSpecAPI for ships that are known to the faction.
         * @param FactionId the ID of the faction.
         */
        @JvmStatic
        fun getKnownWeaponSpecs(FactionId: String) : List<WeaponSpecAPI> = Global.getSettings().allWeaponSpecs.filter { Global.getSector().getFaction(FactionId).knownWeapons.contains(it.weaponId) }

        /**
         * Returns all FighterWingSpecAPI for ships that are known to the faction.
         * @param FactionId the ID of the faction.
         */
        @JvmStatic
        fun getKnownFighterSpecs(FactionId: String) : List<FighterWingSpecAPI> = Global.getSettings().allFighterWingSpecs.filter { Global.getSector().getFaction(FactionId).knownFighters.contains(it.id) }

    }

    /**Class providing utilities for Entity data.*/
    object EntityUtils
    {
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
}