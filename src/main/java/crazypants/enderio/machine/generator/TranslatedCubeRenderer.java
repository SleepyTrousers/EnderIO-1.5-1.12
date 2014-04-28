package crazypants.enderio.machine.generator;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.render.BoundingBox;
import crazypants.render.CustomCubeRenderer;
import crazypants.render.CustomRenderBlocks;
import crazypants.render.IRenderFace;
import crazypants.render.RenderUtil;
import crazypants.render.VertexTransform;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vertex;

public class TranslatedCubeRenderer {

  public static TranslatedCubeRenderer instance = new TranslatedCubeRenderer();

  private XFormRenderer xformRenderer = new XFormRenderer();

  private CustomCubeRenderer ccr = new CustomCubeRenderer();

  public void renderBoundingBox(int x, int y, int z, Block block, BoundingBox bb, VertexTransform vt) {
    renderBoundingBox(x, y, z, block, bb, vt, null);
  }

  public void renderBoundingBox(int x, int y, int z, Block block, BoundingBox bb, VertexTransform vt, IIcon overrideTexture) {
    block.setBlockBounds(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
    xformRenderer.xform = vt;
    ccr.setOverrideTexture(overrideTexture);
    ccr.renderBlock(Minecraft.getMinecraft().theWorld, block, x, y, z, xformRenderer);
    ccr.setOverrideTexture(null);
    block.setBlockBounds(0, 0, 0, 1, 1, 1);
  }

  private class XFormRenderer implements IRenderFace {

    public VertexTransform xform;

    @Override
    public void renderFace(CustomRenderBlocks rb, ForgeDirection face, Block par1Block, double x, double y, double z, IIcon texture, List<Vertex> refVertices,
        boolean translateToXyz) {
      if(xform != null) {
        Vector3d xyz = new Vector3d(x,y,z);
        for (Vertex v : refVertices) {
          v.xyz.sub(xyz);
          xform.apply(v);
        }
      }
      Tessellator.instance.addTranslation((float)x, (float)y, (float)z);
      RenderUtil.addVerticesToTesselator(refVertices);
      Tessellator.instance.addTranslation(-(float)x,- (float)y, -(float)z);
    }
  }

}
