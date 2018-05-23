package crazypants.enderio.base.farming.farmers;

import java.util.Collections;
import java.util.Comparator;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.api.farm.FarmNotification;
import crazypants.enderio.api.farm.FarmingAction;
import crazypants.enderio.api.farm.IFarmer;
import crazypants.enderio.api.farm.IHarvestResult;
import crazypants.enderio.base.farming.FarmingTool;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class StemFarmer extends CustomSeedFarmer {

  private static final @Nonnull HeightCompatator COMP = new HeightCompatator();

  public StemFarmer(@Nonnull Block plantedBlock, @Nonnull ItemStack seeds) {
    super(plantedBlock, seeds);
  }

  @Override
  public boolean prepareBlock(@Nonnull IFarmer farm, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    if (plantedBlock == state.getBlock()) {
      return true;
    }
    return plantFromInventory(farm, pos);
  }

  @Override
  public boolean canHarvest(@Nonnull IFarmer farm, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    return farm.getBlockState(pos.up()).getBlock() == plantedBlock;
  }

  @Override
  public boolean canPlant(@Nonnull ItemStack stack) {
    return seeds.isItemEqual(stack);
  }

  @Override
  public IHarvestResult harvestBlock(@Nonnull final IFarmer farm, @Nonnull final BlockPos pos, @Nonnull IBlockState state) {
    boolean hasHoe = farm.hasTool(FarmingTool.HOE);
    if (!hasHoe) {
      return new HarvestResult();
    }
    final World world = farm.getWorld();
    final EntityPlayerMP joe = farm.startUsingItem(FarmingTool.HOE);
    final int fortune = farm.getLootingValue(FarmingTool.HOE);
    final HarvestResult result = new HarvestResult();
    BlockPos harvestCoord = pos;
    boolean done = false;
    do {
      harvestCoord = harvestCoord.offset(EnumFacing.UP);
      final IBlockState harvestState = farm.getBlockState(harvestCoord);
      if (hasHoe && plantedBlock == harvestState.getBlock()) {
        result.getHarvestedBlocks().add(harvestCoord);
        NNList<ItemStack> drops = new NNList<>();
        plantedBlock.getDrops(drops, world, harvestCoord, state, fortune);
        float chance = ForgeEventFactory.fireBlockHarvesting(drops, joe.world, harvestCoord, state, fortune, 1.0F, false, joe);

        BlockPos farmPos = farm.getLocation();
        for (ItemStack drop : drops) {
          if (world.rand.nextFloat() <= chance) {
            result.getDrops().add(new EntityItem(world, farmPos.getX() + 0.5, farmPos.getY() + 0.5, farmPos.getZ() + 0.5, drop.copy()));
          }
        }

        farm.registerAction(FarmingAction.HARVEST, FarmingTool.HOE, harvestState, harvestCoord);
        hasHoe = farm.hasTool(FarmingTool.HOE);
      } else {
        if (!hasHoe) {
          farm.setNotification(FarmNotification.NO_HOE);
        }
        done = true;
      }
    } while (!done);

    NNList.wrap(farm.endUsingItem(FarmingTool.HOE)).apply(new Callback<ItemStack>() {
      @Override
      public void apply(@Nonnull ItemStack drop) {
        result.getDrops().add(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop.copy()));
      }
    });

    NNList<BlockPos> toClear = new NNList<BlockPos>(result.getHarvestedBlocks());
    Collections.sort(toClear, COMP);
    toClear.apply(new Callback<BlockPos>() {
      @Override
      public void apply(@Nonnull BlockPos coord) {
        farm.getWorld().setBlockToAir(coord);
      }
    });

    return result;
  }

  @Override
  protected boolean plantFromInventory(@Nonnull IFarmer farm, @Nonnull BlockPos bc) {
    World world = farm.getWorld();
    if (canPlant(farm, world, bc) && Prep.isValid(farm.takeSeedFromSupplies(seeds, bc))) {
      return plant(farm, world, bc);
    }
    return false;
  }

  private static class HeightCompatator implements Comparator<BlockPos> {

    @Override
    public int compare(BlockPos o1, BlockPos o2) {
      return -Integer.compare(o1.getY(), o2.getY());
    }

  }

}
