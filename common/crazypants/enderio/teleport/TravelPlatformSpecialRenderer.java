package crazypants.enderio.teleport;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import crazypants.enderio.EnderIO;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.IconUtil;
import crazypants.render.RenderUtil;
import crazypants.util.BlockCoord;
import crazypants.vecmath.Vector3d;

public class TravelPlatformSpecialRenderer extends TileEntitySpecialRenderer {

  @Override
  public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {

    if(!TravelPlatformController.instance.showTargets()) {
      return;
    }

    Vector3d eye = RenderUtil.getEyePositionEio(Minecraft.getMinecraft().thePlayer);
    Vector3d loc = new Vector3d(tileentity.xCoord, tileentity.yCoord, tileentity.zCoord);
    if(eye.distanceSquared(loc) > TravelPlatformController.instance.getMaxTravelDistanceSq()) {
      return;
    }

    BlockCoord bc = new BlockCoord(tileentity);
    TravelPlatformController.instance.addCandidate(bc);
    RenderUtil.bindBlockTexture();

    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);

    GL11.glEnable(GL12.GL_RESCALE_NORMAL);

    GL11.glDisable(GL11.GL_DEPTH_TEST);
    GL11.glDisable(GL11.GL_LIGHTING);

    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

    GL11.glPushMatrix();
    GL11.glTranslated(x, y, z);

    Minecraft.getMinecraft().entityRenderer.disableLightmap(0);

    Tessellator.instance.startDrawingQuads();

    Tessellator.instance.setColorRGBA_F(1, 1, 1, 0.75f);
    CubeRenderer.render(BoundingBox.UNIT_CUBE, EnderIO.blockTravelPlatform.getIcon(0, 0));

    Tessellator.instance.setColorRGBA_F(1, 1, 1, 0.25f);
    CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(1.05, 1.05, 1.05), IconUtil.whiteTexture);

    if(TravelPlatformController.instance.isBlockSelected(bc)) {
      Tessellator.instance.setColorRGBA_F(1, 0.25f, 0, 0.5f);
      CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(1.2, 1.2, 1.2), IconUtil.whiteTexture);
    }

    Tessellator.instance.draw();

    Minecraft.getMinecraft().entityRenderer.enableLightmap(0);

    GL11.glPopMatrix();
    GL11.glPopAttrib();
    GL11.glPopAttrib();

  }
}
