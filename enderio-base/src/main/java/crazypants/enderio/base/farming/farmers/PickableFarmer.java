package crazypants.enderio.base.farming.farmers;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.api.farm.FarmNotification;
import crazypants.enderio.api.farm.FarmingAction;
import crazypants.enderio.api.farm.IFarmer;
import crazypants.enderio.api.farm.IHarvestResult;
import crazypants.enderio.base.farming.FarmingTool;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class PickableFarmer extends CustomSeedFarmer {

  public PickableFarmer(@Nonnull Block plantedBlock, int plantedBlockMeta, int grownBlockMeta, @Nonnull ItemStack seeds) {
    super(plantedBlock, plantedBlockMeta, grownBlockMeta, seeds);
  }

  public PickableFarmer(@Nonnull Block plantedBlock, int grownBlockMeta, @Nonnull ItemStack seeds) {
    super(plantedBlock, grownBlockMeta, seeds);
  }

  public PickableFarmer(@Nonnull Block plantedBlock, @Nonnull ItemStack seeds) {
    super(plantedBlock, seeds);
  }

  @Override
  public IHarvestResult harvestBlock(@Nonnull IFarmer farm, @Nonnull final BlockPos pos, @Nonnull IBlockState state) {
    if (!canHarvest(farm, pos, state) || !farm.checkAction(FarmingAction.HARVEST, FarmingTool.HOE)) {
      return null;
    }
    if (!farm.hasTool(FarmingTool.HOE)) {
      farm.setNotification(FarmNotification.NO_HOE);
      return null;
    }
    final IHarvestResult result = new HarvestResult();

    EntityPlayerMP joe = farm.startUsingItem(FarmingTool.HOE);
    joe.interactionManager.processRightClickBlock(joe, joe.world, joe.getHeldItemMainhand(), EnumHand.MAIN_HAND, pos, EnumFacing.DOWN, 0, 0, 0);
    NNList.wrap(farm.endUsingItem(FarmingTool.HOE)).apply(new Callback<ItemStack>() {
      @Override
      public void apply(@Nonnull ItemStack drop) {
        result.addDrop(pos, drop.copy());
      }
    });
    farm.registerAction(FarmingAction.HARVEST, FarmingTool.HOE, state, pos);
    result.getHarvestedBlocks().add(pos);

    return result;
  }

}
