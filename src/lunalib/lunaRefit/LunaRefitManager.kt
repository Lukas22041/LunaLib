package lunalib.lunaRefit

object LunaRefitManager {

    var buttons = ArrayList<lunalib.lunaRefit.BaseRefitButton>()

    @JvmStatic
    fun addRefitButton(button: lunalib.lunaRefit.BaseRefitButton) {
        buttons.add(button)
    }

    fun removeButton(button: lunalib.lunaRefit.BaseRefitButton) {
        buttons.remove(button)
    }

    @JvmStatic
    fun getFirstButtonOfClass(buttonClass: Class<*>) : lunalib.lunaRefit.BaseRefitButton? {
        return buttons.find { it.javaClass.name == buttonClass.name }
    }

    @JvmStatic
    fun hasButtonOfClass(buttonClass: Class<*>) : Boolean {
        return buttons.any { it.javaClass.name == buttonClass.name }
    }


}