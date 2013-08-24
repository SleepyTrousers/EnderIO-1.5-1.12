package crazypants.enderio.machine.power;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.EnderIO;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.RenderUtil;
import crazypants.util.BlockCoord;
import crazypants.vecmath.CoordUV;
import crazypants.vecmath.VecmathUtil;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;
import crazypants.vecmath.Vector4d;

public class CapacitorBankRenderer extends TileEntitySpecialRenderer implements IItemRenderer {

  private static final BlockCoord DEFAULT_BC = new BlockCoord(0, 0, 0);
  private static final BlockCoord[] DEFAULT_MB = new BlockCoord[] { DEFAULT_BC };

  enum VPos {

    SINGLE_BLOCK(0, 10, 3),
    BOTTOM(0.5f, 13, 3),
    MIDDLE(0.75f, 16, 0),
    TOP(0.25f, 13, 0);

    final float uOffset;
    final int numFillPixels;
    final int fillOffset;

    private VPos(float uOffset, int numFillPixels, int fillOffset) {
      this.uOffset = uOffset;
      this.numFillPixels = numFillPixels;
      this.fillOffset = fillOffset;
    }

  }

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {

    if (!(te instanceof TileCapacitorBank)) {
      return;
    }

    Minecraft.getMinecraft().entityRenderer.disableLightmap(0);

    GL11.glPushMatrix();
    GL11.glTranslated(x, y, z);

    float filledRatio = 0.66f;
    float brightness = RenderUtil.claculateTotalBrightnessForLocation(te.worldObj, te.xCoord, te.yCoord, te.zCoord);

    renderBlock((TileCapacitorBank) te, filledRatio, brightness);

    GL11.glPopMatrix();

  }

  private void renderBlock(TileCapacitorBank te, float filledRatio, float brightness) {
    RenderUtil.bindBlockTexture();
    Tessellator tes = Tessellator.instance;

    BlockCoord myBC;
    BlockCoord[] mb;
    if (te != null && te.isMultiblock()) {
      myBC = new BlockCoord(te);
      mb = te.multiblock;
    } else {
      myBC = DEFAULT_BC;
      mb = DEFAULT_MB;
    }

    tes.startDrawingQuads();
    tes.setColorRGBA_F(brightness, brightness, brightness, 1);
    CubeRenderer.render(BoundingBox.UNIT_CUBE, EnderIO.blockCapacitorBank.getIcon(0, 0));
    tes.draw();

    GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
    GL11.glPolygonOffset(-1.0f, -1.0f);

    tes.startDrawingQuads();
    tes.setColorRGBA_F(brightness, brightness, brightness, 1);
    if (te != null) {
      renderBorder(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
    } else {
      renderBorder(null, 0, 0, 0);
    }
    renderGauge(myBC, mb, brightness);
    tes.draw();

    GL11.glPolygonOffset(-3.0F, -3.0F);
    tes.startDrawingQuads();
    tes.setColorRGBA_F(brightness, brightness, brightness, 1);
    renderFillBar(myBC, mb, 0.25f, brightness);
    tes.draw();

    GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
  }

  private void renderGauge(BlockCoord me, BlockCoord[] mb, float brightness) {
    Icon icon = EnderIO.blockCapacitorBank.overlayIcon;
    for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
      if (face != ForgeDirection.UP && face != ForgeDirection.DOWN) {
        boolean isRight = isRightFace(me, mb, face);
        if (isRight) {
          renderGaugeOnFace(me, mb, icon, face, brightness);
        }
      }
    }
  }

  private void renderGaugeOnFace(BlockCoord me, BlockCoord[] mb, Icon icon, ForgeDirection face, float brightness) {
    float minU = icon.getMinU();
    float uWidth = icon.getMaxU() - icon.getMinU();
    float maxU = minU + (uWidth * 0.25f);
    float vWidth = icon.getMaxV() - icon.getMinV();

    float scaleX = 1;
    float scaleY = 1;
    float scaleZ = 1;

    Vector4d uPlane = RenderUtil.getUPlaneForFace(face);
    scaleX = uPlane.x != 0 ? 0.25f : scaleX;
    scaleY = uPlane.y != 0 ? 0.25f : scaleY;
    scaleZ = uPlane.z != 0 ? 0.25f : scaleZ;

    VInfo vInfo = getVPosForFace(me, mb, face);
    VPos yPos = vInfo.pos;

    BoundingBox bb = BoundingBox.UNIT_CUBE.scale(scaleX, scaleY, scaleZ);
    float uOffset = yPos.uOffset * uWidth;

    Tessellator tes = Tessellator.instance;
    tes.setNormal(face.offsetX, face.offsetY, face.offsetZ);
    List<CoordUV> corners = bb.getCornersWithUvForFace(face, minU + uOffset, maxU + uOffset, icon.getMinV(), icon.getMaxV());
    for (CoordUV coord : corners) {
      tes.addVertexWithUV(coord.x(), coord.y(), coord.z(), coord.u(), coord.v());
    }

  }

  private void renderFillBar(BlockCoord me, BlockCoord[] mb, float filledRatio, float brightness) {
    Icon icon = EnderIO.blockCapacitorBank.fillBarIcon;
    for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
      if (face != ForgeDirection.UP && face != ForgeDirection.DOWN) {
        boolean isRight = isRightFace(me, mb, face);
        if (isRight) {
          renderFillBarOnFace(me, mb, icon, face, filledRatio, brightness);
        }
      }
    }
  }

  private void renderFillBarOnFace(BlockCoord me, BlockCoord[] mb, Icon icon, ForgeDirection face, float filledRatio, float brightness) {
    // Filled bar
    if (filledRatio <= 0) {
      return;
    }
    float minU = icon.getMinU();
    float uWidth = icon.getMaxU() - icon.getMinU();
    float maxU = minU + (uWidth * 0.25f);
    float vWidth = icon.getMaxV() - icon.getMinV();

    float scaleX = 1;
    float scaleY = 1;
    float scaleZ = 1;

    Vector4d uPlane = RenderUtil.getUPlaneForFace(face);
    scaleX = uPlane.x != 0 ? 0.25f : scaleX;
    scaleY = uPlane.y != 0 ? 0.25f : scaleY;
    scaleZ = uPlane.z != 0 ? 0.25f : scaleZ;

    VInfo vInfo = getVPosForFace(me, mb, face);
    VPos yPos = vInfo.pos;

    BoundingBox bb = BoundingBox.UNIT_CUBE.scale(scaleX, scaleY, scaleZ);
    float uOffset = yPos.uOffset * uWidth;

    int totalPixels;
    if (vInfo.verticalHeight == 1) {
      totalPixels = VPos.SINGLE_BLOCK.numFillPixels;
    } else {
      totalPixels = VPos.BOTTOM.numFillPixels + VPos.TOP.numFillPixels + (VPos.MIDDLE.numFillPixels * (vInfo.verticalHeight - 2));
    }

    int targetPixelCount = Math.max(1, Math.round(totalPixels * filledRatio));

    int pixelsBellowFace;
    if (vInfo.index < 2) {
      // either none or a bottom section
      pixelsBellowFace = vInfo.index * VPos.BOTTOM.numFillPixels;
    } else { // has middle section
      pixelsBellowFace = VPos.BOTTOM.numFillPixels + (VPos.MIDDLE.numFillPixels * (vInfo.index - 1));
    }

    if (pixelsBellowFace >= targetPixelCount) {
      return;
    }

    int numPixelsLeft = targetPixelCount - pixelsBellowFace;
    int fillPixels = Math.min(numPixelsLeft, yPos.numFillPixels);

    double pixelSize = 1 / 16d;
    double startY = yPos.fillOffset * pixelSize;
    double endY = startY + (fillPixels * pixelSize);

    Vector3d max = bb.getMax();
    max.y = endY;

    bb = new BoundingBox(bb.getMin(), max);
    
    Tessellator tes = Tessellator.instance;
    tes.setNormal(face.offsetX, face.offsetY, face.offsetZ);
    float maxV = icon.getMinV() + ((float)endY * vWidth);
    List<CoordUV> corners = bb.getCornersWithUvForFace(face, minU + uOffset, maxU + uOffset, icon.getMinV(), maxV);
    for (CoordUV coord : corners) {
      tes.addVertexWithUV(coord.x(), coord.y(), coord.z(), coord.u(), coord.v());
    }
  }

  private VInfo getVPosForFace(BlockCoord me, BlockCoord[] mb, ForgeDirection face) {
    int maxY = me.y;
    int minY = me.y;
    int vHeight = 1;
    for (BlockCoord bc : mb) {
      if (bc.x == me.x && bc.z == me.z && !containsLocaction(mb, bc.getLocation(face))) {
        maxY = Math.max(maxY, bc.y);
        minY = Math.min(minY, bc.y);
      }
    }
    if (maxY == me.y && minY == me.y) {
      return new VInfo(VPos.SINGLE_BLOCK, 1, 0);
    }
    int height = maxY - minY + 1;
    if (maxY > me.y) {
      return me.y > minY ? new VInfo(VPos.MIDDLE, height, me.y - minY) : new VInfo(VPos.BOTTOM, height, 0);
    }
    return new VInfo(VPos.TOP, height, height - 1);
  }

  private boolean containsLocaction(BlockCoord[] mb, BlockCoord location) {
    for (BlockCoord bc : mb) {
      if (location.equals(bc)) {
        return true;
      }
    }
    return false;
  }

  private boolean isRightFace(BlockCoord me, BlockCoord[] mb, ForgeDirection dir) {
    Vector4d uPlane = RenderUtil.getUPlaneForFace(dir);

    int myRightVal = (int) uPlane.x * me.x + (int) uPlane.y * me.y + (int) uPlane.z * me.z;
    int max = myRightVal;
    for (BlockCoord bc : mb) {
      int val = (int) uPlane.x * bc.x + (int) uPlane.y * bc.y + (int) uPlane.z * bc.z;
      if (val > max) {
        max = val;
      }
    }
    return myRightVal == max;
  }

  private void renderBorder(IBlockAccess blockAccess, int x, int y, int z) {
    Icon texture = EnderIO.blockAlloySmelter.getBlockTextureFromSide(3);
    for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
      RenderUtil.renderConnectedTextureFace(blockAccess, x, y, z, face, texture,
          blockAccess == null, false, false);
    }
  }

  // ------------------------- Item renderer

  @Override
  public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    return true;
  }

  @Override
  public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
    return true;
  }

  @Override
  public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
    renderBlock(null, 0.66f, 1);
  }

  static class VInfo {
    VPos pos;
    int verticalHeight;
    int index;

    VInfo(VPos pos, int verticalHeight, int index) {
      this.pos = pos;
      this.verticalHeight = verticalHeight;
      this.index = index;
    }

  }

}
