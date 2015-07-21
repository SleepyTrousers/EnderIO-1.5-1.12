package crazypants.enderio.machine.killera;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTank;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.CubeRenderer;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.ForgeDirectionOffsets;
import com.enderio.core.common.vecmath.Vector3d;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.machine.generator.zombie.ModelZombieJar;

@SideOnly(Side.CLIENT)
public class KillerJoeRenderer extends TileEntitySpecialRenderer implements IItemRenderer {

  private static final String TEXTURE = "enderio:models/KillerJoe.png";

  private static final ItemStack DEFAULT_SWORD = new ItemStack(Items.iron_sword);

  private ModelZombieJar model = new ModelZombieJar();

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float tick) {

    World world = te.getWorldObj();
    TileKillerJoe gen = (TileKillerJoe) te;
    float swingProg = gen.getSwingProgress(tick);

    float f = world.getBlockLightValue(te.xCoord, te.yCoord, te.zCoord);
    int l = world.getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord, 0);
    int l1 = l % 65536;
    int l2 = l / 65536;
    Tessellator.instance.setColorOpaque_F(f, f, f);
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, l1, l2);

    GL11.glPushMatrix();
    GL11.glTranslatef((float) x, (float) y, (float) z);
    renderModel(gen.facing);
    renderSword(gen.facing, gen.getStackInSlot(0), swingProg);
    renderFluid(gen);
    GL11.glPopMatrix();

  }

  private void renderSword(int facing, ItemStack sword, float swingProgress) {

    if(sword == null || sword.getIconIndex() == null) {
      return;
    }

    ForgeDirection dir = ForgeDirection.getOrientation(facing);
    if(dir == ForgeDirection.SOUTH) {
      facing = 0;
    } else if(dir == ForgeDirection.WEST) {
      facing = -1;
    }

    //Sword
    GL11.glPushMatrix();

    GL11.glTranslatef(0.5f, 0, 0.5f);
    float offset = 90f;
    if(dir.offsetX != 0) {
      offset *= -1;
    }

    GL11.glRotatef((facing * -90F) + offset, 0F, 1F, 0F);
    GL11.glTranslatef(-0.5f, 0, -0.5F);

    GL11.glPushMatrix();
    if(swingProgress > 0) {
      float f6 = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
      float f7 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
      GL11.glRotatef(f6 * 5.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(f7 * 50.0F, 0.0F, 0.0F, 1.0F);
    }
    GL11.glTranslatef(-0.255f, 0.2f, 0.05f);

    RenderUtil.bindItemTexture();

    IIcon icon = sword.getIconIndex();
    float f9 = 0.0625F;
    float minU = icon.getMinU();
    float maxU = icon.getMaxU();
    float minV = icon.getMinV();
    float maxV = icon.getMaxV();
    ItemRenderer.renderItemIn2D(Tessellator.instance, maxU, minV, minU, maxV, icon.getIconWidth(), icon.getIconHeight(), f9);

    GL11.glPopMatrix();
    GL11.glPopMatrix();

  }

  protected void renderFluid(TileKillerJoe gen) {
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
      bb = bb.translate((float) offset.x, ty, (float) offset.z);

      int brightness;
      if(gen.getWorldObj() == null) {
        brightness = 15 << 20 | 15 << 4;
      } else {
        brightness = gen.getWorldObj().getLightBrightnessForSkyBlocks(gen.xCoord, gen.yCoord, gen.zCoord, 0);
      }
      tes.setBrightness(brightness);
      tes.setColorOpaque_F(1, 1, 1);

      CubeRenderer.render(bb, icon);

      GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      GL11.glDisable(GL11.GL_LIGHTING);
      GL11.glDepthMask(false);
      GL11.glColor3f(1, 1, 1);

      tes.draw();
      GL11.glDepthMask(true);
      GL11.glPopAttrib();
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
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    renderModel(ForgeDirection.EAST.ordinal());
    renderSword(ForgeDirection.EAST.ordinal(), DEFAULT_SWORD, 0);
    GL11.glDisable(GL11.GL_BLEND);
    GL11.glPopMatrix();
  }
}
