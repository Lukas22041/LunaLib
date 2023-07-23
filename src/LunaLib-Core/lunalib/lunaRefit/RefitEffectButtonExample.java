package lunalib.lunaRefit;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

//Example refit button that lets the player toggle a ship between automated/crewed.
public class RefitEffectButtonExample extends BaseRefitButton {

    @Override
    public String getButtonName(FleetMemberAPI member, ShipVariantAPI variant) {
        if (isAutomated(variant)) return "Crew Conversion";
        return "Automated Conversion";
    }

    @Override
    public String getIconName(FleetMemberAPI member, ShipVariantAPI variant) {
        if (isAutomated(variant)) return "graphics/icons/skills/crew_training.png";
        return "graphics/icons/skills/automated_ships.png";
    }

    @Override
    public int getOrder(FleetMemberAPI member, ShipVariantAPI variant) {
        return 9999;
    }

    @Override
    public boolean hasTooltip(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        return true;
    }

    @Override
    public void addTooltip(TooltipMakerAPI tooltip, FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        tooltip.addPara("[Lunalib Devmode Example]", 0f, Misc.getBasePlayerColor(), Misc.getBasePlayerColor());
        tooltip.addSpacer(5f);

        if (isAutomated(variant)) tooltip.addPara("Converts this hull in to a crewed ship. ", 0f);
        else tooltip.addPara("Converts this hull in to an automated ship.", 0f);

        if (hasCaptain(member)) {
            tooltip.addSpacer(10f);
            tooltip.addPara("Can not converted while an officer is assigned to the ship.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor());
        }
    }

    //Gets triggered when the button was pressed.
    @Override
    public void onClick(FleetMemberAPI member, ShipVariantAPI variant, InputEventAPI event, MarketAPI market) {

        //When adding hullmods or changing stats, ALWAYS modify the provided variant, do not use member.getVariant()
        if (isAutomated(variant)) {
            variant.removePermaMod(HullMods.AUTOMATED);
        }
        else {
            variant.addPermaMod(HullMods.AUTOMATED);
        }

        member.setCaptain(null);

        //After changing the refit-variant, you always want to refresh it using this method. Otherwise the refit menu will often not have the changes applied, and may loose them
        //when leaving the refit menu.
        //Calling this method will cause the "Undo" button to set its "Revert" variant be set to the current variant. So avoid calling it when no changes were made.
        refreshVariant();

        //Triggers the list of buttons to refresh, which we want in this case to have the changes to the button name & icon apply.
        refreshButtonList();
    }

    //Makes the button not clickable if the ship has a captain.
    @Override
    public boolean isClickable(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        return !hasCaptain(member);
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
