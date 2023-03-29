package lunalib.backend.scripts

import com.fs.starfarer.api.GameState
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.*
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.Fonts
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.combat.CombatState
import com.fs.starfarer.title.TitleScreenState
import com.fs.state.AppDriver
import lunalib.backend.ui.settings.LunaSettingsUIMainPanel
import lunalib.backend.ui.settings.OpenSettingsPanelInteraction
import lunalib.backend.ui.versionchecker.LunaVersionUIPanel
import lunalib.backend.ui.versionchecker.OpenVersionPanelInteraction
import lunalib.backend.util.getLunaString
import lunalib.lunaSettings.LunaSettings
import org.lazywizard.lazylib.MathUtils
import org.lazywizard.lazylib.ui.LazyFont
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.concurrent.TimeUnit


class CombatHandler : EveryFrameCombatPlugin
{

    var engine: CombatEngineAPI? = null
    private var tip: LazyFont.DrawableString? = null
    private var settingsButtonText: LazyFont.DrawableString? = null
    private var versionButtonText: LazyFont.DrawableString? = null

    var settingsKeybind = ""

    var buttonsEnabled = LunaSettings.getBoolean("lunalib", "luna_enableMainMenuButtons")
    var buttonWidth = 130f
    var buttonHeight = 50f

    companion object {
        var isUpdateCheckDone = false
    }

    override fun init(engine: CombatEngineAPI?)
    {
        this.engine = engine

        val font: LazyFont
        font = LazyFont.loadFont(Fonts.DEFAULT_SMALL)

        tip = font.createText("LunaLib\n", Misc.getHighlightColor(), 15f)
        tip!!.append("Open the Mod Settings Menu with $settingsKeybind in the Campaign, or under New Game -> Sector Configuration -> Mod Settings ", Misc.getBasePlayerColor())
        tip!!.maxWidth = 400f

        settingsButtonText = font.createText("Mod Settings", Misc.getBasePlayerColor(), 15f)
        versionButtonText = font.createText("Version Checker", Misc.getBasePlayerColor(), 15f)
    }

    override fun processInputPreCoreControls(amount: Float, events: MutableList<InputEventAPI>?) {
    }

    override fun advance(amount: Float, events: MutableList<InputEventAPI>?)
    {
        if (buttonsEnabled == null)
        {
            LunaSettings.getBoolean("lunalib", "luna_enableMainMenuButtons")
        }
       /* if (Mouse.isButtonDown(0))
        {

            try {
                var combatscreen: CombatState = AppDriver.getInstance().currentState as CombatState

                var test = com.fs.starfarer.ui.newui.o0Oo(null, OpenSettingsPanelInteraction(), combatscreen.screenPanel, combatscreen);
                test.show(0.3f, 0.2f)
            } catch (e: Throwable) {
                try {
                    var titlescreen: TitleScreenState = AppDriver.getInstance().currentState as TitleScreenState

                    var test = com.fs.starfarer.ui.newui.o0Oo(null, OpenSettingsPanelInteraction(), titlescreen.screenPanel, titlescreen);
                    test.show(0.3f, 0.2f)

                } catch (e: Throwable) {}
            }
        }*/
    }

    override fun renderInWorldCoords(viewport: ViewportAPI?)
    {

    }


    var cooldown = 0f
    fun addModSettingsButton()
    {
        var scale = Global.getSettings().screenScaleMult

        var x = 5f
        var y = Global.getSettings().screenHeight * 0.975f - buttonHeight

        var mouseX = Mouse.getX() / scale
        var mouseY = Mouse.getY() / scale


        var back = Misc.getDarkPlayerColor().darker()
        var border = Misc.getDarkPlayerColor()

        cooldown--;
        cooldown = MathUtils.clamp(cooldown, -1f, 1000f)

        if (mouseX in x.. x + buttonWidth && mouseY in y..y + buttonHeight)
        {
            border = Misc.getDarkPlayerColor().brighter()

            if (cooldown < 1 && Mouse.isButtonDown(0) && !LunaSettingsUIMainPanel.panelOpen && !LunaVersionUIPanel.panelOpen)
            {
                cooldown = 60f
                Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f)

                try {
                    var combatscreen: CombatState = AppDriver.getInstance().currentState as CombatState

                    var test = com.fs.starfarer.ui.newui.o0Oo(null, OpenSettingsPanelInteraction(), combatscreen.screenPanel, combatscreen);
                    test.show(0.3f, 0.2f)
                } catch (e: Throwable) {
                    try {
                        var titlescreen: TitleScreenState = AppDriver.getInstance().currentState as TitleScreenState

                        var test = com.fs.starfarer.ui.newui.o0Oo(null, OpenSettingsPanelInteraction(), titlescreen.screenPanel, titlescreen);
                        test.show(0.3f, 0.2f)


                    } catch (e: Throwable) {}
                }
            }
        }

        drawButton(x, y, buttonWidth, buttonHeight, back, border)

        settingsButtonText!!.draw((x + buttonWidth / 2) - settingsButtonText!!.width / 2f , (y + buttonHeight / 2) + settingsButtonText!!.height / 2f)
    }

    fun addVersionButton()
    {
        var scale = Global.getSettings().screenScaleMult

        var x = 5f
        var y = Global.getSettings().screenHeight * 0.975f - buttonHeight * 2 - 5f

        var mouseX = Mouse.getX() / scale
        var mouseY = Mouse.getY() / scale


        var back = Misc.getDarkPlayerColor().darker()
        var border = Misc.getDarkPlayerColor()

        cooldown--;
        cooldown = MathUtils.clamp(cooldown, -1f, 1000f)

        if (mouseX in x.. x + buttonWidth && mouseY in y..y + buttonHeight)
        {
            border = Misc.getDarkPlayerColor().brighter()

            if (cooldown < 1 && Mouse.isButtonDown(0) && !LunaSettingsUIMainPanel.panelOpen && !LunaVersionUIPanel.panelOpen)
            {
                cooldown = 60f
                Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f)

                try {
                    var combatscreen: CombatState = AppDriver.getInstance().currentState as CombatState

                    var test = com.fs.starfarer.ui.newui.o0Oo(null, OpenVersionPanelInteraction(), combatscreen.screenPanel, combatscreen);
                    test.show(0.3f, 0.2f)
                } catch (e: Throwable) {
                    try {
                        var titlescreen: TitleScreenState = AppDriver.getInstance().currentState as TitleScreenState

                        var test = com.fs.starfarer.ui.newui.o0Oo(null, OpenVersionPanelInteraction(), titlescreen.screenPanel, titlescreen);
                        test.show(0.3f, 0.2f)

                    } catch (e: Throwable) {}
                }
            }
        }


        drawButton(x, y, buttonWidth, buttonHeight, back, border)

        var count = LunaVersionUIPanel.getUpdatedModsCount()

        versionButtonText!!.text = "Version Checker"
        if (count == 1)
        {
            versionButtonText!!.text = "Version Checker\n"
            versionButtonText!!.appendIndented("($count Update)", Misc.getHighlightColor(), 3)
        }
        else if (count != 0)
        {
            versionButtonText!!.text = "Version Checker\n"
            versionButtonText!!.appendIndented("($count Updates)", Misc.getHighlightColor(), 3)
        }
        else
        {
            versionButtonText!!.text = "Version Checker"
        }
        versionButtonText!!.draw((x + buttonWidth / 2) - versionButtonText!!.width / 2f , (y + buttonHeight / 2) + versionButtonText!!.height / 2f)
    }


    override fun renderInUICoords(viewport: ViewportAPI?) {


        if (Global.getCurrentState() == GameState.TITLE && tip != null)
        {
            if (!isUpdateCheckDone) {
                // We can't do anything if it's not done checking for updates
                if (!LunaVersionUIPanel.futureUpdateInfo!!.isDone) {
                    return
                }

                // Attempt to retrieve the update results from the other thread
                try {
                    LunaVersionUIPanel.updateInfo = LunaVersionUIPanel.futureUpdateInfo!![1L, TimeUnit.SECONDS]
                    LunaVersionUIPanel.futureUpdateInfo = null
                } catch (ex: Exception) {
                    /* Log.error("Failed to retrieve mod update info", ex)
                     ui.addMessage("Failed to retrieve mod update info!", Color.RED)
                     ui.addMessage("Check starsector.log for details.", Color.RED)
                     Global.getSector().removeTransientScript(this)
                     isDone = true // Just in case*/
                    return
                }
                isUpdateCheckDone = true

                if (LunaVersionUIPanel.updateInfo != null)
                {
                    LunaVersionUIPanel.reconstruct = true
                }
            }

            if (buttonsEnabled != null && buttonsEnabled!! && System.getProperty("os.name").startsWith("Windows"))
            {
                addModSettingsButton()
                addVersionButton()
            }
            else
            {
                settingsKeybind = Keyboard.getKeyName(LunaSettings.getInt("lunalib", "luna_SettingsKeybind")!!)

                var location = "mainMenuTextNonNex".getLunaString()
                if (Global.getSettings().modManager.isModEnabled("nexerelin"))
                {
                    location = "mainMenuTextNex".getLunaString()
                }
                tip!!.draw(100f,100f);
                tip!!.text = "LunaLib "
                tip!!.append(" \n", Misc.getBasePlayerColor())
                tip!!.append("mainMenuText1".getLunaString() + settingsKeybind + "mainMenuText2".getLunaString() + location, Misc.getBasePlayerColor())
            }
        }
    }

    fun drawButton(x: Float, y: Float, width: Float, height: Float, back: Color, border: Color)
    {
        //background
        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)

        GL11.glDisable(GL11.GL_BLEND)

        GL11.glColor4f(back.red / 255f,
            back.green / 255f,
            back.blue / 255f,
            back.alpha / 255f)

        GL11.glRectf(x, y , x + width, y + height)

        GL11.glEnd()
        GL11.glPopMatrix()

        //border
        GL11.glPushMatrix()

        GL11.glTranslatef(0f, 0f, 0f)
        GL11.glRotatef(0f, 0f, 0f, 1f)

        GL11.glDisable(GL11.GL_TEXTURE_2D)

        GL11.glDisable(GL11.GL_BLEND)

        GL11.glColor4f(border.red / 255f,
            border.green / 255f,
            border.blue / 255f,
            border.alpha / 255f )

        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glBegin(GL11.GL_LINE_STRIP)

        GL11.glVertex2f(x, y)
        GL11.glVertex2f(x, y + height)
        GL11.glVertex2f(x + width, y + height)
        GL11.glVertex2f(x + width, y)
        GL11.glVertex2f(x, y)

        GL11.glEnd()
        GL11.glPopMatrix()
    }
}