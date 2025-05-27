package lunalib.lunaTitle

import com.fs.starfarer.api.combat.CombatEngineAPI
import com.fs.starfarer.api.combat.EveryFrameCombatPlugin
import com.fs.starfarer.api.combat.ViewportAPI
import com.fs.starfarer.api.input.InputEventAPI

/**A Plugin that is only called for the titlescreen. Function likes an EveryFrameCombatPlugin, and as such you can use it for decorating the titlescreen to your liking
 * Do Note that the title screen is more restrictive than normal combat sessions, and a bunch of things may crash the game if done
 * Mostly exists to avoid conflicts if multiple mods want title screens, does not make creating them simpler*/
abstract class BaseLunaTitleScreenPlugin : EveryFrameCombatPlugin {

    lateinit var spec: TitleSpecLoader.TitleScreenSpec

    /** Used to enable certain tittlescreens based on which system you were in before returning to title
     * Return true to guarantee the Titlescreen. Should be used only for specific systems */
    abstract fun pickBasedOnSystemCondition(lastSystemID: String, lastSystemTags: ArrayList<String>) : Boolean

    open fun getWeight() = spec.weight

    override fun init(engine: CombatEngineAPI?) {

    }

    override fun processInputPreCoreControls(amount: Float, events: MutableList<InputEventAPI>?) {

    }

}