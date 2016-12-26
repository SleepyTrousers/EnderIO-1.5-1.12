package crazypants.enderio.machine.farm.farmers;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.machine.farm.FarmStationContainer;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.util.Things;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class OredictTreeFarmer extends TreeFarmer {

  protected Things saplings;
  protected Things woodBlocks;
  
  public OredictTreeFarmer(Things saplings, Things woods) {
    super(null);
    this.saplings = saplings;
    this.woodBlocks = woods;
    FarmStationContainer.slotItemsSeeds.addAll(saplings.getItemStacks());
    FarmStationContainer.slotItemsProduce.addAll(woods.getItemStacks());
  }

  @Override
  protected boolean isWood(Block block) {
    return woodBlocks.contains(block);
  }

  @Override
  public boolean canPlant(ItemStack stack) {
    return stack != null && saplings.contains(stack) && Block.getBlockFromItem(stack.getItem()) != null;
  }

  @Override
  public boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, IBlockState meta) {
    if (saplings.contains(block)) {
      return true;
    }
    return plantFromInventory(farm, bc, block, meta);
  }

  @Override
  protected boolean plantFromInventory(TileFarmStation farm, BlockCoord bc, Block block, IBlockState meta) {
    World worldObj = farm.getWorld();
    final ItemStack sapling = farm.getSeedTypeInSuppliesFor(bc);
    if (canPlant(worldObj, bc, sapling)) {
      ItemStack seed = farm.takeSeedFromSupplies(sapling, bc, false);
      if(seed != null) {
        return plant(farm, worldObj, bc, seed);
      }
    }
    return false;
  }

  protected boolean canPlant(World worldObj, BlockCoord bc, ItemStack sapling) {
    if (!saplings.contains(sapling)) {
      return false;
    }
    BlockPos grnPos = bc.getBlockPos().down();
    IBlockState bs = worldObj.getBlockState(grnPos);
    Block ground = bs.getBlock();
    Block saplingBlock = Block.getBlockFromItem(sapling.getItem());
    if (saplingBlock == null) {
      return false;
    }
    if (saplingBlock.canPlaceBlockAt(worldObj, bc.getBlockPos())) {
      if (saplingBlock instanceof IPlantable) {
        return ground.canSustainPlant(bs, worldObj, grnPos, EnumFacing.UP, (IPlantable) saplingBlock);
      }
      return true;
    }
    return false;
  }

  @Override
  protected boolean plant(TileFarmStation farm, World worldObj, BlockCoord bc, ItemStack seed) {    
    if (canPlant(worldObj, bc, seed)) {
      worldObj.setBlockToAir(bc.getBlockPos());
      final Item item = seed.getItem();
      worldObj.setBlockState(bc.getBlockPos(), Block.getBlockFromItem(item).getStateFromMeta(item.getMetadata(seed.getMetadata())), 1 | 2);
      farm.actionPerformed(false);
      return true;
    } else {
      return false;
    }
  }

}
