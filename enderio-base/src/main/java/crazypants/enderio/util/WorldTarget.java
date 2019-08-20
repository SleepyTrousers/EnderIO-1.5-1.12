package crazypants.enderio.util;

import java.lang.ref.WeakReference;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface WorldTarget {

  boolean isValid();

  default boolean isValid(@Nonnull EntityPlayer player) {
    return isValid();
  }

  default boolean isValid(@Nonnull EntityPlayer player, double maxDistanceSq) {
    return isValid(player);
  }

  static final @Nonnull WorldTarget TRUE = new WorldTarget() {

    @Override
    public boolean isValid() {
      return true;
    }

  };

  static final @Nonnull WorldTarget FALSE = new WorldTarget() {

    @Override
    public boolean isValid() {
      return false;
    }

  };

  static @Nonnull WorldTarget ofBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block block) {
    return new BlockTarget(block, world, pos);
  }

  static class BlockTarget implements WorldTarget {
    final @Nonnull WeakReference<World> targetWorld;
    final @Nonnull BlockPos targetPos;
    final @Nonnull Block targetBlock;

    private BlockTarget(@Nonnull Block block, @Nonnull World world, @Nonnull BlockPos pos) {
      targetWorld = new WeakReference<World>(world);
      targetPos = pos.toImmutable();
      targetBlock = block;
    }

    @Override
    public boolean isValid() {
      World world2 = targetWorld.get();
      return world2 != null && world2.isBlockLoaded(targetPos) && world2.getBlockState(targetPos).getBlock() == targetBlock;
    }

    @Override
    public boolean isValid(@Nonnull EntityPlayer player) {
      return targetWorld.get() == player.world && isValid();
    }

    @Override
    public boolean isValid(@Nonnull EntityPlayer player, double maxDistanceSq) {
      return player.getDistanceSqToCenter(targetPos) <= maxDistanceSq && isValid(player);
    }
  }

  static @Nonnull WorldTarget ofBlockState(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState blockstate) {
    return new BlockStateTarget(blockstate, world, pos);
  }

  static class BlockStateTarget extends BlockTarget {

    private @Nonnull IBlockState targetBlockstate;

    private BlockStateTarget(@Nonnull IBlockState blockstate, @Nonnull World world, @Nonnull BlockPos pos) {
      super(blockstate.getBlock(), world, pos);
      targetBlockstate = blockstate;
    }

    @Override
    public boolean isValid() {
      World world2 = targetWorld.get();
      return world2 != null && world2.isBlockLoaded(targetPos) && world2.getBlockState(targetPos) == targetBlockstate;
    }

  }

}
