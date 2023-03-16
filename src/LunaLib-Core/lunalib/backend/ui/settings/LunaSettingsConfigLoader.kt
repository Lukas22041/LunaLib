package lunalib.backend.ui.settings

import com.fs.starfarer.api.Global

object LunaSettingsConfigLoader {

    var configs = Global.getSettings().getMergedJSONForMod("data/config/LunaSettingsConfig.json", "lunalib")

    fun reload() {
        configs = Global.getSettings().getMergedJSONForMod("data/config/LunaSettingsConfig.json", "lunalib")
    }

    fun getConfigsForMod(modId: String) =  configs.getJSONObject(modId)

    fun getIconPath(modId: String) : String
    {
        var iconPath = ""
        try {
            iconPath = getConfigsForMod(modId).getString("iconPath")
        } catch (e: Throwable) {}
        return iconPath
    }

}