package crazypants.enderio.base.conduit.redstone.rsnew;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.EnumDyeColor;

public class AndFilter implements ISignal {

  // needs to be an output, not a signal. oops.

  protected final @Nonnull EnumDyeColor channel, channelA, channelB;

  protected Integer value = null;
  protected boolean dirty = true;

  public AndFilter(@Nonnull EnumDyeColor channel, @Nonnull EnumDyeColor channelA, @Nonnull EnumDyeColor channelB) {
    this.channel = channel;
    this.channelA = channelA;
    this.channelB = channelB;
  }

  @Override
  @Nullable
  public Integer get(@Nonnull EnumDyeColor channelIn) {
    if (channelIn == channel) {
      return value;
    }
    return null;
  }

  @Override
  public boolean acquire(@Nonnull IRedstoneConduitNetwork network) {
    if (dirty) {
      Integer powerA = network.getSignalLevel(channelA);
      Integer powerB = network.getSignalLevel(channelB);
      Integer power = powerA;
      if (powerB != null) {
        if (power == null) {
          power = powerB;
        } else {
          power = Math.min(power, powerB);
        }
      }
      if (power != value) {
        value = power;
        return true;
      }
    }
    return false;
  }

  @Override
  public void setDirty() {
    dirty = true;
  }

  @Override
  public boolean isDirty() {
    return dirty;
  }

  @Override
  @Nonnull
  public UID getUID() {
    return null; // new UID(pos, facing);
  }

}
