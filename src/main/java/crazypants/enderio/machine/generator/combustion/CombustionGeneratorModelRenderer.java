package crazypants.enderio.machine.generator.combustion;

import com.enderio.core.client.render.RenderUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
@Deprecated
public class CombustionGeneratorModelRenderer extends TileEntitySpecialRenderer implements IItemRenderer {

    private static final String TEXTURE = "enderio:models/combustionGenerator.png";

    private CombustionGeneratorModel model = new CombustionGeneratorModel();

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float tick) {

        World world = te.getWorldObj();
        TileCombustionGenerator gen = (TileCombustionGenerator) te;
        //    GL11.glEnable(GL11.GL_LIGHTING);
        //    GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        //    GL11.glDisable(GL11.GL_CULL_FACE);

        float f = world.getBlockLightValue(te.xCoord, te.yCoord, te.zCoord);
        int l = world.getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0);
        int l1 = l % 65536;
        int l2 = l / 65536;
        Tessellator.instance.setColorOpaque_F(f, f, f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, l1, l2);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        renderModel(gen.facing);
        GL11.glPopMatrix();
    }

    private void renderModel(int facing) {

        GL11.glPushMatrix();

        GL11.glTranslatef(0.5F, 1, 0.5F);
        GL11.glRotatef(180F, 1F, 0F, 0F);
        GL11.glScalef(1, 0.667f, 1);

        ForgeDirection dir = ForgeDirection.getOrientation(facing);
        if (dir == ForgeDirection.SOUTH) {
            facing = 0;

        } else if (dir == ForgeDirection.WEST) {
            facing = -1;
        }
        GL11.glRotatef(facing * -90F, 0F, 1F, 0F);

        RenderUtil.bindTexture(TEXTURE);
        model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        GL11.glTranslatef(-0.5F, -1, -0.5F);
        GL11.glPopMatrix();
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
        //    if(true) {
        //      switch (type) {
        //      case ENTITY:
        //        renderItem(0f, 0f, 0f);
        //        return;
        //      case EQUIPPED:
        //      case EQUIPPED_FIRST_PERSON:
        //        renderItem(0f, 1f, 1f);
        //        return;
        //      case INVENTORY:
        //        renderItem(0f, 0f, 0f);
        //        return;
        //      default:
        //        renderItem(0f, 0f, 0f);
        //        return;
        //      }
        //    } else {
        renderItem(0, 0, 0);
        //    }
    }

    private void renderItem(float x, float y, float z) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        renderModel(ForgeDirection.NORTH.ordinal());
        GL11.glPopMatrix();
    }
}
