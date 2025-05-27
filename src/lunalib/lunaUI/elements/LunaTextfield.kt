package lunalib.lunaUI.elements

import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.Fonts
import com.fs.starfarer.api.ui.LabelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.IntervalUtil
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.campaign.CampaignEngine
import lunalib.lunaSettings.LunaSettings
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor


class LunaTextfield(private var text: String, private var multiline: Boolean, var textColor: Color = Misc.getBasePlayerColor(), tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {

    private var textElement: TooltipMakerAPI? = null
    private var para: LabelAPI? = null

    private var blinkInterval = IntervalUtil(1f, 1f)
    private var blink = false

    private var font = Fonts.DEFAULT_SMALL

    init {

        innerElement.setParaFont(font)
        //addText(text, textColor, Misc.getHighlightColor())
        changePara(text)

        onHoverEnter() {
            playScrollSound()
        }
        onClick {
            playClickSound()
            select()
        }
        onClickOutside {
            if (isSelected())
            {
                playSound("ui_typer_buzz")
            }
            unselect()
        }
    }


    override fun advance(amount: Float) {
        super.advance(amount)

        blinkInterval.advance(amount)
        if (!isSelected())
        {
            borderColor = Misc.getDarkPlayerColor()
            blinkInterval.forceIntervalElapsed()
            blink = false
        }
        else
        {
            borderColor = Misc.getDarkPlayerColor().brighter()
        }

        if (blinkInterval.intervalElapsed())
        {
            blink = !blink
            if (para != null)
            {
                if (blink)
                {
                    para!!.text = text + " "
                    //changeText(text )
                }
                else
                {
                    para!!.text = text + "_"
                    //changeText(text + "_")
                }
            }

        }
    }

    fun getText(): String
    {
        return text
    }

    fun changeFont(font: String)
    {
        this.font = font
        changePara(text)
    }

    fun changePara(text: String)
    {
        if (textElement != null)
        {
            elementPanel.removeComponent(textElement)
            para = null
            textElement = null
        }

        this.text = text

        textElement = elementPanel.createUIElement(width, height, false)
        elementPanel.addUIElement(textElement)

        textElement!!.setParaFont(font)

        var add = " "
        if (blink) add = "_"
        para = textElement!!.addPara(text + add, 0f, textColor, Misc.getHighlightColor())
        if (!multiline)
        {
            para!!.position.inTL(5f, height / 2 - para!!.computeTextHeight(para!!.text) / 2)
        }

       /* if (blink)
        {
            changeText(text + " ")
        }
        else
        {
            changeText(text + "_")
        }*/
    }


    override fun processInput(events: MutableList<InputEventAPI>?) {
        super.processInput(events)

        /*playScrollSound()

        if (para!!.position.height + para!!.computeTextHeight("Example Calc Calc") > height) continue

        text += Keyboard.getKeyName(event.eventValue)
        changePara(text)

        var test = para!!.position.y
        var test2 = para!!.position.y*/

        if (para == null) return
        if (!isSelected()) return

        for (event in events!!)
        {
            if (event.isConsumed) continue
            if (event.isKeyboardEvent && (event.isKeyDownEvent || event.isRepeat))
            {
                if (event.eventValue == Keyboard.KEY_RETURN || event.eventValue == Keyboard.KEY_NUMPADENTER)
                {
                    playSound("ui_typer_buzz")
                    unselect()
                    event.consume()
                    break
                }

                if (event.eventValue == Keyboard.KEY_TAB)
                {

                }

                if (event.eventValue == Keyboard.KEY_ESCAPE)
                {
                    playSound("ui_typer_buzz")
                    unselect()
                    event.consume()
                    break
                }

                if (event.eventValue == Keyboard.KEY_V && event.isCtrlDown)
                {
                    var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor) as String
                    for (char in clipboard)
                    {
                        appendCharIfPossible(char)
                    }
                    event.consume()
                    break
                }

                if (event.eventValue == Keyboard.KEY_BACK)
                {
                    var empty = text.isEmpty()

                    if (empty)
                    {
                        playSound("ui_typer_buzz")
                        event.consume()
                    }
                    else if (event.isShiftDown)
                    {
                        deleteAll()
                    }
                    else if (event.isCtrlDown)
                    {
                        deleteLastWord()
                    }
                    else
                    {
                        playSound("ui_typer_type")
                        changePara(text.substring(0, text.length - 1))
                    }


                    event.consume()
                    break
                }

                if (event.isCtrlDown || event.isAltDown)
                {
                    event.consume()
                    break
                }

                var char = event.eventChar
                appendCharIfPossible(char)
                event.consume()
            }
        }
    }


    private fun appendCharIfPossible(char: Char)
    {
        var appended = text + char
        var validChar = isValidChar(char)

        var valid = false

        if (multiline && para!!.position.height + para!!.computeTextHeight("Example Calc") < height) valid = true
        else if (!multiline && para!!.computeTextWidth(para!!.text) < (width - 20)) valid = true

        if (validChar && valid)
        {
            playSound("ui_typer_type")
            changePara(appended)
        }
        else
        {
            //playSound("ui_typer_buzz")
        }
    }

    private fun isValidChar(char: Char?) : Boolean
    {
        if (char == '\u0000')
        {
            return false
        }

        else if (char == '%')
        {
            return false
        }
        else if (char == '$')
        {
            return false
        }

        return true
    }


    private fun deleteAll()
    {
        playSound("ui_typer_type")
        changePara("")
    }

    private fun deleteLastWord()
    {

        var last = text.lastIndexOf(" ")
        if (last == text.length - 1 && last > 0)
        {
            last = text.substring(0, last).lastIndexOf(" ")
        }

        if (last == -1)
        {
            deleteAll()
        }
        else
        {
            playSound("ui_typer_type")
            changePara(text.substring(0, last + 1))
        }
    }

}
