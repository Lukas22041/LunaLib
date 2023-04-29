package lunalib.lunaUI.panel

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate.DialogCallbacks
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import lunalib.backend.ui.components.base.LunaUIBaseElement
import lunalib.lunaUI.LunaUIUtils

abstract class LunaBaseCustomPanelPlugin : CustomUIPanelPlugin {

    lateinit var panel: CustomPanelAPI
    lateinit var callbacks: DialogCallbacks
    lateinit var dialog: InteractionDialogAPI

    private var openedFromExistingDialog: Boolean = false

    fun init(panel: CustomPanelAPI, callbacks: CustomVisualDialogDelegate.DialogCallbacks, dialog: InteractionDialogAPI, openedFromExistingDialog: Boolean)
    {
        this.panel = panel
        this.callbacks = callbacks
        this.dialog = dialog

        this.openedFromExistingDialog = openedFromExistingDialog

        init()
    }

    abstract fun init()

    override fun positionChanged(position: PositionAPI?) {

    }

    override fun renderBelow(alphaMult: Float) {

    }

    override fun render(alphaMult: Float) {

    }

    override fun advance(amount: Float) {

    }

    override fun processInput(events: MutableList<InputEventAPI>) {

    }

    final fun close()
    {
        onClose()

        dialog.showTextPanel()
        dialog.showVisualPanel()
        callbacks.dismissDialog()

        //Not clearing this may cause a memory leak
        LunaUIBaseElement.selectedMap.clear()
        LunaUIUtils.selectedElements.clear()

        if (!openedFromExistingDialog)
        {
            dialog.dismiss()
        }
    }

    fun onClose()
    {

    }
}