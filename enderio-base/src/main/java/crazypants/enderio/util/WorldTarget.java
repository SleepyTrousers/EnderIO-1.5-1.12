package crazypants.enderio.util;

import java.lang.ref.WeakReference;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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

  default @Nonnull IBlockState read() {
    return Blocks.AIR.getDefaultState();
  }

  default boolean write(@Nonnull IBlockState newState) {
    return false;
  }

  @Nonnull
  WorldTarget TRUE = new WorldTarget() {

    @Override
    public boolean isValid() {
      return true;
    }

  };

  @Nonnull
  WorldTarget FALSE = new WorldTarget() {

    @Override
    public boolean isValid() {
      return false;
    }

  };

  static @Nonnull WorldTarget ofBlockPos(@Nonnull World world, @Nonnull BlockPos pos) {
    return new BlockPosTarget(world, pos);
  }

  class BlockPosTarget implements WorldTarget {
    final @Nonnull WeakReference<World> targetWorld;
    final @Nonnull BlockPos targetPos;

    private BlockPosTarget(@Nonnull World world, @Nonnull BlockPos pos) {
      targetWorld = new WeakReference<World>(world);
      targetPos = pos.toImmutable();
    }

    @Override
    public boolean isValid() {
      World world2 = targetWorld.get();
      return world2 != null && world2.isBlockLoaded(targetPos);
    }

    @Override
    public boolean write(@Nonnull IBlockState newState) {
      World world2 = targetWorld.get();
      if (world2 != null && world2.isBlockLoaded(targetPos)) {
        return world2.setBlockState(targetPos, newState);
      }
      return WorldTarget.super.write(newState);
    }

    @Override
    @Nonnull
    public IBlockState read() {
      World world2 = targetWorld.get();
      return world2 != null && world2.isBlockLoaded(targetPos) ? world2.getBlockState(targetPos) : WorldTarget.super.read();
    }

  }

  static @Nonnull WorldTarget ofBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block block) {
    return new BlockTarget(block, world, pos);
  }

  class BlockTarget extends BlockPosTarget {
    final @Nonnull Block targetBlock;

    private BlockTarget(@Nonnull Block block, @Nonnull World world, @Nonnull BlockPos pos) {
      super(world, pos);
      targetBlock = block;
    }

    @Override
    public boolean isValid() {
      return read().getBlock() == targetBlock;
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

  class BlockStateTarget extends BlockTarget {

    private @Nonnull IBlockState targetBlockstate;

    private BlockStateTarget(@Nonnull IBlockState blockstate, @Nonnull World world, @Nonnull BlockPos pos) {
      super(blockstate.getBlock(), world, pos);
      targetBlockstate = blockstate;
    }

    @Override
    public boolean isValid() {
      return read() == targetBlockstate;
    }

  }

}
