package lunalib.lunaSettings

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.SettingsAPI
import com.fs.starfarer.loading.specs.BaseWeaponSpec
import com.fs.starfarer.util.ReplaceableSprite
import lunalib.backend.ui.settings.LunaSettingsData
import lunalib.backend.ui.settings.LunaSettingsLoader
import org.apache.log4j.Level
import java.awt.Color

/**
Class for getting LunaSettings stored data.

[LunaSettings on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/Integrating-LunaSettings)

*/
object LunaSettings
{

    private val log = Global.getLogger(LunaSettings::class.java)

    @JvmStatic
    internal var listeners: MutableList<LunaSettingsListener> = ArrayList()

    init {
        log.level = Level.ALL
    }

    @JvmStatic
    fun addSettingsListener(listener: LunaSettingsListener) {
        listeners.add(listener)
    }

    @JvmStatic
    fun removeSettingsListener(listener: LunaSettingsListener) {
        listeners.remove(listener)
    }

    @JvmStatic
    fun hasSettingsListenerOfClass(listenerClass: Class<*>) : Boolean {
        return listeners.any { it.javaClass.name == listenerClass.name }
    }

    @JvmStatic
    fun reportSettingsChanged(modID: String) {
        for (listener in listeners)
        {
            try {
                listener.settingsChanged(modID)
            }
            catch (e: Throwable) {
                Global.getLogger(this.javaClass).debug("Failed to call LunaSettings listener for $modID")
            }
        }
    }


    @Deprecated("Deprecated due to not being JvmStatic, please use addSettingsListener() instead.")
    fun addListener(listener: LunaSettingsListener) {
        addSettingsListener(listener)
    }

    @Deprecated("Deprecated due to not being JvmStatic, please use removeSettingsListener() instead.")
    fun removeListener(listener: LunaSettingsListener) {
        removeSettingsListener(listener)
    }

    @Deprecated("Deprecated due to not being JvmStatic, please use hasSettingsListenerOfClass() instead.")
    fun hasListenerOfClass(listenerClass: Class<*>) : Boolean {
        return hasSettingsListenerOfClass(listenerClass)
    }

    /*fun addListener(listener: LunaSettingsListener)
    {
        if (!listeners.contains(listener))
        listeners.add(listener)
    }*/

    /**
    Method for getting a Double from LunaSettings.

    [LunaSettings on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/Integrating-LunaSettings)

    Requires the [ModID] and the [FieldID].

    Can Return null if either the mod or field is not found.
     */
    @JvmStatic
    fun getDouble(ModID: String, FieldID: String) : Double?
    {
        if (!LunaSettingsLoader.hasLoaded) LunaSettingsLoader.load()
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
        if (!LunaSettingsLoader.hasLoaded) LunaSettingsLoader.load()
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
        if (!LunaSettingsLoader.hasLoaded) LunaSettingsLoader.load()
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
        if (!LunaSettingsLoader.hasLoaded) LunaSettingsLoader.load()
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
        if (!LunaSettingsLoader.hasLoaded) LunaSettingsLoader.load()
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
        if (!LunaSettingsLoader.hasLoaded) LunaSettingsLoader.load()
        if (LunaSettingsLoader.Settings.get(ModID) != null)
        {
            try {
                var text = LunaSettingsLoader.Settings.get(ModID)!!.getString(FieldID)
                if (!text.contains("#"))
                {
                    text = "#$text"
                }
                var color = Color.decode(text.trim().uppercase())
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
     Class used for creating Settings at Runtime. This is not recommended over the usual approach through LunaSettings.CSV.

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
        fun refresh(ModID: String)
        {
            LunaSettingsLoader.saveDefaultsToFile(modID = ModID)
            LunaSettingsLoader.loadSettings(modID = ModID, reload = true)
        }

        /**
        Deprecated, please use the refresh method with the modID parameter
        */
        @JvmStatic
        fun refresh()
        {
            LunaSettingsLoader.saveDefaultsToFile()
            LunaSettingsLoader.loadSettings()
        }

        @JvmStatic
        fun addHeader(ModID: String, FieldID: String, Title: String, Tab: String)
        {
            if (LunaSettingsLoader.SettingsData.find { it.modID == ModID && it.fieldID == FieldID } != null) return
            LunaSettingsLoader.SettingsData.add(LunaSettingsData(ModID, FieldID, "", "Header", "", Title,"", 0.0, 0.0, Tab))
        }

        @JvmStatic
        fun addText(ModID: String, FieldID: String, Text: String, Tab: String)
        {
            if (LunaSettingsLoader.SettingsData.find { it.modID == ModID && it.fieldID == FieldID } != null) return
            LunaSettingsLoader.SettingsData.add(LunaSettingsData(ModID, FieldID, "", "Text", "", Text, "",0.0, 0.0, Tab))
        }

        @JvmStatic
        fun addInt(ModID: String, FieldID: String, FieldName: String, Tooltip: String, DefaultValue: Int, MinValue: Int, MaxValue: Int, Tab: String)
        {
            if (LunaSettingsLoader.SettingsData.find { it.modID == ModID && it.fieldID == FieldID } != null) return
            LunaSettingsLoader.SettingsData.add(LunaSettingsData(ModID, FieldID, FieldName, "Int", Tooltip, DefaultValue,"", MinValue.toDouble(), MaxValue.toDouble(), Tab))
        }

        @JvmStatic
        fun addDouble(ModID: String, FieldID: String, FieldName: String, Tooltip: String, DefaultValue: Double, MinValue: Double, MaxValue: Double, Tab: String)
        {
            if (LunaSettingsLoader.SettingsData.find { it.modID == ModID && it.fieldID == FieldID } != null) return
            LunaSettingsLoader.SettingsData.add(LunaSettingsData(ModID, FieldID, FieldName, "Double", Tooltip, DefaultValue,"", MinValue, MaxValue, Tab))
        }

        @JvmStatic
        fun addString(ModID: String, FieldID: String, FieldName: String, Tooltip: String, DefaultValue: String, Tab: String)
        {
            if (LunaSettingsLoader.SettingsData.find { it.modID == ModID && it.fieldID == FieldID } != null) return
            LunaSettingsLoader.SettingsData.add(LunaSettingsData(ModID, FieldID, FieldName, "String", Tooltip, DefaultValue, "",0.0, 0.0, Tab))
        }

        @JvmStatic
        fun addBoolean(ModID: String, FieldID: String, FieldName: String, Tooltip: String, DefaultValue: Boolean, Tab: String)
        {
            if (LunaSettingsLoader.SettingsData.find { it.modID == ModID && it.fieldID == FieldID } != null) return
            LunaSettingsLoader.SettingsData.add(LunaSettingsData(ModID, FieldID, FieldName, "Boolean", Tooltip, DefaultValue, "",0.0, 0.0, Tab))
        }

        @JvmStatic
        fun addEnum(ModID: String, FieldID: String, FieldName: String, Tooltip: String, DefaultValue: List<String>, Tab: String)
        {
            if (LunaSettingsLoader.SettingsData.find { it.modID == ModID && it.fieldID == FieldID } != null) return
            LunaSettingsLoader.SettingsData.add(LunaSettingsData(ModID, FieldID, FieldName, "Enum", Tooltip, DefaultValue, "",0.0, 0.0, Tab))
        }

        @JvmStatic
        fun addKeybind(ModID: String, FieldID: String, FieldName: String, Tooltip: String, DefaultValue: Int, Tab: String)
        {
            if (LunaSettingsLoader.SettingsData.find { it.modID == ModID && it.fieldID == FieldID } != null) return
            LunaSettingsLoader.SettingsData.add(LunaSettingsData(ModID, FieldID, FieldName, "Keycode", Tooltip, DefaultValue, "",0.0, 0.0, Tab))
        }

        @JvmStatic
        fun addColor(ModID: String, FieldID: String, FieldName: String, Tooltip: String, DefaultValue: Color, Tab: String)
        {
            if (LunaSettingsLoader.SettingsData.find { it.modID == ModID && it.fieldID == FieldID } != null) return
            var text = String.format("#%02x%02x%02x", DefaultValue!!.red, DefaultValue!!.green, DefaultValue!!.blue);
            LunaSettingsLoader.SettingsData.add(LunaSettingsData(ModID, FieldID, FieldName, "Color", Tooltip, text, "",0.0, 0.0, Tab))
        }

        /**
        Adds a Radio-Button Selection. secondaryValue is a comma-seperated list that generates the choices, default value is the one thats being selected by default.
        */
        @JvmStatic
        fun addRadio(ModID: String, FieldID: String, FieldName: String, Tooltip: String, DefaultValue: String, secondaryValue: String, Tab: String)
        {
            if (LunaSettingsLoader.SettingsData.find { it.modID == ModID && it.fieldID == FieldID } != null) return
            LunaSettingsLoader.SettingsData.add(LunaSettingsData(ModID, FieldID, FieldName, "Radio", Tooltip, DefaultValue, secondaryValue,0.0, 0.0, Tab))
        }


        @JvmStatic
        fun addHeader(ModID: String, FieldID: String, Title: String)
        {
            addHeader(ModID, FieldID, Title, "")
        }

        @JvmStatic
        fun addText(ModID: String, FieldID: String, Text: String)
        {
            addText(ModID, FieldID, Text, "")
        }

        @JvmStatic
        fun addInt(ModID: String, FieldID: String, FieldName: String, Tooltip: String, DefaultValue: Int, MinValue: Int, MaxValue: Int)
        {
            addInt(ModID, FieldID, FieldName, Tooltip, DefaultValue, MinValue, MaxValue, "")
        }

        @JvmStatic
        fun addDouble(ModID: String, FieldID: String, FieldName: String, Tooltip: String, DefaultValue: Double, MinValue: Double, MaxValue: Double)
        {
            addDouble(ModID, FieldID, FieldName, Tooltip, DefaultValue, MinValue, MaxValue, "")

        }

        @JvmStatic
        fun addString(ModID: String, FieldID: String, FieldName: String, Tooltip: String, DefaultValue: String)
        {
            addString(ModID, FieldID, FieldName, Tooltip, DefaultValue, "")
        }

        @JvmStatic
        fun addBoolean(ModID: String, FieldID: String, FieldName: String, Tooltip: String, DefaultValue: Boolean)
        {
            addBoolean(ModID, FieldID, FieldName, Tooltip, DefaultValue, "")
        }

        @JvmStatic
        fun addKeybind(ModID: String, FieldID: String, FieldName: String, Tooltip: String, DefaultValue: Int)
        {
            addKeybind(ModID, FieldID, FieldName, Tooltip, DefaultValue, "")
        }

        @JvmStatic
        fun addColor(ModID: String, FieldID: String, FieldName: String, Tooltip: String, DefaultValue: Color)
        {
            addColor(ModID, FieldID, FieldName, Tooltip, DefaultValue, "")
        }
    }
}