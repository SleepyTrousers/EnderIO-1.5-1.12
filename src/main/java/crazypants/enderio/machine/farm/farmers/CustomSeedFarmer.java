package crazypants.enderio.machine.farm.farmers;

import com.enderio.core.common.util.stackable.Things;
import crazypants.enderio.machine.farm.FarmNotification;
import crazypants.enderio.machine.farm.FarmStationContainer;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.ArrayList;
import java.util.List;

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
    if (!farm.isOpen(bc) || !farm.hasSeed(getSeeds(), bc)) {
      return false;
    }
    if (requiresFarmland() && !isGroundTilled(farm, bc) && !farm.tillBlock(bc)) {
      return false;
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
    World world = farm.getWorld();
    if (canPlant(farm, world, bc) && farm.takeSeedFromSupplies(getSeeds(), bc) != null) {
      return plant(farm, world, bc);
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

    World world = farm.getWorld();
    final EntityPlayerMP fakePlayer = farm.getFakePlayer();
    final int fortune = farm.getMaxLootingValue();
    List<EntityItem> result = new ArrayList<EntityItem>();

    List<ItemStack> drops = block.getDrops(world, bc, meta, fortune);
    float chance = ForgeEventFactory.fireBlockHarvesting(drops, world, bc, meta, fortune, 1.0F, false, fakePlayer);
    farm.damageHoe(1, bc);
    farm.actionPerformed(false);
    boolean removed = false;
    if (drops != null) {
      for (ItemStack stack : drops) {
        if (world.rand.nextFloat() <= chance) {
          if (!removed && stack.isItemEqual(getSeeds())) {
            stack.shrink(1);
            removed = true;
            if (stack.getCount() > 0) {
              result.add(new EntityItem(world, bc.getX() + 0.5, bc.getY() + 0.5, bc.getZ() + 0.5, stack.copy()));
            }
          } else {
            result.add(new EntityItem(world, bc.getX() + 0.5, bc.getY() + 0.5, bc.getZ() + 0.5, stack.copy()));
          }
        }
      }
    }

    ItemStack[] inv = fakePlayer.inventory.mainInventory;
    for (int slot = 0; slot < inv.length; slot++) {
      ItemStack stack = inv[slot];
      if (Prep.isValid(stack)) {
        inv[slot] = Prep.getEmpty();
        EntityItem entityitem = new EntityItem(world, bc.getX() + 0.5, bc.getY() + 1, bc.getZ() + 0.5, stack);
        result.add(entityitem);
      }
    }

    if (removed) {
      if (!plant(farm, world, bc)) {
        result.add(new EntityItem(world, bc.getX() + 0.5, bc.getY() + 0.5, bc.getZ() + 0.5, getSeeds().copy()));
        world.setBlockState(bc, Blocks.AIR.getDefaultState(), 1 | 2);
      }
    } else {
      world.setBlockState(bc, Blocks.AIR.getDefaultState(), 1 | 2);
    }

    return new HarvestResult(result, bc);
  }

  protected boolean isGroundTilled(TileFarmStation farm, BlockPos plantingLocation) {
    return tilledBlocks.contains(farm.getBlock(plantingLocation.down()));
  }

  protected boolean canPlant(TileFarmStation farm, World world, BlockPos bc) {
    Block target = getPlantedBlock();
    BlockPos groundPos = bc.down();
    IBlockState bs = world.getBlockState(groundPos);
    Block ground = bs.getBlock();
    IPlantable plantable = (IPlantable) getPlantedBlock();
    if (target.canPlaceBlockAt(world, bc) && (ground.canSustainPlant(bs, world, groundPos, EnumFacing.UP, plantable) || ignoreSustainCheck)
        && (!checkGroundForFarmland || isGroundTilled(farm, bc))) {
      return true;
    }
    return false;
  }

  protected boolean plant(TileFarmStation farm, World world, BlockPos bc) {
    world.setBlockState(bc, Blocks.AIR.getDefaultState(), 1 | 2);
    if (canPlant(farm, world, bc)) {
      world.setBlockState(bc, getPlantedBlock().getStateFromMeta(getPlantedBlockMeta()), 1 | 2);
      farm.actionPerformed(false);
      return true;
    }
    return false;
  }

  public boolean doesDisableTreeFarm() {
    return disableTreeFarm;
  }
}
