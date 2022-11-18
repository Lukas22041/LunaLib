package lunalib.lunaUtil.campaign

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.combat.ShipHullSpecAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.loading.FighterWingSpecAPI
import com.fs.starfarer.api.loading.HullModSpecAPI
import com.fs.starfarer.api.loading.WeaponSpecAPI

/**
A class containing a collection of different Utilities for SettingsAPI

[SystemUtils on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/CampaignUtils)
 */
object SettingsUtils
{
    fun getAllVariants() : List<ShipVariantAPI>
    {
        var list: MutableList<ShipVariantAPI> = ArrayList()
        Global.getSettings().allVariantIds.forEach { list.add(Global.getSettings().getVariant(it)) }
        return list
    }
}