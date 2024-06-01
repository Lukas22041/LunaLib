package lunalib.lunaSettings

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global

/**
Listener for LunaSettings. [settingsChanged] gets called whenever settings are saved.

[LunaSettingsListener on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaSettingsListener)

 */
interface LunaSettingsListener {
    abstract fun settingsChanged(modID: String)
}

