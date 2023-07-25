package lunalib.lunaRefit;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import lunalib.lunaUI.elements.LunaSpriteElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Example refit button that opens a custom panel that displays a sprite.
public class RefitPanelButtonExample extends BaseRefitButton {



    @Override
    public String getButtonName(FleetMemberAPI member, ShipVariantAPI variant) {
        return "Display Sprite";
    }

    @Override
    public String getIconName(FleetMemberAPI member, ShipVariantAPI variant) {
        return "graphics/icons/missions/analyze_entity.png";
    }

    @Override
    public int getOrder(FleetMemberAPI member, ShipVariantAPI variant) {
        return 100000;
    }

    @Override
    public boolean hasTooltip(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        return true;
    }

    @Override
    public void addTooltip(TooltipMakerAPI tooltip, FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        tooltip.addPara("[Lunalib Devmode Example]", 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor());

        tooltip.addSpacer(10f);
        tooltip.addPara("Displays the ships sprite.", 0f);
    }

    @Override
    public float getPanelWidth(FleetMemberAPI member, ShipVariantAPI variant) {
        SpriteAPI sprite = Global.getSettings().getSprite(variant.getHullSpec().getSpriteName());
        return sprite.getWidth();
    }

    @Override
    public float getPanelHeight(FleetMemberAPI member, ShipVariantAPI variant) {
        SpriteAPI sprite = Global.getSettings().getSprite(variant.getHullSpec().getSpriteName());
        return sprite.getHeight();
    }

    @Override
    public boolean hasPanel(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        return true;
    }

    @Override
    public void initPanel(CustomPanelAPI backgroundPanel, FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {

        float width = getPanelWidth(member, variant);
        float height = getPanelHeight(member, variant);

        TooltipMakerAPI element = backgroundPanel.createUIElement(width, height, false);
        backgroundPanel.addUIElement(element);
        element.getPosition().inTL(0f, 0f);

        LunaSpriteElement sprite =new LunaSpriteElement(variant.getHullSpec().getSpriteName(), LunaSpriteElement.ScalingTypes.NONE, element, width, height);
        sprite.getPosition().inTL(0f, 0f);
    }

    @Override
    public void onPanelClose(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        String test = "";
    }

    //Causes the button to only show up in devmode.
    @Override
    public boolean shouldShow(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        return Global.getSettings().isDevMode();
    }







    public boolean isAutomated(ShipVariantAPI variant) {
        return variant.hasHullMod(HullMods.AUTOMATED);
    }

    public boolean hasCaptain(FleetMemberAPI member) {
        return member.getCaptain() != null && !member.getCaptain().getNameString().equals("");
    }
}
