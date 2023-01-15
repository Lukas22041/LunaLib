package lunalib.lunaExtensions

import com.fs.starfarer.api.campaign.rules.MemoryAPI

//File for Kotlin Extension Functions of MemoryAPI. This is only useable in Kotlin, and not Java.

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun MemoryAPI.isNull(key: String) =
    this.get(key) == null

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun MemoryAPI.isNotNull(key: String) =
    this.get(key) != null

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun MemoryAPI.getList(key: String) : List<*>? {
    if (this.get(key) is List<*>) return this.get(key) as List<*>
    else return null
}

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun MemoryAPI.getMap(key: String) : Map<*, *>? {
    if (this.get(key) is Map<*, *>) return this.get(key) as Map<*, *>?
    else return null
}

/** (**LunaLib Extension Function**) [LunaExtensions on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/LunaExtensions)*/
fun MemoryAPI.getArray(key: String) : Array<*>? {
    if (this.get(key) is Array<*>) return this.get(key) as Array<*>
    else return null
}



