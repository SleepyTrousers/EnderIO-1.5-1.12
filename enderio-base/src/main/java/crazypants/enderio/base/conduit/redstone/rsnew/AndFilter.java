package crazypants.enderio.base.conduit.redstone.rsnew;

import java.util.Set;

import javax.annotation.Nonnull;

import net.minecraft.item.EnumDyeColor;

public class AndFilter implements ISingleSignal {

  protected final @Nonnull ISingleSignal parent;
  protected final @Nonnull EnumDyeColor channelB;

  protected int value = 0;
  protected boolean dirty = true;

  public AndFilter(@Nonnull ISingleSignal parent, @Nonnull EnumDyeColor channelB) {
    this.parent = parent;
    this.channelB = channelB;
  }

  @Override
  public int get(@Nonnull EnumDyeColor channelIn) {
    if (channelIn == parent.getChannel()) {
      return value;
    }
    return 0;
  }

  @Override
  public boolean acquire(@Nonnull ISignalNetwork network) {
    if (isDirty()) {
      dirty = false;
      parent.acquire(network);
      int powerA = parent.get();
      int powerB = network.getSignalLevel(channelB);
      int power = Math.min(powerA, powerB);
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
    return dirty || parent.isDirty();
  }

  @Override
  public boolean needsTicking() {
    return true;
  }

  @Override
  public boolean tick(@Nonnull ISignalNetwork network, @Nonnull Set<EnumDyeColor> changedChannels, boolean firstTick) {
    if (changedChannels.contains(channelB)) {
      dirty = true;
    }
    return firstTick && dirty;
  }

  @Override
  @Nonnull
  public UID getUID() {
    return parent.getUID();
  }

  @Override
  @Nonnull
  public EnumDyeColor getChannel() {
    return parent.getChannel();
  }

}
