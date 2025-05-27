package lunalib.lunaExtensions

import com.fs.starfarer.api.campaign.LocationAPI
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.impl.campaign.procgen.StarAge
import com.fs.starfarer.api.util.Misc
import lunalib.lunaUtil.campaign.SystemUtils

//File for Kotlin Extension Functions of StarSystemAPI and LocationAPI.

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun StarSystemAPI.getCustomEntitiesWithType(typeID: String) =
    SystemUtils.getCustomEntitiesWithType(this, typeID)

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun StarSystemAPI.getPlanetsWithType(typeID: String) =
    SystemUtils.getPlanetsWithType(this, typeID)

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun StarSystemAPI.getPlanetsWithCondition(typeID: String) =
    SystemUtils.getPlanetsWithCondition(this, typeID)

