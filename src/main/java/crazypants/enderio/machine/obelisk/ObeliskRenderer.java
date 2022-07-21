package crazypants.enderio.machine.obelisk;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.CubeRenderer;
import com.enderio.core.client.render.IconUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector3f;
import com.enderio.core.common.vecmath.Vertex;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

public class ObeliskRenderer implements ISimpleBlockRenderingHandler {

    private static final VertXForm2 xform2 = new VertXForm2();
    private static final VertXForm3 xform3 = new VertXForm3();

    private static final float WIDE_PINCH = 0.9f;
    private static final float WIDTH = 18f / 32f * WIDE_PINCH;
    private static final float HEIGHT = 0.475f;

    private static final BoundingBox bb1 =
            BoundingBox.UNIT_CUBE.scale(WIDTH, HEIGHT, 1).translate(0, -0.5f + HEIGHT / 2, 0);
    private static final BoundingBox bb2 =
            BoundingBox.UNIT_CUBE.scale(1, HEIGHT, WIDTH).translate(0, -0.5f + HEIGHT / 2, 0);

    private static final int BOTTOM = ForgeDirection.DOWN.ordinal();
    private static final int TOP = ForgeDirection.UP.ordinal();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        Tessellator.instance.startDrawingQuads();
        renderWorldBlock(null, 0, 0, 0, block, 0, renderer);
        Tessellator.instance.draw();
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    public boolean renderWorldBlock(
            IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

        IIcon[] icons;
        if (world != null) { // block
            RenderUtil.setTesselatorBrightness(world, x, y, z);
            if (renderer.hasOverrideBlockTexture()) { // "block breaking" overlay
                icons = new IIcon[6];
                for (int i = 0; i < icons.length; i++) {
                    icons[i] = renderer.overrideBlockTexture;
                }
            } else {
                icons = RenderUtil.getBlockTextures(world, x, y, z);
            }
        } else { // item
            icons = RenderUtil.getBlockTextures(block, 0);
        }

        // bottom texture goes into its own BB
        IIcon[] bottomIcons = new IIcon[6];
        for (int i = 1; i < bottomIcons.length; i++) {
            bottomIcons[i] = IconUtil.blankTexture;
        }
        bottomIcons[BOTTOM] = icons[BOTTOM];
        icons[BOTTOM] = IconUtil.blankTexture;

        Tessellator.instance.addTranslation(x, y, z);

        xform2.isX = false;
        CubeRenderer.render(bb1, icons, xform2, true);

        xform2.isX = true;
        icons[TOP] = IconUtil.blankTexture;
        CubeRenderer.render(bb2, icons, xform2, true);

        CubeRenderer.render(BoundingBox.UNIT_CUBE, bottomIcons, xform3, true);

        Tessellator.instance.addTranslation(-x, -y, -z);

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return BlockObeliskAbstract.defaultObeliskRenderId;
    }

    private static class VertXForm2 implements VertexTransform {

        boolean isX = true;

        public VertXForm2() {}

        @Override
        public void apply(Vertex vertex) {
            apply(vertex.xyz);
        }

        @Override
        public void apply(Vector3d vec) {
            double pinch = WIDE_PINCH;
            if (vec.y > 0.2) {
                pinch = 0.5;
            }
            if (isX) {
                vec.x -= 0.5;
                vec.x *= pinch;
                vec.x += 0.5;
            } else {
                vec.z -= 0.5;
                vec.z *= pinch;
                vec.z += 0.5;
            }
        }

        @Override
        public void applyToNormal(Vector3f vec) {}
    }

    private static class VertXForm3 implements VertexTransform {

        public VertXForm3() {}

        @Override
        public void apply(Vertex vertex) {
            apply(vertex.xyz);
        }

        @Override
        public void apply(Vector3d vec) {
            vec.x -= 0.5;
            vec.x *= WIDE_PINCH;
            vec.x += 0.5;
            vec.z -= 0.5;
            vec.z *= WIDE_PINCH;
            vec.z += 0.5;
        }

        @Override
        public void applyToNormal(Vector3f vec) {}
    }
}
