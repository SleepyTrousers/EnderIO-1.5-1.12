package crazypants.enderio.machine.ranged;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.IconUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector4f;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RangeRenerer extends RenderEntity {

  public static final Factory FACTORY = new Factory();

  public RangeRenerer(RenderManager renderManagerIn) {
    super(renderManagerIn);
  }

  @Override
  public void doRender(Entity entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {

    RangeEntity se = ((RangeEntity) entity);
    Vector4f color = se.getColor();

    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glDisable(GL11.GL_CULL_FACE);
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GL11.glDepthMask(false);

    float scale = 1 - (se.lifeSpan / (float) se.totalLife);
    scale = Math.min(scale, 1);
    // scale *= se.getRange();

    GL11.glPushMatrix();
    GL11.glTranslatef((float) x, (float) y, (float) z);

    GL11.glColor4f(color.x, color.y, color.z, color.w);

    RenderUtil.bindBlockTexture();

    GL11.glTranslatef(0.5f, 0.5f, 0.5f);
    GL11.glScalef(scale, scale, scale);
    GL11.glTranslatef(-0.5f, -0.5f, -0.5f);

    // Tessellator.instance.setBrightness(15 << 20 | 15 << 4);
    // RenderUtil.renderBoundingBox(BoundingBox.UNIT_CUBE, IconUtil.instance.whiteTexture);
    RenderUtil.renderBoundingBox(se.getRangeBox(), IconUtil.instance.whiteTexture);

    GL11.glDepthMask(true);
    GL11.glPopAttrib();
    GL11.glPopMatrix();

  }

  public static class Factory implements IRenderFactory<RangeEntity> {

    @Override
    public Render<? super RangeEntity> createRenderFor(RenderManager manager) {
      return new RangeRenerer(manager);
    }
  }

}
