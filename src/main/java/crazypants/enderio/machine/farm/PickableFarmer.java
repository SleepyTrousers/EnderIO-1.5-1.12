package crazypants.enderio.machine.farm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import crazypants.util.BlockCoord;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PickableFarmer extends SeedFarmer {
  
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
  public IHarvestResult harvestBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
    
    if(!canHarvest(farm, bc, block, meta) || !farm.hasHarvestTool()) {
      return null;
    }
    EntityPlayerMP player = farm.getFakePlayer();
    World world = farm.getWorldObj();
    player.theItemInWorldManager.activateBlockOrUseItem(player, player.worldObj, null, bc.x, bc.y, bc.z, 0, 0, 0, 0);    
    
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
    farm.actionPerformed();
    farm.damageMaxLootingItem();
    return new HarvestResult(drops, bc);
  }
  

}
