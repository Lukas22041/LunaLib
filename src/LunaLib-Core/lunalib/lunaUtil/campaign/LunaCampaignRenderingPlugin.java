package lunalib.lunaUtil.campaign;

import com.fs.starfarer.api.campaign.CampaignEngineLayers;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.combat.CombatEngineLayers;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.ViewportAPI;

import java.util.EnumSet;

public interface LunaCampaignRenderingPlugin {

    public boolean isExpired();

    public void advance(float amount);
    public EnumSet<CampaignEngineLayers> getActiveLayers();
    public void render(CampaignEngineLayers layer, ViewportAPI viewport);

}
