package lunalib.lunaSettings

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.combat.ShipAPI.HullSize
import org.apache.log4j.Level

/**
Class for getting LunaSettings stored data.

[LunaSettings on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/Integrating-LunaSettings)

*/
object LunaSettings
{

    private val log = Global.getLogger(LunaSettings::class.java)

    init {
        log.level = Level.ALL
    }

    /**
    Method for getting a Double from LunaSettings.

    [LunaSettings on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/Integrating-LunaSettings)

    Requires the [ModID], the [FieldID] of the Setting and a [SaveSpecific] Boolean, that needs to be set to true if the Field is set to "newGame".

    Can Return null if either the mod or field is not found.
     */
    @JvmStatic
    fun getDouble(ModID: String, FieldID: String) : Double?
    {
        if (LunaSettingsLoader.Settings.get(ModID) != null)
        {
            try {
                return LunaSettingsLoader.Settings.get(ModID)!!.getDouble(FieldID)
            }
            catch(e: Throwable) {
                log.error("LunaSettings: Value $FieldID of type Double not found in JSONObject (ModID: $ModID)")
                return null
            }
        }
        else
        {
            log.error("LunaSettings: Could not find mod $ModID")
            return null
        }
    }

    /**
    Method for getting a Float from LunaSettings. Since floats dont exist in .json's, it just converts a saved double.

    [LunaSettings on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/Integrating-LunaSettings)

    Requires the [ModID], the [FieldID] of the Setting and a [SaveSpecific] Boolean, that needs to be set to true if the Field is set to "newGame".

    Can Return null if either the mod or field is not found.
     */
    @JvmStatic
    fun getFloat(ModID: String, FieldID: String) : Float?
    {
        if (LunaSettingsLoader.Settings.get(ModID) != null)
        {
            try {
                return LunaSettingsLoader.Settings.get(ModID)!!.getDouble(FieldID).toFloat()
            }
            catch(e: Throwable) {
                log.error("LunaSettings: Value $FieldID of type Float not found in JSONObject (ModID: $ModID)")
                return null
            }
        }
        else
        {
            log.error("LunaSettings: Could not find mod $ModID")
            return null
        }
    }

    /**
    Method for getting a Boolean from LunaSettings.

    [LunaSettings on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/Integrating-LunaSettings)

    Requires the [ModID], the [FieldID] of the Setting and a [SaveSpecific] Boolean, that needs to be set to true if the Field is set to "newGame".

    Can Return null if either the mod or field is not found.
     */
    @JvmStatic
    fun getBoolean(ModID: String, FieldID: String) : Boolean?
    {
        if (LunaSettingsLoader.Settings.get(ModID) != null)
        {
            try {
                return LunaSettingsLoader.Settings.get(ModID)!!.getBoolean(FieldID)
            }
            catch(e: Throwable) {
                log.error("LunaSettings: Value $FieldID of type Boolean not found in JSONObject (ModID: $ModID)")
                return null
            }
        }
        else
        {
            log.error("LunaSettings: Could not find mod $ModID")
            return null
        }
    }

    /**
    Method for getting an Int from LunaSettings.

    [LunaSettings on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/Integrating-LunaSettings)

    Requires the [ModID], the [FieldID] of the Setting and a [SaveSpecific] Boolean, that needs to be set to true if the Field is set to "newGame".

    Can Return null if either the mod or field is not found.
     */
    @JvmStatic
    fun getInt(ModID: String, FieldID: String) : Int?
    {
        if (LunaSettingsLoader.Settings.get(ModID) != null)
        {
            try {
                return LunaSettingsLoader.Settings.get(ModID)!!.getInt(FieldID)
            }
            catch(e: Throwable) {
                log.error("LunaSettings: Value $FieldID of type Int not found in JSONObject (ModID: $ModID)")
                return null
            }
        }
        else
        {
            log.error("LunaSettings: Could not find mod $ModID")
            return null
        }
    }

    /**
    Method for getting a String from LunaSettings.

    [LunaSettings on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/Integrating-LunaSettings)

    Requires the [ModID], the [FieldID] of the Setting and a [SaveSpecific] Boolean, that needs to be set to true if the Field is set to "newGame".

    Can Return null if either the mod or field is not found.
     */
    @JvmStatic
    fun getString(ModID: String, FieldID: String) : String?
    {
        if (LunaSettingsLoader.Settings.get(ModID) != null)
        {
            try {
                return LunaSettingsLoader.Settings.get(ModID)!!.getString(FieldID)
            }
            catch(e: Throwable) {
                log.error("LunaSettings: Value $FieldID of type String not found in JSONObject (ModID: $ModID)")
                return null
            }
        }
        else
        {
            log.error("LunaSettings: Could not find mod $ModID")
            return null
        }
    }





    /**
    Deprecated, use the methods without the [SaveSpecific] parameter
     */
    @JvmStatic
    fun getDouble(ModID: String, FieldID: String, SaveSpecific: Boolean) : Double?
    {
        if (LunaSettingsLoader.Settings.get(ModID) != null)
        {
            try {
                return LunaSettingsLoader.Settings.get(ModID)!!.getDouble(FieldID)
            }
            catch(e: Throwable) {
                log.error("LunaSettings: Value $FieldID of type Double not found in JSONObject (ModID: $ModID)")
                return null
            }
        }
        else
        {
            log.error("LunaSettings: Could not find mod $ModID")
            return null
        }
    }

    /**
    Deprecated, use the methods without the [SaveSpecific] parameter
     */
    @JvmStatic
    fun getFloat(ModID: String, FieldID: String, SaveSpecific: Boolean) : Float?
    {
        if (LunaSettingsLoader.Settings.get(ModID) != null)
        {
            try {
                return LunaSettingsLoader.Settings.get(ModID)!!.getDouble(FieldID).toFloat()
            }
            catch(e: Throwable) {
                log.error("LunaSettings: Value $FieldID of type Float not found in JSONObject (ModID: $ModID)")
                return null
            }
        }
        else
        {
            log.error("LunaSettings: Could not find mod $ModID")
            return null
        }
    }

    /**
    Deprecated, use the methods without the [SaveSpecific] parameter
     */
    @JvmStatic
    fun getBoolean(ModID: String, FieldID: String, SaveSpecific: Boolean) : Boolean?
    {
        if (LunaSettingsLoader.Settings.get(ModID) != null)
        {
            try {
                return LunaSettingsLoader.Settings.get(ModID)!!.getBoolean(FieldID)
            }
            catch(e: Throwable) {
                log.error("LunaSettings: Value $FieldID of type Boolean not found in JSONObject (ModID: $ModID)")
                return null
            }
        }
        else
        {
            log.error("LunaSettings: Could not find mod $ModID")
            return null
        }
    }

    /**
    Deprecated, use the methods without the [SaveSpecific] parameter
     */
    @JvmStatic
    fun getInt(ModID: String, FieldID: String, SaveSpecific: Boolean) : Int?
    {
        if (LunaSettingsLoader.Settings.get(ModID) != null)
        {
            try {
                return LunaSettingsLoader.Settings.get(ModID)!!.getInt(FieldID)
            }
            catch(e: Throwable) {
                log.error("LunaSettings: Value $FieldID of type Int not found in JSONObject (ModID: $ModID)")
                return null
            }
        }
        else
        {
            log.error("LunaSettings: Could not find mod $ModID")
            return null
        }
    }

    /**
    Deprecated, use the methods without the [SaveSpecific] parameter
     */
    @JvmStatic
    fun getString(ModID: String, FieldID: String, SaveSpecific: Boolean) : String?
    {
        if (LunaSettingsLoader.Settings.get(ModID) != null)
        {
            try {
                return LunaSettingsLoader.Settings.get(ModID)!!.getString(FieldID)
            }
            catch(e: Throwable) {
                log.error("LunaSettings: Value $FieldID of type String not found in JSONObject (ModID: $ModID)")
                return null
            }
        }
        else
        {
            log.error("LunaSettings: Could not find mod $ModID")
            return null
        }
    }
}