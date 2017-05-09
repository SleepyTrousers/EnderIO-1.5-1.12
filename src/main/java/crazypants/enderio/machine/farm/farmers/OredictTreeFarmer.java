package crazypants.enderio.machine.farm.farmers;

import crazypants.enderio.machine.farm.FarmStationContainer;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.util.Prep;
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

  public OredictTreeFarmer(Things saplings, Things woods) {
    super(null, woods);
    this.saplings = saplings;
    FarmStationContainer.slotItemsSeeds.addAll(saplings.getItemStacks());
  }

  @Override
  public boolean canPlant(ItemStack stack) {
    return stack != null && saplings.contains(stack) && Block.getBlockFromItem(stack.getItem()) != null;
  }

  @Override
  public boolean prepareBlock(TileFarmStation farm, BlockPos bc, Block block, IBlockState meta) {
    if (saplings.contains(block)) {
      return true;
    }
    return plantFromInventory(farm, bc, block, meta);
  }

  @Override
  protected boolean plantFromInventory(TileFarmStation farm, BlockPos bc, Block block, IBlockState meta) {
    World world = farm.getWorld();
    final ItemStack currentSapling = farm.getSeedTypeInSuppliesFor(bc);
    if (canPlant(world, bc, currentSapling)) {
      ItemStack seed = farm.takeSeedFromSupplies(currentSapling, bc, false);
      if (Prep.isValid(seed)) {
        return plant(farm, world, bc, seed);
      }
    }
    return false;
  }

  protected boolean canPlant(World world, BlockPos bc, ItemStack saplingIn) {
    if (!saplings.contains(saplingIn)) {
      return false;
    }
    BlockPos grnPos = bc.down();
    IBlockState bs = world.getBlockState(grnPos);
    Block ground = bs.getBlock();
    Block saplingBlock = Block.getBlockFromItem(saplingIn.getItem());
    if (saplingBlock == null) {
      return false;
    }
    if (saplingBlock.canPlaceBlockAt(world, bc)) {
      if (saplingBlock instanceof IPlantable) {
        return ground.canSustainPlant(bs, world, grnPos, EnumFacing.UP, (IPlantable) saplingBlock);
      }
      return true;
    }
    return false;
  }

  @Override
  protected boolean plant(TileFarmStation farm, World world, BlockPos bc, ItemStack seed) {
    if (canPlant(world, bc, seed)) {
      world.setBlockToAir(bc);
      final Item item = seed.getItem();
      world.setBlockState(bc, Block.getBlockFromItem(item).getStateFromMeta(item.getMetadata(seed.getMetadata())), 1 | 2);
      farm.actionPerformed(false);
      return true;
    } else {
      return false;
    }
  }

}
