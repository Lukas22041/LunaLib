package lunalib.backend.scripts

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
        for (renderer in LunaCampaignRenderer.getScript().getRenderers()) {
            if (renderer.activeLayers.contains(layer)) {
                renderer.render(layer, viewport)
            }
        }
    }

    override fun getRenderRange(): Float {
        return 100000000f
    }
}