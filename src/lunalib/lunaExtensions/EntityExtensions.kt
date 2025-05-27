package lunalib.lunaExtensions

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.InteractionDialogPlugin
import com.fs.starfarer.api.campaign.JumpPointAPI
import com.fs.starfarer.api.campaign.PlanetAPI
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.impl.campaign.procgen.PlanetConditionGenerator
import com.fs.starfarer.api.impl.campaign.procgen.StarAge
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.api.util.Misc.FleetFilter

//File for Kotlin Extension Functions of SectorEntityToken and PlanetAPI.

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun SectorEntityToken.showInteractionDialog(plugin: InteractionDialogPlugin) =
    Global.getSector().campaignUI.showInteractionDialog(plugin, this)

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun PlanetAPI.generatePlanetCondition(starAge: StarAge) =
    PlanetConditionGenerator.generateConditionsForPlanet(this, starAge)


