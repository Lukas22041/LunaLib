package lunalib.backend.scripts

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CoreUITabId
import com.fs.starfarer.api.impl.campaign.DebugFlags
import com.fs.starfarer.api.util.IntervalUtil
import lunalib.backend.ui.debug.OpenDebugWindowInteractionV2
import lunalib.backend.ui.settings.OpenSettingsPanelInteraction
import org.lwjgl.input.Keyboard

internal class KeybindsScript :  EveryFrameScript
{

    var keyPressed = false
    var interval = IntervalUtil(0.8f, 0.8f)


    init {
        Global.getSector().listenerManager.addListener(this, true)
    }

    override fun advance(amount: Float) {

        interval.advance(amount)

        val paused = Global.getSector().campaignUI.currentCoreTab == CoreUITabId.FLEET || Global.getSector().campaignUI.currentCoreTab == null
                && !Global.getSector().campaignUI.isShowingDialog && !Global.getSector().campaignUI.isShowingMenu

        if (!paused) return

        if (interval.intervalElapsed())  keyPressed = false
        if (keyPressed) return

        if (Keyboard.isKeyDown(LoadedSettings.devmodeKeybind!!) && !Keyboard.isKeyDown(Keyboard.KEY_NONE))
        {
            Global.getSettings().isDevMode = !Global.getSettings().isDevMode
            DebugFlags.setStandardConfig()
            keyPressed = true
        }

        if (Keyboard.isKeyDown(LoadedSettings.settingsKeybind!!)) openSettings()
        else if (Keyboard.isKeyDown(Keyboard.KEY_F2) && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) openSettings()
        else if (Keyboard.isKeyDown(LoadedSettings.debugKeybind!!)) openDebug()
    }

    fun openSettings()
    {
        if (Keyboard.isKeyDown(Keyboard.KEY_NONE) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LMENU)) return
        var ui = Global.getSector().campaignUI

        ui.showInteractionDialog(OpenSettingsPanelInteraction(), Global.getSector().playerFleet)


        keyPressed = true
    }

    fun openDebug()
    {
        if (Keyboard.isKeyDown(Keyboard.KEY_NONE) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) return

        var ui = Global.getSector().campaignUI
        //ui.showInteractionDialog(OpenDebugWindowInteraction(), Global.getSector().playerFleet)
        ui.showInteractionDialog(OpenDebugWindowInteractionV2(), Global.getSector().playerFleet)

        keyPressed = true
    }

    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
        return true
    }
}