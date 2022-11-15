package lunalib.lunaSettings

import com.fs.starfarer.api.Global

object LunaSettings
{
    /**
     * Gets a double from the LunaSettings.
     * @param modID The ID of the mod the setting is from.
     * @param fieldID the ID of the field you are trying to get the value from
     * @param SaveSpecific if false, accesses a cross-save value, if set to true, accesses a save specific value.
     * @exception exception throws an exception if no setting is found.
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
                    throw Exception("LunaSettings: $FieldID of type Double not found in JSONObject (Mod: $ModID)")
                }
            }
            else
            {
                try {
                    return (Global.getSector().memoryWithoutUpdate.get("\$LunaSettings") as MutableMap<String, Map<String, Any>>).get(ModID)!!.get(FieldID) as Double
                }
                catch(e: Throwable) {
                    throw Exception("LunaSettings: $FieldID of type Double not found in Memory (Mod: $ModID)")
                }
            }
        }
        else
        {
            throw Exception("LunaSettings: Could not find mod $ModID")
        }
    }

    /**
     * Gets a double from the LunaSettings and converts it in to a float.
     * @param modID The ID of the mod the setting is from.
     * @param fieldID the ID of the field you are trying to get the value from
     * @param SaveSpecific if false, accesses a cross-save value, if set to true, accesses a save specific value.
     * @exception exception throws an exception if no setting is found.
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
                    throw Exception("LunaSettings: $FieldID of type Float not found in JSONObject (Mod: $ModID)")
                }
            }
            else
            {
                try {
                    return ((Global.getSector().memoryWithoutUpdate.get("\$LunaSettings") as MutableMap<String, Map<String, Any>>).get(ModID)!!.get(FieldID) as Double).toFloat()
                }
                catch(e: Throwable) {
                    throw Exception("LunaSettings: $FieldID of type Float not found in Memory (Mod: $ModID)")
                }
            }
        }
        else
        {
            throw Exception("LunaSettings: Could not find mod $ModID")
        }
    }

    /**
     * Gets a Boolean from the LunaSettings.
     * @param modID The ID of the mod the setting is from.
     * @param fieldID the ID of the field you are trying to get the value from
     * @param SaveSpecific if false, accesses a cross-save value, if set to true, accesses a save specific value.
     * @exception exception throws an exception if no setting is found.
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
                    throw Exception("LunaSettings: $FieldID of type Boolean not found in JSONObject (Mod: $ModID)")
                }
            }
            else
            {
                try {
                    return (Global.getSector().memoryWithoutUpdate.get("\$LunaSettings") as MutableMap<String, Map<String, Any>>).get(ModID)!!.get(FieldID) as Boolean
                }
                catch(e: Throwable) {
                    throw Exception("LunaSettings: $FieldID of type Boolean not found in Memory (Mod: $ModID)")
                }
            }
        }
        else
        {
            throw Exception("LunaSettings: Could not find mod $ModID")
        }
    }

    /**
     * Gets an Integer from the LunaSettings.
     * @param modID The ID of the mod the setting is from.
     * @param fieldID the ID of the field you are trying to get the value from
     * @param SaveSpecific if false, accesses a cross-save value, if set to true, accesses a save specific value.
     * @exception exception throws an exception if no setting is found.
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
                    throw Exception("LunaSettings: $FieldID of type Int not found in JSONObject (Mod: $ModID)")
                }
            }
            else
            {
                try {
                    return (Global.getSector().memoryWithoutUpdate.get("\$LunaSettings") as MutableMap<String, Map<String, Any>>).get(ModID)!!.get(FieldID) as Int
                }
                catch(e: Throwable) {
                    throw Exception("LunaSettings: $FieldID of type Int not found in Memory (Mod: $ModID)")
                }
            }
        }
        else
        {
            throw Exception("LunaSettings: Could not find mod $ModID")
        }
    }

    /**
     * Gets a String from the LunaSettings.
     * @param modID The ID of the mod the setting is from.
     * @param fieldID the ID of the field you are trying to get the value from
     * @param SaveSpecific if false, accesses a cross-save value, if set to true, accesses a save specific value.
     * @exception exception throws an exception if no setting is found.
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
                    throw Exception("LunaSettings: $FieldID of type String not found in JSONObject (Mod: $ModID)")
                }
            }
            else
            {
                try {
                    return (Global.getSector().memoryWithoutUpdate.get("\$LunaSettings") as MutableMap<String, Map<String, Any>>).get(ModID)!!.get(FieldID) as String
                }
                catch(e: Throwable) {
                    throw Exception("LunaSettings: $FieldID of type String not found in Memory (Mod: $ModID)")
                }
            }
        }
        else
        {
            throw Exception("LunaSettings: Could not find mod $ModID")
        }
    }

}