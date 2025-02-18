package lunalib.lunaTitle

import com.fs.starfarer.api.GameState
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.BaseCombatLayeredRenderingPlugin
import com.fs.starfarer.api.util.WeightedRandomPicker

object TitlescreenManager {

    var isFirstLoad = true

    var lastSystemId: String = ""
    var lastSystemTags = ArrayList<String>()

    fun decideOnTitle() {
        if (Global.getCurrentState() != GameState.TITLE) return

        var specs = TitleSpecLoader.getSpecs()

        //Handle title screens that are based on random chance
        if (isFirstLoad) {
            isFirstLoad = false

            var picker = WeightedRandomPicker<String>()
            picker.add("vanilla", 1000f)
            specs.forEach { picker.add(it.id, it.createPlugin().getWeight()) }

            var pick = picker.pick()
            if (pick == "vanilla") return

            var spec = specs.find { it.id == pick }
            var plugin = spec!!.createPlugin()
            Global.getCombatEngine().addPlugin(plugin)
        }
        //Handle title screens based on tags from the campaign.
        else{
            for (spec in specs) {
                var plugin = spec.createPlugin()
                if (plugin.pickBasedOnSystemCondition(lastSystemId, lastSystemTags)) {
                    Global.getCombatEngine().addPlugin(plugin)
                    break
                }
            }
        }
    }
}