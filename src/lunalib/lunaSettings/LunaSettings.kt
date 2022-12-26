package lunalib.lunaSettings

import com.fs.starfarer.api.Global
import org.apache.log4j.Level
import java.awt.Color

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

    Requires the [ModID] and the [FieldID].

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

    Requires the [ModID] and the [FieldID].

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

    Requires the [ModID] and the [FieldID].

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

    Requires the [ModID] and the [FieldID].

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

    Requires the [ModID] and the [FieldID].

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
    Method for getting a Color from LunaSettings.

    [LunaSettings on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/Integrating-LunaSettings)

    Requires the [ModID] and the [FieldID].

    Can Return null if either the mod or field is not found.
     */
    @JvmStatic
    fun getColor(ModID: String, FieldID: String) : Color?
    {
        if (LunaSettingsLoader.Settings.get(ModID) != null)
        {
            try {
                var text = LunaSettingsLoader.Settings.get(ModID)!!.getString(FieldID)
                var rgba = text.split(",")
                var color = Color(rgba.get(0).trim().toInt(), rgba.get(1).trim().toInt(), rgba.get(2).trim().toInt(), rgba.get(3).trim().toInt())
                return color
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
     Class used for creating Settings at Runtime. This is not recommended over the usual approach through LunaSettings.CSV
     Main use is for Settings that should only appear when another mod is loaded, or to dynamicly create Settings.
     I.e creating a setting that changes behaviour for every Faction that has been loaded.
     ```java
     *public void addNexelerinSettings()
     *{
     *   if (Global.getSettings().getModManager().isModEnabled("nexerelin"))
     *   {
     *      LunaSettings.SettingsCreator.addHeader("lunalib", "lunalib_nex_compat_settings", "Nexerelin Compat Settings");
     *      LunaSettings.SettingsCreator.addBoolean("lunalib", "lunalib_enable_thing", "Enable ______", "Tooltip", false, new ArrayList<String>());
     *      //SettingsCreator.Refresh has to be called to apply any added Settings.
     *      LunaSettings.SettingsCreator.refresh();
     *   }
     *}
    ```
     */
     object SettingsCreator
    {
        /**
        Has to be called after Settings have been added, otherwise they will not load.
         */
        @JvmStatic
        fun refresh()
        {
            LunaSettingsLoader.saveDefaultsToFile()
            LunaSettingsLoader.loadSettings()
        }

        @JvmStatic
        fun addHeader(ModID: String, FieldID: String, Title: String)
        {
            LunaSettingsLoader.SettingsData.add(LunaSettingsData(ModID, FieldID, "", "Header", "", Title, 0.0, 0.0, listOf("")))
        }

        @JvmStatic
        fun addText(ModID: String, FieldID: String, Text: String)
        {
            LunaSettingsLoader.SettingsData.add(LunaSettingsData(ModID, FieldID, "", "Text", "", Text, 0.0, 0.0, listOf("")))
        }

        @JvmStatic
        fun addInt(ModID: String, FieldID: String, FieldName: String, Tooltip: String, DefaultValue: Int, MinValue: Int, MaxValue: Int, Tags: List<String>)
        {
            LunaSettingsLoader.SettingsData.add(LunaSettingsData(ModID, FieldID, FieldName, "Int", Tooltip, DefaultValue, MinValue.toDouble(), MaxValue.toDouble(), Tags))
        }

        @JvmStatic
        fun addDouble(ModID: String, FieldID: String, FieldName: String, Tooltip: String, DefaultValue: Double, MinValue: Double, MaxValue: Double, Tags: List<String>)
        {
            LunaSettingsLoader.SettingsData.add(LunaSettingsData(ModID, FieldID, FieldName, "Double", Tooltip, DefaultValue, MinValue, MaxValue, Tags))
        }

        @JvmStatic
        fun addString(ModID: String, FieldID: String, FieldName: String, Tooltip: String, DefaultValue: String, Tags: List<String>)
        {
            LunaSettingsLoader.SettingsData.add(LunaSettingsData(ModID, FieldID, FieldName, "String", Tooltip, DefaultValue, 0.0, 0.0, Tags))
        }

        @JvmStatic
        fun addBoolean(ModID: String, FieldID: String, FieldName: String, Tooltip: String, DefaultValue: Boolean, Tags: List<String>)
        {
            LunaSettingsLoader.SettingsData.add(LunaSettingsData(ModID, FieldID, FieldName, "Boolean", Tooltip, DefaultValue, 0.0, 0.0, Tags))
        }

        @JvmStatic
        fun addEnum(ModID: String, FieldID: String, FieldName: String, Tooltip: String, DefaultValue: List<String>, Tags: List<String>)
        {
            LunaSettingsLoader.SettingsData.add(LunaSettingsData(ModID, FieldID, FieldName, "Enum", Tooltip, DefaultValue, 0.0, 0.0, Tags))
        }

        @JvmStatic
        fun addKeybind(ModID: String, FieldID: String, FieldName: String, Tooltip: String, DefaultValue: Int, Tags: List<String>)
        {
            LunaSettingsLoader.SettingsData.add(LunaSettingsData(ModID, FieldID, FieldName, "Keycode", Tooltip, DefaultValue, 0.0, 0.0, Tags))
        }

        @JvmStatic
        fun addColor(ModID: String, FieldID: String, FieldName: String, Tooltip: String, DefaultValue: Color, Tags: List<String>)
        {
            var text = "${DefaultValue.red}, ${DefaultValue.green}, ${DefaultValue.blue}, ${DefaultValue.alpha}"
            LunaSettingsLoader.SettingsData.add(LunaSettingsData(ModID, FieldID, FieldName, "Color", Tooltip, text, 0.0, 0.0, Tags))
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