package crazypants.enderio.base.conduit.redstone.rsnew;

import javax.annotation.Nonnull;

import net.minecraft.item.EnumDyeColor;

/**
 * An {@link ISignal} that only can supply a data on a single channel.
 * <p>
 * This is the typical Redstone connection which ins configured for one channel in the connection GUI.
 *
 */
public interface ISingleSignal extends ISignal {

  @Nonnull
  EnumDyeColor getChannel();

  default int get() {
    return get(getChannel());
  }

}
