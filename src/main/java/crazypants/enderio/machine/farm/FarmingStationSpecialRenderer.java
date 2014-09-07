package crazypants.enderio.machine.farm;

import crazypants.enderio.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import static org.lwjgl.opengl.GL11.*;

public class FarmingStationSpecialRenderer extends TileEntitySpecialRenderer{

  private void renderFarmStationAt(TileFarmStation tile, double x, double y, double z, float partialTickTime) {
    Minecraft mc = Minecraft.getMinecraft();
    FontRenderer fnt = mc.fontRenderer;
    Tessellator tessellator = Tessellator.instance;
    String toRender = tile.notification;

    if ("".equals(toRender) || Config.disableFarmNotification) {
      return;
    }
    
    glPushMatrix();
    glTranslated(x + 0.5, y + 1.5, z + 0.5); // center of block and 1 block up
    glRotatef(180, 1, 0, 0); // text is upside-down by default (idk)
    float scale = 0.025f;
    glScalef(scale, scale, scale); // text is huge!
    glDisable(GL_LIGHTING);
    rotateToPlayer();
    
    
    glPushMatrix();
    glTranslatef(-fnt.getStringWidth(toRender) / 2, 0, 0);
    
    renderBackground(tessellator, fnt, toRender);
    
    fnt.drawString(toRender, 0, 0, 0xFFFFFF);
    glTranslatef(0.5f, 0.5f, 0.1f);
    fnt.drawString(toRender, 0, 0, 0x555555); // manual shadow
    
    glEnable(GL_LIGHTING);
    glPopMatrix();
    glPopMatrix();
  }
  
  private void rotateToPlayer()
  {
    glRotatef(RenderManager.instance.playerViewY + 180, 0.0F, 1.0F, 0.0F);
    glRotatef(-RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
  }
  
  private void renderBackground(Tessellator tessellator, FontRenderer fnt, String toRender) {
    glPushAttrib(GL_ALL_ATTRIB_BITS);
    glDisable(GL_TEXTURE_2D);
    glEnable(GL_BLEND);
    glShadeModel(GL_SMOOTH);
    glDisable(GL_ALPHA_TEST);
    glDisable(GL_CULL_FACE);
    glDepthMask(false);
    RenderHelper.disableStandardItemLighting();
    OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO); // stop random disappearing
    
    float width = (float) fnt.getStringWidth(toRender);
    float height = (float) fnt.FONT_HEIGHT;
    float padding = 2f;
    
    tessellator.startDrawingQuads();
    tessellator.setColorRGBA(70, 20, 100, 200);
    tessellator.addVertex(-padding, -padding, 0);
    tessellator.addVertex(-padding, height + padding, 0);
    tessellator.addVertex(width + padding, height + padding, 0);
    tessellator.addVertex(width + padding, -padding, 0);
    tessellator.draw();
    
    glPopAttrib();
  }
  
  @Override
  public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
    if (tile instanceof TileFarmStation) {
      renderFarmStationAt((TileFarmStation) tile, x, y, z, partialTickTime);
    }
  }
}
