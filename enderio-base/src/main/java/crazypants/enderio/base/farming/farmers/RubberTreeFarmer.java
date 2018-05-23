package crazypants.enderio.base.farming.farmers;

import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.farm.FarmNotification;
import crazypants.enderio.api.farm.FarmingAction;
import crazypants.enderio.api.farm.IFarmer;
import crazypants.enderio.api.farm.IHarvestResult;
import crazypants.enderio.base.farming.FarmersRegistry;
import crazypants.enderio.base.farming.FarmingTool;
import crazypants.enderio.base.farming.harvesters.IHarvestingTarget;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class RubberTreeFarmer extends TreeFarmer {

  protected final @Nonnull ItemStack stickyResin;

  public RubberTreeFarmer(@Nonnull Block sapling, @Nonnull Block wood, @Nonnull Item treetap, @Nonnull ItemStack resin) {
    super(sapling, wood);
    FarmingTool.TREETAP.getThings().add(treetap);
    stickyResin = resin;
    FarmersRegistry.slotItemsProduce.add(stickyResin);
  }

  public boolean isValid() {
    return !saplings.isEmpty() && !woods.isEmpty() && !FarmingTool.TREETAP.getThings().isEmpty() && Prep.isValid(stickyResin);
  }

  @Override
  public boolean prepareBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull IBlockState meta) {
    if (canPlant(farm.getSeedTypeInSuppliesFor(bc))) {
      // we'll lose some spots in the center, but we can plant in the outer ring, which gives a net gain
      if (Math.abs(farm.getLocation().getX() - bc.getX()) % 2 == 0) {
        return true;
      }
      if (Math.abs(farm.getLocation().getZ() - bc.getZ()) % 2 == 0) {
        return true;
      }
      final World world = farm.getWorld();
      for (int x = -1; x < 2; x++) {
        for (int z = -1; z < 2; z++) {
          final BlockPos pos = bc.add(x, 0, z);
          final IBlockState state = world.getBlockState(pos);
          final Block block = state.getBlock();
          if (!(block.isLeaves(state, world, pos) || block.isAir(state, world, pos) || block.canBeReplacedByLeaves(state, world, pos))) {
            return true;
          }
        }
      }
      return super.prepareBlock(farm, bc, meta);
    }
    return false;
  }

  @Override
  public IHarvestResult harvestBlock(@Nonnull final IFarmer farm, @Nonnull BlockPos pos, @Nonnull IBlockState meta) {
    final HarvestResult res = new HarvestResult();
    final World world = farm.getWorld();

    setupHarvesting(farm, pos);

    while (pos.getY() <= 255) {
      IBlockState state = world.getBlockState(pos);
      if (isWood(state.getBlock())) {
        if (canHarvest(world, pos)) {
          if (farm.hasTool(FarmingTool.TREETAP)) {
            harvest(res, world, pos);
            farm.registerAction(FarmingAction.HARVEST, FarmingTool.TREETAP, state, pos);
          } else {
            farm.setNotification(FarmNotification.NO_TREETAP);
          }
        }
        harvestLeavesAround(farm, world, res, pos);
      } else if (IHarvestingTarget.isDefaultLeaves(state)) {
        harvestLeavesBlock(farm, res, world, pos);
      } else {
        return res;
      }
      pos = pos.up();
    }
    return res;
  }

  private void harvestLeavesBlock(@Nonnull final IFarmer farm, @Nonnull final HarvestResult res, final @Nonnull World world, final @Nonnull BlockPos pos) {
    IBlockState state = world.getBlockState(pos);
    if (IHarvestingTarget.isDefaultLeaves(state)) {
      res.getHarvestedBlocks().add(pos);
      harvestSingleBlock(farm, world, res, pos);
      harvestLeavesAround(farm, world, res, pos);
    }
  }

  void harvestLeavesAround(final @Nonnull IFarmer farm, final @Nonnull World world, final @Nonnull HarvestResult res, final @Nonnull BlockPos pos) {
    NNList.FACING_HORIZONTAL.apply(new NNList.Callback<EnumFacing>() {
      @Override
      public void apply(@Nonnull EnumFacing face) {
        harvestLeavesBlock(farm, res, world, pos.offset(face));
      }
    });
  }

  private boolean canHarvest(@Nonnull World world, @Nonnull BlockPos pos) {
    return hasResin(world.getBlockState(pos));
  }

  private void harvest(@Nonnull HarvestResult res, @Nonnull World world, @Nonnull BlockPos pos) {
    world.setBlockState(pos, removeResin(world.getBlockState(pos)), 3);
    ItemStack drop = makeResin(world.rand);
    EntityItem dropEnt = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, drop);
    res.getDrops().add(dropEnt);
    res.getHarvestedBlocks().add(pos);
  }

  protected @Nonnull ItemStack makeResin(@Nonnull Random rand) {
    return stickyResin.copy();
  }

  protected abstract boolean hasResin(@Nonnull IBlockState state);

  protected abstract @Nonnull IBlockState removeResin(@Nonnull IBlockState state);

}
