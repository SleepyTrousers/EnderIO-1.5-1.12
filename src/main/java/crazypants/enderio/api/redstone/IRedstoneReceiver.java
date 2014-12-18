package crazypants.enderio.api.redstone;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public interface IRedstoneReceiver extends IRedstoneConnectable {

  void inputsChanged(World world, int x, int y, int z, ForgeDirection from, byte[] inputs);

}
