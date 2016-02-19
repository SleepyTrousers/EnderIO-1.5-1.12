package crazypants.enderio.item;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public interface IRotatableFacade {
  boolean tryRotateFacade(World world, int x, int y, int z, EnumFacing axis);
}
