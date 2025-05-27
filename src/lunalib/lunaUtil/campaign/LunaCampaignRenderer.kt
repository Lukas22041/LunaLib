package lunalib.lunaUtil.campaign

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.LocationAPI
import com.fs.starfarer.api.impl.campaign.ids.Factions
import com.fs.starfarer.api.util.IntervalUtil
import com.fs.starfarer.api.util.Misc
import lunalib.lunaSettings.LunaSettings
import org.lwjgl.util.vector.Vector2f

class LunaCampaignRenderer : EveryFrameScript {

    companion object {
        @JvmStatic
        fun addRenderer(renderer: lunalib.lunaUtil.campaign.LunaCampaignRenderingPlugin) {
            getScript().getNonTransientRenderers().add(renderer)
        }

        @JvmStatic
        fun addTransientRenderer(renderer: lunalib.lunaUtil.campaign.LunaCampaignRenderingPlugin) {
            getScript().getTransientRenderers().add(renderer)
        }

        @JvmStatic
        fun removeRenderer(renderer: lunalib.lunaUtil.campaign.LunaCampaignRenderingPlugin) {
            getScript().getNonTransientRenderers().remove(renderer)
        }

        @JvmStatic
        fun removeTransientRenderer(renderer: lunalib.lunaUtil.campaign.LunaCampaignRenderingPlugin) {
            getScript().getTransientRenderers().remove(renderer)
        }

        @JvmStatic
        fun hasRenderer(renderer: lunalib.lunaUtil.campaign.LunaCampaignRenderingPlugin) : Boolean {
            return getScript().getRenderers().contains(renderer)
        }

        @JvmStatic
        fun hasRendererOfClass(rendererClass: Class<*>) : Boolean {
            return getScript().getRenderers().any { it.javaClass.name == rendererClass.name }
        }

        @JvmStatic
        fun getRendererOfClass(rendererClass: Class<*>) : lunalib.lunaUtil.campaign.LunaCampaignRenderingPlugin? {
            return getScript().getRenderers().find { it.javaClass.name == rendererClass.name }
        }

        @JvmStatic
        fun getScript() : LunaCampaignRenderer {
            var script = Global.getSector().memoryWithoutUpdate.get("\$luna_campaign_renderer") as LunaCampaignRenderer?
            if (script == null) {
                script = LunaCampaignRenderer()
                Global.getSector().memoryWithoutUpdate.set("\$luna_campaign_renderer", script)
            }
            return script
        }
    }

    private var renderers: ArrayList<lunalib.lunaUtil.campaign.LunaCampaignRenderingPlugin> = ArrayList()
    @Transient private var transientRenderers: ArrayList<lunalib.lunaUtil.campaign.LunaCampaignRenderingPlugin>?  = ArrayList()


    fun getTransientRenderers() : ArrayList<lunalib.lunaUtil.campaign.LunaCampaignRenderingPlugin> {
        if (transientRenderers == null) {
            transientRenderers = ArrayList<lunalib.lunaUtil.campaign.LunaCampaignRenderingPlugin>()
        }
        return transientRenderers!!
    }

    fun getNonTransientRenderers() : ArrayList<lunalib.lunaUtil.campaign.LunaCampaignRenderingPlugin> {
        return renderers
    }

    fun getRenderers() : ArrayList<lunalib.lunaUtil.campaign.LunaCampaignRenderingPlugin> {
        var renderers = ArrayList<lunalib.lunaUtil.campaign.LunaCampaignRenderingPlugin>()
        renderers.addAll(getTransientRenderers())
        renderers.addAll(getNonTransientRenderers())
        return renderers
    }


    override fun isDone(): Boolean {
        return false
    }


    override fun runWhilePaused(): Boolean {
        return true
    }


    var interval = IntervalUtil(2f, 3f)
    override fun advance(amount: Float) {

        interval.advance(amount)
        var renderers = getRenderers()

        var currentLocation = Global.getSector().playerFleet?.containingLocation
        if (currentLocation != null) {
            if (currentLocation.customEntities.none { it.customEntitySpec?.id == "luna_campaign_renderer" }) {
                var entity = currentLocation.addCustomEntity("luna_campaign_renderer_${Misc.genUID()}", "", "luna_campaign_renderer", Factions.NEUTRAL)
                entity.location.set(Vector2f())
            }
        }

        if (interval.intervalElapsed()) {
            var locations = ArrayList<LocationAPI>()
            locations.add(Global.getSector().hyperspace)
            locations.addAll(Global.getSector().starSystems)

            /*for (location in locations) {
                var entity = location.customEntities.find { it.customEntitySpec?.id == "luna_campaign_renderer" } ?: continue
                if (!entity.isInCurrentLocation) {
                    location.removeEntity(entity)
                }
            }*/

            for (location in locations) {
                if (location.customEntities.none { it.customEntitySpec?.id == "luna_campaign_renderer" }) {
                    var entity = location.addCustomEntity("luna_campaign_renderer_${Misc.genUID()}", "", "luna_campaign_renderer", Factions.NEUTRAL)
                    entity.location.set(Vector2f())
                }
            }
        }


    }
}