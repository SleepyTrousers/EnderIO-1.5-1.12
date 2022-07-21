package crazypants.enderio.machine.hypercube;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.CubeRenderer;
import com.enderio.core.client.render.RenderUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class HyperCubeRenderer extends TileEntitySpecialRenderer implements IItemRenderer {

    private IModel model;

    private BoundingBox bb;

    private boolean adjustForItem = false;

    public HyperCubeRenderer() {
        float scale = 0.7f;
        if (Config.useAlternateTesseractModel) {
            model = new HyperCubeModel2();
            scale = 0.8f;
            adjustForItem = true;
        } else {
            model = new HyperCubeModel();
        }
        bb = BoundingBox.UNIT_CUBE.scale(scale, scale, scale);
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {

        TileHyperCube cube = (TileHyperCube) te;

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        model.render(cube, x, y, z);

        if (cube.getChannel() != null) {
            // if(cube.getEnergyStored() > 0) {
            renderPower(te.getWorldObj(), x, y, z, cube.getChannel() != null);
            // }
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

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
        if (adjustForItem) {
            switch (type) {
                case ENTITY:
                    renderItem(0f, 0f, 0f);
                    return;
                case EQUIPPED:
                case EQUIPPED_FIRST_PERSON:
                    renderItem(0f, 1f, 1f);
                    return;
                case INVENTORY:
                    renderItem(0f, 0f, 0f);
                    return;
                default:
                    renderItem(0f, 0f, 0f);
                    return;
            }
        } else {
            renderItem(0, 0, 0);
        }
    }

    private void renderPower(World world, double x, double y, double z, boolean isActive) {

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);

        RenderUtil.bindBlockTexture();
        IIcon icon = EnderIO.blockHyperCube.getPortalIcon();

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        if (!isActive) {
            GL11.glColor4f(0, 1, 1, 0.5f);
        } else {
            GL11.glColor4f(1, 1, 1, 1f);
        }
        CubeRenderer.render(bb, icon);
        tessellator.draw();

        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void renderItem(float x, float y, float z) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        model.render();
        GL11.glPopMatrix();
    }
}
