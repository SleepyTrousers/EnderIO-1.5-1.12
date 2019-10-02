package crazypants.enderio.base.conduit.redstone.rsnew;

import java.util.Set;

import javax.annotation.Nonnull;

import net.minecraft.item.EnumDyeColor;

public interface ISignal {

  /**
   * Gets the current cached signal value
   */
  int get(@Nonnull EnumDyeColor channel);

  /**
   * Acquires the signal value and caches it.
   * 
   * @return true if the value changed, false otherwise
   */
  boolean acquire(@Nonnull IRedstoneConduitNetwork network);

  void setDirty();

  boolean isDirty();

  @Nonnull
  UID getUID();

  default boolean needsTicking() {
    return false;
  }

  /**
   * Ticks the signal. The signal needs to set itself dirty so will be acquire()'ed. There's no need to set the network dirty, the return value determines that.
   * 
   * @param network
   * @param changedChannels
   *          a set containing all channels that changed their value since the last tick(). May be empty. Computed signals should use this to check if they need
   *          to do something or not.
   * @param firstTick
   *          true if this is the first time tick() is called in a tick. false if it is repeat call after signals were re-acquired because a tick() returned
   *          true.
   * @return true if the signal needs to be acquired, false otherwise
   */
  default boolean tick(@Nonnull IRedstoneConduitNetwork network, @Nonnull Set<EnumDyeColor> changedChannels, boolean firstTick) {
    return false;
  }
}
