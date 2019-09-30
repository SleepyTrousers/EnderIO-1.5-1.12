package crazypants.enderio.base.conduit.redstone.rsnew;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;

public interface IRedstoneConduitNetwork {

  /**
   * Adds or updates a signal.
   * <p>
   * Note that an updated signal still needs to be dirty to be acquired, this only causes the overall state to be set to dirty.
   * 
   * @param signal
   */
  void addSignal(@Nonnull ISignal signal);

  void removeSignal(@Nonnull ISignal signal);

  void removeSignal(@Nonnull UID uuid);

  default void removeAllSignals(@Nonnull BlockPos source) {
    NNList.FACING.apply(side -> {
      removeSignal(new UID(source.offset(side), side));
    });
  }

  /**
   * Notifies the network that one or more signals have become dirty. The network will find out which signals are dirty and acquire them later.
   * <p>
   * Note that {@link #addSignal(ISignal)}, {@link #removeSignal(ISignal)} and {@link #removeSignal(crazypants.enderio.base.conduit.redstone.rsnew.ISignal.UID)}
   * automatically set the network status to dirty.
   */
  void setSignalsDirty();

  /**
   * Gets the cached signal level for the given channel.
   * 
   * @param channel
   * @return <code>null</code> if there's no signal on that channel, a signal level otherwise
   */
  Integer getSignalLevel(@Nonnull EnumDyeColor channel);

}
