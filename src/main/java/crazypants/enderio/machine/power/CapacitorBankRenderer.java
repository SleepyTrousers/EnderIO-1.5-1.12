package crazypants.enderio.machine.power;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.CubeRenderer;
import com.enderio.core.client.render.IconUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.vecmath.Vector2f;
import com.enderio.core.common.vecmath.Vector4d;
import com.enderio.core.common.vecmath.Vertex;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.power.PowerHandlerUtil;

@SideOnly(Side.CLIENT)
public class CapacitorBankRenderer extends TileEntitySpecialRenderer implements IItemRenderer {

  private static final BlockCoord DEFAULT_BC = new BlockCoord(0, 0, 0);
  private static final BlockCoord[] DEFAULT_MB = new BlockCoord[] { DEFAULT_BC };
  private static final double PIXEL_SIZE = 1 / 16d;

  //------------------------- Item renderer

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
    renderBlock(null, PowerHandlerUtil.getStoredEnergyForItem(item) / TileCapacitorBank.BASE_CAP.getMaxEnergyStored(), item.getItemDamage());
  }

  //------------------------- Entity renderer

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
    if(!(te instanceof TileCapacitorBank)) {
      return;
    }

    Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
    GL11.glPushMatrix();
    GL11.glTranslated(x, y, z);

    TileCapacitorBank cb = (TileCapacitorBank) te;
    renderBlock(cb, cb.getEnergyStoredRatio(), 0);

    GL11.glPopMatrix();
    Minecraft.getMinecraft().entityRenderer.enableLightmap(0);

  }

  private void renderBlock(TileCapacitorBank te, double filledRatio, int meta) {
    RenderUtil.bindBlockTexture();
    Tessellator tes = Tessellator.instance;

    BlockCoord myBC;
    BlockCoord[] mb;
    if(te != null && te.isMultiblock()) {
      myBC = new BlockCoord(te);
      mb = te.multiblock;
    } else {
      myBC = DEFAULT_BC;
      mb = DEFAULT_MB;
    }

    List<GaugeBounds> gaugeBounds = calculateGaugeBounds(myBC, mb);

    float[] brightness;
    float maxBrightness = 0;
    if(te != null) {
      brightness = new float[6];
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        brightness[dir.ordinal()] = RenderUtil.claculateTotalBrightnessForLocation(te.getWorldObj(), te.xCoord + dir.offsetX, te.yCoord + dir.offsetY, te.zCoord
            + dir.offsetZ);
        maxBrightness = Math.max(brightness[dir.ordinal()], maxBrightness);
      }
    } else {
      brightness = new float[] { 1, 1, 1, 1, 1, 1 };
      maxBrightness = 1;
    }

    tes.startDrawingQuads();
    CubeRenderer.render(BoundingBox.UNIT_CUBE, EnderIO.blockCapacitorBank.getIcon(0, 0), null, brightness, true);
    tes.draw();

    GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
    GL11.glPolygonOffset(-1.0f, -1.0f);

    tes.startDrawingQuads();
    tes.setColorRGBA_F(maxBrightness, maxBrightness, maxBrightness, 1);
    if(te != null) {
      renderBorder(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord, meta);
    } else {
      renderBorder(null, 0, 0, 0, meta);
    }
    for (GaugeBounds gb : gaugeBounds) {
      renderGaugeOnFace(gb, EnderIO.blockCapacitorBank.overlayIcon);
    }
    tes.draw();

    GL11.glPolygonOffset(-3.0F, -3.0F);
    tes.startDrawingQuads();
    tes.setColorRGBA_F(maxBrightness, maxBrightness, maxBrightness, 1);

    for (GaugeBounds gb : gaugeBounds) {
      renderFillBarOnFace(gb, EnderIO.blockCapacitorBank.fillBarIcon, filledRatio);
    }
    tes.draw();

    GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
  }

  private void renderBorder(IBlockAccess blockAccess, int x, int y, int z, int meta) {
    IIcon texture;
    if(meta == 0) {
      texture = EnderIO.blockAlloySmelter.getBlockTextureFromSide(3);
    } else {
      texture = IconUtil.whiteTexture;
    }
    for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
      RenderUtil.renderConnectedTextureFace(blockAccess, EnderIO.blockCapacitorBank, x, y, z, face, texture,
          blockAccess == null, false, false);
    }
  }

  private List<GaugeBounds> calculateGaugeBounds(BlockCoord me, BlockCoord[] mb) {
    List<GaugeBounds> res = new ArrayList<GaugeBounds>();
    for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
      if(face != ForgeDirection.UP && face != ForgeDirection.DOWN) {
        boolean isRight = isRightFace(me, mb, face);
        if(isRight) {
          res.add(new GaugeBounds(me, mb, face));
        }
      }
    }
    return res;
  }

  private void renderGaugeOnFace(GaugeBounds gb, IIcon icon) {
    Tessellator tes = Tessellator.instance;
    tes.setNormal(gb.face.offsetX, gb.face.offsetY, gb.face.offsetZ);
    Vector2f u = gb.getMinMaxU(icon);
    List<Vertex> corners = gb.bb.getCornersWithUvForFace(gb.face, u.x, u.y, icon.getMinV(), icon.getMaxV());
    for (Vertex coord : corners) {
      tes.setNormal(coord.nx(), coord.ny(), coord.nz());
      if(coord.uv != null) {
        tes.addVertexWithUV(coord.x(), coord.y(), coord.z(), coord.u(), coord.v());
      } else {
        tes.addVertexWithUV(coord.x(), coord.y(), coord.z(), 0, 0);
      }
    }
  }

  private void renderFillBarOnFace(GaugeBounds gb, IIcon icon, double filledRatio) {

    int totalPixels;
    if(gb.vInfo.verticalHeight == 1) {
      totalPixels = VPos.SINGLE_BLOCK.numFillPixels;
    } else {
      totalPixels = VPos.BOTTOM.numFillPixels + VPos.TOP.numFillPixels + (VPos.MIDDLE.numFillPixels * (gb.vInfo.verticalHeight - 2));
    }

    int targetPixelCount = (int)Math.max(0, Math.round(totalPixels * filledRatio));
    int pixelsBellowFace;
    if(gb.vInfo.index < 2) {
      // either none or a bottom section
      pixelsBellowFace = gb.vInfo.index * VPos.BOTTOM.numFillPixels;
    } else { // has middle section
      pixelsBellowFace = VPos.BOTTOM.numFillPixels + (VPos.MIDDLE.numFillPixels * (gb.vInfo.index - 1));
    }

    if(pixelsBellowFace >= targetPixelCount) {
      return;
    }

    VPos yPos = gb.vInfo.pos;
    int numPixelsLeft = targetPixelCount - pixelsBellowFace;
    int fillPixels = Math.min(numPixelsLeft, yPos.numFillPixels);

    double maxY = (yPos.fillOffset * PIXEL_SIZE) + (fillPixels * PIXEL_SIZE);
    float vWidth = icon.getMaxV() - icon.getMinV();
    float maxV = icon.getMinV() + ((float) maxY * vWidth);

    Tessellator tes = Tessellator.instance;
    tes.setNormal(gb.face.offsetX, gb.face.offsetY, gb.face.offsetZ);
    Vector2f u = gb.getMinMaxU(icon);
    List<com.enderio.core.common.vecmath.Vertex> corners = gb.bb.getCornersWithUvForFace(gb.face, u.x, u.y, icon.getMinV(), maxV);
    for (Vertex coord : corners) {
      if(coord.uv != null) {
        tes.addVertexWithUV(coord.x(), Math.min(coord.y(), maxY), coord.z(), coord.u(), coord.v());
      } else {
        tes.addVertexWithUV(coord.x(), Math.min(coord.y(), maxY), coord.z(), 0, 0);
      }
    }
  }

  private boolean isRightFace(BlockCoord me, BlockCoord[] mb, ForgeDirection dir) {
    Vector4d uPlane = RenderUtil.getUPlaneForFace(dir);

    int myRightVal = (int) uPlane.x * me.x + (int) uPlane.y * me.y + (int) uPlane.z * me.z;
    int max = myRightVal;
    for (BlockCoord bc : mb) {
      int val = (int) uPlane.x * bc.x + (int) uPlane.y * bc.y + (int) uPlane.z * bc.z;
      if(val > max) {
        max = val;
      }
    }
    return myRightVal == max;
  }

  //------------ Inner Classes

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

  static class GaugeBounds {

    final BoundingBox bb;
    final VInfo vInfo;
    final ForgeDirection face;

    GaugeBounds(BlockCoord me, BlockCoord[] mb, ForgeDirection face) {
      this.face = face;
      vInfo = getVPosForFace(me, mb, face);

      Vector4d uPlane = RenderUtil.getUPlaneForFace(face);
      float scaleX = uPlane.x != 0 ? 0.25f : 1;
      float scaleY = uPlane.y != 0 ? 0.25f : 1;
      float scaleZ = uPlane.z != 0 ? 0.25f : 1;
      bb = BoundingBox.UNIT_CUBE.scale(scaleX, scaleY, scaleZ);
    }

    Vector2f getMinMaxU(IIcon icon) {
      VPos yPos = vInfo.pos;
      float uWidth = icon.getMaxU() - icon.getMinU();
      float uOffset = yPos.uOffset * uWidth;
      float minU = icon.getMinU() + uOffset;
      float maxU = minU + (uWidth * 0.25f);
      return new Vector2f(minU, maxU);
    }

    private VInfo getVPosForFace(BlockCoord me, BlockCoord[] mb, ForgeDirection face) {
      int maxY = me.y;
      int minY = me.y;
      int vHeight = 1;
      for (BlockCoord bc : mb) {
        if(bc.x == me.x && bc.z == me.z && !containsLocaction(mb, bc.getLocation(face))) {
          maxY = Math.max(maxY, bc.y);
          minY = Math.min(minY, bc.y);
        }
      }
      if(maxY == me.y && minY == me.y) {
        return new VInfo(VPos.SINGLE_BLOCK, 1, 0);
      }
      int height = maxY - minY + 1;
      if(maxY > me.y) {
        return me.y > minY ? new VInfo(VPos.MIDDLE, height, me.y - minY) : new VInfo(VPos.BOTTOM, height, 0);
      }
      return new VInfo(VPos.TOP, height, height - 1);
    }

    private boolean containsLocaction(BlockCoord[] mb, BlockCoord location) {
      for (BlockCoord bc : mb) {
        if(location.equals(bc)) {
          return true;
        }
      }
      return false;
    }

  }

}
