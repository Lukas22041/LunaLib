package lunalib.lunaUtil

import org.json.JSONObject
import org.lazywizard.lazylib.JSONUtils

/**
 * A small class for saving cross-save data.
 *
 * The file for this is saved within: Starsector/Saves/Commons/LunaCommons.json.data.
 *
 * All mods share the same file.
 */
object LunaCommons {

    private var init = false
    private var masterObject: JSONUtils.CommonDataJSONObject = JSONUtils.CommonDataJSONObject("")

    @JvmStatic
    fun set(modID: String, key: String, value: Any) {
        init()

        var modObject = try {
            masterObject.getJSONObject(modID)
        } catch (e: Throwable) { JSONObject() }

        modObject.put(key, value)
        masterObject.put(modID, modObject)
        masterObject.save()
    }

    @JvmStatic
    fun get(modKey: String, key: String) : Any? {
        init()

        var modObject = try {
            masterObject.getJSONObject(modKey)
        } catch (e: Throwable) { return null }

        try {
            return modObject.get(key)
        } catch (e: Throwable) { return null }
    }

    @JvmStatic
    fun getString(modKey: String, key: String) : String? {
        return get(modKey, key) as String?
    }

    @JvmStatic
    fun getInt(modKey: String, key: String) : Int? {
        return get(modKey, key) as Int?
    }

    @JvmStatic
    fun getDouble(modKey: String, key: String) : Double? {
        return get(modKey, key) as Double?
    }

    @JvmStatic
    fun getBoolean(modKey: String, key: String) : Boolean? {
        return get(modKey, key) as Boolean?
    }

    @JvmStatic
    fun remove(modKey: String, key: String) {
        var modObject = try {
            masterObject.getJSONObject(modKey)
        } catch (e: Throwable) { return  }

        try {
             modObject.remove(key)
        } catch (e: Throwable) { return }

        masterObject.save()
    }

    @JvmStatic
    fun init() {
        if (init) return
        masterObject = JSONUtils.loadCommonJSON("LunaCommons.json")
        masterObject.save()
        init = true
    }
}