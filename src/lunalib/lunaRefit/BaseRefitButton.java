package lunalib.lunaRefit;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.loading.specs.HullVariantSpec;
import lunalib.backend.ui.refit.RefitButtonAdder;

import java.awt.*;

public abstract class BaseRefitButton {


    /* Closes the panel when called.*/
    public final void closePanel() {
        RefitButtonAdder.setRemoveActivePanel(true);
    }

    /* Refreshes the list of Refit Buttons. */
    public final void refreshButtonList() {
        RefitButtonAdder.setUpdateBackgroundPanel(true);
    }

    /* Has to be called after modifying a ShipVariantAPI in any form*/
    public final void refreshVariant() {
        RefitButtonAdder.setRequiresVariantUpdate(true);
    }

    /** Sets the Width of the background panel.*/
    public float getPanelWidth(FleetMemberAPI member, ShipVariantAPI variant) {
        return 960f;
    }

   /** Sets the Height of the Background panel.*/
    public float getPanelHeight(FleetMemberAPI member, ShipVariantAPI variant) {
        return 540f;
    }

    public String getButtonName(FleetMemberAPI member, ShipVariantAPI variant) {
        return "Button";
    }

    /**The order in which buttons are displayed, Lower values are shown at the top*/
    public int getOrder(FleetMemberAPI member, ShipVariantAPI variant) {
        return 100;
    }

    public String getIconName(FleetMemberAPI member, ShipVariantAPI variant) {
        return "graphics/icons/missions/tutorial_mission.png";
    }

    /** Return true to make the button visible*/
    public boolean shouldShow(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        return true;
    }

    //Grays out the button and prevents onClick() and initPanel() from being called.
    public boolean isClickable(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        return true;
    }

    /** Gets called when the button is clicked*/
    public void onClick(FleetMemberAPI member, ShipVariantAPI variant, InputEventAPI event, MarketAPI market) {

    }

    /** Return false to prevent the panel from being created, also stops initPanel() from being called*/
    public boolean hasPanel(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        return false;
    }

    /** Override this method to be able to interact with the created panel.*/
    public void initPanel(CustomPanelAPI backgroundPanel, FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {

    }

    public void onPanelClose(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {

    }

    /** Called when the button exists in the menu.*/
    public void advance( FleetMemberAPI member, ShipVariantAPI variant, Float amount, MarketAPI market) {

    }

    /** Return true to add a tooltip to the button.*/
    public boolean hasTooltip(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        return false;
    }


    public float getToolipWidth(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        return 300f;
    }

    /** Allows adding a tooltip to the button, only active if hasTooltip() returns true*/
    public void addTooltip(TooltipMakerAPI tooltip, FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {

    }
}
