package lunalib.lunaRefit

object LunaRefitManager {

    internal var buttons = ArrayList<BaseRefitButton>()

    @JvmStatic
    fun addRefitButton(button: BaseRefitButton) {
        buttons.add(button)
    }

    fun removeButton(button: BaseRefitButton) {
        buttons.remove(button)
    }

    @JvmStatic
    fun getFirstButtonOfClass(buttonClass: Class<*>) : BaseRefitButton? {
        return buttons.find { it.javaClass.name == buttonClass.name }
    }

    @JvmStatic
    fun hasButtonOfClass(buttonClass: Class<*>) : Boolean {
        return buttons.any { it.javaClass.name == buttonClass.name }
    }


}