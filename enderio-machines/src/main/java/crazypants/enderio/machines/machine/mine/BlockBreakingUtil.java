package crazypants.enderio.machines.machine.mine;

import java.util.Iterator;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class BlockBreakingUtil {

  /**
   * Break a block and return its drops. Will make sure that the block is set to AIR unless it is not loaded.
   * <p>
   * This is about the simplest implementation and I do expect it to become more complicated over time...
   */
  public static @Nonnull NNList<ItemStack> breakBlock(@Nonnull World world, @Nonnull BlockPos pos, boolean playSFX) {
    NNList<ItemStack> result = new NNList<>();
    if (world.isBlockLoaded(pos) && !world.isAirBlock(pos)) {
      IBlockState state = world.getBlockState(pos);
      state.getBlock().getDrops(result, world, pos, state, 0);
      float chance = ForgeEventFactory.fireBlockHarvesting(result, world, pos, state, 0, 1.0F, false, null);
      for (Iterator<ItemStack> iterator = result.iterator(); iterator.hasNext();) {
        iterator.next();
        if (world.rand.nextFloat() > chance) {
          iterator.remove();
        }
      }
      world.setBlockState(pos, Blocks.AIR.getDefaultState(), 1 | 2);
      if (playSFX) {
        world.playEvent(2001, pos, Block.getStateId(state));
      }

      for (EntityItem entity : world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos).grow(1))) {
        if (!entity.isDead && Prep.isValid(entity.getItem())) {
          result.add(entity.getItem());
          entity.setDead();
        }
      }
    }
    return result;
  }

}
