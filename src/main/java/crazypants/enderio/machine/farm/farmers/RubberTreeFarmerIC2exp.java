package crazypants.enderio.machine.farm.farmers;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RubberTreeFarmerIC2exp extends RubberTreeFarmer {

  private RubberTreeFarmerIC2exp(Block sapling, Block wood, Item treetap, ItemStack resin) {
    super(sapling, wood, treetap, resin);
  }

  public static RubberTreeFarmer create() {
    Block sapling = Block.REGISTRY.getObject(new ResourceLocation("ic2", "sapling"));
    Block wood = Block.REGISTRY.getObject(new ResourceLocation("ic2", "rubber_wood"));
    Item treetap = Item.REGISTRY.getObject(new ResourceLocation("ic2", "treetap"));
    Item resin = Item.REGISTRY.getObject(new ResourceLocation("ic2", "misc_resource"));

    if (sapling != Blocks.AIR && wood != Blocks.AIR && treetap != null && resin != null) {
      return new RubberTreeFarmerIC2exp(sapling, wood, treetap, new ItemStack(resin, 1, 4));
    }

    return null;
  }

  @Override
  protected boolean hasResin(IBlockState state) {
    return state.getBlock().getMetaFromState(state) > 6;
  }

  @Override
  protected IBlockState removeResin(IBlockState state) {
    return state.getBlock().getStateFromMeta(state.getBlock().getMetaFromState(state) - 4);
  }

  @Override
  protected ItemStack makeResin(Random rand) {
    ItemStack copy = stickyResin.copy();
    copy.stackSize = rand.nextInt(3) + 1;
    return copy;
  }

}
