package crazypants.enderio.machine.farm;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.util.BlockCoord;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RubberTreeFarmerIC2 extends TreeFarmer {

  private Class<?> treeTap;
  private ItemStack stickyResin;

  public RubberTreeFarmerIC2() {
    super(GameRegistry.findBlock("IC2", "blockRubSapling"), GameRegistry.findBlock("IC2", "blockRubWood"));    
    Item item = GameRegistry.findItem("IC2", "itemTreetap");
    if(item != null) {
      treeTap = item.getClass();
      FarmersCommune.instance.registerToolType(treeTap);
    }
    item = GameRegistry.findItem("IC2", "itemHarz");
    if(item != null) {
      stickyResin = new ItemStack(item);  
    }    
  }
  
  public boolean isValid() {
    return woods != null && woods.length > 0 && sapling != null && saplingItem != null && treeTap != null && stickyResin != null; 
  }

  @Override
  public IHarvestResult harvestBlock(TileFarmStation farm, BlockCoord bc, Block block, int meta) {
    HarvestResult res = new HarvestResult();
    int y = bc.y;
    boolean done = false;
    while (!done && farm.hasTool(treeTap)) {
      bc = new BlockCoord(bc.x, y, bc.z);
      block = farm.getBlock(bc);
      if(!isWood(block)) {
        done = true;
      } else {
        meta = farm.getBlockMeta(bc);
        if(attemptHarvest(res, farm.getWorldObj(), bc.x, y, bc.z, meta)) {
          farm.damageTool(treeTap, woods[0], bc, 1);
        }
      }
      y++;
    }
    return res;
  }

  private boolean attemptHarvest(HarvestResult res, World world, int x, int y, int z, int meta) {
    if(meta > 1 && meta < 6) {
      world.setBlockMetadataWithNotify(x, y, z, meta + 6, 3);      
      world.scheduleBlockUpdate(x, y, z, woods[0], woods[0].tickRate(world));      
      ItemStack drop = stickyResin.copy();
      drop.stackSize = world.rand.nextInt(3) + 1;
      EntityItem dropEnt = new EntityItem(world, x + 0.5, y + 1, z + 0.5, drop);
      res.getDrops().add(dropEnt);      
      return true;
    } 
    return false;
  }

}
