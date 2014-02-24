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
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.power.GaugeBounds.VPos;
import crazypants.render.ConnectedTextureRenderer;
import crazypants.render.CustomCubeRenderer;
import crazypants.render.CustomRenderBlocks;
import crazypants.render.IRenderFace;
import crazypants.render.RenderUtil;
import crazypants.util.BlockCoord;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.vecmath.Vector2f;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector4d;
import crazypants.vecmath.Vector4f;
import crazypants.vecmath.Vertex;

public class CapBankRenderer2 implements ISimpleBlockRenderingHandler {

  private static final BlockCoord DEFAULT_BC = new BlockCoord(0, 0, 0);
  private static final BlockCoord[] DEFAULT_MB = new BlockCoord[] { DEFAULT_BC };
  private static final double PIXEL_SIZE = 1 / 16d;

  private final List<IRenderFace> renderers = new ArrayList<IRenderFace>(2);

  private ConnectedTextureRenderer connectedTexRenderer;

  public CapBankRenderer2() {
    connectedTexRenderer = new ConnectedTextureRenderer();

    GaugueRenderer gaugeRenderer = new GaugueRenderer();
    renderers.add(connectedTexRenderer);
    if(Config.renderCapBankGauge) {
      renderers.add(gaugeRenderer);
    }
  }

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(te instanceof TileCapacitorBank) {
      TileCapacitorBank cb = ((TileCapacitorBank) te);
      cb.energyAtLastRender = cb.getEnergyStored();
    }
    connectedTexRenderer.setEdgeTexture(EnderIO.blockAlloySmelter.getBlockTextureFromSide(3)); //can't do in constructor as texture not loaded yet          
    CustomCubeRenderer.instance.renderBlock(world, block, x, y, z, renderers);
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

  private class GaugueRenderer implements IRenderFace {

    @Override
    public void renderFace(CustomRenderBlocks rb, ForgeDirection face, Block par1Block, double x, double y, double z, Icon texture, List<Vertex> refVertices,
        boolean translateToXyz) {
      //Gauge
      TileEntity te = rb.blockAccess.getBlockTileEntity((int) x, (int) y, (int) z);
      if(!(te instanceof TileCapacitorBank)) {
        return;
      }
      List<GaugeBounds> gaugeBounds = null;
      gaugeBounds = ((TileCapacitorBank) te).getGaugeBounds();

      if(gaugeBounds == null || gaugeBounds.isEmpty()) {
        return;
      }

      for (GaugeBounds gb : gaugeBounds) {
        if(gb.face == face) {
          Tessellator tes = Tessellator.instance;
          tes.addTranslation((float) x, (float) y, (float) z);
          int b = 0;
          Vector4f col = new Vector4f();
          int colCount = 0;
          for (Vertex v : refVertices) {
            b += v.brightness;
            if(v.color != null) {
              colCount++;
              col.add(v.color);
            }
          }
          if(b > 0) {
            b /= 4;
            tes.setBrightness(b);
          } else {
            tes.setBrightness(15 << 20 | 0 << 4);
          }
          if(colCount > 0) {
            col.scale(0.25);
            tes.setColorRGBA_F(col.x, col.y, col.z, col.w);
          } else {
            tes.setColorOpaque_F(1, 1, 1);
          }

          if(Config.renderCapBankGaugeBackground) {
            renderGaugeOnFace(gb, EnderIO.blockCapacitorBank.overlayIcon, refVertices, x, y, z);
          }
          if(Config.renderCapBankGaugeLevel) {
            renderFillBarOnFace(gb, EnderIO.blockCapacitorBank.fillBarIcon, ((TileCapacitorBank) te).getEnergyStoredRatio(), refVertices, x, y, z);
          }
          tes.addTranslation((float) -x, (float) -y, (float) -z);
          return;
        }
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

    private void renderGaugeOnFace(GaugeBounds gb, Icon icon, List<Vertex> vertices, double x, double y, double z) {
      Tessellator tes = Tessellator.instance;
      Vector2f u = gb.getMinMaxU(icon);
      List<Vertex> corners = gb.bb.getCornersWithUvForFace(gb.face, u.x, u.y, icon.getMinV(), icon.getMaxV());
      for (Vertex coord : corners) {
        coord.xyz.add(ForgeDirectionOffsets.offsetScaled(gb.face, 0.001f));
        Vector3d xyz = new Vector3d(coord.xyz);
        xyz.x += x;
        xyz.y += y;
        xyz.z += z;
        Vertex v = getClosestVertex(vertices, xyz);
        if(v != null) {
          if(v.color != null) {
            tes.setColorRGBA_F(v.color.x, v.color.y, v.color.z, v.color.w);
          }
          if(v.brightness > 0) {
            tes.setBrightness(v.brightness);
          }
        }
        if(coord.uv != null) {
          tes.addVertexWithUV(coord.x(), coord.y(), coord.z(), coord.u(), coord.v());
        } else {
          tes.addVertexWithUV(coord.x(), coord.y(), coord.z(), 0, 0);
        }
      }
    }

    private void renderFillBarOnFace(GaugeBounds gb, Icon icon, float filledRatio, List<Vertex> vertices, double x, double y, double z) {

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
      Vector2f u = gb.getMinMaxU(icon);
      List<crazypants.vecmath.Vertex> corners = gb.bb.getCornersWithUvForFace(gb.face, u.x, u.y, icon.getMinV(), maxV);
      for (Vertex coord : corners) {
        coord.xyz.add(ForgeDirectionOffsets.offsetScaled(gb.face, 0.002f));

        Vector3d xyz = new Vector3d(coord.xyz);
        xyz.x += x;
        xyz.y += y;
        xyz.z += z;
        Vertex v = getClosestVertex(vertices, xyz);
        if(v != null) {
          if(v.color != null) {
            tes.setColorRGBA_F(v.color.x, v.color.y, v.color.z, v.color.w);
          }
          if(v.brightness > 0) {
            tes.setBrightness(v.brightness);
          }
        }

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

    private Vertex getClosestVertex(List<Vertex> vertices, Vector3d corner) {
      Vertex result = null;
      double d2 = Double.MAX_VALUE;
      for (Vertex v : vertices) {
        double tmp = corner.distanceSquared(v.xyz);
        if(tmp <= d2) {
          result = v;
          d2 = tmp;
        }
      }
      return result;
    }

  }

  //------------ Inner Classes

}
