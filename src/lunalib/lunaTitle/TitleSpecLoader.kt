package lunalib.lunaTitle

import com.fs.starfarer.api.Global

object TitleSpecLoader {

    private var specs = ArrayList<TitleScreenSpec>()
    fun getSpecs() = ArrayList(specs)

    data class TitleScreenSpec(var id: String, val weight: Float, var pluginPath: String) {
        fun createPlugin() : BaseLunaTitleScreenPlugin {
            var plugin = Global.getSettings().scriptClassLoader.loadClass(pluginPath).newInstance() as BaseLunaTitleScreenPlugin
            plugin.spec = this
            return plugin
        }
    }

    fun loadTitlesFromCSV()
    {
        var CSV = Global.getSettings().getMergedSpreadsheetDataForMod("id", "data/config/LunaTitleScreens.csv", "lunalib")

        for (index in 0 until  CSV.length())
        {
            val row = CSV.getJSONObject(index)

            val id = row.getString("id")
            if (id.startsWith("#") || id == "") continue
            val weight = row.getDouble("weight").toFloat()
            val pluginPath = row.getString("pluginPath")

            var spec = TitleScreenSpec(id, weight, pluginPath)

            specs.add(spec)
        }
    }
}