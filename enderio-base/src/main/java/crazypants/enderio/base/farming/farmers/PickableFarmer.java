package crazypants.enderio.base.farming.farmers;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.farming.FarmNotification;
import crazypants.enderio.base.farming.FarmingAction;
import crazypants.enderio.base.farming.FarmingTool;
import crazypants.enderio.base.farming.IFarmer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
  public IHarvestResult harvestBlock(@Nonnull IFarmer farm, @Nonnull final BlockPos bc, @Nonnull Block block, @Nonnull IBlockState meta) {
    if (!canHarvest(farm, bc, block, meta)) {
      return null;
    }
    if (!farm.hasTool(FarmingTool.HOE)) {
      farm.setNotification(FarmNotification.NO_HOE);
      return null;
    }
    final HarvestResult result = new HarvestResult();
    final World world = farm.getWorld();

    EntityPlayerMP joe = farm.startUsingItem(FarmingTool.HOE);
    joe.interactionManager.processRightClickBlock(joe, joe.world, joe.getHeldItemMainhand(), EnumHand.MAIN_HAND, bc, EnumFacing.DOWN, 0, 0, 0);
    farm.endUsingItem(FarmingTool.HOE).apply(new Callback<ItemStack>() {
      @Override
      public void apply(@Nonnull ItemStack drop) {
        result.getDrops().add(new EntityItem(world, bc.getX() + 0.5, bc.getY() + 0.5, bc.getZ() + 0.5, drop.copy()));
      }
    });
    farm.registerAction(FarmingAction.HARVEST, FarmingTool.HOE, meta, bc);
    result.getHarvestedBlocks().add(bc);

    return result;
  }

}
