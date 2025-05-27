package lunalib.backend.ui.refit

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CoreUITabId
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.ui.*
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.campaign.CampaignState
import com.fs.state.AppDriver
import lunalib.lunaExtensions.*
import lunalib.lunaRefit.BaseRefitButton
import lunalib.lunaRefit.LunaRefitManager
import lunalib.lunaUI.elements.LunaSpriteElement
import org.lazywizard.lazylib.MathUtils
import java.awt.Color
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

class RefitButtonAdder : EveryFrameScript {

    var mainPanel: CustomPanelAPI? = null
    var backgroundPanel: CustomPanelAPI? = null

    var corePanel: UIPanelAPI? = null
    var activePanel: CustomPanelAPI? = null
    var activePanelButton: BaseRefitButton? = null

    var member: FleetMemberAPI? = null
    var variant: ShipVariantAPI? = null
    var market: MarketAPI? = null

    var firstButtonLoad = true
    var lastCount = 0


    private val fieldClass = Class.forName("java.lang.reflect.Field", false, Class::class.java.classLoader)
    private val setFieldHandle = MethodHandles.lookup().findVirtual(fieldClass, "set", MethodType.methodType(Void.TYPE, Any::class.java, Any::class.java))
    private val setFieldAccessibleHandle = MethodHandles.lookup().findVirtual(fieldClass,"setAccessible", MethodType.methodType(Void.TYPE, Boolean::class.javaPrimitiveType))

    private val methodClass = Class.forName("java.lang.reflect.Method", false, Class::class.java.classLoader)
    private val getMethodNameHandle = MethodHandles.lookup().findVirtual(methodClass, "getName", MethodType.methodType(String::class.java))
    private val invokeMethodHandle = MethodHandles.lookup().findVirtual(methodClass, "invoke", MethodType.methodType(Any::class.java, Any::class.java, Array<Any>::class.java))

    companion object {
        @JvmStatic
        var requiresVariantUpdate = false

        @JvmStatic
        var updateBackgroundPanel = false

        @JvmStatic
        var removeActivePanel = false
    }


    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
       return true
    }

    override fun advance(amount: Float) {

        if (Global.getSector().campaignUI.currentCoreTab != CoreUITabId.REFIT) {
            mainPanel = null
            backgroundPanel = null
            member = null
            variant = null
            corePanel = null
            market = null
            activePanel = null
            activePanelButton = null
            lastCount = 0
            return
        }

        var state = AppDriver.getInstance().currentState

        //Makes sure that the current state is the campaign state.
        if (state !is CampaignState) return


        var base: UIPanelAPI? = null

        if (corePanel != null && removeActivePanel) {
            removeActivePanel = false
            if (activePanel != null){
                activePanelButton!!.onPanelClose(member, variant, market)
                corePanel!!.removeComponent(activePanel)

                activePanel = null
                activePanelButton = null
            }
        }

        var core = ReflectionUtils.invoke("getCore", state)

        var dialog = ReflectionUtils.invoke("getEncounterDialog", state)
        if (dialog != null)
        {
            core = ReflectionUtils.invoke("getCoreUI", dialog)

            var target = Global.getSector().campaignUI?.currentInteractionDialog?.interactionTarget
            if (target != null && target.market != null)
            {
                market = target.market
            }
        }
        else
        {
            market = null
        }


        if (core is UIPanelAPI)
        {

            val refitTab = ReflectionUtils.invoke("getCurrentTab", core) as UIPanelAPI?

            if (refitTab is UIPanelAPI)
            {
                var child3 = refitTab.getChildrenCopy().find { ReflectionUtils.hasMethodOfName("syncWithCurrentVariant", it) }

                if (child3 is UIPanelAPI)
                {
                    member = ReflectionUtils.invoke("getMember", child3) as FleetMemberAPI
                    base = child3

                    var shipdisplay = ReflectionUtils.invoke("getShipDisplay", child3!!) as UIPanelAPI?
                    variant = ReflectionUtils.invoke("getCurrentVariant", shipdisplay!!) as ShipVariantAPI?

                    if (requiresVariantUpdate)
                    {
                        try {
                            ReflectionUtils.invoke("syncWithCurrentVariant", child3!!, true)
                        } catch (e: Throwable) {
                            //do nothing
                            try {
                                ReflectionUtils.invoke("syncWithCurrentVariant", child3!!)
                            } catch (e: Throwable) {
                                println("error while pre-syncing variant in refit: $e")
                            }
                        }

                        try {
                            ReflectionUtils.invoke("saveCurrentVariant", child3!!, false)
                        } catch (e: Throwable) {
                            //do nothing
                            try {
                                ReflectionUtils.invoke("saveCurrentVariant", child3!!)
                            } catch (e: Throwable) {
                                println("error while saving variant in refit: $e")
                            }
                        }

                        try {
                            ReflectionUtils.invoke("setEditedSinceSave", child3!!, false)
                        } catch (e: Throwable) {
                            //do nothing
                        }

                        try {
                            ReflectionUtils.invoke("syncWithCurrentVariant", child3!!, true)
                        } catch (e: Throwable) {
                            try {
                                ReflectionUtils.invoke("syncWithCurrentVariant", child3!!)
                            } catch (e: Throwable) {
                                println("error while post-syncing variant in refit: $e")
                            }
                        }

                        requiresVariantUpdate = false
                    }

                    if (base.getChildrenCopy().any { it == mainPanel }) {

                        if (updateBackgroundPanel) {
                            updateBackgroundPanel = false
                            recreateBackgroundPanel()
                        }

                        return
                    }
                }
            }
        }
        if (base == null)
        {
            firstButtonLoad = true
        }

        if (base != null)
        {

            //var portraitPanel = officerAndCR.getChildrenCopy().find { hasMethodOfName("getText", it) }
            // var portraitPanel =  base!!.getChildrenCopy().random()
            var opPanel =  ReflectionUtils.invoke("getOpDisplay", base!!) as UIPanelAPI?
            if (opPanel == null) return

            corePanel = core as UIPanelAPI?
            if (dialog is UIPanelAPI) corePanel = dialog

            var buttonWidth = 320f

            mainPanel = Global.getSettings().createCustom(buttonWidth , 300f, null)

            base.addComponent(mainPanel)
            //buttonPanel!!.position.inTL(0f, 0f)
            mainPanel!!.position.leftOfTop(opPanel, 75f)

            var openButtonElement = mainPanel!!.createUIElement(buttonWidth, 20f, false)
            mainPanel!!.addUIElement(openButtonElement)
            var openButton = openButtonElement.addLunaSpriteElement("graphics/ui/lunaRefitButtonMain.png", LunaSpriteElement.ScalingTypes.STRETCH_SPRITE, buttonWidth, 20f).apply {
                getSprite().color = Misc.getDarkPlayerColor().setAlpha(255)
                innerElement.bringComponentToTop(innerElement.prev)

                onHoverEnter {
                    playScrollSound()
                    getSprite().color = Misc.getDarkPlayerColor().brighter().setAlpha(255)
                }

                onHoverExit {
                    getSprite().color = Misc.getDarkPlayerColor().setAlpha(255)
                }
            }

            openButtonElement.addTooltipToPrevious( object : TooltipMakerAPI.TooltipCreator {
                override fun isTooltipExpandable(tooltipParam: Any?): Boolean {
                    return false
                }

                override fun getTooltipWidth(tooltipParam: Any?): Float {
                    return 300f
                }

                override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {
                    tooltip!!.addPara("Click to open/close an expanded list of ship configurations added by mods. The number displayed on the button shows how many options are available for the current ship.", 0f)
                }

            }, TooltipMakerAPI.TooltipLocation.BELOW)

            var size = LunaRefitManager.buttons.filter { it.shouldShow(member, variant, market) }.size
            lastCount = size

            openButtonElement.setTitleFont(Fonts.DEFAULT_SMALL)
            var openPara = openButtonElement.addTitle("Additional Options  ($size)", Misc.getBasePlayerColor().setAlpha(0))

            openButton.advance {
                if (member != null && variant != null)
                {
                    var buttons = LunaRefitManager.buttons.filter { it.shouldShow(member, variant, market) }.size
                    if (buttons != lastCount)
                    {
                        lastCount = buttons
                        openPara.text = "Additional Options  ($buttons)"
                        openPara.position.inTL(openButtonElement.position.width / 2 - openPara.computeTextWidth(openPara.text) / 2 + 5 ,1f)
                    }

                }
            }

            openPara.position.inTL(openButtonElement.position.width / 2 - openPara.computeTextWidth(openPara.text) / 2 + 5 ,1f)

            openButtonElement.position.inTL(0f, -5f)

            if (firstButtonLoad)
            {
                openButton.getSprite().alphaMult = 0.0f
                openButton.advance {
                    var sprite = openButton.getSprite()
                    if (sprite.alphaMult < 1f)
                    {
                        sprite.alphaMult += 0.1f

                        var colorAlpha = (155 * sprite.alphaMult) + 100
                        colorAlpha = MathUtils.clamp(colorAlpha, 0f, 255f)

                        openPara.setColor(Misc.getBasePlayerColor().setAlpha(colorAlpha.toInt()))
                    }
                }
            }


            openButton.onClick {
                openButton.playClickSound()
                recreateBackgroundPanel()

            }
        }
    }

    fun recreateBackgroundPanel() {

        if (mainPanel == null) return
        if (backgroundPanel != null) {
            mainPanel!!.removeComponent(backgroundPanel)
        }

        var plugin = RefitPanelBackgroundPlugin(mainPanel!!, false)
        backgroundPanel = mainPanel!!.createCustomPanel(mainPanel!!.position.width, mainPanel!!.position.height, plugin)

        plugin.panel = backgroundPanel
        mainPanel!!.addComponent(backgroundPanel)
        // backgroundPanel.position.inTL(595f, 5f)
        backgroundPanel!!.position.inTL(5f, -5f)

        var closeButtonElement = backgroundPanel!!.createUIElement(backgroundPanel!!.position.width, 20f, false)
        backgroundPanel!!.addUIElement(closeButtonElement)
        var closeButton = closeButtonElement.addLunaSpriteElement("graphics/ui/lunaRefitButtonMain.png", LunaSpriteElement.ScalingTypes.STRETCH_SPRITE, mainPanel!!.position.width, 20f).apply {
            getSprite().color = Misc.getDarkPlayerColor().setAlpha(255)
            innerElement.bringComponentToTop(innerElement.prev)

            onHoverEnter {
                playScrollSound()
                getSprite().color = Misc.getDarkPlayerColor().brighter().setAlpha(255)
            }

            onHoverExit {
                getSprite().color = Misc.getDarkPlayerColor().setAlpha(255)
            }

            onClick {
                playClickSound()
                plugin.close()
            }
        }

        closeButtonElement.addTooltipToPrevious( object : TooltipMakerAPI.TooltipCreator {
            override fun isTooltipExpandable(tooltipParam: Any?): Boolean {
                return false
            }

            override fun getTooltipWidth(tooltipParam: Any?): Float {
                return 300f
            }

            override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {
                tooltip!!.addPara("Click to open/close an expanded list of ship configurations added by mods. The number displayed on the button shows how many options are available for the current ship.", 0f)
            }

        }, TooltipMakerAPI.TooltipLocation.BELOW)

        closeButton.position.inTL(0f, 0f)

        closeButtonElement.setTitleFont(Fonts.DEFAULT_SMALL)

        var closePara = closeButtonElement.addTitle("Close", Misc.getBasePlayerColor())
        closePara.position.inTL(closeButtonElement.position.width / 2 - closePara.computeTextWidth(closePara.text) / 2 ,1f)
        closeButtonElement.position.inTL(0f, 0f)



        var buttonElement = backgroundPanel!!.createUIElement(backgroundPanel!!.position.width, backgroundPanel!!.position.height - closeButton.height, true)
        buttonElement.position.belowLeft(closeButtonElement, 0f)
        buttonElement.addPara("", 0f).position.inTL(20f, 0f)
        //buttonElement.addPara("Test", 0f)

        var last: UIComponentAPI? = null
        for (button in LunaRefitManager.buttons.sortedBy {it.getOrder(member, variant)})
        {
            if (!button.shouldShow(member, variant, market)) continue

            var sprite = buttonElement.addLunaSpriteElement(button.getIconName(member, variant), LunaSpriteElement.ScalingTypes.STRETCH_SPRITE, 0f, 0f).apply {
                elementPanel.position.setSize(40f, 40f)
                innerElement.position.setSize(40f, 40f)
                getSprite().setSize(40f, 40f)
            }

            var uiButton = buttonElement.addLunaElement(225f, 40f).apply {
                enableTransparency = true

                if (!button.isClickable(member, variant, market))
                {
                    backgroundAlpha = 0.4f
                }
                else
                {
                    backgroundAlpha = 0.8f
                }
                borderAlpha = 0.8f

                addText(button.getButtonName(member, variant), baseColor = Misc.getBasePlayerColor())
                centerText()

                advance {
                    button.advance(member, variant, it, market)
                }

                onClick {

                    if (!button.isClickable(member, variant, market)) {
                        playSound("ui_button_disabled_pressed", 1f, 1f)
                        return@onClick
                    }

                    playClickSound()

                    button.onClick(member, variant, it, market)

                    if (!it.isLMBEvent) return@onClick

                    if (button.hasPanel(member, variant, market)) {

                        var buttonPlugin = RefitPanelBackgroundPlugin(corePanel!!, true)

                        var width = button.getPanelWidth(member, variant)
                        var height = button.getPanelHeight(member, variant)
                        activePanel = Global.getSettings().createCustom(width, height, buttonPlugin)
                        activePanelButton = button

                        activePanel!!.position.inTL(Global.getSettings().screenWidth / 2 - width / 2, Global.getSettings().screenHeight / 2 - height / 2)

                        buttonPlugin.panel = activePanel
                        corePanel!!.addComponent(activePanel)

                        //button.prePanelInit(openedPanel, corePanel)
                        button.initPanel(activePanel, member, variant, market)
                    }
                }

                onHoverEnter{
                    playScrollSound()

                    if (button.isClickable(member, variant, market)) {
                        borderAlpha = 1f
                        backgroundAlpha = 1f
                    }
                }

                onHoverExit {
                    if (!button.isClickable(member, variant, market))
                    {
                        backgroundAlpha = 0.4f
                    }
                    else
                    {
                        backgroundAlpha = 0.8f
                    }
                }
            }
            uiButton.elementPanel.position.rightOfMid(sprite.elementPanel, 10f)

            if (button.hasTooltip(member, variant, market)) {
                buttonElement.addTooltipToPrevious( object : TooltipMakerAPI.TooltipCreator {
                    override fun isTooltipExpandable(tooltipParam: Any?): Boolean {
                        return false
                    }

                    override fun getTooltipWidth(tooltipParam: Any?): Float {
                        return button.getToolipWidth(member, variant, market)
                    }

                    override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {
                        button.addTooltip(tooltip, member, variant, market)
                    }

                }, TooltipMakerAPI.TooltipLocation.RIGHT)
            }

            buttonElement.addSpacer(20f)
            if (last != null) {
                sprite.elementPanel.position.belowLeft(last, 20f)
            }

            last = sprite.elementPanel
        }


        backgroundPanel!!.addUIElement(buttonElement)


    }

   /* //Used to be able to find specific files without having to reference their obfuscated class name.
    private fun hasMethodOfName(name: String, instance: Any) : Boolean {

        val instancesOfMethods: Array<out Any> = instance.javaClass.getDeclaredMethods() as Array<out Any>
        return instancesOfMethods.any { getMethodNameHandle.invoke(it) == name }
    }

    //Required to execute obfuscated methods without referencing their obfuscated class name.
    private fun invokeMethod(methodName: String, instance: Any, vararg arguments: Any?) : Any?
    {
        var method: Any? = null

        val clazz = instance.javaClass
        val args = arguments.map { it!!::class.javaPrimitiveType ?: it::class.java }
        val methodType = MethodType.methodType(Void.TYPE, args)

        method = clazz.getMethod(methodName, *methodType.parameterArray()) as Any?

        return invokeMethodHandle.invoke(method, instance, arguments)
    }

    fun setPrivateVariable(fieldName: String, instanceToModify: Any, newValue: Any?)
    {
        var field: Any? = null
        try {  field = instanceToModify.javaClass.getField(fieldName) } catch (e: Throwable) {
            try {  field = instanceToModify.javaClass.getDeclaredField(fieldName) } catch (e: Throwable) { }
        }

        setFieldAccessibleHandle.invoke(field, true)
        setFieldHandle.invoke(field, instanceToModify, newValue)
    }
*/

    //Extends the UI API by adding the required method to get the child objects of a panel, only when used within this class.
    private fun UIPanelAPI.getChildrenCopy() : List<UIComponentAPI> {
        return ReflectionUtils.invoke("getChildrenCopy", this) as List<UIComponentAPI>
    }

    private fun Color.setAlpha(alpha: Int) : Color {
        return Color(this.red, this.green, this.blue, alpha)
    }

}

/*
Made by Lukas04, improved with help from Starficz.
Concepts have been learned from lyravega, float, andylizi and their mods.
**/
internal object ReflectionUtils {

    private val fieldClass = Class.forName("java.lang.reflect.Field", false, Class::class.java.classLoader)
    private val setFieldHandle = MethodHandles.lookup().findVirtual(fieldClass, "set", MethodType.methodType(Void.TYPE, Any::class.java, Any::class.java))
    private val getFieldHandle = MethodHandles.lookup().findVirtual(fieldClass, "get", MethodType.methodType(Any::class.java, Any::class.java))
    private val getFieldTypeHandle = MethodHandles.lookup().findVirtual(fieldClass, "getType", MethodType.methodType(Class::class.java))
    private val getFieldNameHandle = MethodHandles.lookup().findVirtual(fieldClass, "getName", MethodType.methodType(String::class.java))
    private val setFieldAccessibleHandle = MethodHandles.lookup().findVirtual(fieldClass,"setAccessible", MethodType.methodType(Void.TYPE, Boolean::class.javaPrimitiveType))

    private val methodClass = Class.forName("java.lang.reflect.Method", false, Class::class.java.classLoader)
    private val getMethodNameHandle = MethodHandles.lookup().findVirtual(methodClass, "getName", MethodType.methodType(String::class.java))
    private val getMethodParametersHandle = MethodHandles.lookup().findVirtual(methodClass, "getParameterTypes", MethodType.methodType(arrayOf<Class<*>>().javaClass))
    private val getMethodReturnTypeHandle = MethodHandles.lookup().findVirtual(methodClass, "getReturnType", MethodType.methodType(Class::class.java))

    private val invokeMethodHandle = MethodHandles.lookup().findVirtual(methodClass, "invoke", MethodType.methodType(Any::class.java, Any::class.java, Array<Any>::class.java))


    //Cache
    private var fieldsCache = HashMap<ReflectedFieldKey, ReflectedField>()
    private var fieldNameCache = HashMap<Class<*>, HashSet<String>>()
    private var fieldTypesCache = HashMap<Class<*>, HashSet<Class<*>>>()

    private var methodsCache = HashMap<ReflectedMethodKey, ReflectedMethod>()
    private var methodNameCache = HashMap<Class<*>, HashSet<String>>()

    //Cached Results of Reflected Fields
    private data class ReflectedFieldKey(
        val clazz: Class<*>,
        val fieldName: String?,
        val fieldType: Class<*>?,
        val withSuper: Boolean)
    class ReflectedField(private val field: Any) {
        fun get(instance: Any?): Any? {
            return getFieldHandle.invoke(field, instance)
        }
        fun set(instance: Any?, value: Any?) {
            setFieldHandle.invoke(field, instance, value)
        }
    }

    //Cached Results of Reflected Fields
    private data class ReflectedMethodKey(
        val clazz: Class<*>,
        val methodName: String?,
        val numOfParams: Int?,
        val returnType: Class<*>?,
        val parameterTypes: List<Class<*>?>?) {
    }
    class ReflectedMethod(private val method: Any) {
        fun invoke(instance: Any?, vararg arguments: Any?): Any? = invokeMethodHandle.invoke(method, instance, arguments)
    }

    fun set(fieldName: String? = null, instanceToModify: Any, newValue: Any?, fieldType: Class<*>? = null)
    {
        getField(fieldName, instanceToModify.javaClass, fieldType)!!.set(instanceToModify, newValue)
    }

    fun get(fieldName: String? = null, instanceToGetFrom: Any, fieldType: Class<*>? = null): Any? {
        return getField(fieldName, instanceToGetFrom.javaClass, fieldType)!!.get(instanceToGetFrom)
    }

    fun instantiate(clazz: Class<*>, vararg arguments: Any?) : Any?
    {
        val args = arguments.map { it!!::class.javaPrimitiveType ?: it!!::class.java }
        val methodType = MethodType.methodType(Void.TYPE, args)

        val constructorHandle = MethodHandles.lookup().findConstructor(clazz, methodType)
        val instance = constructorHandle.invokeWithArguments(arguments.toList())

        return instance
    }

    @JvmStatic
    fun invoke(methodName: String? = null, instance: Any, vararg arguments: Any?, returnType: Class<*>? = null, parameterCount: Int? = null) : Any?
    {
        var method: Any? = null

        val clazz = instance.javaClass
        val args = arguments.map { it!!::class.javaPrimitiveType ?: it::class.java }

        return getMethod(methodName, instance.javaClass, returnType, parameterCount, args)!!.invoke(instance, *arguments)
    }

    //Cache for field names to reduce reflection calls during UI Crawling
    fun hasVariableOfName(name: String, instance: Any) : Boolean {
        var clazz = instance.javaClass
        return fieldNameCache.getOrPut(clazz) {
            //Class has not been cached yet, save all method names to the cache
            var set = HashSet<String>()
            val instancesOfFields: Array<out Any> = instance.javaClass.declaredFields as Array<out Any>
            for (field in instancesOfFields) {
                set.add(getFieldNameHandle.invoke(field) as String)
            }
            set //Returns the list
        }.contains(name)
    }

    //Cache for field types to reduce reflection calls during UI Crawling
    fun hasVariableOfType(type: Class<*>, instance: Any) : Boolean {
        var clazz = instance.javaClass
        return fieldTypesCache.getOrPut(clazz) {
            //Class has not been cached yet, save all method names to the cache
            var set = HashSet<Class<*>>()
            val instancesOfFields: Array<out Any> = instance.javaClass.declaredFields as Array<out Any>
            for (field in instancesOfFields) {
                set.add(getFieldTypeHandle.invoke(field) as Class<*>)
            }
            set //Returns the list
        }.contains(type)
    }

    //Cache for method names to reduce reflection calls during UI Crawling
    fun hasMethodOfName(name: String, instance: Any) : Boolean {
        var clazz = instance.javaClass
        return methodNameCache.getOrPut(clazz) {
            //Class has not been cached yet, save all method names to the cache
            var set = HashSet<String>()
            val instancesOfMethods: Array<out Any> = instance.javaClass.getDeclaredMethods() as Array<out Any>
            for (method in instancesOfMethods) {
                set.add(getMethodNameHandle.invoke(method) as String)
            }
            set //Returns the list
        }.contains(name)
    }

    fun getField(fieldName: String? = null, fieldClazz: Class<*>, fieldType: Class<*>? = null, recursionLimit: Int? = null, superclazz: Class<*>? = null) : ReflectedField? {

        var clazz = fieldClazz
        if (superclazz != null) clazz = superclazz

        var withSuper = false
        if (recursionLimit != null) withSuper = true

        val key = ReflectedFieldKey(clazz, fieldName, fieldType, withSuper)
        return fieldsCache.getOrPut(key) {
            var targetField: Any? = null

            var fields = (clazz.fields + clazz.declaredFields) as Array<out Any>

            for (field in fields) {

                if (fieldName != null) {
                    var name = getFieldNameHandle.invoke(field)
                    if (name != fieldName) continue
                }

                if (fieldType != null) {
                    var type = getFieldTypeHandle.invoke(field)
                    if (type != fieldType) continue
                }

                targetField = field
            }

            if (targetField == null && recursionLimit != null && recursionLimit > 0) {
                var superc = clazz.superclass
                if (superc != null) {
                    targetField = getField(fieldName, clazz, fieldType, recursionLimit-1, superc)
                }
            }

            setFieldAccessibleHandle.invoke(targetField, true)
            ReflectedField(targetField!!)
        }
    }

    fun getMethod(methodName: String? = null, clazz: Class<*>, returnType: Class<*>?=null, parameterCount: Int?=null, parameters: List<Class<*>?>?) : ReflectedMethod? {
        val key = ReflectedMethodKey(clazz, methodName, parameterCount, returnType, parameters)
        return methodsCache.getOrPut(key) {
            var targetMethod: Any? = null

            var methods = ((clazz.methods + clazz.declaredMethods)).toSet() as Set<Any>
            for (method in methods) {

                if (methodName != null) {
                    var name = getMethodNameHandle.invoke(method)
                    if (name != methodName) continue
                }

                if (parameters?.isNotEmpty() == true || parameterCount != null) {
                    var methodParams = getMethodParametersHandle.invoke(method) as Array<Class<*>>

                    //Skip if parameters do not match
                    if (parameters?.isNotEmpty() == true) {
                        if (methodParams.any { !parameters.contains(it) }) continue
                    }

                    //Skip if parameters count does not match
                    if (parameterCount != null) {
                        if (methodParams.size != parameterCount) continue
                    }
                }

                if (returnType != null) {
                    var type = getMethodReturnTypeHandle.invoke(method)
                    if (type != returnType) continue
                }


                targetMethod = method
                break
            }

            ReflectedMethod(targetMethod!!) //Returns the method
        }
    }

    //Useful for some classes with just one field
    fun getFirstDeclaredField(instanceToGetFrom: Any): Any? {
        var field: Any? = instanceToGetFrom.javaClass.declaredFields[0]
        setFieldAccessibleHandle.invoke(field, true)
        return getFieldHandle.invoke(field, instanceToGetFrom)
    }

}