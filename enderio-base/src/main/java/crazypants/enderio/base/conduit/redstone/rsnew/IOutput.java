package crazypants.enderio.base.conduit.redstone.rsnew;

import java.util.Collection;
import java.util.Set;

import javax.annotation.Nonnull;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This represents an output of the conduit network that needs to be notified after a change. The network will collect all locations that need notifications,
 * de-dupe them and then notify them all at once.
 *
 */
public interface IOutput {

  @Nonnull
  Collection<Target> getNotificationTargets(@Nonnull IRedstoneConduitNetwork network, @Nonnull Set<EnumDyeColor> changedChannels);

  @Nonnull
  UID getUID();

  public class Target {
    private final @Nonnull BlockPos from, to;
    private final @Nonnull World world;

    public Target(@Nonnull World world, @Nonnull BlockPos from, @Nonnull BlockPos to) {
      this.world = world;
      this.from = from;
      this.to = to;
    }

    public @Nonnull BlockPos getFrom() {
      return from;
    }

    public @Nonnull BlockPos getTo() {
      return to;
    }

    public @Nonnull World getWorld() {
      return world;
    }
  }
}
