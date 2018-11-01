package crazypants.enderio.base.integration.ic2c;

import javax.annotation.Nonnull;

import crazypants.enderio.base.farming.farmers.RubberTreeFarmer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RubberTreeFarmerIC2classic extends RubberTreeFarmer {

  private RubberTreeFarmerIC2classic(@Nonnull Block sapling, @Nonnull Block wood, @Nonnull ItemStack resin) {
    super(sapling, wood, resin);
  }

  public static RubberTreeFarmer create() {
    Block sapling = Block.REGISTRY.getObject(new ResourceLocation("ic2", "blockrubsapling"));
    Block wood = Block.REGISTRY.getObject(new ResourceLocation("ic2", "blockrubwood"));
    Item resin = Item.REGISTRY.getObject(new ResourceLocation("ic2", "itemharz"));

    if (sapling != Blocks.AIR && wood != Blocks.AIR && resin != null) {
      return new RubberTreeFarmerIC2classic(sapling, wood, new ItemStack(resin));
    }

    return null;
  }

  /*
   * meta: 0=no resin; 1=north, harvested; 2=north harvestable; 3=south, harvested; 4=south harvestable; 5=west, harvested; 6=west harvestable; 7=west,
   * harvested; 8=west harvestable;
   */

  @Override
  protected boolean canHaveResin(@Nonnull IBlockState state) {
    final int meta = state.getBlock().getMetaFromState(state);
    return meta > 0 && meta <= 8;
  }

  @Override
  protected boolean hasResin(@Nonnull IBlockState state) {
    final int meta = state.getBlock().getMetaFromState(state);
    return meta == 2 || meta == 4 || meta == 6 || meta == 8;
  }

  @Override
  protected @Nonnull IBlockState removeResin(@Nonnull IBlockState state) {
    return state.getBlock().getStateFromMeta(state.getBlock().getMetaFromState(state) - 1);
  }

}
