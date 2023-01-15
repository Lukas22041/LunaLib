package lunalib.lunaExtensions

import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.util.Misc

//File for Kotlin Extension Functions of SectorEntityToken. This is only useable in Kotlin, and not Java.

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun MarketAPI.getStorageCargo() =
    Misc.getStorageCargo(this)

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun MarketAPI.getLocalResourcesCargo() =
    Misc.getLocalResourcesCargo(this)

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun MarketAPI.doesPlayerHaveStorageAccess() =
    Misc.playerHasStorageAccess(this)