package lunalib.lunaSettings

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ModSpecAPI
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate.DialogCallbacks
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.*
import com.fs.starfarer.api.ui.TooltipMakerAPI.TooltipCreator
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.JSONUtils
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color

//Probably the worst code ive ever written, it works, but at some point it should probably be rewritten.
//Do not use this as an example for your own UI.
class LunaSettingsUI(newGame: Boolean) : CustomUIPanelPlugin
{
    private var dialog: InteractionDialogAPI? = null
    private var callbacks: DialogCallbacks? = null
    private var panel: CustomPanelAPI? = null
    private var newgame = newGame //True if its called from the new game menu
    private var pW = 0f
    private var pH = 0f
    private var selectedMod: ModSpecAPI? = null

    //Mods Panel
    private var modsPanel: CustomPanelAPI? = null
    private var modsPanelList: TooltipMakerAPI? = null
    private var modButtons: HashMap<ButtonAPI, ModSpecAPI> = HashMap()

    //Settings Panel
    private var settingsPanel: CustomPanelAPI? = null
    private var settingsPanelList: TooltipMakerAPI? = null
    private var saveButton: ButtonAPI? = null
    private var resetButton: ButtonAPI? = null
    private var buttonPanel: TooltipMakerAPI? = null

    //Saves data
    private var stringFields: MutableMap<LunaSettingsData, TextFieldAPI> = HashMap()
    private var intFields: MutableMap<LunaSettingsData, TextFieldAPI> = HashMap()
    private var doubleFields: MutableMap<LunaSettingsData, TextFieldAPI> = HashMap()
    private var booleanField: MutableMap<LunaSettingsData, ButtonAPI> = HashMap()

    private var enumField: MutableMap<LunaSettingsData,ButtonAPI> = HashMap()
    private var enumFieldsPara: MutableMap<LunaSettingsData, LabelAPI> = HashMap()
    private var enumOptions: MutableMap<LunaSettingsData, MutableMap<ButtonAPI, String>> = HashMap()
    private var selectedEnum: LunaSettingsData? = null
    private var isEnumOpen = false
    private var enumPanel: TooltipMakerAPI? = null




    fun init(panel: CustomPanelAPI?, callbacks: DialogCallbacks?, dialog: InteractionDialogAPI?) {
        this.panel = panel
        this.callbacks = callbacks
        this.dialog = dialog

        pW = this.panel!!.position.width
        pH = this.panel!!.position.height

        reset()
    }

    private fun reset() {
        ModsDisplay()
        optionsDisplay()
    }

    private fun resetOption()
    {
        panel!!.removeComponent(modsPanelList)
        panel!!.removeComponent(modsPanel)
        modsPanel = null
        ModsDisplay()

        panel!!.removeComponent(buttonPanel)
        panel!!.removeComponent(enumPanel)
        panel!!.removeComponent(settingsPanelList)
        panel!!.removeComponent(settingsPanel)

        stringFields.clear()
        intFields.clear()
        doubleFields.clear()
        booleanField.clear()

        enumField.clear()
        enumFieldsPara.clear()
        enumOptions.clear()
        selectedEnum = null
        isEnumOpen = false

        settingsPanel = null
        optionsDisplay()
    }

    override fun advance(amount: Float) {

        if (saveButton != null && saveButton!!.isChecked)
        {
            saveButton!!.isChecked = false
            saveData()
            resetOption()
        }

        if (selectedMod != null)
        {
            for (button in modButtons)
            {
                if (button.value == selectedMod)
                {
                    button.key.highlight()
                }
            }
        }


        if (resetButton != null && resetButton!!.isChecked)
        {
            for (map in booleanField)
            {
                map.value.isChecked = map.key.defaultValue as Boolean
            }
            for (map in doubleFields)
            {
                map.value.text = map.key.defaultValue.toString()
            }
            for (map in intFields)
            {
                map.value.text = map.key.defaultValue.toString()
            }
            for (map in stringFields)
            {
                map.value.text = map.key.defaultValue.toString()
            }
            for (map in enumFieldsPara)
            {
                map.value.text = "Selected: " + (map.key.defaultValue as List<String>).get(0)
            }
            resetButton!!.isChecked = false
        }

        for (button in modButtons) {
            if (button.key.isChecked)
            {
                if (selectedMod != null)
                {
                    button.key.isChecked = false
                    if (selectedMod != button.value)
                    {
                        selectedMod = button.value
                        resetOption()
                        break
                    }
                }
                else
                {
                    button.key.isChecked = false
                    selectedMod = button.value
                    resetOption()
                    break
                }
            }
        }

        checkIllegalChar()

        if (settingsPanelList != null)
        {
            for (map in enumField)
            {
                if (map.value.isChecked && !isEnumOpen)
                {
                    isEnumOpen = true
                    selectedEnum = map.key
                    var list = map.key.defaultValue as List<String>
                    list = list.sorted()
                    val buttons: MutableMap<ButtonAPI, String> = HashMap()

                    enumPanel = panel!!.createUIElement(settingsPanel!!.position.width, pH * 0.2f, false)
                    enumPanel!!.position.inTL(pW * 0.670f, pH * 0.35f)
                    panel!!.addUIElement(enumPanel)

                    for (entry in list)
                    {
                        val enumButton = enumPanel!!.addAreaCheckbox("$entry", null,
                            Misc.getHighlightColor(),
                            Misc.getHighlightColor(),
                            Misc.getHighlightColor(),
                            300f,
                            pH * 0.05f,
                            0f)

                        //enumButton!!.position.inTL(400f,spacing + pH * 0.045f)
                        buttons.put(enumButton, entry)
                    }
                    enumOptions.put(map.key, buttons)
                }
                else if (selectedEnum == map.key && !map.value.isChecked)
                {
                    panel!!.removeComponent(enumPanel)
                    isEnumOpen = false
                }
                else if (selectedEnum != map.key)
                {
                    map.value.isChecked = false
                }
            }
            for (map in enumOptions)
            {
                for (button in map.value)
                {
                    if (button.key.isChecked)
                    {

                        enumFieldsPara.get(map.key)!!.text = "Selected: ${button.value}"
                        panel!!.removeComponent(enumPanel)
                        isEnumOpen = false
                        enumOptions.clear()

                        for (but in enumField)
                        {
                            but.value.isChecked = false
                        }
                    }
                }
            }
        }
    }

    //Displays all active mods that have atleast one setting loaded.
    private fun ModsDisplay()
    {
        modsPanel = panel!!.createCustomPanel(pW * 0.2f , pH, null)
        modsPanel!!.position.setLocation(0f,0f).inTL(0f, 0f)

        modsPanelList = modsPanel!!.createUIElement(pW * 0.2f, pH, true)
        modsPanelList!!.position.inTL(0f, 0f)

        val modsWithData = LunaSettingsLoader.SettingsData.map { it.modID }.distinct()

        val mods: List<ModSpecAPI> = Global.getSettings().modManager.enabledModsCopy.filter { modsWithData.contains( it.id) }
        var spacing = 0f
        for (mod in mods)
        {
            val button: ButtonAPI = modsPanelList!!.addAreaCheckbox(mod.name, null,
                Misc.getBasePlayerColor(),
                Misc.getDarkPlayerColor(),
                Misc.getBrightPlayerColor(),
                modsPanel!!.position.width,
                pH * 0.1f,
                0f)
            button.position.inTL(0f, spacing)
            spacing += pH * 0.1f
            modButtons.put(button, mod)

            val tooltip = TooltipPreset("ModID: ${mod.id}\n" +
                                             "Version: ${mod.version}\n" +
                                             "Author: ${mod.author}\n", pW * 0.1f, "ModID:", "Version:", "Author:")

            modsPanelList!!.addTooltipToPrevious(tooltip, TooltipMakerAPI.TooltipLocation.BELOW)
        }

        modsPanel!!.addUIElement(modsPanelList)
        panel!!.addComponent(modsPanel)
    }

    //Adds the general body for the Settings Screen
    private fun optionsDisplay()
    {
        settingsPanel = panel!!.createCustomPanel(pW * 0.7f , pH * 0.8f, null)
        settingsPanel!!.position.setLocation(0f, 0f).inTL(pW * 0.25f, pH * 0.15f)

        if (selectedMod == null)
        {
            panel!!.addComponent(settingsPanel)
            return
        }

        buttonPanel = panel!!.createUIElement(pW * 0.7f, pH * 0.3f, false)
        buttonPanel!!.position.inTL(pW * 0.25f, pH * 0.02f)
        panel!!.addUIElement(buttonPanel)

        saveButton = buttonPanel!!.addAreaCheckbox("Save ${selectedMod!!.name}'s settings", null,
            Misc.getHighlightColor(),
            Misc.getHighlightColor(),
            Misc.getHighlightColor(),
            pW * 0.35f,
            pH * 0.1f,
            0f)
        saveButton!!.position.setLocation(0f, 0f).inTL(0f, pH * 0.03f)

        var tooltip = TooltipPreset("Saves the settings of the currently selected mod. Switching to another mod or " +
                "closing the panel without saving will restore all settings back to their pre-saved state.", pW * 0.3f, "")

        buttonPanel!!.addTooltipToPrevious(tooltip, TooltipMakerAPI.TooltipLocation.BELOW)

        resetButton = buttonPanel!!.addAreaCheckbox("Reset Settings to default", null,
            Misc.getHighlightColor(),
            Misc.getHighlightColor(),
            Misc.getHighlightColor(),
            pW * 0.35f,
            pH * 0.1f,
            0f)
        resetButton!!.position.setLocation(0f, 0f).inTL(pW * 0.35f, pH * 0.03f)

        settingsPanelList = settingsPanel!!.createUIElement(settingsPanel!!.position.width , pH * 0.80f, true)

        val newGameData = LunaSettingsLoader.SettingsData.filter { it.newGame }
        val crossData = LunaSettingsLoader.SettingsData.filter { !it.newGame }

        var spacing = 0f
        if (newgame) spacing = addOptions(newGameData, true, spacing)
        addOptions(crossData, false, spacing)

        settingsPanel!!.addUIElement(settingsPanelList)
        panel!!.addComponent(settingsPanel)
    }

    //Adds all available configs to the Settings panel
    private fun addOptions(SettingsData: List<LunaSettingsData>, firstRun: Boolean, spacing: Float) : Float
    {
        var spacing = spacing
        if (firstRun)
        {
            val heading = settingsPanelList!!.addSectionHeading("Save-Specific Settings (Only applies to a new save)", Alignment.MID, 0f)
            val tooltip = TooltipPreset("Anything in this section will only be saved within the saves and wont be changeable in the middle of a run. ",  pW * 0.4f)
            settingsPanelList!!.addTooltipToPrevious(tooltip, TooltipMakerAPI.TooltipLocation.BELOW)
            spacing += heading.position.height
        }

        else
        {
            val heading = settingsPanelList!!.addSectionHeading("Global Settings (Applies to all Saves)", Alignment.MID, 0f)
            heading.position.inTL(0f, spacing)
            spacing += heading.position.height
        }

        for (data in SettingsData)
        {
            if (data.modID != selectedMod!!.id) continue
            var mult = 1f
            for (tag in data.tags)
            {
                if (tag.contains("spacing:"))
                {
                    try {
                        mult = tag.replace("spacing:", "").toFloat()
                    }
                    catch (e: Throwable)
                    {

                    }
                }
            }
            val spacingOffset = (pH * 0.15f) * mult

            var borderColor = Misc.getBasePlayerColor()
            var highlightColor = Misc.getBasePlayerColor()

            for (tag in data.tags)
            {
                if (tag == "noBorder") borderColor = Color(0, 0,0, 0)
                if (tag == "noHighlight") highlightColor = Color(0, 0,0, 0)
            }

            //Adds outlines and enforces spacing
            val spacingButton = settingsPanelList!!.addAreaCheckbox("", null,
                highlightColor,
                borderColor,
                borderColor,
                settingsPanel!!.position.width,
                spacingOffset,
                0f)
            spacingButton.isEnabled = false
            spacingButton.position.inTL(0f,spacing)

            val presetTooltip: String = when(data.fieldType)
            {
                "Int" -> "Accepts whole numbers only (i.e 1200)"
                "Boolean" -> "Switches between True/False"
                "Double" -> "Accepts decimal numbers, seperated by a dot (i.e 2.523)"
                "String" -> "Accepts Letters, Numbers and Characters (i.e Text100*)"
                "Enum" -> "Press the button to select from a preset of options."
                else -> ""
            }
            val minMaxValue: String = when(data.fieldType)
            {
                "Int" -> "\nMin: ${data.minValue.toInt()}   Max: ${data.maxValue.toInt()}"
                "Double" -> "\nMin: ${data.minValue}   Max: ${data.maxValue}"
                else -> ""
            }

            val tooltip = TooltipPreset("${data.fieldName} ($presetTooltip) $minMaxValue\n\n${data.FieldTooltip}", pW * 0.4f, "${data.fieldName}","Min", "Max")

            if (data.fieldType != "Text")
            {
                settingsPanelList!!.addTooltipToPrevious(tooltip, TooltipMakerAPI.TooltipLocation.BELOW)
                val para = settingsPanelList!!.addPara("${data.fieldName}", 0f)
                para.position.inTL(pW * 0.10f,(spacing + spacingOffset / 2) - para.position.height / 2)
            }
            else
            {
                val fieldNamePara = settingsPanelList!!.addPara("${data.defaultValue}", 0f)
                fieldNamePara.position.inTL(pW * 0.10f,(spacing + spacingOffset / 4))

            }

            when (data.fieldType)
            {
                "Int" ->
                {
                    val spacingPara = settingsPanelList!!.addPara("", 0f)
                    spacingPara.position.inTL(pW * 0.35f,(spacing + spacingOffset / 2) - spacingPara.position.height * 2)
                    var test= spacingPara.position.height

                    val field = settingsPanelList!!.addTextField(pW * 0.25f, 0f)

                    if (firstRun) field.text = LunaSettingsLoader.newGameSettings.get(selectedMod!!.id)!!.get(data.fieldID).toString()
                    else field.text = LunaSettings.getInt(data.modID, data.fieldID, false).toString()
                    intFields.put(data, field)
                }
                "Double" ->
                {
                    val spacingPara = settingsPanelList!!.addPara("", 0f)
                    spacingPara.position.inTL(pW * 0.35f,(spacing + spacingOffset / 2) - spacingPara.position.height * 2)
                    val field = settingsPanelList!!.addTextField(pW * 0.25f, 0f)
                    if (firstRun) field.text = LunaSettingsLoader.newGameSettings.get(selectedMod!!.id)!!.get(data.fieldID).toString()
                    else field.text = LunaSettings.getDouble(data.modID, data.fieldID, false).toString()
                    doubleFields.put(data, field)
                }
                "String" ->
                {
                    val spacingPara = settingsPanelList!!.addPara("", 0f)
                    spacingPara.position.inTL(pW * 0.35f,(spacing + spacingOffset / 2) - spacingPara.position.height * 2)
                    val field = settingsPanelList!!.addTextField(pW * 0.25f, 0f)
                    if (firstRun) field.text = LunaSettingsLoader.newGameSettings.get(selectedMod!!.id)!!.get(data.fieldID).toString()
                    else field.text = LunaSettings.getString(data.modID, data.fieldID, false).toString()
                    stringFields.put(data, field)
                }
                "Boolean" ->
                {
                    val booleanButton = settingsPanelList!!.addAreaCheckbox("True/False", null,
                        Misc.getBasePlayerColor(),
                        Misc.getDarkPlayerColor(),
                        Misc.getBrightPlayerColor(),
                        pW * 0.25f,
                        pH * 0.05f,
                        0f)
                    if (firstRun) booleanButton.isChecked = LunaSettingsLoader.newGameSettings.get(selectedMod!!.id)!!.get(data.fieldID) as Boolean
                    else booleanButton.isChecked = LunaSettings.getBoolean(data.modID, data.fieldID, false) ?: false
                    booleanButton.position.inTL(pW * 0.35f,(spacing + spacingOffset / 2) - booleanButton.position.height / 2)
                    booleanField.put(data, booleanButton)
                }
                "Enum" ->
                {
                    val selectedPara = settingsPanelList!!.addPara("Selected: ", 0f)
                    selectedPara.position.inTL(pW * 0.35f,(spacing + spacingOffset / 2) - pH * 0.05f)
                    enumFieldsPara.put(data, selectedPara)

                    if (firstRun) selectedPara.text = "Selected: " + LunaSettingsLoader.newGameSettings.get(selectedMod!!.id)!!.get(data.fieldID).toString()
                    else selectedPara.text = "Selected: " + LunaSettings.getString(data.modID, data.fieldID, false).toString()

                    val enumButton = settingsPanelList!!.addAreaCheckbox("Select", null,
                        Misc.getBasePlayerColor(),
                        Misc.getDarkPlayerColor(),
                        Misc.getBrightPlayerColor(),
                        pW * 0.25f,
                        pH * 0.05f,
                        0f)

                    enumButton.position.inTL(pW * 0.35f,(spacing + spacingOffset / 2) - enumButton.position.height / 2)
                    enumField.put(data, enumButton)
                }
            }

            spacing += spacingOffset
        }
        return spacing
    }

    private fun saveData()
    {
        val data = JSONUtils.loadCommonJSON("LunaSettings/${selectedMod!!.id}.json", "data/config/LunaSettingsDefault.default");
        val saveData: MutableMap<String, Any> = HashMap()

        checkIllegalChar()

        //Saves active Int Fields.
        for (toSave in intFields)
        {
            if (toSave.value.text == "")
            {
                if (toSave.key.newGame && newgame) saveData.put(toSave.key.fieldID, toSave.key.defaultValue)
                else data.put(toSave.key.fieldID, toSave.key.defaultValue)
            }
            else
            {
                if (toSave.key.newGame && newgame) saveData.put(toSave.key.fieldID, toSave.value.text.toInt())
                else data.put(toSave.key.fieldID, toSave.value.text.toInt())
            }
        }

        //Saves active Double Fields.
        for (toSave in doubleFields)
        {
            if (toSave.value.text == "")
            {
                if (toSave.key.newGame && newgame) saveData.put(toSave.key.fieldID, toSave.key.defaultValue)
                else data.put(toSave.key.fieldID, toSave.key.defaultValue)
            }
            else
            {
                if (toSave.key.newGame && newgame) saveData.put(toSave.key.fieldID, toSave.value.text.toDouble())
                else data.put(toSave.key.fieldID, toSave.value.text.toDouble())
            }
        }

        //Saves active String Fields.
        for (toSave in stringFields)
        {
            if (toSave.key.newGame && newgame) saveData.put(toSave.key.fieldID, toSave.value.text)
            else data.put(toSave.key.fieldID, toSave.value.text)
        }

        //Saves Booleans
        for (toSave in booleanField)
        {
            if (toSave.key.newGame && newgame) saveData.put(toSave.key.fieldID, toSave.value.isChecked)
            else data.put(toSave.key.fieldID, toSave.value.isChecked)
        }

        //Saves Enums as Strings
        for (toSave in enumFieldsPara)
        {
            if (toSave.key.newGame && newgame) saveData.put(toSave.key.fieldID, toSave.value.text.replace("Selected: ", ""))
            else data.put(toSave.key.fieldID, toSave.value.text.replace("Selected: ", ""))
        }

        data.save()
        LunaSettingsLoader.newGameSettings.put(selectedMod!!.id, saveData)
        LunaSettingsLoader.Settings.put(selectedMod!!.id, data)

        if (!newgame) callSettingsChangedListener()
    }

    private fun callSettingsChangedListener()
    {
        val listeners = Global.getSector().listenerManager.getListeners(LunaSettingsListener::class.java)
        for (listener in listeners)
        {
            listener.settingsChanged()
        }
    }

    override fun render(alphaMult: Float)
    {
        val playercolor = Misc.getDarkPlayerColor()

        val panelsToOutline: MutableList<CustomPanelAPI> = ArrayList()
        if (modsPanel != null) panelsToOutline.add(modsPanel!!)
        if (settingsPanel != null && selectedMod != null) panelsToOutline.add(settingsPanel!!)

        for (panel in panelsToOutline)
        {
            GL11.glPushMatrix()

            GL11.glTranslatef(0f, 0f, 0f)
            GL11.glRotatef(0f, 0f, 0f, 1f)

            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
            GL11.glColor4ub(playercolor.red.toByte(), playercolor.green.toByte(), playercolor.blue.toByte(), playercolor.alpha.toByte())

            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glBegin(GL11.GL_LINE_STRIP)

            GL11.glVertex2f(panel.position.x, panel.position.y)
            GL11.glVertex2f(panel.position.x + panel.position.width, panel.position.y)
            GL11.glVertex2f(panel.position.x + panel.position.width, panel.position.y + panel.position.height)
            GL11.glVertex2f(panel.position.x, panel.position.y + panel.position.height)
            GL11.glVertex2f(panel.position.x, panel.position.y)

            GL11.glEnd()
        }
    }

    override fun positionChanged(position: PositionAPI?) {

    }

    override fun renderBelow(alphaMult: Float) {
        val bgColor = Color(20, 20, 20)
        val x: Float = panel!!.position.x
        val y: Float = panel!!.position.y
        val w: Float = panel!!.position.width
        val h: Float = panel!!.position.height
        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glColor4f(bgColor.red / 255f,
            bgColor.green / 255f,
            bgColor.blue / 255f,
            bgColor.alpha / 255f * alphaMult)
        GL11.glRectf(x, y, x + w, y + h)
        GL11.glColor4f(1f, 1f, 1f, 1f)
        GL11.glPopMatrix()
    }

    override fun processInput(events: MutableList<InputEventAPI>?) {
        for (event in events!!) {
            if (event.isConsumed) continue
            if (event.isKeyDownEvent && event.eventValue == Keyboard.KEY_ESCAPE)
            {
                event.consume()

                dialog!!.showTextPanel()
                dialog!!.showVisualPanel()
                callbacks!!.dismissDialog()

                if (!newgame) dialog!!.dismiss()

                return
            }
        }
    }

    //Removes illegal characters from fields
    private fun checkIllegalChar()
    {
        for (map in intFields)
        {
            if (map.value.hasFocus()) continue
            var modifiedString = map.value.text.replace("[^0-9]".toRegex(), "")

            var value = 0
            try {
                value = modifiedString.toInt()
                value = MathUtils.clamp(value, map.key.minValue.toInt(), map.key.maxValue.toInt())
                modifiedString = "$value"
            }
            catch (e: Throwable)
            {
                modifiedString = "" + map.key.defaultValue
            }

            map.value.text = modifiedString
        }
        for (map in doubleFields)
        {
            if (map.value.hasFocus()) continue
            var modifiedString = map.value.text.replace("[^0-9.]".toRegex(), "")

            try {
                if (modifiedString.toDouble() < map.key.minValue) modifiedString = "" + map.key.minValue
                if (modifiedString.toDouble() > map.key.maxValue) modifiedString = "" + map.key.maxValue
            }
            catch (e: Throwable)
            {
                modifiedString = "" + map.key.defaultValue
            }

            map.value.text = modifiedString
        }
    }
}

private class TooltipPreset(var text: String, var width: Float, vararg highlights: String) : TooltipCreator
{

    var Highlights = highlights

    override fun isTooltipExpandable(tooltipParam: Any?): Boolean {
        return false
    }

    override fun getTooltipWidth(tooltipParam: Any?): Float {
        return width
    }

    override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {
        tooltip!!.addPara(text, 0f, Misc.getBasePlayerColor(), Misc.getHighlightColor(), *Highlights)
    }

}