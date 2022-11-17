package lunalib.lunaSettings

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global

//Listener used to check if settings changed. Only gets called from the Mod Menu in the campaign.
//Has to be added through Global.getSector().listenerManager.addListener()
/**
* Listener used to check if LunaSettings got changed. Only gets called from the Mod Menu in the campaign.
 *
* Has to be added through Global.getSector().listenerManager.addListener()
*/
interface LunaSettingsListener {
    abstract fun settingsChanged()
}

