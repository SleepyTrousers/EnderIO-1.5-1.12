package crazypants.enderio.base.farming.farmers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.api.farm.FarmNotification;
import crazypants.enderio.api.farm.FarmingAction;
import crazypants.enderio.api.farm.IFarmer;
import crazypants.enderio.api.farm.IHarvestResult;
import crazypants.enderio.base.farming.FarmersRegistry;
import crazypants.enderio.base.farming.FarmingTool;
import crazypants.enderio.base.farming.harvesters.FarmHarvestingTarget;
import crazypants.enderio.base.farming.harvesters.IHarvestingTarget;
import crazypants.enderio.base.farming.harvesters.TreeHarvester;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
  public Result tryPrepareBlock(@Nonnull IFarmer farm, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    if (canPlant(farm.getSeedTypeInSuppliesFor(pos))) {
      // we'll lose some spots in the center, but we can plant in the outer ring, which gives a net gain
      if (Math.abs(farm.getLocation().getX() - pos.getX()) % 2 == 0) {
        return Result.CLAIM;
      }
      if (Math.abs(farm.getLocation().getZ() - pos.getZ()) % 2 == 0) {
        return Result.CLAIM;
      }
      final World world = farm.getWorld();
      for (int x = -1; x < 2; x++) {
        for (int z = -1; z < 2; z++) {
          final BlockPos pos2 = pos.add(x, 0, z);
          final IBlockState state2 = world.getBlockState(pos2);
          final Block block = state2.getBlock();
          if (!(block.isLeaves(state2, world, pos2) || block.isAir(state2, world, pos2) || block.canBeReplacedByLeaves(state2, world, pos2))) {
            return Result.CLAIM;
          }
        }
      }
      return super.tryPrepareBlock(farm, pos, state);
    }
    return Result.NEXT;
  }

  protected boolean hasTap;

  @Override
  protected void setupHarvesting(@Nonnull IFarmer farm, @Nonnull BlockPos harvestLocation) {
    hasTap = farm.hasTool(FarmingTool.TREETAP);
    if (hasTap) {
      hasAxe = farm.hasTool(FarmingTool.AXE);
      fortune = farm.getLootingValue(FarmingTool.AXE);
      hasShears = farm.hasTool(FarmingTool.SHEARS);
      hasHoe = farm.hasTool(FarmingTool.HOE);
      noShearingPercentage = farm.isLowOnSaplings(harvestLocation);
      shearCount = 0;
    }
  }

  @Override
  public IHarvestResult harvestBlock(@Nonnull final IFarmer farm, @Nonnull BlockPos bc, @Nonnull IBlockState meta) {
    setupHarvesting(farm, bc);

    if (!hasTap) {
      farm.setNotification(FarmNotification.NO_TREETAP);
      return null;
    }

    final World world = farm.getWorld();
    final HarvestResult res = new HarvestResult();

    final IHarvestingTarget target = new FarmHarvestingTarget(this, farm);
    TreeHarvester.harvest(world, bc, res, target);
    Collections.sort(res.getHarvestedBlocks(), comp);

    List<BlockPos> actualHarvests = new ArrayList<BlockPos>();
    boolean harvesting = true;

    for (int i = 0; i < res.getHarvestedBlocks().size() && harvesting; i++) {
      final BlockPos pos = res.getHarvestedBlocks().get(i);
      IBlockState state = world.getBlockState(pos);

      if (isWood(state.getBlock())) {
        if (hasResin(state)) {
          if (farm.checkAction(FarmingAction.HARVEST, FarmingTool.TREETAP)) {
            harvest(res, world, pos, state);
            actualHarvests.add(pos);
            farm.registerAction(FarmingAction.HARVEST, FarmingTool.TREETAP, state, pos);
          } else {
            harvesting = false;
          }
        }
      } else if (IHarvestingTarget.isDefaultLeaves(state)) {
        if (harvestSingleBlock(farm, world, res, pos)) {
          actualHarvests.add(pos);
        } else {
          harvesting = false;
        }
      }
    }

    res.getHarvestedBlocks().clear();
    res.getHarvestedBlocks().addAll(actualHarvests);

    return res;
  }

  private void harvest(@Nonnull HarvestResult res, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    world.setBlockState(pos, removeResin(state), 3);
    ItemStack drop = makeResin(world.rand);
    EntityItem dropEnt = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, drop);
    res.getDrops().add(dropEnt);
  }

  protected @Nonnull ItemStack makeResin(@Nonnull Random rand) {
    return stickyResin.copy();
  }

  protected abstract boolean hasResin(@Nonnull IBlockState state);

  protected abstract @Nonnull IBlockState removeResin(@Nonnull IBlockState state);

}
