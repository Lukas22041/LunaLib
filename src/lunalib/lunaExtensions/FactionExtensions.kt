package lunalib.lunaExtensions

import com.fs.starfarer.api.campaign.*
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.combat.ShipHullSpecAPI
import com.fs.starfarer.api.loading.FighterWingSpecAPI
import com.fs.starfarer.api.loading.HullModSpecAPI
import com.fs.starfarer.api.loading.WeaponSpecAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaUtil.campaign.FactionUtils


//File for Kotlin Extension Functions of FactionAPI.

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun FactionAPI.getMarketsCopy() : List<MarketAPI> =
   FactionUtils.getMarketsCopy(this.id)

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun FactionAPI.getKnownShipSpecs() : List<ShipHullSpecAPI> =
    FactionUtils.getKnownShipSpecs(this.id)

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun FactionAPI.getKnownHullmodSpecs() : List<HullModSpecAPI> =
    FactionUtils.getKnownHullmodSpecs(this.id)

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun FactionAPI.getKnownWeaponSpecs() : List<WeaponSpecAPI> =
    FactionUtils.getKnownWeaponSpecs(this.id)

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun FactionAPI.getKnownFightersSpecs() : List<FighterWingSpecAPI> =
    FactionUtils.getKnownFighterSpecs(this.id)

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun FactionAPI.existsInSector() =
    FactionUtils.existsInSector(this.id)

