package crazypants.enderio.machine.farm.farmers;

import java.util.ArrayList;
import java.util.List;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.machine.farm.FarmNotification;
import crazypants.enderio.machine.farm.FarmStationContainer;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.util.Things;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class CustomSeedFarmer implements IFarmerJoe {

  protected Block plantedBlock;
  protected int plantedBlockMeta;  
  protected int grownBlockMeta;
  protected ItemStack seeds;
  protected boolean requiresFarmland = true;
  protected Things tilledBlocks = new Things();
  protected boolean ignoreSustainCheck = false;
  protected boolean checkGroundForFarmland = false;
  protected boolean disableTreeFarm;

  public CustomSeedFarmer(Block plantedBlock, ItemStack seeds) {
    this(plantedBlock, 0, 7, seeds);
  }

  public CustomSeedFarmer(Block plantedBlock, int grownBlockMeta, ItemStack seeds) {
    this(plantedBlock, 0, grownBlockMeta, seeds);
  }

  public CustomSeedFarmer(Block plantedBlock, int plantedBlockMeta, int grownBlockMeta, ItemStack seeds) {
    this.plantedBlock = plantedBlock;
    this.plantedBlockMeta = plantedBlockMeta;
    this.grownBlockMeta = grownBlockMeta;
    this.seeds = seeds;
    FarmStationContainer.slotItemsSeeds.add(seeds);
    addTilledBlock(Blocks.FARMLAND);   
  }
  
  public void clearTilledBlocks() {
    tilledBlocks = new Things();
  }

  public void addTilledBlock(Things block) {
    tilledBlocks.add(block);
  }

  public void addTilledBlock(Block block) {
    tilledBlocks.add(block);
  }

  public boolean isIgnoreGroundCanSustainCheck() {
    return ignoreSustainCheck;
  }

  public void setIgnoreGroundCanSustainCheck(boolean ignoreSustainCheck) {
    this.ignoreSustainCheck = ignoreSustainCheck;
  }

  public boolean isCheckGroundForFarmland() {
    return checkGroundForFarmland;
  }

  public void setCheckGroundForFarmland(boolean checkGroundForFarmland) {
    this.checkGroundForFarmland = checkGroundForFarmland;
  }

  public int getPlantedBlockMeta() {
    return plantedBlockMeta;
  }

  public Block getPlantedBlock() {
    return plantedBlock;
  }

  public ItemStack getSeeds() {
    return seeds;
  }

  public int getFullyGrownBlockMeta() {
    return grownBlockMeta;
  }

  @Override
  public boolean canHarvest(TileFarmStation farm, BlockCoord bc, Block block, IBlockState bs) {
    int meta = bs.getBlock().getMetaFromState(bs);
    return block == getPlantedBlock() && getFullyGrownBlockMeta() == meta;
  }

  @Override
  public boolean canPlant(ItemStack stack) {
    if(stack == null) {
      return false;
    }
    return stack.isItemEqual(getSeeds());
  }

  @Override
  public boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, IBlockState meta) {
    if (!farm.isOpen(bc.getBlockPos())) {
      return false;
    }
    if(requiresFarmland()) {      
      if(isGroundTilled(farm, bc)) {
        return plantFromInventory(farm, bc);
      }
      if(farm.hasSeed(getSeeds(), bc)) {
        boolean tilled = tillBlock(farm, bc);
        if(!tilled) {
          return false;
        }
      }
    }
    return plantFromInventory(farm, bc);
  }

  public boolean requiresFarmland() {
    return requiresFarmland;
  }
  
  public void setRequiresFarmland(boolean requiresFarmland) {
    this.requiresFarmland = requiresFarmland;
  }

  protected boolean plantFromInventory(TileFarmStation farm, BlockCoord bc) {
    World worldObj = farm.getWorld();
    if (canPlant(farm, worldObj, bc) && farm.takeSeedFromSupplies(getSeeds(), bc) != null) {
      return plant(farm, worldObj, bc);
    }
    return false;
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

    World worldObj = farm.getWorld();
    List<EntityItem> result = new ArrayList<EntityItem>();

    List<ItemStack> drops = block.getDrops(worldObj, bc.getBlockPos(), meta, farm.getMaxLootingValue());
    farm.damageHoe(1, bc);
    farm.actionPerformed(false);
    boolean removed = false;
    if(drops != null) {
      for (ItemStack stack : drops) {
        if(!removed && stack.isItemEqual(getSeeds())) {
          stack.stackSize--;
          removed = true;
          if(stack.stackSize > 0) {
            result.add(new EntityItem(worldObj, bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, stack.copy()));
          }
        } else {
          result.add(new EntityItem(worldObj, bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, stack.copy()));
        }
      }
    }

    if(removed) {
      if(!plant(farm, worldObj, bc)) {
        result.add(new EntityItem(worldObj, bc.x + 0.5, bc.y + 0.5, bc.z + 0.5, getSeeds().copy()));
        worldObj.setBlockState(bc.getBlockPos(), Blocks.AIR.getDefaultState(), 1 | 2);
      }
    } else {
      worldObj.setBlockState(bc.getBlockPos(), Blocks.AIR.getDefaultState(), 1 | 2);
    }

    return new HarvestResult(result, bc.getBlockPos());
  }

  protected boolean tillBlock(TileFarmStation farm, BlockCoord plantingLocation) {
    World worldObj = farm.getWorld();
    BlockCoord dirtLoc = plantingLocation.getLocation(EnumFacing.DOWN);
    Block dirtBlock = farm.getBlock(dirtLoc);
    if((dirtBlock == Blocks.DIRT || dirtBlock == Blocks.GRASS) && farm.hasHoe()) {
      farm.damageHoe(1, dirtLoc);
      worldObj.setBlockState(dirtLoc.getBlockPos(), Blocks.FARMLAND.getDefaultState());
      worldObj.playSound(dirtLoc.x + 0.5F, dirtLoc.y + 0.5F, dirtLoc.z + 0.5F, Blocks.FARMLAND.getSoundType().getStepSound(), SoundCategory.BLOCKS,
          (Blocks.FARMLAND.getSoundType().getVolume() + 1.0F) / 2.0F, Blocks.FARMLAND.getSoundType().getPitch() * 0.8F, false);
      farm.actionPerformed(false);
      return true;
    }
    return false;
  }

  protected boolean isGroundTilled(TileFarmStation farm, BlockCoord plantingLocation) {
    return tilledBlocks.contains(farm.getBlock(plantingLocation.getLocation(EnumFacing.DOWN)));
  }

  protected boolean canPlant(TileFarmStation farm, World worldObj, BlockCoord bc) {
    Block target = getPlantedBlock();
    BlockPos groundPos = bc.getBlockPos().down();
    IBlockState bs = worldObj.getBlockState(groundPos);
    Block ground = bs.getBlock();
    IPlantable plantable = (IPlantable) getPlantedBlock();
    if(target.canPlaceBlockAt(worldObj, bc.getBlockPos()) &&        
        (ground.canSustainPlant(bs, worldObj, groundPos, EnumFacing.UP, plantable) || ignoreSustainCheck)
        && (!checkGroundForFarmland || isGroundTilled(farm, bc))) {
      return true;
    }
    return false;
  }

  protected boolean plant(TileFarmStation farm, World worldObj, BlockCoord bc) {
    worldObj.setBlockState(bc.getBlockPos(), Blocks.AIR.getDefaultState(), 1 | 2);
    if (canPlant(farm, worldObj, bc)) {
      worldObj.setBlockState(bc.getBlockPos(), getPlantedBlock().getStateFromMeta(getPlantedBlockMeta()), 1 | 2);
      farm.actionPerformed(false);
      return true;
    }
    return false;
  }

  public boolean doesDisableTreeFarm()
  {
    return disableTreeFarm;
  }
}
