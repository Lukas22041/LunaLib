package lunalib.backend.ui.components.base

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.VisualPanelAPI
import com.fs.starfarer.api.graphics.SpriteAPI
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.PositionAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.campaign.CampaignEngine
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.Sphere
import java.awt.Color

//Based on Code provided by luddygreat
class LunaUISphere(var spritePath: String, var size: Float, width: Float, height: Float, key: Any, group: String, panel: CustomPanelAPI, uiElement: TooltipMakerAPI, var isCloud: Boolean = false, var cloudColor: Color = Color(255, 255, 255)) : LunaUIBaseElement(width, height, key, group, panel, uiElement) {

    private val orb = Sphere()
    private val sprite: SpriteAPI? = Global.getSettings().getSprite(spritePath)

    var rotation = 0f

    init {
        orb.setTextureFlag(true);
        orb.setDrawStyle(100012);
        orb.setNormals(100000);
        orb.setOrientation(100020);
    }

    override fun positionChanged(position: PositionAPI) {
        super.positionChanged(position)
        if (this.position != null)
        {
            if (sprite != null)
            {

            }
        }
    }

    override fun renderBelow(alphaMult: Float) {

    }

    override fun render(alphaMult: Float) {
        if (position != null && sprite != null)
        {
            GL11.glPushMatrix();
            //where you want the sphere to render
            GL11.glTranslatef(posX + width / 3, posY , 1f);

            //rotates the sphere
            //not gonna lie, I've got no idea what's going on with this matrix maths, I'm just pressing buttons until it looks ok
            GL11.glRotatef(90f, 1f, 0f, 0f);

            if (rotation < Float.MAX_VALUE)
            {
                if (isCloud)
                {
                    rotation += 0.1f
                }
                else
                {
                    rotation += 0.05f
                }
            }


            GL11.glRotatef(-rotation, -0.2f, 0f, 1f);

            //set caps
            //blend should be disabled instead if you want it to be a solid colour
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_CULL_FACE);
            //bind the texture to be used by the sphere
            sprite.bindTexture();

            //set colour to white so it gets drawn nomrally
            if (isCloud)
            {
                GL11.glColor4f(cloudColor.red / 255f, cloudColor.green / 255f, cloudColor.blue / 255f, cloudColor.alpha / 255f);
            }
            else
            {
                GL11.glColor4f(0.9f, 0.9f, 0.9f, 0.8f);
            }
            orb.draw(size, 32, 32);

            GL11.glPopMatrix();

            //reset caps for safety, enable blend if you disabled it etc
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

}