package crazypants.enderio.machine.generator.zombie;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.CubeRenderer;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.ForgeDirectionOffsets;
import com.enderio.core.common.vecmath.Vector3d;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.util.RenderPassHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ZombieGeneratorRenderer extends TileEntitySpecialRenderer implements IItemRenderer {

    private ModelZombieJar model = new ModelZombieJar();

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float tick) {

        World world = te.getWorldObj();
        TileZombieGenerator gen = (TileZombieGenerator) te;

        float f = world.getBlockLightValue(te.xCoord, te.yCoord, te.zCoord);
        int l = world.getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0);
        int l1 = l % 65536;
        int l2 = l / 65536;
        Tessellator.instance.setColorOpaque_F(f, f, f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, l1, l2);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        if (RenderPassHelper.getEntityRenderPass() == 0) {
            renderModel(gen.getGeneratorType(), gen.facing);
        } else if (RenderPassHelper.getEntityRenderPass() == 1) {
            renderFluid(gen);
        }
        GL11.glPopMatrix();
    }

    protected void renderFluid(TileZombieGenerator gen) {
        FluidTank tank = gen.fuelTank;
        if (tank.getFluidAmount() <= 0) {
            return;
        }
        IIcon icon = tank.getFluid().getFluid().getStillIcon();
        if (icon != null) {
            RenderUtil.bindBlockTexture();
            Tessellator tes = Tessellator.instance;
            tes.startDrawingQuads();

            ForgeDirection facingDir = ForgeDirection.values()[gen.facing];
            double facingOffset = 0.075;

            BoundingBox bb = BoundingBox.UNIT_CUBE.scale(0.85, 0.96, 0.85);
            float fullness = (float) (tank.getFluidAmount()) / (tank.getCapacity());
            Vector3d absFac = ForgeDirectionOffsets.absolueOffset(facingDir);

            double scaleX = absFac.x == 0 ? 0.95 : 1 - facingOffset / 2;
            double scaleY = 0.85 * fullness;
            double scaleZ = absFac.z == 0 ? 0.95 : 1 - facingOffset / 2;

            bb = bb.scale(scaleX, 0.85 * fullness, scaleZ);

            float ty = -(0.85f - (bb.maxY - bb.minY)) / 2;
            Vector3d transOffset = ForgeDirectionOffsets.offsetScaled(facingDir, -facingOffset);
            bb = bb.translate((float) transOffset.x, ty, (float) transOffset.z);

            int brightness;
            if (gen.getWorldObj() == null) {
                brightness = 15 << 20 | 15 << 4;
            } else {
                brightness = gen.getWorldObj().getLightBrightnessForSkyBlocks(gen.xCoord, gen.yCoord, gen.zCoord, 0);
            }
            tes.setBrightness(brightness);

            CubeRenderer.render(bb, icon);

            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDepthMask(false);
            tes.draw();
            GL11.glDepthMask(true);
            GL11.glPopAttrib();
        }
    }

    private void renderModel(GeneratorType gen, int facing) {

        GL11.glPushMatrix();

        GL11.glTranslatef(0.5F, 0, 0.5F);
        GL11.glRotatef(180F, 1F, 0F, 0F);
        GL11.glScalef(1.2f, 0.9f, 1.2f);

        ForgeDirection dir = ForgeDirection.getOrientation(facing);
        if (dir == ForgeDirection.SOUTH) {
            facing = 0;

        } else if (dir == ForgeDirection.WEST) {
            facing = -1;
        }
        GL11.glRotatef(facing * -90F, 0F, 1F, 0F);

        RenderUtil.bindTexture(gen.getTexture());
        model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        GL11.glTranslatef(-0.5F, 0, -0.5F);
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
        renderItem(((BlockZombieGenerator) Block.getBlockFromItem(item.getItem())).getGeneratorType(), 0, 0, 0);
    }

    private void renderItem(GeneratorType gen, float x, float y, float z) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        renderModel(gen, ForgeDirection.SOUTH.ordinal());
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
