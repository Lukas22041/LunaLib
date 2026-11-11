package lunalib.backend.scripts

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.listeners.CampaignInputListener
import com.fs.starfarer.api.impl.campaign.DebugFlags
import com.fs.starfarer.api.input.InputEventAPI
import lunalib.lunaSettings.LunaSettings
import org.lwjgl.input.Keyboard

class DevmodeKeyListener : CampaignInputListener {
    override fun getListenerInputPriority(): Int {
        return 0;
    }

    override fun processCampaignInputPreCore(events: List<InputEventAPI?>?) {

    }

    override fun processCampaignInputPreFleetControl(events: List<InputEventAPI?>?) {
        if (events == null) return
        for (event in events) {
            if (event!!.isConsumed) return
            if (event.isKeyDownEvent && event.eventValue == LoadedSettings.devmodeKeybind) {
                if (event.eventValue != Keyboard.KEY_NONE) {
                    Global.getSettings().isDevMode = !Global.getSettings().isDevMode
                    DebugFlags.setStandardConfig()
                    break
                }
            }
        }
    }

    override fun processCampaignInputPostCore(events: List<InputEventAPI?>?) {

    }
}