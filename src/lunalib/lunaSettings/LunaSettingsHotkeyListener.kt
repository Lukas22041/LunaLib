package lunalib.lunaSettings

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CoreUITabId
import lunalib.lunaUtil.LunaTimer
import org.lwjgl.input.Keyboard

class LunaSettingsHotkeyListener : EveryFrameScript
{

    var keyPressed = false
    var keyCooldown = 0
    var keyCooldownMax = 50

    var timer = LunaTimer()

    override fun advance(amount: Float) {

        val paused = Global.getSector().campaignUI.currentCoreTab == CoreUITabId.FLEET || Global.getSector().campaignUI.currentCoreTab == null
                && !Global.getSector().campaignUI.isShowingDialog && !Global.getSector().campaignUI.isShowingMenu

        var test = timer.getDays()
        var test2 = timer.getSeconds()
        var test3 = 0


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

        var key: Int = when(LunaSettings.getString("lunalib", "luna_SettingsHotkey", false))
        {
            "F1" -> Keyboard.KEY_F1
            "F2" -> Keyboard.KEY_F2
            "F3" -> Keyboard.KEY_F3
            "F4" -> Keyboard.KEY_F4
            else -> Keyboard.KEY_F2
        }

        if (Keyboard.isKeyDown(key))
        {

            var ui = Global.getSector().campaignUI
            ui.showInteractionDialog(OpenSettingsPanelInteraction(), Global.getSector().playerFleet)

            /*  var ui = Global.getSector().campaignUI
            ui.showInteractionDialog(Example(), Global.getSector().playerFleet)*/

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