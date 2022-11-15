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
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11
import java.awt.Button
import java.awt.Color


class LunaSettingsUI(newGame: Boolean) : CustomUIPanelPlugin
{


    private var dialog: InteractionDialogAPI? = null
    private var callbacks: DialogCallbacks? = null
    private var panel: CustomPanelAPI? = null
    private var dW = 0
    private var dH = 0
    private var pW = 0
    private var pH = 0
    private var tooltip: TooltipMakerAPI? = null

    var selectedMod: ModSpecAPI? = null


    //Left Side Panel
    var ModsInnerPannel: CustomPanelAPI? = null
    var ModsList: TooltipMakerAPI? = null
    var modButtons: HashMap<ButtonAPI, ModSpecAPI> = HashMap()


    //Right Side Panel
    var OptionsInnerPannel: CustomPanelAPI? = null
    var OptionsList: TooltipMakerAPI? = null
    var saveButton: ButtonAPI? = null
    var resetButton: ButtonAPI? = null
    var buttonPanel: TooltipMakerAPI? = null

    //Saves data
    var OptionStringMap: MutableMap<LunaSettingsData, TextFieldAPI> = HashMap()
    var OptionIntMap: MutableMap<LunaSettingsData, TextFieldAPI> = HashMap()
    var OptionDoubleMap: MutableMap<LunaSettingsData, TextFieldAPI> = HashMap()
    var OptionBooleanMap: MutableMap<LunaSettingsData, ButtonAPI> = HashMap()

    var OptionEnumMap: MutableMap<LunaSettingsData,ButtonAPI> = HashMap()
    var OptionEnumTextMap: MutableMap<LunaSettingsData, LabelAPI> = HashMap()
    var EnumOptions: MutableMap<LunaSettingsData, MutableMap<ButtonAPI, String>> = HashMap()
    var selectedEnum: LunaSettingsData? = null
    var EnumOpen = false
    var enumPanel: TooltipMakerAPI? = null

    var newgame = newGame

    init {

    }

    fun init(panel: CustomPanelAPI?, callbacks: DialogCallbacks?, dialog: InteractionDialogAPI?) {
        //so we can get back to the original InteractionDialogPlugin and do stuff with it or close it
        this.panel = panel
        this.callbacks = callbacks
        this.dialog = dialog

        //these might be helpful if you are doing custom rendering
        dW = Display.getWidth()
        dH = Display.getHeight()
        pW = this.panel!!.position.width.toInt()
        pH = this.panel!!.position.height.toInt()

        //when something changes in the UI and it needs to be re-drawn, call this
        reset()
    }

    fun reset() {
        //clears the ui panel
        if (tooltip != null) {
            panel!!.removeComponent(tooltip)
            modButtons.clear()
        }

        tooltip = panel!!.createUIElement(panel!!.position.width, panel!!.position.height, false)
        tooltip!!.setForceProcessInput(true)
        panel!!.addUIElement(tooltip).inTL(0f, 0f)

        ModsDisplay()
        optionsDisplay()
    }

    fun resetOption()
    {
        panel!!.removeComponent(ModsList)
        panel!!.removeComponent(ModsInnerPannel)
        ModsInnerPannel = null
        ModsDisplay()

        panel!!.removeComponent(buttonPanel)
        panel!!.removeComponent(enumPanel)
        panel!!.removeComponent(OptionsList)
        panel!!.removeComponent(OptionsInnerPannel)

        OptionStringMap.clear()
        OptionIntMap.clear()
        OptionDoubleMap.clear()
        OptionBooleanMap.clear()

        OptionEnumMap.clear()
        OptionEnumTextMap.clear()
        EnumOptions.clear()
        selectedEnum = null
        EnumOpen = false

        OptionsInnerPannel = null
        optionsDisplay()
    }

    override fun advance(amount: Float) {

        if (saveButton != null)
        {
            if (saveButton!!.isChecked)
            {
                saveButton!!.isChecked = false
                saveData()
                resetOption()
            }
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

        if (resetButton != null)
        {
            if (resetButton!!.isChecked)
            {
                for (map in OptionBooleanMap)
                {
                    map.value.isChecked = map.key.defaultValue as Boolean
                }
                for (map in OptionDoubleMap)
                {
                    map.value.text = map.key.defaultValue.toString()
                }
                for (map in OptionIntMap)
                {
                    map.value.text = map.key.defaultValue.toString()
                }
                for (map in OptionStringMap)
                {
                    map.value.text = map.key.defaultValue.toString()
                }
                resetButton!!.isChecked = false
            }
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

        //Removes banned characters from fields
        for (map in OptionIntMap)
        {
            var modifiedString = map.value.text.replace("[^0-9]".toRegex(), "")

            var value = 0
            try {
                value = modifiedString.toInt()
                value = MathUtils.clamp(value, map.key.minValue.toInt(), map.key.maxValue.toInt())
                modifiedString = "$value"
            }
            catch (e: Throwable)
            {
                modifiedString = ""
            }

            map.value.text = modifiedString
        }
        for (map in OptionDoubleMap)
        {
            var modifiedString = map.value.text.replace("[^0-9.]".toRegex(), "")

            try {
                if (modifiedString.toDouble() !in map.key.minValue..map.key.maxValue)
                {
                    modifiedString = modifiedString.substring(0, modifiedString.length - 1) + "" + modifiedString.substring(modifiedString.length)
                }
            }
            catch (e: Throwable)
            {
                modifiedString = ""
            }

            map.value.text = modifiedString
        }


        if (OptionsList != null)
        {
            for (map in OptionEnumMap)
            {
                if (map.value.isChecked && !EnumOpen)
                {
                    EnumOpen = true
                    selectedEnum = map.key
                    var list = map.key.defaultValue as List<String>
                    var buttons: MutableMap<ButtonAPI, String> = HashMap()

                    enumPanel = panel!!.createUIElement(OptionsInnerPannel!!.position.width, pH * 0.2f, false)
                    enumPanel!!.position.inTL(pW * 0.670f, pH * 0.35f)
                    panel!!.addUIElement(enumPanel)

                    for (entry in list)
                    {
                        var enumButton = enumPanel!!.addAreaCheckbox("$entry", null,
                            Misc.getHighlightColor(),
                            Misc.getHighlightColor(),
                            Misc.getHighlightColor(),
                            300f,
                            pH * 0.05f,
                            0f)

                        //enumButton!!.position.inTL(400f,spacing + pH * 0.045f)
                        buttons.put(enumButton, entry)
                    }
                    EnumOptions.put(map.key, buttons)
                }
                else if (selectedEnum == map.key && !map.value.isChecked)
                {
                    panel!!.removeComponent(enumPanel)
                    EnumOpen = false
                }
                else if (selectedEnum != map.key)
                {
                    map.value.isChecked = false
                }
            }
            for (map in EnumOptions)
            {
                for (button in map.value)
                {
                    if (button.key.isChecked)
                    {

                        OptionEnumTextMap.get(map.key)!!.text = "Selected: ${button.value}"
                        panel!!.removeComponent(enumPanel)
                        EnumOpen = false
                        EnumOptions.clear()

                        for (but in OptionEnumMap)
                        {
                            but.value.isChecked = false
                        }
                    }
                }
            }
        }
    }

    fun ModsDisplay()
    {
        ModsInnerPannel = panel!!.createCustomPanel(pW / 4f , pH.toFloat(), null)
        ModsInnerPannel!!.position.setLocation(0f,0f).inTL(0f, 0f);


        ModsList = ModsInnerPannel!!.createUIElement(pW / 4f , pH.toFloat(), true)
        ModsList!!.position.inTL(0f, 0f)


        var list: MutableList<String> = ArrayList()
        for (mod in LunaSettingsLoader.SettingsData)
        {
            if (list.contains(mod.modID)) continue
            list.add(mod.modID)
        }


        var mods: List<ModSpecAPI> = Global.getSettings().modManager.enabledModsCopy.filter { list.contains( it.id) }
        var spacing = 0f
        for (mod in mods)
        {
            val button: ButtonAPI = ModsList!!.addAreaCheckbox(mod.name, null,
                Misc.getBasePlayerColor(),
                Misc.getDarkPlayerColor(),
                Misc.getBrightPlayerColor(),
                ModsInnerPannel!!.position.width,
                pH * 0.1f,
                0f)
            button.position.inTL(0f, spacing);
            spacing += pH * 0.1f
            modButtons.put(button, mod)

            val tooltip: TooltipCreator = object : TooltipCreator {
                override fun isTooltipExpandable(tooltipParam: Any): Boolean {
                    return false
                }

                override fun getTooltipWidth(tooltipParam: Any): Float {
                    return dW / 4f
                }

                override fun createTooltip(tooltip: TooltipMakerAPI, expanded: Boolean, tooltipParam: Any) {
                    tooltip.addPara( "ModID: ${mod.id}\n" +
                                            "Version: ${mod.version}\n" +
                                            "Author: ${mod.author}\n" , 0f, Misc.getHighlightColor(), "ModID:", "Version:", "Author:")
                }
            }
            ModsList!!.addTooltipToPrevious(tooltip, TooltipMakerAPI.TooltipLocation.BELOW)
        }

        ModsInnerPannel!!.addUIElement(ModsList)
        panel!!.addComponent(ModsInnerPannel)
    }

    fun optionsDisplay()
    {
        OptionsInnerPannel = panel!!.createCustomPanel(pW * 0.7f , pH.toFloat() * 0.9f, null)
        OptionsInnerPannel!!.position.setLocation(0f, 0f).inTL(pW * 0.273f, pH * 0.05f)

        if (selectedMod == null)
        {
            panel!!.addComponent(OptionsInnerPannel)
            return
        }

        buttonPanel = panel!!.createUIElement(OptionsInnerPannel!!.position.width, pH * 0.2f, false)
        buttonPanel!!.position.inTL(pW * 0.270f, pH * 0.05f)
        panel!!.addUIElement(buttonPanel)

        saveButton = buttonPanel!!.addAreaCheckbox("Save ${selectedMod!!.name}'s settings", null,
            Misc.getHighlightColor(),
            Misc.getHighlightColor(),
            Misc.getHighlightColor(),
            buttonPanel!!.position.width / 2,
            pH * 0.1f,
            0f)

        resetButton = buttonPanel!!.addAreaCheckbox("Reset Settings to default", null,
            Misc.getHighlightColor(),
            Misc.getHighlightColor(),
            Misc.getHighlightColor(),
            buttonPanel!!.position.width / 2,
            pH * 0.1f,
            0f)

        resetButton!!.position.inTL(pW * 0.353f, 0f)


        OptionsList = OptionsInnerPannel!!.createUIElement(OptionsInnerPannel!!.position.width , pH * 0.8f, true)
        OptionsList!!.position.inTL(50f, 100f)

        var newGameData = LunaSettingsLoader.SettingsData.filter { it.newGame }
        var crossData = LunaSettingsLoader.SettingsData.filter { !it.newGame }

        var spacing = 0f
        if (newgame) spacing = addOptions(newGameData, true, spacing)
        addOptions(crossData, false, spacing)

        OptionsInnerPannel!!.addUIElement(OptionsList)
        panel!!.addComponent(OptionsInnerPannel)
    }

    fun addOptions(SettingsData: List<LunaSettingsData>, firstRun: Boolean, spacing: Float) : Float
    {
        var spacing = spacing
        if (firstRun)
        {
            OptionsList!!.addSectionHeading("Save-Specific Settings (Only applies to a new save)", Alignment.MID, 0f)
            val tooltip: TooltipCreator = object : TooltipCreator {
                override fun isTooltipExpandable(tooltipParam: Any): Boolean {
                    return false
                }
                override fun getTooltipWidth(tooltipParam: Any): Float {
                    return dW / 4f
                }
                override fun createTooltip(tooltip: TooltipMakerAPI, expanded: Boolean, tooltipParam: Any) {
                    tooltip.addPara("Anything in this section will only be saved within the saves and wont be changeable in the middle of a run. ",0f, Misc.getBasePlayerColor(), Misc.getHighlightColor(), "")
                }
            }
            OptionsList!!.addTooltipToPrevious(tooltip, TooltipMakerAPI.TooltipLocation.BELOW)
        }

        else
        {
            var heading = OptionsList!!.addSectionHeading("Global Settings (Applies to all Saves)", Alignment.MID, 0f)
            heading.position.inTL(0f ,spacing)
        }
        var color = Misc.getBasePlayerColor()

        spacing += 20f
        for (data in SettingsData)
        {
            if (data.modID != selectedMod!!.id) continue
            //Adds outlines and enforces spacing
            var spacingButton = OptionsList!!.addAreaCheckbox("", null,
                Misc.getBasePlayerColor(),
                color,
                color,
                OptionsInnerPannel!!.position.width,
                pH * 0.15f,
                0f)
            spacingButton.isEnabled = false
            spacingButton.position.inTL(0f,spacing)

            var presetTooltip = ""
            var minMaxValue = ""
            val tooltip: TooltipCreator = object : TooltipCreator {
                override fun isTooltipExpandable(tooltipParam: Any): Boolean {
                    return false
                }

                override fun getTooltipWidth(tooltipParam: Any): Float {
                    return dW / 4f
                }

                override fun createTooltip(tooltip: TooltipMakerAPI, expanded: Boolean, tooltipParam: Any) {

                    presetTooltip = when(data.fieldType)
                    {
                        "Int" -> "Accepts whole numbers only (i.e 1200)"
                        "Boolean" -> "Switches between True/False"
                        "Double" -> "Accepts decimal numbers, seperated by a dot (i.e 2.523)"
                        "String" -> "Accepts Letters, Numbers and Characters (i.e Text100*)"
                        "Enum" -> "Press the button to select from a preset of options."
                        else -> ""
                    }
                    minMaxValue = when(data.fieldType)
                    {
                        "Int" -> "\nMin: ${data.minValue.toInt()}   Max: ${data.maxValue.toInt()}"
                        "Double" -> "\nMin: ${data.minValue}   Max: ${data.maxValue}"
                        else -> ""
                    }

                    tooltip.addPara("${data.fieldName} ($presetTooltip) $minMaxValue\n\n${data.FieldTooltip}",0f, Misc.getBasePlayerColor(), Misc.getHighlightColor(), "${data.fieldName}","Min", "Max")
                }
            }

            if (data.fieldType != "Text")
            {
                OptionsList!!.addTooltipToPrevious(tooltip, TooltipMakerAPI.TooltipLocation.BELOW)
                var para = OptionsList!!.addPara("${data.fieldName}", 0f)
                para.position.inTL(100f ,spacing + pH * 0.060f)
            }
            else
            {
                var para = OptionsList!!.addPara("${data.defaultValue}", 0f)
                para.position.inTL(100f ,spacing + pH * 0.030f)
            }

            when (data.fieldType)
            {
                "Int" ->
                {
                    var para1 = OptionsList!!.addPara("", 0f)
                    para1.position.inTL(400f ,spacing + pH * 0.030f)
                    var field = OptionsList!!.addTextField(300f, 0f)

                    if (firstRun) field.text = LunaSettingsLoader.newGameSettings.get(selectedMod!!.id)!!.get(data.fieldID).toString()
                    else field.text = LunaSettings.getInt(data.modID, data.fieldID, false).toString()

                    OptionIntMap.put(data, field)
                }
                "Double" ->
                {
                    var para1 = OptionsList!!.addPara("", 0f)
                    para1.position.inTL(400f ,spacing + pH * 0.030f)
                    var field = OptionsList!!.addTextField(300f, 0f)
                    if (firstRun) field.text = LunaSettingsLoader.newGameSettings.get(selectedMod!!.id)!!.get(data.fieldID).toString()
                    else field.text = LunaSettings.getDouble(data.modID, data.fieldID, false).toString()
                    OptionDoubleMap.put(data, field)
                }
                "String" ->
                {
                    var para1 = OptionsList!!.addPara("", 0f)
                    para1.position.inTL(400f ,spacing + pH * 0.030f)
                    var field = OptionsList!!.addTextField(300f, 0f)
                    if (firstRun) field.text = LunaSettingsLoader.newGameSettings.get(selectedMod!!.id)!!.get(data.fieldID).toString()
                    else field.text = LunaSettings.getString(data.modID, data.fieldID, false).toString()
                    OptionStringMap.put(data, field)
                }
                "Boolean" ->
                {
                    var booleanButton = OptionsList!!.addAreaCheckbox("True/False", null,
                        Misc.getBasePlayerColor(),
                        Misc.getDarkPlayerColor(),
                        Misc.getBrightPlayerColor(),
                        300f,
                        pH * 0.05f,
                        0f)
                    if (firstRun) booleanButton.isChecked = LunaSettingsLoader.newGameSettings.get(selectedMod!!.id)!!.get(data.fieldID) as Boolean
                    else booleanButton.isChecked = LunaSettings.getBoolean(data.modID, data.fieldID, false) ?: false
                    booleanButton.position.inTL(400f,spacing + pH * 0.045f)
                    OptionBooleanMap.put(data, booleanButton)
                }
                "Enum" ->
                {
                    var para1 = OptionsList!!.addPara("Selected: ", 0f)
                    para1.position.inTL(400f ,spacing + pH * 0.025f)
                    OptionEnumTextMap.put(data, para1)

                    if (firstRun) para1.text = "Selected: " + LunaSettingsLoader.newGameSettings.get(selectedMod!!.id)!!.get(data.fieldID).toString()
                    else para1.text = "Selected: " + LunaSettings.getString(data.modID, data.fieldID, false).toString()

                    var enumButton = OptionsList!!.addAreaCheckbox("Select", null,
                        Misc.getBasePlayerColor(),
                        Misc.getDarkPlayerColor(),
                        Misc.getBrightPlayerColor(),
                        300f,
                        pH * 0.05f,
                        0f)

                    enumButton!!.position.inTL(400f,spacing + pH * 0.045f)
                    OptionEnumMap.put(data, enumButton)

                }
            }

            spacing += pH * 0.15f
        }
        return spacing
    }

    fun saveData()
    {
        var data = JSONUtils.loadCommonJSON("LunaSettings/${selectedMod!!.id}.json", "data/config/LunaSettingsDefault.default");
        var saveData: MutableMap<String, Any> = HashMap()

        for (toSave in OptionIntMap)
        {
            if (toSave.key.newGame)
            {
                if (!newgame) continue
                if (toSave.value.text == "")
                {
                    saveData.put(toSave.key.fieldID, toSave.key.defaultValue)
                }
                else
                {
                    saveData.put(toSave.key.fieldID, toSave.value.text.toInt())
                }
            }
            else
            {
                if (toSave.value.text == "")
                {
                    data.put(toSave.key.fieldID, toSave.key.defaultValue)
                }
                else
                {
                    data.put(toSave.key.fieldID, toSave.value.text.toInt())
                }
            }
        }
        for (toSave in OptionDoubleMap)
        {
            if (toSave.key.newGame)
            {
                if (!newgame) continue
                if (toSave.value.text == "")
                {
                    saveData.put(toSave.key.fieldID, toSave.key.defaultValue)
                }
                else
                {
                    saveData.put(toSave.key.fieldID, toSave.value.text.toDouble())
                }
            }
            else
            {
                if (toSave.value.text == "")
                {
                    data.put(toSave.key.fieldID, toSave.key.defaultValue)
                }
                else
                {
                    data.put(toSave.key.fieldID, toSave.value.text.toDouble())
                }
            }
        }
        for (toSave in OptionStringMap)
        {
            if (toSave.key.newGame)
            {
                if (!newgame) continue
                if (toSave.value.text == "")
                {
                    saveData.put(toSave.key.fieldID, toSave.key.defaultValue)
                }
                else
                {
                    saveData.put(toSave.key.fieldID, toSave.value.text)
                }
            }
            else
            {
                if (toSave.value.text == "")
                {
                    data.put(toSave.key.fieldID, toSave.key.defaultValue)
                }
                else
                {
                    data.put(toSave.key.fieldID, toSave.value.text)
                }
            }
        }
        for (toSave in OptionBooleanMap)
        {
            if (toSave.key.newGame)
            {
                if (!newgame) continue
                saveData.put(toSave.key.fieldID, toSave.value.isChecked)
            }
            else
            {
                data.put(toSave.key.fieldID, toSave.value.isChecked)
            }
        }

        for (toSave in OptionEnumTextMap)
        {
            if (toSave.key.newGame)
            {
                if (!newgame) continue
                saveData.put(toSave.key.fieldID, toSave.value.text.replace("Selected: ", ""))
            }
            else
            {
                data.put(toSave.key.fieldID, toSave.value.text.replace("Selected: ", ""))
            }
        }



        data.save()
        LunaSettingsLoader.newGameSettings.put(selectedMod!!.id, saveData)
        LunaSettingsLoader.Settings.put(selectedMod!!.id, data)
    }

    override fun render(alphaMult: Float)
    {
        var playercolor = Misc.getDarkPlayerColor()

        if (ModsInnerPannel != null)
        {
            GL11.glPushMatrix()

            GL11.glTranslatef(0f, 0f, 0f)
            GL11.glRotatef(0f, 0f, 0f, 1f)

            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
            GL11.glColor4ub(playercolor.getRed().toByte(), playercolor.getGreen().toByte(), playercolor.getBlue().toByte(), playercolor.getAlpha().toByte())

            //Inner Pannel Border
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glBegin(GL11.GL_LINE_STRIP)

            GL11.glVertex2f(ModsInnerPannel!!.position.x, ModsInnerPannel!!.position.y)
            GL11.glVertex2f(ModsInnerPannel!!.position.x + ModsInnerPannel!!.position.width, ModsInnerPannel!!.position.y)
            GL11.glVertex2f(ModsInnerPannel!!.position.x + ModsInnerPannel!!.position.width, ModsInnerPannel!!.position.y + ModsInnerPannel!!.position.height)
            GL11.glVertex2f(ModsInnerPannel!!.position.x, ModsInnerPannel!!.position.y + ModsInnerPannel!!.position.height)
            GL11.glVertex2f(ModsInnerPannel!!.position.x, ModsInnerPannel!!.position.y)

            GL11.glEnd()
        }
        if (OptionsInnerPannel != null)
        {
            GL11.glPushMatrix()

            GL11.glTranslatef(0f, 0f, 0f)
            GL11.glRotatef(0f, 0f, 0f, 1f)

            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
            GL11.glColor4ub(playercolor.getRed().toByte(), playercolor.getGreen().toByte(), playercolor.getBlue().toByte(), playercolor.getAlpha().toByte())

            //Inner Pannel Border
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glBegin(GL11.GL_LINE_STRIP)

            GL11.glVertex2f(OptionsInnerPannel!!.position.x, OptionsInnerPannel!!.position.y)
            GL11.glVertex2f(OptionsInnerPannel!!.position.x + OptionsInnerPannel!!.position.width, OptionsInnerPannel!!.position.y)
            GL11.glVertex2f(OptionsInnerPannel!!.position.x + OptionsInnerPannel!!.position.width, OptionsInnerPannel!!.position.y + OptionsInnerPannel!!.position.height)
            GL11.glVertex2f(OptionsInnerPannel!!.position.x, OptionsInnerPannel!!.position.y + OptionsInnerPannel!!.position.height)
            GL11.glVertex2f(OptionsInnerPannel!!.position.x, OptionsInnerPannel!!.position.y)

            GL11.glEnd()
        }
    }


    override fun positionChanged(position: PositionAPI?) {

    }

    override fun renderBelow(alphaMult: Float) {
        var bgColor = Color(20, 20, 20)
        val x: Float = panel!!.position.x
        val y: Float = panel!!.position.y
        val w: Float = panel!!.position.width
        val h: Float = panel!!.position.height
        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glColor4f(bgColor.getRed() / 255f,
            bgColor.getGreen() / 255f,
            bgColor.getBlue() / 255f,
            bgColor.getAlpha() / 255f * alphaMult)
        GL11.glRectf(x, y, x + w, y + h)
        GL11.glColor4f(1f, 1f, 1f, 1f)
        GL11.glPopMatrix()
    }

    override fun processInput(events: MutableList<InputEventAPI>?) {
        for (event in events!!) {
            if (event.isConsumed) continue
            //is ESC is pressed, close the custom UI panel and the blank IDP we used to create it
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
}