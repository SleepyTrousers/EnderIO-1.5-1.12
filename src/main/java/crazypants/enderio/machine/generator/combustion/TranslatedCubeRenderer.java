package crazypants.enderio.machine.generator.combustion;

import com.enderio.core.api.client.render.IRenderFace;
import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.CustomCubeRenderer;
import com.enderio.core.client.render.CustomRenderBlocks;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector4f;
import com.enderio.core.common.vecmath.Vertex;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public class TranslatedCubeRenderer {

    public static TranslatedCubeRenderer instance = new TranslatedCubeRenderer();

    private XFormRenderer xformRenderer = new XFormRenderer();

    private CustomCubeRenderer ccr = new CustomCubeRenderer();

    public void renderBoundingBox(
            int x, int y, int z, Block block, BoundingBox bb, VertexTransform vt, boolean enableLighting) {
        renderBoundingBox(x, y, z, block, bb, vt, null, enableLighting);
    }

    public void renderBoundingBox(int x, int y, int z, Block block, BoundingBox bb, VertexTransform vt) {
        renderBoundingBox(x, y, z, block, bb, vt, null);
    }

    public void renderBoundingBox(
            int x, int y, int z, Block block, BoundingBox bb, VertexTransform vt, IIcon overrideTexture) {
        renderBoundingBox(x, y, z, block, bb, vt, overrideTexture, true);
    }

    public void renderBoundingBox(
            int x,
            int y,
            int z,
            Block block,
            BoundingBox bb,
            VertexTransform vt,
            IIcon overrideTexture,
            boolean doLighting) {
        block.setBlockBounds(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
        xformRenderer.xform = vt;
        xformRenderer.enableLighting = doLighting;
        ccr.setOverrideTexture(overrideTexture);
        ccr.renderBlock(Minecraft.getMinecraft().theWorld, block, x, y, z, xformRenderer);
        ccr.setOverrideTexture(null);
        block.setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    private class XFormRenderer implements IRenderFace {

        VertexTransform xform;
        boolean enableLighting = true;

        @Override
        public void renderFace(
                CustomRenderBlocks rb,
                ForgeDirection face,
                Block par1Block,
                double x,
                double y,
                double z,
                IIcon texture,
                List<Vertex> refVertices,
                boolean translateToXyz) {
            if (xform != null) {
                Vector3d xyz = new Vector3d(x, y, z);
                for (Vertex v : refVertices) {
                    v.xyz.sub(xyz);
                    xform.apply(v);
                    if (!enableLighting) {
                        v.brightness = 15 << 20 | 15 << 4;
                        float col = RenderUtil.getColorMultiplierForFace(face);
                        v.color = new Vector4f(col, col, col, 1);
                        v.normal = null;
                    }
                }
            }
            Tessellator.instance.addTranslation((float) x, (float) y, (float) z);
            RenderUtil.addVerticesToTesselator(refVertices);
            Tessellator.instance.addTranslation(-(float) x, -(float) y, -(float) z);
        }
    }

    public CustomCubeRenderer getRenderer() {
        return ccr;
    }
}
