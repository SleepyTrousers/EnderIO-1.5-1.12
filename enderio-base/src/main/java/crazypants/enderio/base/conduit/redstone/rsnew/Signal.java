package crazypants.enderio.base.conduit.redstone.rsnew;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Signal implements ISingleSignal {

  protected final @Nonnull World world;
  protected final @Nonnull BlockPos pos;
  protected final @Nonnull EnumFacing facing;
  protected final @Nonnull EnumDyeColor channel;

  protected int value = 0;
  protected boolean dirty = true;

  public Signal(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, @Nonnull EnumDyeColor channel) {
    this.world = world;
    this.pos = pos;
    this.facing = facing;
    this.channel = channel;
  }

  @Override
  public int get(@Nonnull EnumDyeColor channelIn) {
    if (channelIn == channel) {
      return value;
    }
    return 0;
  }

  @Override
  public boolean acquire(@Nonnull ISignalNetwork network) {
    if (dirty) {
      dirty = false;
      int power = world.getStrongPower(pos, facing);
      if (power == 0) {
        power = world.getRedstonePower(pos, facing);
        if (power < 15) {
          IBlockState bs = world.getBlockState(pos);
          if (bs.getBlock() == Blocks.REDSTONE_WIRE) {
            power = Math.max(power, bs.getValue(BlockRedstoneWire.POWER));
          }
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
    return new UID(pos, facing);
  }

  @Override
  @Nonnull
  public EnumDyeColor getChannel() {
    return channel;
  }

}
