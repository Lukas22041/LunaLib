package lunalib.lunaSettings

import com.fs.starfarer.api.Global
import org.apache.log4j.Level
import org.json.JSONArray
import org.lazywizard.lazylib.JSONUtils

data class LunaSettingsData(
    val modID: String,
    val fieldID: String,
    val fieldName: String,
    val fieldType: String,
    val fieldTooltip: String,
    val defaultValue: Any,
    val minValue: Double,
    val maxValue: Double,
    val tags: List<String>)

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
            val tooltip = rows.getString("fieldTooltip")
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

            var tagsToBeFiltered = rows.getString("tags").split(",")
            var tags: MutableList<String> = ArrayList()
            for (tag in tagsToBeFiltered)
            {
                tags.add(tag.trim())
            }

            if (type == "Int" || type == "Double")
            {
                try {
                    minValue = rows.getString("minValue").toDouble()
                }
                catch (e: Throwable)
                {
                    maxValue = 0.0
                    log.debug("LunaSettings: No min value for for $id, setting to 0")
                }

                try {
                    maxValue = rows.getString("maxValue").toDouble()
                }
                catch (e: Throwable)
                {
                    maxValue = 100.0
                    log.debug("LunaSettings: No max value for for $id, setting to 100")
                }
            }

            SettingsData.add(LunaSettingsData(modID, id, name, type, tooltip, default, minValue, maxValue, tags))
            log.debug("LunaSettings: Loaded default settings data: $id, from $modID")
        }
    }

    fun saveDefaultsToFile()
    {
        var defaults = SettingsData
        var mods = Global.getSettings().modManager.enabledModsCopy

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

    fun loadSettings()
    {
        //Settings.clear()
        var mods = Global.getSettings().modManager.enabledModsCopy

        for (mod in mods)
        {
            var data = JSONUtils.loadCommonJSON("LunaSettings/${mod.id}.json", "data/config/LunaSettingsDefault.default");
            if (data.length() == 0 || data == null)
            {
                log.debug("LunaSettings: Could not find any mod settings for ${mod.id}, skipping.")
                continue
            }
            Settings.put(mod.id, data)
            log.debug("LunaSettings: Loaded Mod Settings for ${mod.id}")
        }
    }
}