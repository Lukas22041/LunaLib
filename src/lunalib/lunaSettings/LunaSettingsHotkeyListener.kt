package lunalib.lunaSettings

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CoreUITabId
import org.lwjgl.input.Keyboard

class LunaSettingsHotkeyListener : EveryFrameScript
{

    var keyPressed = false
    var keyCooldown = 0
    var keyCooldownMax = 50

    override fun advance(amount: Float) {

        val paused = Global.getSector().campaignUI.currentCoreTab == CoreUITabId.FLEET || Global.getSector().campaignUI.currentCoreTab == null
                && !Global.getSector().campaignUI.isShowingDialog && !Global.getSector().campaignUI.isShowingMenu

        if (keyPressed)
        {
            if (keyCooldown <= keyCooldownMax)
            {
                keyCooldown++
            }
            else
            {
                keyCooldown = 0
                keyPressed = false
            }
            return
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_F2))
        {
            var ui = Global.getSector().campaignUI
            ui.showInteractionDialog(OpenSettingsPanelInteraction(), Global.getSector().playerFleet)

            keyPressed = true
        }
    }

    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
        return true
    }

}