package crazypants.enderio.base.integration.techreborn;

import javax.annotation.Nonnull;

import crazypants.enderio.base.farming.farmers.RubberTreeFarmer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RubberTreeFarmerTechReborn extends RubberTreeFarmer {

  private RubberTreeFarmerTechReborn(@Nonnull Block sapling, @Nonnull Block wood, @Nonnull ItemStack resin) {
    super(sapling, wood, resin);
  }

  public static RubberTreeFarmer create() {
    Block sapling = Block.REGISTRY.getObject(new ResourceLocation("techreborn", "rubberSapling"));
    Block wood = Block.REGISTRY.getObject(new ResourceLocation("techreborn", "rubberLog"));
    Item resin = Item.REGISTRY.getObject(new ResourceLocation("techreborn", "part"));

    if (sapling != Blocks.AIR && wood != Blocks.AIR && resin != null) {
      return new RubberTreeFarmerTechReborn(sapling, wood, new ItemStack(resin, 1, 31));
    }

    return null;
  }

  @Override
  protected boolean hasResin(@Nonnull IBlockState state) {
    return state.getBlock().getMetaFromState(state) >= 3;
  }

  @Override
  protected @Nonnull IBlockState removeResin(@Nonnull IBlockState state) {
    return state.getBlock().getStateFromMeta(state.getBlock().getMetaFromState(state) - 3);
  }

}
