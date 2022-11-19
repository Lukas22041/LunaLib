package lunalib.lunaExtensions

import com.fs.starfarer.api.campaign.LocationAPI
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.impl.campaign.procgen.StarAge
import com.fs.starfarer.api.util.Misc
import lunalib.lunaUtil.LunaMisc
import lunalib.lunaUtil.campaign.SystemUtils

//File for Kotlin Extension Functions of MemoryAPI. This is only useable in Kotlin, and not Java.

/** (**LunaLib Extension Function**)*/
fun MemoryAPI.isNull(key: String) =
    this.get(key) == null

/** (**LunaLib Extension Function**)*/
fun MemoryAPI.isNotNull(key: String) =
    this.get(key) != null

/** (**LunaLib Extension Function**)*/
fun MemoryAPI.getList(key: String) : List<*>? {
    if (this.get(key) is List<*>) return this.get(key) as List<*>
    else return null
}

/** (**LunaLib Extension Function**)*/
fun MemoryAPI.getMap(key: String) : Map<*, *>? {
    if (this.get(key) is Map<*, *>) return this.get(key) as Map<*, *>?
    else return null
}

/** (**LunaLib Extension Function**)*/
fun MemoryAPI.getArray(key: String) : Array<*>? {
    if (this.get(key) is Array<*>) return this.get(key) as Array<*>
    else return null
}



