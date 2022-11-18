package lunalib.lunaUtil

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.ShipVariantAPI

/**
A class containing a collection of different Utilities for SettingsAPI

[SystemUtils on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/CampaignUtils)
 */
object SettingsUtils
{
    fun getAllVariants() : List<ShipVariantAPI> =
         Global.getSettings().allVariantIds.map { Global.getSettings().getVariant(it) }
}