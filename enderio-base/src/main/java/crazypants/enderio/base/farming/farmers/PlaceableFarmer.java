package crazypants.enderio.base.farming.farmers;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.api.farm.AbstractFarmerJoe;
import crazypants.enderio.api.farm.FarmNotification;
import crazypants.enderio.api.farm.FarmingAction;
import crazypants.enderio.api.farm.IFarmer;
import crazypants.enderio.api.farm.IHarvestResult;
import crazypants.enderio.base.farming.FarmingTool;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class PlaceableFarmer extends AbstractFarmerJoe {

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
  public boolean prepareBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull IBlockState state) {
    IBlockState blockStateGround = farm.getBlockState(bc.down());
    Block ground = blockStateGround.getBlock();
    if (!DIRT.contains(ground)) {
      return false;
    }

    ItemStack seedStack = farm.getSeedTypeInSuppliesFor(bc);
    if (!canPlant(seedStack)) {
      if (Prep.isInvalid(seedStack)) {
        farm.setNotification(FarmNotification.NO_SEEDS);
      }
      return false;
    }

    return plant(farm, bc, state);
  }

  protected boolean plant(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull IBlockState state) {
    if (Prep.isValid(farm.takeSeedFromSupplies(bc, true)) && farm.checkAction(FarmingAction.PLANT, FarmingTool.HOE)) {
      final ItemStack seedStack = farm.takeSeedFromSupplies(bc, false);
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
  public boolean canHarvest(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull IBlockState state) {
    return false;
  }

  @Override
  public IHarvestResult harvestBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull IBlockState state) {
    return null;
  }

}
