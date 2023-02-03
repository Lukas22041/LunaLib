package lunalib.backend.ui.settings

import com.fs.starfarer.api.Global
import org.apache.log4j.Level
import org.json.JSONArray
import org.lazywizard.lazylib.JSONUtils
import org.lazywizard.lazylib.MathUtils

data class LunaSettingsData(
    val modID: String,
    val fieldID: String,
    val fieldName: String,
    val fieldType: String,
    val fieldDescription: String,
    val defaultValue: Any,
    val minValue: Double,
    val maxValue: Double)

/**
Class that both loads and holds data for LunaSettings. Can not be used outside LunaLib.

[LunaSettings on the Github Wiki](https://github.com/Lukas22041/LunaLib/wiki/Integrating-LunaSettings)
*/
internal object LunaSettingsLoader
{

    @JvmStatic
    var Settings: MutableMap<String, JSONUtils.CommonDataJSONObject> = HashMap()

    @JvmStatic
    var SettingsData: MutableList<LunaSettingsData> = ArrayList()

    private val log = Global.getLogger(LunaSettingsLoader::class.java)

    init {
        log.level = Level.ALL
    }

    fun loadDefault()
    {
        //SettingsData.clear()
        val CSV: JSONArray = Global.getSettings().getMergedSpreadsheetDataForMod("fieldID", "data/config/LunaSettings.csv", "LunaLib")

        var data: MutableList<LunaSettingsData> = ArrayList()
        for (index in 0 until CSV.length())
        {
            val rows = CSV.getJSONObject(index)

            val modID = rows.getString("modID");
            val id = rows.getString("fieldID");
            val name = rows.getString("fieldName");
            val type = rows.getString("fieldType")

            var description = ""
            try {
                description = rows.getString("fieldTooltip")
            } catch (e: Throwable) {}
            try {
                description = rows.getString("fieldDescription")
            } catch (e: Throwable) {}

            var default: Any = rows.getString("defaultValue")

            when (type)
            {
                "Int" -> default = default.toString().toInt()
                "Boolean" -> default = default.toString().toBoolean()
                "Double" -> default = default.toString().toDouble()
                "String" -> default = default.toString()
                "Enum" -> default = default.toString().split(",")
                "Keycode" -> default = default.toString().toInt()
            }

            if (type == "Enum")
            {
                var list: MutableList<String> = ArrayList()
                for (entry in default as List<String>)
                {
                    list.add(entry.trim())
                }
                default = list
            }

            var minValue = 0.0
            var maxValue = 0.0

            if (type == "Int" || type == "Double")
            {
                try {
                    minValue = MathUtils.clamp(rows.getString("minValue").toFloat(), Int.MIN_VALUE.toFloat(), Int.MAX_VALUE.toFloat()).toDouble()
                }
                catch (e: Throwable)
                {
                    maxValue = 0.0
                }

                try {
                    maxValue =  MathUtils.clamp(rows.getString("maxValue").toFloat(), Int.MIN_VALUE.toFloat(), Int.MAX_VALUE.toFloat()).toDouble()
                }
                catch (e: Throwable)
                {
                    maxValue = 100.0
                }
            }

            SettingsData.add(LunaSettingsData(modID, id, name, type, description, default, minValue, maxValue))
            log.debug("LunaSettings: Loaded default settings data: $id, from $modID")
        }
    }

    fun saveDefaultsToFile(modID: String = "")
    {
        var defaults = SettingsData
        var mods = when (modID) {
            "" -> Global.getSettings().modManager.enabledModsCopy
            else -> listOf(Global.getSettings().modManager.getModSpec(modID))
        }

        for (mod in mods)
        {
            var data = JSONUtils.loadCommonJSON("LunaSettings/${mod.id}.json", "data/config/LunaSettingsDefault.default");

            for (default in defaults)
            {
                if (default.fieldType == "Text" || default.fieldType == "Header") continue
                if (default.modID == mod.id)
                {
                    try
                    {
                        data!!.get(default.fieldID)
                    }
                    catch (e: Throwable)
                    {
                        if (default.fieldType == "Enum")
                        {
                            data!!.put(default.fieldID, (default.defaultValue as List<String>).get(0))
                        }
                        else
                        {
                            data!!.put(default.fieldID, default.defaultValue)
                        }
                    }
                }
            }

            if (data.length() > 0)
            {
                data.save()
            }
        }
    }

    fun loadSettings(modID: String = "", reload: Boolean = false)
    {
        //Settings.clear()
        var mods = when (modID) {
            "" -> Global.getSettings().modManager.enabledModsCopy
            else -> listOf(Global.getSettings().modManager.getModSpec(modID))
        }

        for (mod in mods)
        {
            var data = JSONUtils.loadCommonJSON("LunaSettings/${mod.id}.json", "data/config/LunaSettingsDefault.default");
            if (data.length() == 0 || data == null)
            {
                if (!reload)
                {
                    log.debug("LunaSettings: Could not find any mod settings for ${mod.id}, skipping.")
                }
                continue
            }
            Settings.put(mod.id, data)
            if (!reload)
            {
                log.debug("LunaSettings: Loaded Mod Settings for ${mod.id}")
            }
            else
            {
                log.debug("LunaSettings: Reloaded Mod Settings for ${mod.id}")
            }
        }
    }
}