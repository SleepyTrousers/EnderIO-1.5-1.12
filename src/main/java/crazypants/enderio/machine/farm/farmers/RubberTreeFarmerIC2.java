package crazypants.enderio.machine.farm.farmers;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.machine.farm.FarmStationContainer;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.enderio.machine.farm.TileFarmStation.ToolType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RubberTreeFarmerIC2 extends TreeFarmer {

  public static Class<?> treeTap;
  private ItemStack stickyResin;

  public RubberTreeFarmerIC2() {
    super(GameRegistry.findBlock("IC2", "blockRubSapling"), GameRegistry.findBlock("IC2", "blockRubWood"));    
    Item item = GameRegistry.findItem("IC2", "itemTreetap");
    if(item != null) {
      treeTap = item.getClass();
    }
    item = GameRegistry.findItem("IC2", "itemHarz");
    if(item != null) {
      stickyResin = new ItemStack(item);  
      FarmStationContainer.slotItemsProduce.add(stickyResin);
    }    
  }
  
  public boolean isValid() {
    return woods != null && woods.length > 0 && sapling != null && saplingItem != null && treeTap != null && stickyResin != null; 
  }

  @Override
  public boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, IBlockState meta) {
    for(int x=-1;x<2;x++) {
      for(int z=-1;z<2;z++) {
       Block blk = farm.getBlock(bc.x + x, bc.y, bc.z + z);
       if(isWood(blk) || sapling == blk) {
         return false;
       }
      }      
    }
    return super.prepareBlock(farm, bc, block, meta);
  }
  
  @Override
  public IHarvestResult harvestBlock(TileFarmStation farm, BlockCoord bc, Block block, IBlockState meta) {
    HarvestResult res = new HarvestResult();
    int y = bc.y;
    boolean done = false;
    while (!done && farm.hasTool(ToolType.TREETAP)) {
      bc = new BlockCoord(bc.x, y, bc.z);
      block = farm.getBlock(bc);
      if(!isWood(block)) {
        done = true;
      } else {        
        if(attemptHarvest(res, farm.getWorld(), bc.x, y, bc.z, farm.getBlockState(bc.getBlockPos()))) {
          farm.damageTool(ToolType.TREETAP, woods[0], bc, 1);
        }
      }
      y++;
    }
    return res;
  }

  private boolean attemptHarvest(HarvestResult res, World world, int x, int y, int z, IBlockState bs) {
    int meta = bs.getBlock().getMetaFromState(bs);
    if(meta > 1 && meta < 6) {
      world.setBlockState(new BlockPos(x, y, z), bs.getBlock().getStateFromMeta(meta + 6), 3);
      world.scheduleBlockUpdate(new BlockPos(x, y, z), woods[0], woods[0].tickRate(world), 0);      
      ItemStack drop = stickyResin.copy();
      drop.stackSize = world.rand.nextInt(3) + 1;
      EntityItem dropEnt = new EntityItem(world, x + 0.5, y + 1, z + 0.5, drop);
      res.getDrops().add(dropEnt);      
      return true;
    } 
    return false;
  }

}
