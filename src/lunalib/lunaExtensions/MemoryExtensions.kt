package lunalib.lunaExtensions

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.rules.MemoryAPI

//File for Kotlin Extension Functions of MemoryAPI.

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun MemoryAPI.isNull(key: String) =
    this.get(key) == null

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun MemoryAPI.isNotNull(key: String) =
    this.get(key) != null

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun <T> MemoryAPI.getList(key: String) : List<T>? {
    return this.get(key) as List<T> ?: return null
}

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun <T, K> MemoryAPI.getMap(key: String) : Map<T, K>? {
    return this.get(key) as Map<T, K>? ?: return null
}

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun <T> MemoryAPI.getArray(key: String) : Array<T>? {
    return this.get(key) as Array<T> ?: return null
}

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun SectorEntityToken.saveToMemory(key: String) {
    Global.getSector().memoryWithoutUpdate.set(key, this)
}



