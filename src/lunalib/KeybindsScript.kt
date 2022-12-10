package lunalib

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CoreUITabId
import com.fs.starfarer.api.impl.campaign.DebugFlags
import com.fs.starfarer.api.util.IntervalUtil
import lunalib.lunaSettings.LunaSettings
import lunalib.lunaSettings.LunaSettingsListener
import lunalib.lunaSettings.OpenSettingsPanelInteraction
import org.lwjgl.input.Keyboard

internal class KeybindsScript : LunaSettingsListener, EveryFrameScript
{

    var keyPressed = false
    var interval = IntervalUtil(1f, 1f)
    var settingsKeybind = LunaSettings.getInt("lunalib", "luna_SettingsKeybind")
    var debugKeybind = LunaSettings.getInt("lunalib", "luna_DebugKeybind")
    var devmodeKeybind = LunaSettings.getInt("lunalib", "luna_DevmodeKeybind")

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

        if (Keyboard.isKeyDown(devmodeKeybind!!))
        {
            Global.getSettings().isDevMode = !Global.getSettings().isDevMode
            DebugFlags.setStandardConfig()
            keyPressed = true
        }

        if (Keyboard.isKeyDown(settingsKeybind!!)) openSettings()
        else if (Keyboard.isKeyDown(Keyboard.KEY_F2) && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) openSettings()
        else if (Keyboard.isKeyDown(debugKeybind!!)) openDebug()
    }

    fun openSettings()
    {
        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LMENU)) return
        var ui = Global.getSector().campaignUI
        ui.showInteractionDialog(OpenSettingsPanelInteraction(), Global.getSector().playerFleet)

        keyPressed = true
    }

    fun openDebug()
    {
        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) return

        var ui = Global.getSector().campaignUI
        ui.showInteractionDialog(OpenDebugWindowInteraction(), Global.getSector().playerFleet)

        keyPressed = true
    }

    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
        return true
    }

    override fun settingsChanged() {
        settingsKeybind = LunaSettings.getInt("lunalib", "luna_SettingsKeybind")
        debugKeybind = LunaSettings.getInt("lunalib", "luna_DebugKeybind")
        devmodeKeybind = LunaSettings.getInt("lunalib", "luna_DevmodeKeybind")
    }
}