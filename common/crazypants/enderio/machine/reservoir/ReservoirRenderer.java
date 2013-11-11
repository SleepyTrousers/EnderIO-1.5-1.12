package crazypants.enderio.machine.reservoir;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.RenderUtil;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;

public class ReservoirRenderer extends TileEntitySpecialRenderer {

  private ResourceLocation texName = null;
  private Icon tex = null;
  private float switchSize = 0.25f;
  private float switchHSize = switchSize / 2f;
  private BoundingBox switchBB = new BoundingBox(0.5 - switchHSize, 0.5 - switchHSize, 0.5 - switchHSize, 0.5 + switchHSize, 0.5 + switchHSize,
      0.5 + switchHSize);

  private final BlockReservoir block;

  public ReservoirRenderer(BlockReservoir res) {
    block = res;
  }

  @Override
  public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {

    TileReservoir res = (TileReservoir) tileentity;
    if(res.haveRendered(tileentity.worldObj.getTotalWorldTime(), f)) {
      return;
    }

    float fullness = res.getFilledRatio();
    if(fullness <= 0 && !res.isAutoEject()) {
      return;
    }

    float val = RenderUtil.claculateTotalBrightnessForLocation(tileentity.worldObj, tileentity.xCoord, tileentity.yCoord, tileentity.zCoord);
    Minecraft.getMinecraft().entityRenderer.disableLightmap(0);

    GL11.glPushMatrix();
    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    GL11.glEnable(GL11.GL_CULL_FACE);
    GL11.glDisable(GL11.GL_LIGHTING);

    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

    Vector3f offset = res.getOffsetFromController();

    GL11.glTranslatef((float) x + offset.x, (float) y + offset.y, (float) z + offset.z);

    BoundingBox bb = res.getLiquidRenderBounds();

    if(res.isAutoEject()) {

      // switch
      RenderUtil.bindBlockTexture();

      Tessellator.instance.startDrawingQuads();
      Tessellator.instance.setColorRGBA_F(val, val, val, 1);
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        drawSwitch(dir, bb);
      }
      Tessellator.instance.draw();
    }

    if(fullness > 0) {
      RenderUtil.bindTexture(getLiquidSheet());

      float margin = 0.01f;

      Icon tex = getLiquidTexture();
      float maxV = tex.getMinV() + ((tex.getMaxV() - tex.getMinV()) * fullness);

      Tessellator.instance.startDrawingQuads();
      Tessellator.instance.setColorRGBA_F(val, val, val, 1);
      CubeRenderer.render(
          new BoundingBox(bb.minX + margin, bb.minY + margin, bb.minZ + margin, bb.maxX - margin,
              bb.minY + (fullness * (Math.abs(bb.maxY - bb.minY))) - margin, bb.maxZ - margin), tex.getMinU(), tex.getMaxU(), tex.getMinV(), maxV);
      Tessellator.instance.draw();
    }

    GL11.glPopAttrib();
    GL11.glPopMatrix();

    Minecraft.getMinecraft().entityRenderer.enableLightmap(0);

  }

  private Vector3d forward = new Vector3d();
  private Vector3d left = new Vector3d();
  private Vector3d up = new Vector3d();
  private Vector3d offset = new Vector3d();

  private void drawSwitch(ForgeDirection dir, BoundingBox bb) {
    Tessellator tes = Tessellator.instance;

    Vector3d cent = bb.getCenter();
    offset.set(cent);

    boolean isUp = dir.offsetY != 0;

    if(dir == ForgeDirection.UP) {
      int i = 0;
    }

    forward.set(dir.offsetX, dir.offsetY, dir.offsetZ);
    forward.scale(0.5);
    forward.x *= bb.sizeX();
    forward.y *= bb.sizeY();
    forward.z *= bb.sizeZ();

    offset.add(forward);

    if(dir.offsetY == 0) {
      offset.y += bb.sizeY() * 0.25;
    }
    if(dir.offsetX == 0) {
      offset.x -= (isUp ? dir.offsetY : dir.offsetZ) * bb.sizeX() * 0.25;
    }
    if(dir.offsetZ == 0) {
      offset.z += (isUp ? -dir.offsetY : dir.offsetX) * bb.sizeZ() * 0.25;
    }

    left.set(isUp ? -dir.offsetY : -dir.offsetZ, 0, dir.offsetX);

    if(isUp) {
      up.set(0, 0, -1);
    } else {
      up.set(0, 1, 0);
    }

    forward.scale(0.5);
    left.scale(0.125);
    up.scale(0.125);

    Icon icon = block.switchIcon;

    tes.addVertexWithUV(offset.x + left.x - up.x, offset.y + left.y - up.y,
        offset.z + left.z - up.z, icon.getMinU(), icon.getMaxV());
    tes.addVertexWithUV(offset.x - left.x - up.x, offset.y - left.y - up.y,
        offset.z - left.z - up.z, icon.getMaxU(), icon.getMaxV());
    tes.addVertexWithUV(offset.x - left.x + up.x, offset.y - left.y + up.y,
        offset.z - left.z + up.z, icon.getMaxU(), icon.getMinV());
    tes.addVertexWithUV(offset.x + left.x + up.x, offset.y + left.y + up.y,
        offset.z + left.z + up.z, icon.getMinU(), icon.getMinV());

  }

  private ResourceLocation getLiquidSheet() {
    if(texName == null) {
      texName = TextureMap.locationBlocksTexture;
    }
    return texName;
  }

  private Icon getLiquidTexture() {
    if(tex == null) {
      tex = ReservoirTank.WATER.getFluid().getStillIcon();
    }
    return tex;
  }

}
