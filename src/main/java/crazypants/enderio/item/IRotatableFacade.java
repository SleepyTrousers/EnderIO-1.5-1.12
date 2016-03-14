package crazypants.enderio.item;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public interface IRotatableFacade {
  boolean tryRotateFacade(World world, BlockPos pos, EnumFacing axis);
}
