package crazypants.enderio.farming.farmers;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.farming.FarmNotification;
import crazypants.enderio.farming.TileFarmStation;
import crazypants.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class PlaceableFarmer implements IFarmerJoe {

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

  public void addSeed(String seed) {
    SEEDS.add(seed);
  }

  public void addDirt(String dirt) {
    DIRT.add(dirt);
  }

  public boolean isValid() {
    return !SEEDS.isEmpty() && !DIRT.isEmpty();
  }

  @Override
  public boolean canPlant(ItemStack stack) {
    return SEEDS.contains(stack);
  }

  @Override
  public boolean prepareBlock(TileFarmStation farm, BlockPos bc, Block block, IBlockState meta) {
    Block ground = farm.getBlock(bc.down());
    if (!DIRT.contains(ground)) {
      return false;
    }

    int slot = farm.getSupplySlotForCoord(bc);
    ItemStack seedStack = farm.getSeedTypeInSuppliesFor(slot);
    if (!canPlant(seedStack)) {
      if (!farm.isSlotLocked(slot)) {
        farm.setNotification(FarmNotification.NO_SEEDS);
      }
      return false;
    }

    return plant(farm, bc);
  }

  protected boolean plant(TileFarmStation farm, BlockPos bc) {
    final ItemStack seedStack = farm.takeSeedFromSupplies(bc);
    if (Prep.isValid(seedStack)) {
      EntityPlayerMP fakePlayer = farm.getFakePlayer();
      farm.setJoeUseItem(seedStack);
      EnumActionResult res = seedStack.getItem().onItemUse(fakePlayer, fakePlayer.world, bc.down(), EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 0.5f, 0.5f);
      farm.clearJoeUseItem(false);
      if (res == EnumActionResult.SUCCESS) {
        farm.actionPerformed(false);
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean canHarvest(TileFarmStation farm, BlockPos bc, Block block, IBlockState meta) {
    return false;
  }

  @Override
  public IHarvestResult harvestBlock(TileFarmStation farm, BlockPos bc, Block block, IBlockState meta) {
    return null;
  }

}
