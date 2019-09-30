package crazypants.enderio.base.conduit.redstone.rsnew;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.EnumDyeColor;

public class BinaryFilter implements ISignal {

  protected final @Nonnull ISignal parent;

  public BinaryFilter(@Nonnull ISignal parent) {
    this.parent = parent;
  }

  @Override
  @Nullable
  public Integer get(@Nonnull EnumDyeColor channelIn) {
    Integer value = parent.get(channelIn);
    return value == null ? null : value < 15 ? 0 : 15;
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

}
