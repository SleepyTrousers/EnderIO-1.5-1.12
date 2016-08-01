package crazypants.enderio.machine.farm.farmers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.config.Config;
import crazypants.enderio.machine.farm.FarmNotification;
import crazypants.enderio.machine.farm.FarmStationContainer;
import crazypants.enderio.machine.farm.TileFarmStation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.event.ForgeEventFactory;

public class TreeFarmer implements IFarmerJoe {

  private static final HeightComparator comp = new HeightComparator();

  protected Block sapling;
  protected ItemStack saplingItem;
  protected Block[] woods;
  
  protected TreeHarvestUtil harvester = new TreeHarvestUtil();
  private boolean ignoreMeta;

  public TreeFarmer(Block sapling, Block... wood) {
    this.sapling = sapling;
    if(sapling != null) {
      saplingItem = new ItemStack(sapling);
      FarmStationContainer.slotItemsSeeds.add(saplingItem);
    }
    woods = wood;
    for (Block awood : woods) {
      FarmStationContainer.slotItemsProduce.add(new ItemStack(awood));
    }
  }

  public TreeFarmer(boolean ignoreMeta, Block sapling, Block... wood) {
    this(sapling,wood);
    this.ignoreMeta = ignoreMeta;
  }

  public TreeFarmer(ItemStack sapling, ItemStack wood) {
    this(Block.getBlockFromItem(sapling.getItem()), Block.getBlockFromItem(wood.getItem()));
  }

  @Override
  public boolean canHarvest(TileFarmStation farm, BlockCoord bc, Block block, IBlockState bs) {
    return isWood(block);
  }

  protected boolean isWood(Block block) {
    for(Block wood : woods) {
      if(block == wood) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean canPlant(ItemStack stack) {
    return stack != null && stack.getItem() == saplingItem.getItem();
  }

  @Override
  public boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, IBlockState meta) {
    if(block == sapling) {
      return true;
    }
    return plantFromInventory(farm, bc, block, meta);
  }

  protected boolean plantFromInventory(TileFarmStation farm, BlockCoord bc, Block block, IBlockState meta) {
    World worldObj = farm.getWorld();
    if(canPlant(worldObj, bc)) {
      ItemStack seed = farm.takeSeedFromSupplies(saplingItem, bc, false);
      if(seed != null) {
        return plant(farm, worldObj, bc, seed);
      }
    }
    return false;
  }

  protected boolean canPlant(World worldObj, BlockCoord bc) {
    BlockPos grnPos = bc.getBlockPos().down();
    IBlockState bs = worldObj.getBlockState(grnPos);
    Block ground =bs.getBlock(); 
    IPlantable plantable = (IPlantable) sapling;
    if(sapling.canPlaceBlockAt(worldObj, bc.getBlockPos()) &&        
        ground.canSustainPlant(bs, worldObj, grnPos, EnumFacing.UP, plantable)) {
      return true;
    }
    return false;
  }

  protected boolean plant(TileFarmStation farm, World worldObj, BlockCoord bc, ItemStack seed) {    
    worldObj.setBlockToAir(bc.getBlockPos());
    if(canPlant(worldObj, bc)) {            
      worldObj.setBlockState(bc.getBlockPos(), sapling.getStateFromMeta(seed.getItemDamage()), 1 | 2);
      farm.actionPerformed(false);
      return true;
    }
    return false;
  }

  @Override
  public IHarvestResult harvestBlock(TileFarmStation farm, BlockCoord bc, Block block, IBlockState meta) {

    boolean hasAxe = farm.hasAxe();

    if(!hasAxe) {
      farm.setNotification(FarmNotification.NO_AXE);
      return null;
    }

    World worldObj = farm.getWorld();
    final EntityPlayerMP fakePlayer = farm.getFakePlayer();
    final int fortune = farm.getMaxLootingValue();
    HarvestResult res = new HarvestResult();
    harvester.harvest(farm, this, bc.getBlockPos(), res);
    Collections.sort(res.harvestedBlocks, comp);

    List<BlockPos> actualHarvests = new ArrayList<BlockPos>();

    // avoid calling this in a loop
    boolean hasShears = farm.hasShears();
    int noShearingPercentage = farm.isLowOnSaplings(bc);
    int shearCount = 0;

    for (int i = 0; i < res.harvestedBlocks.size() && hasAxe; i++) {
      BlockPos coord = res.harvestedBlocks.get(i);
      Block blk = farm.getBlock(coord);

      List<ItemStack> drops;
      boolean wasSheared = false;
      boolean wasAxed = false;
      boolean wasWood = isWood(blk);
      float chance = 1.0F;

      if (blk instanceof IShearable && hasShears && ((shearCount / res.harvestedBlocks.size() + noShearingPercentage) < 100)) {
        drops = ((IShearable) blk).onSheared(null, worldObj, coord, 0);
        wasSheared = true;
        shearCount += 100;
      } else {
        drops = blk.getDrops(worldObj, coord, farm.getBlockState(coord), fortune);
        chance = ForgeEventFactory.fireBlockHarvesting(drops, worldObj, coord, farm.getBlockState(coord), fortune, chance, false, fakePlayer);
        wasAxed = true;
      }

      if(drops != null) {
        for (ItemStack drop : drops) {
          if (worldObj.rand.nextFloat() <= chance) {
            res.drops.add(new EntityItem(worldObj, bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, drop.copy()));
          }
        }
      }

      if (wasAxed && !wasWood) {
        wasAxed = Config.farmAxeDamageOnLeafBreak;
      }

      farm.actionPerformed(wasWood || wasSheared);
      if(wasAxed) {
        farm.damageAxe(blk, new BlockCoord(coord));
        hasAxe = farm.hasAxe();
      } else if (wasSheared) {
        farm.damageShears(blk, new BlockCoord(coord));
        hasShears = farm.hasShears();
      }
      
      farm.getWorld().setBlockToAir(coord);
      actualHarvests.add(coord);
    }
    
    ItemStack[] inv = fakePlayer.inventory.mainInventory;
    for (int slot = 0; slot < inv.length; slot++) {
      ItemStack stack = inv[slot];
      if (stack != null) {
        inv[slot] = null;
        EntityItem entityitem = new EntityItem(worldObj, bc.x + 0.5, bc.y + 1, bc.z + 0.5, stack);
        res.drops.add(entityitem);
      }
    }

    if (!hasAxe) {
      farm.setNotification(FarmNotification.NO_AXE);
    }
    
    res.harvestedBlocks.clear();
    res.harvestedBlocks.addAll(actualHarvests);
    
    //try replant    
    for(EntityItem drop : res.drops) {
      if(canPlant(drop.getEntityItem()) && plant(farm, worldObj, bc, drop.getEntityItem())) {     
        res.drops.remove(drop);
        break;
      }
    }    
    return res;
  }

  public boolean getIgnoreMeta()
  {
    return ignoreMeta;
  }

  private static class HeightComparator implements Comparator<BlockPos> {

    @Override
    public int compare(BlockPos o1, BlockPos o2) {
      return compare(o2.getY(), o1.getY()); //reverse order
    }

    //same as 1.7 Integer.compare
    public static int compare(int x, int y) {
      return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }
  }

}
