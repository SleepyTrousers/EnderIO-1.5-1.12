package crazypants.enderio.machine.cobbleworks;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.framework.IFrameworkMachine;
import crazypants.enderio.machine.framework.IFrameworkMachine.TankSlot;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.RenderUtil;

@SideOnly(Side.CLIENT)
public class TESRCobbleworks extends TileEntitySpecialRenderer {

  private static final float EPSILON = 0.01f;

  private static final int[][] direction = {
      // TankSlot, x/y/z => direction from block center
      { +1, -1, +1 }, // FRONT_LEFT
      { +1, -1, -1 }, // BACK_LEFT
      { -1, -1, -1 }, // BACK_RIGHT
      { -1, -1, +1 }, // FRONT_RIGHT
  };

  static final int[][] rotation = {
      // ForgeDirection facing, TankSlot logical => TankSlot physical
      { 0, 0, 0, 0 }, // DOWN
      { 0, 0, 0, 0 }, // UP
      { 1, 2, 3, 0 }, // SOUTH
      { 3, 0, 1, 2 }, // NORTH
      { 2, 3, 0, 1 }, // WEST
      { 0, 1, 2, 3 }, // EAST
  };

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
    if (te instanceof AbstractMachineEntity && te instanceof IFrameworkMachine) {
      renderTankPass(te, x, y, z, false);
      renderTankPass(te, x, y, z, true);
    }
  }

  private void renderTankPass(TileEntity te, double x, double y, double z, boolean waterPass) {
    int facing = ((AbstractMachineEntity) te).facing;
    for (TankSlot tankSlot : TankSlot.values()) {
      if (((IFrameworkMachine) te).hasTank(tankSlot)) {
        Fluid fluid = ((IFrameworkMachine) te).getTankFluid(tankSlot);
        if (fluid != null && (fluid == FluidRegistry.WATER) == waterPass) {
          int[] r = direction[rotation[facing][tankSlot.ordinal()]];
          renderTankFluid((float) x, (float) y, (float) z, r[0], r[1], r[2], 6.00f, 6.00f, 6.00f, fluid.getStillIcon());
        }
      }
    }
  }

  public static void renderTankFluid(float x, float y, float z, float xOffset, float yOffset, float zOffset, float xSize,
      float ySize, float zSize, IIcon icon) {
    float xScale = xSize - 2 * EPSILON;
    float yScale = ySize - 2 * EPSILON;
    float zScale = zSize - 2 * EPSILON;
    BoundingBox bb = BoundingBox.UNIT_CUBE.scale(xScale / 16f, yScale / 16f, zScale / 16f);
    float xPos = (xSize / 2 + 1f) * xOffset;
    float yPos = (ySize / 2 + 1f) * yOffset;
    float zPos = (zSize / 2 + 1f) * zOffset;
    bb = bb.translate(xPos / 16f, yPos / 16f, zPos / 16f);

    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    GL11.glEnable(GL11.GL_CULL_FACE);
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

    RenderUtil.bindBlockTexture();

    Tessellator.instance.startDrawingQuads();
    Tessellator.instance.addTranslation(x, y, z);
    CubeRenderer.render(bb, icon);
    Tessellator.instance.addTranslation(-x, -y, -z);
    Tessellator.instance.draw();

    GL11.glPopAttrib();
  }

}
