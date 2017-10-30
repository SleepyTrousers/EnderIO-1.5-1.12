package crazypants.enderio.conduit.redstone.signals;

import javax.annotation.Nonnull;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public interface ISignalSource {

  @Nonnull
  BlockPos getSource();

  @Nonnull
  EnumFacing getDir();

}