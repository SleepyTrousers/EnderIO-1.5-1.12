package crazypants.enderio.machine.farm.farmers;

import java.util.ArrayList;

import crazypants.enderio.machine.farm.FarmNotification;
import crazypants.enderio.machine.farm.TileFarmStation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class PickableFarmer extends CustomSeedFarmer {

  public PickableFarmer(Block plantedBlock, int plantedBlockMeta, int grownBlockMeta, ItemStack seeds) {
    super(plantedBlock, plantedBlockMeta, grownBlockMeta, seeds);
  }

  public PickableFarmer(Block plantedBlock, int grownBlockMeta, ItemStack seeds) {
    super(plantedBlock, grownBlockMeta, seeds);
  }

  public PickableFarmer(Block plantedBlock, ItemStack seeds) {
    super(plantedBlock, seeds);
  }

  @Override
  public IHarvestResult harvestBlock(TileFarmStation farm, BlockPos bc, Block block, IBlockState meta) {
    if (!canHarvest(farm, bc, block, meta)) {
      return null;
    }
    if (!farm.hasHoe()) {
      farm.setNotification(FarmNotification.NO_HOE);
      return null;
    }
    EntityPlayerMP player = farm.getFakePlayer();
    player.interactionManager.processRightClickBlock(player, player.worldObj, null, EnumHand.MAIN_HAND, bc, EnumFacing.DOWN, 0, 0, 0);

    farm.clearJoeUseItem(false);

    farm.actionPerformed(false);
    farm.damageHoe(1, bc);
    return new HarvestResult(new ArrayList<EntityItem>(), bc);
  }

}
