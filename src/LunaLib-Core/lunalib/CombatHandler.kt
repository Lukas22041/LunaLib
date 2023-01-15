package lunalib

import com.fs.starfarer.api.GameState
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.*
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaSettings.LunaSettings
import org.lazywizard.lazylib.ui.LazyFont
import org.lwjgl.input.Keyboard


class CombatHandler : EveryFrameCombatPlugin
{

    var engine: CombatEngineAPI? = null
    private var toDraw: LazyFont.DrawableString? = null
    var settingsKeybind = ""
    override fun init(engine: CombatEngineAPI?)
    {
        this.engine = engine

        val font: LazyFont
        font = LazyFont.loadFont("graphics/fonts/insignia15LTaa.fnt")

        toDraw = font.createText("LunaLib\n", Misc.getHighlightColor(), 15f)
        toDraw!!.append("Open the Mod Settings Menu with $settingsKeybind in the Campaign, or under New Game -> Sector Configuration -> Mod Settings ", Misc.getBasePlayerColor())
        toDraw!!.maxWidth = 400f
    }

    override fun processInputPreCoreControls(amount: Float, events: MutableList<InputEventAPI>?) {
    }

    override fun advance(amount: Float, events: MutableList<InputEventAPI>?)
    {

    }

    override fun renderInWorldCoords(viewport: ViewportAPI?)
    {

    }

    override fun renderInUICoords(viewport: ViewportAPI?) {
        if (Global.getCurrentState() == GameState.TITLE && toDraw != null)
        {
            settingsKeybind = Keyboard.getKeyName(LunaSettings.getInt("lunalib", "luna_SettingsKeybind")!!)

            var location = "New Game -> Ship Selection -> Difficulty -> Mod Settings."
            if (Global.getSettings().modManager.isModEnabled("nexerelin"))
            {
                 location = "New Game -> Sector Configuration -> Mod Settings."
            }
            toDraw!!.draw(100f,100f);
            toDraw!!.text = "LunaLib "
            toDraw!!.append(" \n", Misc.getBasePlayerColor())
            toDraw!!.append("Open the Mod Settings Menu with $settingsKeybind in the Campaign, or at $location", Misc.getBasePlayerColor())

        }
    }
}