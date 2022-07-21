package crazypants.enderio.machine.ranged;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.CubeRenderer;
import com.enderio.core.client.render.IconUtil;
import com.enderio.core.client.render.RenderUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RangeRenerer extends RenderEntity {

    @Override
    public void doRender(Entity entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {

        RangeEntity se = ((RangeEntity) entity);

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);

        float scale = 1 - (se.lifeSpan / (float) se.totalLife);
        scale = Math.min(scale, 1);
        scale *= se.range;

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);

        GL11.glTranslatef(0.5f, 0.5f, 0.5f);
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);

        GL11.glColor4f(1, 1, 1, 0.4f);

        RenderUtil.bindBlockTexture();
        Tessellator.instance.startDrawingQuads();
        Tessellator.instance.setBrightness(15 << 20 | 15 << 4);
        CubeRenderer.render(BoundingBox.UNIT_CUBE, IconUtil.whiteTexture);
        Tessellator.instance.draw();

        RenderUtil.bindItemTexture();

        GL11.glDepthMask(true);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
