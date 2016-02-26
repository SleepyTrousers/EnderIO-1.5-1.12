package crazypants.enderio.item.skull;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class EndermanSkullRenderer extends TileEntitySpecialRenderer<TileEndermanSkull> {

  @Override
  public void renderTileEntityAt(TileEndermanSkull te, double x, double y, double z, float partialTicks, int destroyStage) {

    RenderUtil.setupLightmapCoords(te.getPos(), te.getWorld());
    GL11.glPushMatrix();
    GL11.glTranslatef((float) x, (float) y, (float) z);

    GL11.glPushMatrix();
    GL11.glTranslatef(0.5f, 0, 0.5f);
    GL11.glRotatef(te.yaw, 0, 1, 0);
    GL11.glTranslatef(-0.5f, 0, -0.5f);

    GL11.glDisable(GL11.GL_LIGHTING);
    GlStateManager.color(1, 1, 1);
    RenderUtil.bindBlockTexture();

    RenderUtil.renderBlockModel(te.getWorld(), te.getPos(), true);

    GL11.glEnable(GL11.GL_LIGHTING);

    GL11.glPopMatrix();
    GL11.glPopMatrix();

  }

}
