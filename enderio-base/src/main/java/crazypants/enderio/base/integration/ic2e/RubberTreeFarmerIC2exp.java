package crazypants.enderio.base.integration.ic2e;

import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.base.farming.farmers.RubberTreeFarmer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RubberTreeFarmerIC2exp extends RubberTreeFarmer {

  private RubberTreeFarmerIC2exp(@Nonnull Block sapling, @Nonnull Block wood, @Nonnull ItemStack resin) {
    super(sapling, wood, resin);
  }

  public static RubberTreeFarmer create() {
    Block sapling = Block.REGISTRY.getObject(new ResourceLocation("ic2", "sapling"));
    Block wood = Block.REGISTRY.getObject(new ResourceLocation("ic2", "rubber_wood"));
    Item resin = Item.REGISTRY.getObject(new ResourceLocation("ic2", "misc_resource"));

    if (sapling != Blocks.AIR && wood != Blocks.AIR && resin != null) {
      return new RubberTreeFarmerIC2exp(sapling, wood, new ItemStack(resin, 1, 4));
    }

    return null;
  }

  @Override
  protected boolean hasResin(@Nonnull IBlockState state) {
    return state.getBlock().getMetaFromState(state) > 6;
  }

  @Override
  protected @Nonnull IBlockState removeResin(@Nonnull IBlockState state) {
    return state.getBlock().getStateFromMeta(state.getBlock().getMetaFromState(state) - 4);
  }

  @Override
  protected @Nonnull ItemStack makeResin(@Nonnull Random rand) {
    ItemStack copy = stickyResin.copy();
    copy.setCount(rand.nextInt(3) + 1);
    return copy;
  }

}
