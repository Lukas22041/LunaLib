package lunalib.backend.scripts

import com.fs.starfarer.api.GameState
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.*
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.Fonts
import com.fs.starfarer.api.ui.UIPanelAPI
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.combat.CombatState
import com.fs.starfarer.title.TitleScreenState
import com.fs.state.AppDriver
import lunalib.backend.ui.components.base.LunaUIBaseElement
import lunalib.backend.ui.settings.LunaSettingsUIMainPanel
import lunalib.backend.ui.settings.LunaSettingsUIModsPanel
import lunalib.backend.ui.settings.LunaSettingsUISettingsPanel
import lunalib.backend.ui.versionchecker.LunaVersionUIPanel
import lunalib.backend.util.getLunaString
import lunalib.lunaSettings.LunaSettings
import org.lazywizard.lazylib.MathUtils
import org.lazywizard.lazylib.ui.LazyFont
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.concurrent.TimeUnit


class CombatHandler : EveryFrameCombatPlugin
{

    var engine: CombatEngineAPI? = null
    private var tip: LazyFont.DrawableString? = null
    private var settingsButtonText: LazyFont.DrawableString? = null
    private var versionButtonText: LazyFont.DrawableString? = null

    var settingsKeybind = ""

    //var buttonsEnabled = LunaSettings.getBoolean("lunalib", "luna_enableMainMenuButtons")
    var buttonWidth = 130f
    var buttonHeight = 50f

    var settingsPlugin: LunaSettingsUIMainPanel? = null
    var settingsPanel: CustomPanelAPI? = null

    var versionPlugin: LunaVersionUIPanel? = null
    var versionPanel: CustomPanelAPI? = null

    companion object {
        var isUpdateCheckDone = false
        var canBeRemoved = false

        var enableVersionChecker: Boolean? = null
    }

    override fun init(engine: CombatEngineAPI?)
    {
        if (enableVersionChecker == null) {
            enableVersionChecker = LunaSettings.getBoolean("lunalib", "luna_enableVC")
        }

        this.engine = engine

        val font: LazyFont
        font = LazyFont.loadFont(Fonts.DEFAULT_SMALL)

        tip = font.createText("LunaLib\n", Misc.getHighlightColor(), 15f)
        tip!!.append("Open the Mod Settings Menu with $settingsKeybind in the Campaign, or under New Game -> Sector Configuration -> Mod Settings ", Misc.getBasePlayerColor())
        tip!!.maxWidth = 400f

        settingsButtonText = font.createText("Mod Settings", Misc.getBasePlayerColor(), 15f)

        versionButtonText = font.createText("Version Checker", Misc.getBasePlayerColor(), 15f)
    }


    fun closeSettingsUI()
    {
        getScreenPanel().removeComponent(settingsPanel)

        LunaSettingsUIMainPanel.panelOpen = false

        LunaSettingsUIModsPanel.selectedMod = null
        LunaSettingsUISettingsPanel.addedElements.clear()
        LunaSettingsUISettingsPanel.changedSettings.clear()
        LunaSettingsUISettingsPanel.unsavedCounter.clear()
        LunaSettingsUISettingsPanel.unsaved = false
    }

    fun closeVersionUI()
    {
        getScreenPanel().removeComponent(versionPanel)
        LunaVersionUIPanel.panelOpen = false
    }

    override fun processInputPreCoreControls(amount: Float, events: MutableList<InputEventAPI>?) {

        events!!.forEach {
            if (it.isConsumed) return@forEach
            if (AppDriver.getInstance().currentState !is TitleScreenState) return@forEach
            if (canBeRemoved)
            {
                var titlescreen: TitleScreenState = AppDriver.getInstance().currentState as TitleScreenState

                if (LunaSettingsUIMainPanel.panelOpen)
                {

                    closeSettingsUI()

                    it.consume()
                    canBeRemoved = false
                    return@forEach
                }
                if (LunaVersionUIPanel.panelOpen)
                {

                    closeVersionUI()

                    it.consume()
                    canBeRemoved = false
                    return@forEach
                }
            }

        }
    }

    fun getScreenPanel() : UIPanelAPI
    {
        var titlescreen: TitleScreenState = AppDriver.getInstance().currentState as TitleScreenState


        val methodClass = Class.forName("java.lang.reflect.Method", false, Class::class.java.classLoader)
        val getNameMethod = MethodHandles.lookup().findVirtual(methodClass, "getName", MethodType.methodType(String::class.java))
        val invokeMethod = MethodHandles.lookup().findVirtual(methodClass, "invoke", MethodType.methodType(Any::class.java, Any::class.java, Array<Any>::class.java))

        var foundMethod: Any? = null
        for (method in titlescreen::class.java.methods as Array<Any>)
        {
            if (getNameMethod.invoke(method) == "getScreenPanel")
            {
                foundMethod = method
            }
        }

        return invokeMethod.invoke(foundMethod, titlescreen) as UIPanelAPI
    }

    override fun advance(amount: Float, events: MutableList<InputEventAPI>?)
    {


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
                cooldown = 20f
                Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f)

                try {
                    settingsPlugin = LunaSettingsUIMainPanel(false)
                    settingsPanel = Global.getSettings().createCustom(Global.getSettings().screenWidth * 0.8f, Global.getSettings().screenHeight * 0.8f, settingsPlugin)
                    settingsPlugin!!.initFromScript(settingsPanel!!)
                    settingsPanel!!.position.inTL(Global.getSettings().screenWidth * 0.1f, Global.getSettings().screenHeight * 0.1f)

                    var titlescreen: TitleScreenState = AppDriver.getInstance().currentState as TitleScreenState
                    getScreenPanel().addComponent(settingsPanel)

                    (settingsPlugin as LunaSettingsUIMainPanel).handler = this


                    LunaSettingsUIMainPanel.panelOpen = true


                } catch (e: Throwable) {
                    throw Exception("Error occured while creating panel" + e.printStackTrace())
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
                cooldown = 20f
                Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f)

                try {
                    versionPlugin = LunaVersionUIPanel()
                    versionPanel = Global.getSettings().createCustom(Global.getSettings().screenWidth * 0.8f, Global.getSettings().screenHeight * 0.8f, versionPlugin)
                    versionPlugin!!.initFromScript(versionPanel!!)
                    versionPanel!!.position.inTL(Global.getSettings().screenWidth * 0.1f, Global.getSettings().screenHeight * 0.1f)

                    var titlescreen: TitleScreenState = AppDriver.getInstance().currentState as TitleScreenState
                    getScreenPanel().addComponent(versionPanel)
                    (versionPlugin as LunaVersionUIPanel).handler = this

                    LunaVersionUIPanel.panelOpen = true


                } catch (e: Throwable) { }
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

      //  panel!!.render(1f)

        if (enableVersionChecker == null) {
            enableVersionChecker = LunaSettings.getBoolean("lunalib", "luna_enableVC")
        }


        if (Global.getCurrentState() == GameState.TITLE && tip != null)
        {
           // if (System.getProperty("os.name").startsWith("Windows") && buttonsEnabled != null && buttonsEnabled!!)
            // if (buttonsEnabled != null && buttonsEnabled!!)
            if (true)
            {
                var dialogActive: Any? = null

                try {
                    var combatscreen: CombatState = AppDriver.getInstance().currentState as CombatState

                } catch (e: Throwable) {
                    try {

                        var titlescreen: TitleScreenState = AppDriver.getInstance().currentState as TitleScreenState
                        var instanceToGetFrom = titlescreen

                        val fieldClass = Class.forName("java.lang.reflect.Field", false, Class::class.java.classLoader)
                        val getMethod = MethodHandles.lookup().findVirtual(fieldClass, "get", MethodType.methodType(Any::class.java, Any::class.java))
                        val getNameMethod = MethodHandles.lookup().findVirtual(fieldClass, "getName", MethodType.methodType(String::class.java))
                        val setAcessMethod = MethodHandles.lookup().findVirtual(fieldClass,"setAccessible", MethodType.methodType(Void.TYPE, Boolean::class.javaPrimitiveType))

                        val instancesOfFields: Array<out Any> = instanceToGetFrom.javaClass.getDeclaredFields()
                        for (obj in instancesOfFields)
                        {
                            setAcessMethod.invoke(obj, true)
                            val name = getNameMethod.invoke(obj)
                            if (name.toString() == "dialogType")
                            {
                                dialogActive = getMethod.invoke(obj, instanceToGetFrom)
                            }
                        }
                    } catch (e: Throwable) {}

                }

                if (dialogActive == null && !LunaSettingsUIMainPanel.panelOpen && !LunaVersionUIPanel.panelOpen)
                {
                    addModSettingsButton()
                    if (enableVersionChecker!!) addVersionButton()
                }
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

            if (!isUpdateCheckDone && enableVersionChecker!!) {
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