package lunalib.lunaExtensions

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.SettingsAPI
import com.fs.starfarer.api.campaign.LocationAPI
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.impl.campaign.procgen.StarAge
import com.fs.starfarer.api.util.Misc
import lunalib.lunaUtil.LunaMisc
import lunalib.lunaUtil.campaign.SettingsUtils
import lunalib.lunaUtil.campaign.SystemUtils

//File for Kotlin Extension Functions of SettingsAPI. This is only useable in Kotlin, and not Java.

/** (**LunaLib Extension Function**)*/
fun SettingsAPI.getAllVariants() : List<ShipVariantAPI>
{
    return SettingsUtils.getAllVariants()
}
