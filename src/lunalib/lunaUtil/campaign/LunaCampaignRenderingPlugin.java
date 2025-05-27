package lunalib.lunaUtil.campaign;

import com.fs.starfarer.api.campaign.CampaignEngineLayers;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.combat.CombatEngineLayers;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.ViewportAPI;

import java.util.EnumSet;

/**
*A plugin that functions similary as to a CombatLayeredRenderingPlugin but for campaign use.
*Allows rendering without having to add your own entity for everything.

* Needs to be added to the campaign through LunaCampaignRenderer.addRenderer() or LunaCampaignRenderer.addTransientRenderer().
* Transient Renderers are removed on safe-load, non Transient Renderers remain until expired.
 */
public interface LunaCampaignRenderingPlugin {

    public boolean isExpired();

    public void advance(float amount);
    public EnumSet<CampaignEngineLayers> getActiveLayers();
    public void render(CampaignEngineLayers layer, ViewportAPI viewport);

}
