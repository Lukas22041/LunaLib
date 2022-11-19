package lunalib.lunaExtensions

import com.fs.starfarer.api.SettingsAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import lunalib.lunaUtil.LunaMisc

//File for Kotlin Extension Functions of SettingsAPI. This is only useable in Kotlin, and not Java.

/** (**LunaLib Extension Function**)*/
fun SettingsAPI.getAllShipVariants() : List<ShipVariantAPI> =
     LunaMisc.getAllShipVariants()
