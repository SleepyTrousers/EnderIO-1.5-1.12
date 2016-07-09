package crazypants.enderio.machine.farm.farmers;

import java.util.ArrayList;
import java.util.List;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.machine.farm.FarmNotification;
import crazypants.enderio.machine.farm.TileFarmStation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
  public IHarvestResult harvestBlock(TileFarmStation farm, BlockCoord bc, Block block, IBlockState meta) {
    
    if(!canHarvest(farm, bc, block, meta)) {
      return null;
    }
    if(!farm.hasHoe()) {
      farm.setNotification(FarmNotification.NO_HOE);
      return null;
    }
    EntityPlayerMP player = farm.getFakePlayer();
    World world = farm.getWorld();        
    player.interactionManager.processRightClickBlock(player, player.worldObj, null, EnumHand.MAIN_HAND, bc.getBlockPos(), EnumFacing.DOWN, 0, 0, 0);
    
    List<EntityItem> drops = new ArrayList<EntityItem>();
    
    ItemStack[] inv = player.inventory.mainInventory;
    for(int slot=0;slot < inv.length;slot++) {
      ItemStack stack = inv[slot];
      if(stack != null) {
        inv[slot] = null;        
        
        EntityItem entityitem = new EntityItem(world, bc.x + 0.5, bc.y + 1, bc.z + 0.5, stack);
        drops.add(entityitem);
      }
    }
    farm.actionPerformed(false);
    farm.damageHoe(1, bc);
    return new HarvestResult(drops, bc.getBlockPos());
  }
  

}
