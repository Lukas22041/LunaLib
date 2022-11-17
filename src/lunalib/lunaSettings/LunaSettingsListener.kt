package lunalib.lunaSettings

import com.fs.starfarer.api.EveryFrameScript

interface LunaSettingsListener : EveryFrameScript {
    abstract fun settingsChanged()
}

