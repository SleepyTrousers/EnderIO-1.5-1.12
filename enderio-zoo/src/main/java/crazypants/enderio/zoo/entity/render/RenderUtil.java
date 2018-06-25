package crazypants.enderio.zoo.entity.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderUtil {

  public static void renderEntityBoundingBox(EntityLiving entity, double x, double y, double z) {

    AxisAlignedBB bb = entity.getEntityBoundingBox();

    // System.out.println("RenderUtil.renderEntityBoundingBox: w= " + (bb.maxX - bb.minX) + " d=" + (bb.maxZ - bb.minZ));

    GL11.glDisable(GL11.GL_TEXTURE_2D);
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glDisable(GL11.GL_CULL_FACE);

    GL11.glPushMatrix();

    GL11.glTranslatef((float) x, (float) y, (float) z);
    GL11.glPushMatrix();
    GL11.glRotatef(-entity.renderYawOffset, 0, 1, 0);

    BufferBuilder tes = Tessellator.getInstance().getBuffer();

    double width = (bb.maxX - bb.minX) / 2;
    double height = bb.maxY - bb.minY;
    double depth = (bb.maxZ - bb.minZ) / 2;

    GlStateManager.color(1, 1, 1, 1);
    tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

    tes.pos(-width, 0, 0).endVertex();
    tes.pos(width, 0, 0).endVertex();
    tes.pos(width, height, 0).endVertex();
    tes.pos(-width, height, 0).endVertex();

    tes.pos(0, 0, -depth).endVertex();
    tes.pos(0, 0, depth).endVertex();
    tes.pos(0, height, depth).endVertex();
    tes.pos(0, height, -depth).endVertex();

    Tessellator.getInstance().draw();

    GL11.glPopMatrix();
    GL11.glPopMatrix();

    GL11.glEnable(GL11.GL_TEXTURE_2D);
    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_CULL_FACE);
  }

  private RenderUtil() {
  }

}
