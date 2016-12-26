package crazypants.enderio.machine.farm.farmers;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.machine.farm.FarmNotification;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.util.Prep;
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
    World world = farm.getWorld();
    player.interactionManager.processRightClickBlock(player, player.worldObj, null, EnumHand.MAIN_HAND, bc, EnumFacing.DOWN, 0, 0, 0);

    List<EntityItem> drops = new ArrayList<EntityItem>();

    ItemStack[] inv = player.inventory.mainInventory;
    for (int slot = 0; slot < inv.length; slot++) {
      ItemStack stack = inv[slot];
      if (Prep.isValid(stack)) {
        inv[slot] = null;
        drops.add(new EntityItem(world, bc.getX() + 0.5, bc.getY() + 1, bc.getZ() + 0.5, stack));
      }
    }

    farm.actionPerformed(false);
    farm.damageHoe(1, bc);
    return new HarvestResult(drops, bc);
  }

}
