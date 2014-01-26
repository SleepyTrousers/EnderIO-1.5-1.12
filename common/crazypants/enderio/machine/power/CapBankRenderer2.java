package crazypants.enderio.machine.power;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.power.GaugeBounds.VPos;
import crazypants.render.CustomCubeRenderer;
import crazypants.render.RenderUtil;
import crazypants.util.BlockCoord;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.vecmath.Vector2f;
import crazypants.vecmath.Vector4d;
import crazypants.vecmath.Vertex;

public class CapBankRenderer2 implements ISimpleBlockRenderingHandler {

  private static final BlockCoord DEFAULT_BC = new BlockCoord(0, 0, 0);
  private static final BlockCoord[] DEFAULT_MB = new BlockCoord[] { DEFAULT_BC };
  private static final double PIXEL_SIZE = 1 / 16d;

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    //Base block
    CustomCubeRenderer.instance.renderBlock(world, block, x, y, z, EnderIO.blockAlloySmelter.getBlockTextureFromSide(3), false);

    //Gauge
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(!(te instanceof TileCapacitorBank)) {
      return true;
    }
    List<GaugeBounds> gaugeBounds = null;
    gaugeBounds = ((TileCapacitorBank) te).getGaugeBounds();

    if(gaugeBounds == null || gaugeBounds.isEmpty()) {
      return true;
    }
    Tessellator tes = Tessellator.instance;
    tes.addTranslation(x, y, z);
    for (GaugeBounds gb : gaugeBounds) {
      renderGaugeOnFace(gb, EnderIO.blockCapacitorBank.overlayIcon);
      renderFillBarOnFace(gb, EnderIO.blockCapacitorBank.fillBarIcon, ((TileCapacitorBank) te).getEnergyStoredRatio());
    }
    tes.addTranslation(-x, -y, -z);
    return true;
  }

  @Override
  public boolean shouldRender3DInInventory() {
    return true;
  }

  @Override
  public int getRenderId() {
    return BlockCapacitorBank.renderId;
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

  private void renderGaugeOnFace(GaugeBounds gb, Icon icon) {
    Tessellator tes = Tessellator.instance;
    tes.setNormal(gb.face.offsetX, gb.face.offsetY, gb.face.offsetZ);
    Vector2f u = gb.getMinMaxU(icon);
    List<Vertex> corners = gb.bb.getCornersWithUvForFace(gb.face, u.x, u.y, icon.getMinV(), icon.getMaxV());
    for (Vertex coord : corners) {
      coord.xyz.add(ForgeDirectionOffsets.offsetScaled(gb.face, 0.001f));

      tes.setNormal(coord.nx(), coord.ny(), coord.nz());
      if(coord.uv != null) {
        tes.addVertexWithUV(coord.x(), coord.y(), coord.z(), coord.u(), coord.v());
      } else {
        tes.addVertexWithUV(coord.x(), coord.y(), coord.z(), 0, 0);
      }
    }
  }

  private void renderFillBarOnFace(GaugeBounds gb, Icon icon, float filledRatio) {

    int totalPixels;
    if(gb.vInfo.verticalHeight == 1) {
      totalPixels = VPos.SINGLE_BLOCK.numFillPixels;
    } else {
      totalPixels = VPos.BOTTOM.numFillPixels + VPos.TOP.numFillPixels + (VPos.MIDDLE.numFillPixels * (gb.vInfo.verticalHeight - 2));
    }

    int targetPixelCount = Math.max(0, Math.round(totalPixels * filledRatio));
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
    List<crazypants.vecmath.Vertex> corners = gb.bb.getCornersWithUvForFace(gb.face, u.x, u.y, icon.getMinV(), maxV);
    for (Vertex coord : corners) {
      coord.xyz.add(ForgeDirectionOffsets.offsetScaled(gb.face, 0.002f));
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

}
