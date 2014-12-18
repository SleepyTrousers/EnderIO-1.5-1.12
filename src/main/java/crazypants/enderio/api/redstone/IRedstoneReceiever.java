package crazypants.enderio.api.redstone;

import java.util.Map;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.api.DyeColor;

public interface IRedstoneReceiever extends IRedstoneConnectable {

  void inputsChanged(World world, int x, int y, int z, ForgeDirection from, Map<DyeColor, Integer> inputs);

}
