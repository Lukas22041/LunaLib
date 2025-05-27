package lunalib.lunaUI.panel

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import org.lwjgl.opengl.GL11
import java.awt.Color

class LunaWindowPlugin(private var parentPanel: CustomPanelAPI, private var parentElement: TooltipMakerAPI) : CustomUIPanelPlugin {

    lateinit var panel: CustomPanelAPI
    lateinit var position: PositionAPI



    fun close() {
        parentPanel.removeComponent(panel)
        parentElement.removeComponent(parentPanel)
    }

    override fun positionChanged(position: PositionAPI) {
        this.position = position
    }


    override fun renderBelow(alphaMult: Float) {

    }


    override fun render(alphaMult: Float) {

    }


    override fun advance(amount: Float) {

    }


    override fun processInput(events: MutableList<InputEventAPI>) {

        var x = position.x
        var y = position.y
        var width = position.width
        var height = position.height

        for (event in events) {
            if (event.isConsumed) continue
            if (event.isMouseDownEvent) {
                if (event.x.toFloat() in x..(x + width) && event.y.toFloat() in y..(y +height))
                {

                }
                else {
                    close()
                }
                event.consume()
                continue
            }
            if (event.isMouseEvent) {
                if (event.x.toFloat() in x..(x + width) && event.y.toFloat() in y..(y +height))
                {
                    event.consume()
                    continue
                }
            }
        }
    }

    override fun buttonPressed(buttonId: Any?) {

    }


}