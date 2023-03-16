package lunalib.backend.scripts

import com.fs.starfarer.api.GameState
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.campaign.InteractionDialogPlugin
import com.fs.starfarer.api.campaign.VisualPanelAPI
import com.fs.starfarer.api.combat.*
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.campaign.CampaignEngine
import com.fs.starfarer.combat.CombatEngine
import com.fs.starfarer.combat.CombatMain
import com.fs.starfarer.combat.CombatState
import com.fs.starfarer.title.TitleScreenState
import com.fs.starfarer.ui.newui.o0Oo
import com.fs.state.AppDriver
import lunalib.backend.ui.settings.LunaSettingsUIMainPanel
import lunalib.backend.ui.settings.OpenSettingsPanelInteraction
import lunalib.backend.util.getLunaString
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
       /* if (Keyboard.isKeyDown(Keyboard.KEY_K))
        {
            try {
                var combatscreen: CombatState = AppDriver.getInstance().currentState as CombatState

                *//*CampaignEngine.getInstance().campaignUI
                CombatEngine.getInstance().combatUI.dialog*//*


                var test = com.fs.starfarer.ui.newui.o0Oo(null, OpenSettingsPanelInteraction(), combatscreen.screenPanel, combatscreen);
                test.show(0.3f, 0.2f)
            } catch (e: Throwable) {
                try {
                    var titlescreen: TitleScreenState = AppDriver.getInstance().currentState as TitleScreenState

                  //  var panel = Global.getSettings().createCustom(0.8f, 0.7f, LunaSettingsUIMainPanel(false))

                    *//*CampaignEngine.getInstance().campaignUI
                    CombatEngine.getInstance().combatUI.dialog*//*

                    var test = com.fs.starfarer.ui.newui.o0Oo(null, OpenSettingsPanelInteraction(), titlescreen.screenPanel, titlescreen);
                    test.show(0.3f, 0.2f)

                } catch (e: Throwable) {}
            }

        }*/
    }

    override fun renderInWorldCoords(viewport: ViewportAPI?)
    {

    }

    override fun renderInUICoords(viewport: ViewportAPI?) {
        if (Global.getCurrentState() == GameState.TITLE && toDraw != null)
        {
            settingsKeybind = Keyboard.getKeyName(LunaSettings.getInt("lunalib", "luna_SettingsKeybind")!!)

            var location = "mainMenuTextNonNex".getLunaString()
            if (Global.getSettings().modManager.isModEnabled("nexerelin"))
            {
                 location = "mainMenuTextNex".getLunaString()
            }
            toDraw!!.draw(100f,100f);
            toDraw!!.text = "LunaLib "
            toDraw!!.append(" \n", Misc.getBasePlayerColor())
            toDraw!!.append("mainMenuText1".getLunaString() + settingsKeybind + "mainMenuText2".getLunaString() + location, Misc.getBasePlayerColor())
        }
    }
}