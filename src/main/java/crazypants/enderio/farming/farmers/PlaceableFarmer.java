package crazypants.enderio.farming.farmers;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.farming.FarmNotification;
import crazypants.enderio.farming.FarmingAction;
import crazypants.enderio.farming.FarmingTool;
import crazypants.enderio.farming.IFarmer;
import crazypants.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry.Impl;

public class PlaceableFarmer extends Impl<IFarmerJoe> implements IFarmerJoe {

  private final @Nonnull Things DIRT;
  private final @Nonnull Things SEEDS;

  public PlaceableFarmer(@Nonnull String... seeds) {
    this("block:minecraft:dirt", seeds);
  }

  public PlaceableFarmer(@Nonnull String dirt, @Nonnull String[] seeds) {
    this(new String[] { dirt }, seeds);
  }

  public PlaceableFarmer(@Nonnull String[] dirt, @Nonnull String[] seeds) {
    SEEDS = new Things(seeds);
    DIRT = new Things(dirt);
  }

  public void addSeed(@Nonnull String seed) {
    SEEDS.add(seed);
  }

  public void addDirt(@Nonnull String dirt) {
    DIRT.add(dirt);
  }

  public boolean isValid() {
    return !SEEDS.isEmpty() && !DIRT.isEmpty();
  }

  @Override
  public boolean canPlant(@Nonnull ItemStack stack) {
    return SEEDS.contains(stack);
  }

  @Override
  public boolean prepareBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState meta) {
    IBlockState blockStateGround = farm.getBlockState(bc.down());
    Block ground = blockStateGround.getBlock();
    if (!DIRT.contains(ground)) {
      return false;
    }

    ItemStack seedStack = farm.getSeedTypeInSuppliesFor(bc);
    if (!canPlant(seedStack)) {
      if (!farm.isSlotLocked(bc)) {
        farm.setNotification(FarmNotification.NO_SEEDS);
      }
      return false;
    }

    return plant(farm, bc, meta);
  }

  protected boolean plant(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull IBlockState state) {
    final ItemStack seedStack = farm.takeSeedFromSupplies(bc);
    if (Prep.isValid(seedStack)) {
      EntityPlayerMP joe = farm.startUsingItem(seedStack);
      EnumActionResult res = seedStack.getItem().onItemUse(joe, joe.world, bc.down(), EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 0.5f, 0.5f);
      farm.handleExtraItems(farm.endUsingItem(FarmingTool.HOE), bc);
      if (res == EnumActionResult.SUCCESS) {
        farm.registerAction(FarmingAction.PLANT, FarmingTool.HOE, state, bc);
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean canHarvest(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState meta) {
    return false;
  }

  @Override
  public IHarvestResult harvestBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState meta) {
    return null;
  }

}
