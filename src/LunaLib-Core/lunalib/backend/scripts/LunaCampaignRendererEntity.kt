package lunalib.backend.scripts

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignEngineLayers
import com.fs.starfarer.api.campaign.CustomCampaignEntityPlugin
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.combat.ViewportAPI
import com.fs.starfarer.api.impl.campaign.BaseCustomEntityPlugin
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.campaign.BaseCampaignEntity
import lunalib.lunaUtil.campaign.LunaCampaignRenderer

class LunaCampaignRendererEntity : BaseCustomEntityPlugin() {


    override fun render(layer: CampaignEngineLayers?, viewport: ViewportAPI?) {

        if (!entity.isInCurrentLocation) return

        for (renderer in LunaCampaignRenderer.getScript().getRenderers()) {
            if (renderer.activeLayers.contains(layer)) {
                renderer.render(layer, viewport)
            }
        }
    }

    override fun advance(amount: Float) {

        if (!entity.isInCurrentLocation) return

        var script = LunaCampaignRenderer.getScript()
        var renderers = script.getRenderers()

        for (renderer in renderers) {

            var expired = renderer.isExpired

            if (expired) {
                script.getTransientRenderers().remove(renderer)
                script.getNonTransientRenderers().remove(renderer)
            } else {
                renderer.advance(amount)
            }
        }
    }

    override fun getRenderRange(): Float {
        return 100000000f
    }
}