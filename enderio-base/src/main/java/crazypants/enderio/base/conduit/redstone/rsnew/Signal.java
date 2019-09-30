package crazypants.enderio.base.conduit.redstone.rsnew;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Signal implements ISignal {

  protected final @Nonnull World world;
  protected final @Nonnull BlockPos pos;
  protected final @Nonnull EnumFacing facing;
  protected final @Nonnull EnumDyeColor channel;

  protected int value = -1;
  protected boolean dirty = true;

  public Signal(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, @Nonnull EnumDyeColor channel) {
    this.world = world;
    this.pos = pos;
    this.facing = facing;
    this.channel = channel;
  }

  @Override
  @Nullable
  public Integer get(@Nonnull EnumDyeColor channelIn) {
    if (channelIn == channel && value >= 0) {
      return value;
    }
    return null;
  }

  @Override
  public boolean acquire(@Nonnull IRedstoneConduitNetwork network) {
    if (dirty) {
      int power = world.getRedstonePower(pos, facing);
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
    return new UID(pos, facing);
  }

}
