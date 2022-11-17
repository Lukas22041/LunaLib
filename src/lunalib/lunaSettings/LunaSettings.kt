package lunalib.lunaSettings

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipAPI.HullSize
import org.apache.log4j.Level

object LunaSettings
{

    private val log = Global.getLogger(LunaSettings::class.java)

    init {
        log.level = Level.ALL
    }

    /**
     * Gets a double from the LunaSettings.
     * @param modID The ID of the mod the setting is from.
     * @param fieldID the ID of the field you are trying to get the value from
     * @param SaveSpecific if false, accesses a cross-save value, if set to true, accesses a save specific value.
     * @return returns the requested value. Can be null if the mod or value was not found.
    */
    @JvmStatic
    fun getDouble(ModID: String, FieldID: String, SaveSpecific: Boolean) : Double?
    {
        if (LunaSettingsLoader.Settings.get(ModID) != null)
        {
            if (!SaveSpecific)
            {
                try {
                    return LunaSettingsLoader.Settings.get(ModID)!!.getDouble(FieldID)
                }
                catch(e: Throwable) {
                    log.error("LunaSettings: Value $FieldID of type Double not found in JSONObject (Mod: $ModID)")
                    return null
                }
            }
            else
            {
                try {
                    return (Global.getSector().memoryWithoutUpdate.get("\$LunaSettings") as MutableMap<String, Map<String, Any>>).get(ModID)!!.get(FieldID) as Double
                }
                catch(e: Throwable) {
                    log.error("LunaSettings: Value $FieldID of type Double not found in Memory (Mod: $ModID)")
                    return null
                }
            }
        }
        else
        {
            log.error("LunaSettings: Could not find mod $ModID")
            return null
        }
    }

    /**
     * Gets a double from the LunaSettings and converts it in to a float.
     * @param modID The ID of the mod the setting is from.
     * @param fieldID the ID of the field you are trying to get the value from
     * @param SaveSpecific if false, accesses a cross-save value, if set to true, accesses a save specific value.
     * @return returns the requested value. Can be null if the mod or value was not found.
     */
    @JvmStatic
    fun getFloat(ModID: String, FieldID: String, SaveSpecific: Boolean) : Float?
    {
        if (LunaSettingsLoader.Settings.get(ModID) != null)
        {
            if (!SaveSpecific)
            {
                try {
                    return LunaSettingsLoader.Settings.get(ModID)!!.getDouble(FieldID).toFloat()
                }
                catch(e: Throwable) {
                    log.error("LunaSettings: Value $FieldID of type Float not found in JSONObject (Mod: $ModID)")
                    return null
                }
            }
            else
            {
                try {
                    return ((Global.getSector().memoryWithoutUpdate.get("\$LunaSettings") as MutableMap<String, Map<String, Any>>).get(ModID)!!.get(FieldID) as Double).toFloat()
                }
                catch(e: Throwable) {
                    log.error("LunaSettings: Value $FieldID of type Float not found in Memory (Mod: $ModID)")
                    return null
                }
            }
        }
        else
        {
            log.error("LunaSettings: Could not find mod $ModID")
            return null
        }
    }

    /**
     * Gets a Boolean from the LunaSettings.
     * @param modID The ID of the mod the setting is from.
     * @param fieldID the ID of the field you are trying to get the value from
     * @param SaveSpecific if false, accesses a cross-save value, if set to true, accesses a save specific value.
     * @return returns the requested value. Can be null if the mod or value was not found.
     */
    @JvmStatic
    fun getBoolean(ModID: String, FieldID: String, SaveSpecific: Boolean) : Boolean?
    {
        if (LunaSettingsLoader.Settings.get(ModID) != null)
        {
            if (!SaveSpecific)
            {
                try {
                    return LunaSettingsLoader.Settings.get(ModID)!!.getBoolean(FieldID)
                }
                catch(e: Throwable) {
                    log.error("LunaSettings: Value $FieldID of type Boolean not found in JSONObject (Mod: $ModID)")
                    return null
                }
            }
            else
            {
                try {
                    return (Global.getSector().memoryWithoutUpdate.get("\$LunaSettings") as MutableMap<String, Map<String, Any>>).get(ModID)!!.get(FieldID) as Boolean
                }
                catch(e: Throwable) {
                    log.error("LunaSettings: Value $FieldID of type Boolean not found in Memory (Mod: $ModID)")
                    return null
                }
            }
        }
        else
        {
            log.error("LunaSettings: Could not find mod $ModID")
            return null
        }
    }

    /**
     * Gets an Integer from the LunaSettings.
     * @param modID The ID of the mod the setting is from.
     * @param fieldID the ID of the field you are trying to get the value from
     * @param SaveSpecific if false, accesses a cross-save value, if set to true, accesses a save specific value.
     * @return returns the requested value. Can be null if the mod or value was not found.
     */
    @JvmStatic
    fun getInt(ModID: String, FieldID: String, SaveSpecific: Boolean) : Int?
    {
        if (LunaSettingsLoader.Settings.get(ModID) != null)
        {
            if (!SaveSpecific)
            {
                try {
                    return LunaSettingsLoader.Settings.get(ModID)!!.getInt(FieldID)
                }
                catch(e: Throwable) {
                    log.error("LunaSettings: Value $FieldID of type Int not found in JSONObject (Mod: $ModID)")
                    return null
                }
            }
            else
            {
                try {
                    return (Global.getSector().memoryWithoutUpdate.get("\$LunaSettings") as MutableMap<String, Map<String, Any>>).get(ModID)!!.get(FieldID) as Int
                }
                catch(e: Throwable) {
                    log.error("LunaSettings: Value $FieldID of type Int not found in Memory (Mod: $ModID)")
                    return null
                }
            }
        }
        else
        {
            log.error("LunaSettings: Could not find mod $ModID")
            return null
        }
    }

    /**
     * Gets a String from the LunaSettings.
     * @param modID The ID of the mod the setting is from.
     * @param fieldID the ID of the field you are trying to get the value from
     * @param SaveSpecific if false, accesses a cross-save value, if set to true, accesses a save specific value.
     * @return returns the requested value. Can be null if the mod or value was not found.
     */
    @JvmStatic
    fun getString(ModID: String, FieldID: String, SaveSpecific: Boolean) : String?
    {
        if (LunaSettingsLoader.Settings.get(ModID) != null)
        {
            if (!SaveSpecific)
            {
                try {
                    return LunaSettingsLoader.Settings.get(ModID)!!.getString(FieldID)
                }
                catch(e: Throwable) {
                    log.error("LunaSettings: Value $FieldID of type String not found in JSONObject (Mod: $ModID)")
                    return null
                }
            }
            else
            {
                try {
                    return (Global.getSector().memoryWithoutUpdate.get("\$LunaSettings") as MutableMap<String, Map<String, Any>>).get(ModID)!!.get(FieldID) as String
                }
                catch(e: Throwable) {
                    log.error("LunaSettings: Value $FieldID of type String not found in Memory (Mod: $ModID)")
                    return null
                }
            }
        }
        else
        {
            log.error("LunaSettings: Could not find mod $ModID")
            return null
        }
    }
}