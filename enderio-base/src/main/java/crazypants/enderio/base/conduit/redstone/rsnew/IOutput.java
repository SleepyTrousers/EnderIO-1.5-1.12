package crazypants.enderio.base.conduit.redstone.rsnew;

import java.util.Collection;
import java.util.Set;

import javax.annotation.Nonnull;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;

/**
 * This represents an output of the conduit network that needs to be notified after a change. The network will collect all locations that need notifications,
 * de-dupe them and then notify them all at once.
 *
 */
public interface IOutput {

  @Nonnull
  Collection<BlockPos> getNotificationTargets(@Nonnull IRedstoneConduitNetwork network, @Nonnull Set<EnumDyeColor> changedChannels);

  @Nonnull
  UID getUID();

}
