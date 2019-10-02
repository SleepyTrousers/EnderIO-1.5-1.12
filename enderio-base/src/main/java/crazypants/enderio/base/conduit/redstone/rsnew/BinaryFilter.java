package crazypants.enderio.base.conduit.redstone.rsnew;

import javax.annotation.Nonnull;

import net.minecraft.item.EnumDyeColor;

public class BinaryFilter implements ISingleSignal {

  protected final @Nonnull ISingleSignal parent;

  public BinaryFilter(@Nonnull ISingleSignal parent) {
    this.parent = parent;
  }

  @Override
  public int get(@Nonnull EnumDyeColor channelIn) {
    if (channelIn == parent.getChannel()) {
      int value = parent.get();
      return value < 15 ? 0 : 15;
    }
    return 0;
  }

  @Override
  public boolean acquire(@Nonnull IRedstoneConduitNetwork network) {
    return parent.acquire(network);
  }

  @Override
  public void setDirty() {
    parent.setDirty();
  }

  @Override
  public boolean isDirty() {
    return parent.isDirty();
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
