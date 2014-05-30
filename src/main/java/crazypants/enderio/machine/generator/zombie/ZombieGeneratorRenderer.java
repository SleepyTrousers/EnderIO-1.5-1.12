package crazypants.enderio.machine.generator.zombie;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.generator.combustion.CombustionGeneratorModel;
import crazypants.enderio.machine.generator.combustion.TileCombustionGenerator;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.IconUtil;
import crazypants.render.RenderUtil;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.vecmath.Vector3d;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class ZombieGeneratorRenderer extends TileEntitySpecialRenderer implements IItemRenderer {

  private static final String TEXTURE = "enderio:models/ZombieJar.png";

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
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) l1, (float) l2);

    GL11.glPushMatrix();
    GL11.glTranslatef((float) x, (float) y, (float) z);
    renderModel(gen.facing);
    renderFluid(gen);
    GL11.glPopMatrix();
  }

  protected void renderFluid(TileZombieGenerator gen) {
    FluidTank tank = gen.fuelTank;
    if(tank.getFluidAmount() <= 0) {
      return;
    }
    IIcon icon = tank.getFluid().getFluid().getStillIcon();
    if(icon != null) {
      RenderUtil.bindBlockTexture();
      Tessellator tes = Tessellator.instance;
      tes.startDrawingQuads();

      float fullness = (float) (tank.getFluidAmount()) / (tank.getCapacity());
      BoundingBox bb = BoundingBox.UNIT_CUBE.scale(0.85, 0.96, 0.85);
      bb = bb.scale(1, 0.85 * fullness, 1);
      float ty = -(0.85f - (bb.maxY - bb.minY)) / 2;      
      
      
      Vector3d offset = ForgeDirectionOffsets.offsetScaled(ForgeDirection.values()[gen.facing], -0.075);
      bb = bb.translate((float)offset.x, ty, (float)offset.z);
      
      CubeRenderer.render(bb, icon);

      GL11.glEnable(GL11.GL_BLEND);
      GL11.glDepthMask(false);
      tes.draw();
      GL11.glDepthMask(true);
      GL11.glDisable(GL11.GL_BLEND);

    }
  }

  private void renderModel(int facing) {

    GL11.glPushMatrix();

    GL11.glTranslatef(0.5F, 0, 0.5F);
    GL11.glRotatef(180F, 1F, 0F, 0F);
    GL11.glScalef(1.2f, 0.9f, 1.2f);

    ForgeDirection dir = ForgeDirection.getOrientation(facing);
    if(dir == ForgeDirection.SOUTH) {
      facing = 0;

    } else if(dir == ForgeDirection.WEST) {
      facing = -1;
    }
    GL11.glRotatef(facing * -90F, 0F, 1F, 0F);

    RenderUtil.bindTexture(TEXTURE);
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
    renderItem(0, 0, 0);
  }

  private void renderItem(float x, float y, float z) {
    GL11.glPushMatrix();
    GL11.glTranslatef(x, y, z);
    renderModel(ForgeDirection.NORTH.ordinal());
    GL11.glPopMatrix();
  }

}
