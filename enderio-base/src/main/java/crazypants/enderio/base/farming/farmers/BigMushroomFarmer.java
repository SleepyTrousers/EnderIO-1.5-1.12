package crazypants.enderio.base.farming.farmers;

import java.util.Comparator;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BigMushroomFarmer extends TreeFarmer {

  public BigMushroomFarmer(boolean ignoreMeta, Block sapling, Block... wood) {
    super(ignoreMeta, sapling, wood);
  }

  @Override
  protected boolean canPlant(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ItemStack sapling) {
    Block block1 = world.getBlockState(pos.down()).getBlock();
    // hardcoded check from net.minecraft.world.gen.feature.WorldGenBigMushroom.generate()
    return (block1 == Blocks.DIRT || block1 == Blocks.GRASS || block1 == Blocks.MYCELIUM) && super.canPlant(world, pos, sapling);
  }

  @Override
  protected Comparator<BlockPos> getComperator(@Nonnull BlockPos base) {
    return new DistanceComparator(base);
  }

}
