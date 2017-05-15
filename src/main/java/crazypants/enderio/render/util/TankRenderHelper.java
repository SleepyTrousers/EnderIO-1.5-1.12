package crazypants.enderio.render.util;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.render.util.HalfBakedQuad.HalfBakedList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class TankRenderHelper {

  private static final Double px = 1d / 16d;

  /**
   * Generate a cube of liquid for a SmartTank.
   * 
   * @param tank
   *          The tank that contains the liquid.
   * @param xzBorder
   *          How many pixels of space to leave around the y and z sides. (0-7.99)
   * @param miny
   *          Height to start the tank. (0-15.99)
   * @param maxy
   *          Height to end the tank (0.01-16)
   * @param renderBottom
   *          Render the bottom face?
   * @return A HalfBakedList with the tank content or null if the tank is empty
   */
  public static HalfBakedList mkTank(SmartTank tank, double xzBorder, double miny, double maxy, boolean renderBottom) {
    if (tank != null) {
      float ratio = tank.getFilledRatio();
      final FluidStack fluid = tank.getFluid();
      if (fluid != null && ratio > 0) {

        float height = 1 - ratio;

        ResourceLocation still = fluid.getFluid().getStill(fluid);
        int color = fluid.getFluid().getColor(fluid);
        Vector4f vecC = new Vector4f((color >> 16 & 0xFF) / 255d, (color >> 8 & 0xFF) / 255d, (color & 0xFF) / 255d, 1);
        TextureAtlasSprite sprite = still == null ? null : Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(still.toString());
        if (sprite == null) {
          sprite = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
          vecC = null;
        }
        boolean gas = fluid.getFluid().isGaseous(fluid);

        BoundingBox bb = gas
            ? new BoundingBox(xzBorder * px, (maxy - (maxy - miny) * ratio) * px, xzBorder * px, (16 - xzBorder) * px, maxy * px, (16 - xzBorder) * px)
            : new BoundingBox(xzBorder * px, miny * px, xzBorder * px, (16 - xzBorder) * px, ((maxy - miny) * ratio + miny) * px, (16 - xzBorder) * px);

        HalfBakedList buffer = new HalfBakedList();

        buffer.add(bb, EnumFacing.NORTH, 0f, 1f, height, 1f, sprite, vecC, gas);
        buffer.add(bb, EnumFacing.EAST, 0f, 1f, height, 1f, sprite, vecC, gas);
        buffer.add(bb, EnumFacing.SOUTH, 0f, 1f, height, 1f, sprite, vecC, gas);
        buffer.add(bb, EnumFacing.WEST, 0f, 1f, height, 1f, sprite, vecC, gas);
        if (!gas || renderBottom) {
          buffer.add(bb, EnumFacing.UP, 0f, 1f, 0f, 1f, sprite, vecC);
        }
        if (gas || renderBottom) {
          buffer.add(bb, EnumFacing.DOWN, 0f, 1f, 0f, 1f, sprite, vecC);
        }

        return buffer;
      }
    }
    return null;
  }

}
