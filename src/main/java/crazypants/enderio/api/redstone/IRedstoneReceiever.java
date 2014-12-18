package crazypants.enderio.api.redstone;

import java.util.Map;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.api.DyeColor;

public interface IRedstoneReceiever extends IRedstoneConnectable {

  void inputsChanged(World world, int x, int y, int z, ForgeDirection from, Map<DyeColor, Integer> inputs);

  /**
   * Whether this connection is "special", meaning it will not allow the user to
   * choose the output signal color.
   * 
   * @param world
   *          {@link World} object
   * @param x
   *          X position of your block
   * @param y
   *          Y position of your block
   * @param z
   *          Z position of your block
   * @param from
   *          The {@link ForgeDirection} that the conduit is coming from
   * 
   * @return Whether this connection is special
   */
  boolean isSpecialConnection(World world, int x, int y, int z, ForgeDirection from);

}
