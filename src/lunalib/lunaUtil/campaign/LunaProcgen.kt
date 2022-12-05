package lunalib.lunaUtil.campaign

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CustomCampaignEntityAPI
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin
import com.fs.starfarer.api.impl.campaign.ids.Entities
import com.fs.starfarer.api.impl.campaign.ids.Factions
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator.LocationType
import lunalib.lunaSettings.LunaSettingsLoader
import org.apache.log4j.Level
import java.util.*
import kotlin.random.asKotlinRandom

object LunaProcgen
{

    private val log = Global.getLogger(LunaSettingsLoader::class.java)

    internal var random = Random()

    init {
        log.level = Level.ALL
    }

    /**
     *Generates a derelict ship when called. Can generate in any system if [systemTags] is null, and can generate in any system Location if [systemLocations] is null, or it found no other suiting locations.
     *
     * Should be called in onNewGameAfterEconomyLoad or onNewGameAfterProcGen

    ```Java
    //Tags for where it generates in.
    List<String> tags = new ArrayList<>();
    tags.add(Tags.THEME_REMNANT);

    //Location within a system, and the likelyhood of it spawning there.
    Map<BaseThemeGenerator.LocationType, Float> locations = new HashMap<>();
    locations.put(BaseThemeGenerator.LocationType.NEAR_STAR, 3f);
    locations.put(BaseThemeGenerator.LocationType.JUMP_ORBIT, 5f);

    LunaProcgen.generateDerelictShip("VariantID", "ChooseYourOwnID", tags, locations, true);
    ```
    */
    @JvmStatic
    fun generateDerelictShip(variantId: String, derelictId: String, systemTags: List<String>?, systemLocations: Map<LocationType, Float>?, printLocation: Boolean) : CustomCampaignEntityAPI?
    {
        if (!Global.getSettings().doesVariantExist(variantId)) { log.error("LunaProcgen: Variant $variantId not found"); return null }

        var system = if (systemTags.isNullOrEmpty()) {
            Global.getSector().starSystems.random(random.asKotlinRandom())
        }
        else {
            var temp = Global.getSector().starSystems
                .filter { system -> system.tags
                .any { systemTag -> systemTags.contains(systemTag) } }

            if (temp.isNotEmpty())
            {
                temp.random(random.asKotlinRandom())
            }
            else
            {
                null
            }
        }

        if (system == null) {
             log.error("LunaProcgen: Couldnt find a suitable system for derelict $variantId");
             return null
        }

        var loc = if (systemLocations.isNullOrEmpty()) {
            BaseThemeGenerator.pickAnyLocation(random, system, 70f, null)
        } else {
            val locs = BaseThemeGenerator.getLocations(random, system, null, 70f, systemLocations as LinkedHashMap<BaseThemeGenerator.LocationType, Float>)
            if (locs.isEmpty) {
                 BaseThemeGenerator.pickAnyLocation(random, system, 70f, null)
            }
            else
            {
                locs.pick()
            }
        }

        val params = DerelictShipEntityPlugin.createVariant(variantId, random, DerelictShipEntityPlugin.getDefaultSModProb())
        val entity = BaseThemeGenerator.addSalvageEntity(random, system, Entities.WRECK, Factions.NEUTRAL, params) as CustomCampaignEntityAPI
        entity.isDiscoverable = true
        entity.id = derelictId
        BaseThemeGenerator.setEntityLocation(entity, loc, Entities.WRECK);

        if (printLocation)
        {
            log.debug("LunaProcGen: Generated $variantId in the ${entity.starSystem.name}")
        }

        return entity
    }
}