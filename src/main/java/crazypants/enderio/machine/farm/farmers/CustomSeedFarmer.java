package crazypants.enderio.machine.farm.farmers;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.machine.farm.FarmNotification;
import crazypants.enderio.machine.farm.FarmStationContainer;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.util.Prep;
import crazypants.util.Things;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.ForgeEventFactory;

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
  public boolean canHarvest(TileFarmStation farm, BlockPos bc, Block block, IBlockState bs) {
    int meta = bs.getBlock().getMetaFromState(bs);
    return block == getPlantedBlock() && getFullyGrownBlockMeta() == meta;
  }

  @Override
  public boolean canPlant(ItemStack stack) {
    if (Prep.isInvalid(stack)) {
      return false;
    }
    return stack.isItemEqual(getSeeds());
  }

  @Override
  public boolean prepareBlock(TileFarmStation farm, BlockPos bc, Block block, IBlockState meta) {
    if (!farm.isOpen(bc)) {
      return false;
    }
    if (requiresFarmland()) {
      if (isGroundTilled(farm, bc)) {
        return plantFromInventory(farm, bc);
      }
      if (farm.hasSeed(getSeeds(), bc)) {
        boolean tilled = tillBlock(farm, bc);
        if (!tilled) {
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

  protected boolean plantFromInventory(TileFarmStation farm, BlockPos bc) {
    World worldObj = farm.getWorld();
    if (canPlant(farm, worldObj, bc) && farm.takeSeedFromSupplies(getSeeds(), bc) != null) {
      return plant(farm, worldObj, bc);
    }
    return false;
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

    World worldObj = farm.getWorld();
    final EntityPlayerMP fakePlayer = farm.getFakePlayer();
    final int fortune = farm.getMaxLootingValue();
    List<EntityItem> result = new ArrayList<EntityItem>();

    List<ItemStack> drops = block.getDrops(worldObj, bc, meta, fortune);
    float chance = ForgeEventFactory.fireBlockHarvesting(drops, worldObj, bc, meta, fortune, 1.0F, false, fakePlayer);
    farm.damageHoe(1, bc);
    farm.actionPerformed(false);
    boolean removed = false;
    if (drops != null) {
      for (ItemStack stack : drops) {
        if (worldObj.rand.nextFloat() <= chance) {
          if (!removed && stack.isItemEqual(getSeeds())) {
            stack.stackSize--;
            removed = true;
            if (stack.stackSize > 0) {
              result.add(new EntityItem(worldObj, bc.getX() + 0.5, bc.getY() + 0.5, bc.getZ() + 0.5, stack.copy()));
            }
          } else {
            result.add(new EntityItem(worldObj, bc.getX() + 0.5, bc.getY() + 0.5, bc.getZ() + 0.5, stack.copy()));
          }
        }
      }
    }

    ItemStack[] inv = fakePlayer.inventory.mainInventory;
    for (int slot = 0; slot < inv.length; slot++) {
      ItemStack stack = inv[slot];
      if (Prep.isValid(stack)) {
        inv[slot] = Prep.getEmpty();
        EntityItem entityitem = new EntityItem(worldObj, bc.getX() + 0.5, bc.getY() + 1, bc.getZ() + 0.5, stack);
        result.add(entityitem);
      }
    }

    if (removed) {
      if (!plant(farm, worldObj, bc)) {
        result.add(new EntityItem(worldObj, bc.getX() + 0.5, bc.getY() + 0.5, bc.getZ() + 0.5, getSeeds().copy()));
        worldObj.setBlockState(bc, Blocks.AIR.getDefaultState(), 1 | 2);
      }
    } else {
      worldObj.setBlockState(bc, Blocks.AIR.getDefaultState(), 1 | 2);
    }

    return new HarvestResult(result, bc);
  }

  protected boolean tillBlock(TileFarmStation farm, BlockPos plantingLocation) {
    World worldObj = farm.getWorld();
    BlockPos dirtLoc = plantingLocation.down();
    Block dirtBlock = farm.getBlock(dirtLoc);
    if ((dirtBlock == Blocks.DIRT || dirtBlock == Blocks.GRASS) && farm.hasHoe()) {
      farm.damageHoe(1, dirtLoc);
      worldObj.setBlockState(dirtLoc, Blocks.FARMLAND.getDefaultState());
      final SoundType soundType = Blocks.FARMLAND.getSoundType(Blocks.FARMLAND.getDefaultState(), worldObj, dirtLoc, null);
      worldObj.playSound(dirtLoc.getX() + 0.5F, dirtLoc.getY() + 0.5F, dirtLoc.getZ() + 0.5F, soundType.getStepSound(), SoundCategory.BLOCKS,
          (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F, false);
      farm.actionPerformed(false);
      return true;
    }
    return false;
  }

  protected boolean isGroundTilled(TileFarmStation farm, BlockPos plantingLocation) {
    return tilledBlocks.contains(farm.getBlock(plantingLocation.down()));
  }

  protected boolean canPlant(TileFarmStation farm, World worldObj, BlockPos bc) {
    Block target = getPlantedBlock();
    BlockPos groundPos = bc.down();
    IBlockState bs = worldObj.getBlockState(groundPos);
    Block ground = bs.getBlock();
    IPlantable plantable = (IPlantable) getPlantedBlock();
    if (target.canPlaceBlockAt(worldObj, bc) && (ground.canSustainPlant(bs, worldObj, groundPos, EnumFacing.UP, plantable) || ignoreSustainCheck)
        && (!checkGroundForFarmland || isGroundTilled(farm, bc))) {
      return true;
    }
    return false;
  }

  protected boolean plant(TileFarmStation farm, World worldObj, BlockPos bc) {
    worldObj.setBlockState(bc, Blocks.AIR.getDefaultState(), 1 | 2);
    if (canPlant(farm, worldObj, bc)) {
      worldObj.setBlockState(bc, getPlantedBlock().getStateFromMeta(getPlantedBlockMeta()), 1 | 2);
      farm.actionPerformed(false);
      return true;
    }
    return false;
  }

  public boolean doesDisableTreeFarm() {
    return disableTreeFarm;
  }
}
