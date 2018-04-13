package crazypants.enderio.conduits.conduit.redstone;

import java.util.Set;

import crazypants.enderio.base.conduit.redstone.signals.Signal;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISignalProvider {

  boolean connectsToNetwork(World world, BlockPos pos, EnumFacing side);

  Set<Signal> getNetworkInputs(World world, BlockPos pos, EnumFacing side);
}
