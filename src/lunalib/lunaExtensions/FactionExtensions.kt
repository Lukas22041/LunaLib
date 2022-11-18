package lunalib.lunaExtensions

import com.fs.starfarer.api.campaign.*
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.combat.ShipHullSpecAPI
import com.fs.starfarer.api.loading.FighterWingSpecAPI
import com.fs.starfarer.api.loading.HullModSpecAPI
import com.fs.starfarer.api.loading.WeaponSpecAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaUtil.LunaMisc
import lunalib.lunaUtil.campaign.FactionUtils


//File for Kotlin Extension Functions of FactionAPI. This is only useable in Kotlin, and not Java.

/** (**LunaLib Extension Function**)*/
fun FactionAPI.getMarketsCopy() : List<MarketAPI> =
   FactionUtils.getMarketsCopy(this.id)

/** (**LunaLib Extension Function**)*/
fun FactionAPI.getKnownShipSpecs() : List<ShipHullSpecAPI> =
    FactionUtils.getKnownShipSpecs(this.id)

/** (**LunaLib Extension Function**)*/
fun FactionAPI.getKnownHullmodSpecs() : List<HullModSpecAPI> =
    FactionUtils.getKnownHullmodSpecs(this.id)

/** (**LunaLib Extension Function**)*/
fun FactionAPI.getKnownWeaponSpecs() : List<WeaponSpecAPI> =
    FactionUtils.getKnownWeaponSpecs(this.id)

/** (**LunaLib Extension Function**)*/
fun FactionAPI.getKnownFightersSpecs() : List<FighterWingSpecAPI> =
    FactionUtils.getKnownFighterSpecs(this.id)

/** (**LunaLib Extension Function**)*/
fun FactionAPI.getNumHostileMarkets(from: SectorEntityToken, maxDist: Float) =
    Misc.getNumHostileMarkets(this, from, maxDist)

/** (**LunaLib Extension Function**)*/
fun FactionAPI.isPirateFaction() =
    Misc.isPirateFaction(this)

/** (**LunaLib Extension Function**)*/
fun FactionAPI.isDecentralized() =
    Misc.isDecentralized(this)
