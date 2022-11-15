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
    val FieldTooltip: String,
    val defaultValue: Any,
    val newGame: Boolean,
    val minValue: Double,
    val maxValue: Double,
    val tags: List<String>)

internal object LunaSettingsLoader
{
    private val log = Global.getLogger(LunaSettingsLoader::class.java)

    @JvmStatic
    var Settings: MutableMap<String, JSONUtils.CommonDataJSONObject> = HashMap()

    @JvmStatic
    var newGameSettings: MutableMap<String, Map<String, Any>> = HashMap()

    @JvmStatic
    var SettingsData: MutableList<LunaSettingsData> = ArrayList()

    init {
        log.level = Level.ALL
    }

    fun loadDefault()
    {
        SettingsData.clear()
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
            }

            var newGame = false
            var minValue = 0.0
            var maxValue = 0.0

            var tagsToBeFiltered = rows.getString("tags").split(",")
            var tags: MutableList<String> = ArrayList()
            for (tag in tagsToBeFiltered)
            {
                tags.add(tag.trim())
            }

            try {
                newGame = rows.getString("newGame").toBoolean()
            }
            catch (e: Throwable)
            {
                newGame = false
                log.debug("LunaSettings: No newGame value for for $id, setting to false")
            }

            try {
                minValue = rows.getString("minValue").toDouble()
            }
            catch (e: Throwable)
            {
                minValue = 0.0
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


            SettingsData.add(LunaSettingsData(modID, id, name, type, tooltip, default, newGame, minValue, maxValue, tags))
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
                if (default.newGame) continue
                if (default.fieldType == "Text") continue
                if (default.modID == mod.id)
                {
                    try
                    {
                        data!!.get(default.fieldID)
                    }
                    catch (e: Throwable)
                    {
                        data!!.put(default.fieldID, default.defaultValue)
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
        var mods = Global.getSettings().modManager.enabledModsCopy

        for (mod in mods)
        {
            try {
                var data = JSONUtils.loadCommonJSON("LunaSettings/${mod.id}.json", "data/config/LunaSettingsDefault.default");
                if (data.length() == 0 || data == null) continue
                Settings.put(mod.id, data)
                log.debug("Loaded Mod Settings for ${mod.id}")
            }
            catch (e: Throwable)
            {
                log.error("Could not find any mod settings for ${mod.id}")
            }
        }
    }

    fun loadSaveSettingDefaults()
    {
        var mods = Global.getSettings().modManager.enabledModsCopy
        for (mod in mods)
        {
            var saveData: MutableMap<String, Any> = HashMap()
            for (data in SettingsData)
            {
                if (data.modID != mod.id || !data.newGame) continue
                saveData.put(data.fieldID, data.defaultValue)
            }
            if (saveData.isNotEmpty()) newGameSettings.put(mod.id, saveData)

        }
    }

    fun storeSaveSettingsInToMemory()
    {
        Global.getSector().memoryWithoutUpdate.set("\$LunaSettings", newGameSettings.toMap())
    }

    fun saveToMemory()
    {

    }
}