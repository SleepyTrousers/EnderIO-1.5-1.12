package crazypants.enderio.api.redstone;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Default implementation for a signal source that connects a vanilla redstone source to a bundled redstone network.
 *
 */
public interface IBundledRedstoneSignalSourceDefault extends IBundledRedstoneSignalSource {

  @Nullable
  World getWorld();

  @Nullable
  BlockPos getPos();

  @Nonnull
  EnumFacing getSide();

  @Nonnull
  EnumDyeColor getChannel();

  @Override
  default @Nonnull Map<EnumDyeColor, Integer> getSignals() {
    final World world = getWorld();
    final BlockPos pos = getPos();
    if (world == null || pos == null || !world.isBlockLoaded(pos)) {
      return Collections.emptyMap();
    }
    return Collections.singletonMap(getChannel(), world.getRedstonePower(pos, getSide()) << 8);
  }

}
