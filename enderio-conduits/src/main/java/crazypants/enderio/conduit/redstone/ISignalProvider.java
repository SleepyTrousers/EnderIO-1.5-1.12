package crazypants.enderio.conduit.redstone;

import crazypants.enderio.base.conduit.redstone.signals.Signal;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;

// TODO Move to New Signal System

public interface ISignalProvider {

  boolean connectsToNetwork(World world, BlockPos pos, EnumFacing side);

  Set<Signal> getNetworkInputs(World world, BlockPos pos, EnumFacing side);
}
